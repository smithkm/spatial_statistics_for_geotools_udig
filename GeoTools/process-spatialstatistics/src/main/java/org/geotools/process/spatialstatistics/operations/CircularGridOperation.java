/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2014, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotools.process.spatialstatistics.operations;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.grid.hexagon.HexagonOrientation;
import org.geotools.process.spatialstatistics.core.FeatureTypes;
import org.geotools.process.spatialstatistics.enumeration.CircularType;
import org.geotools.process.spatialstatistics.storage.IFeatureInserter;
import org.geotools.process.spatialstatistics.transformation.GXTSimpleFeatureCollection;
import org.geotools.util.logging.Logging;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Creates circular grids from extent or bounds source features.
 * 
 * @author Minpa Lee, MangoSystem
 * 
 * @source $URL$
 * 
 */
public class CircularGridOperation extends GeneralOperation {
    protected static final Logger LOGGER = Logging.getLogger(CircularGridOperation.class);

    private static final String TYPE_NAME = "circulargrid";

    private static final String UID = "uid";

    private static int quadrantSegments = 16;

    private CircularType circularType = CircularType.Grid;

    private SimpleFeatureCollection boundsSource = null;

    private String the_geom = null;

    private Geometry boundsGeometry = null;

    public CircularType getCircularType() {
        return circularType;
    }

    public void setCircularType(CircularType circularType) {
        this.circularType = circularType;
    }

    public void setBoundsGeometry(Geometry geometryBoundary) {
        this.boundsGeometry = geometryBoundary;
    }

    public void setBoundsSource(SimpleFeatureCollection boundsSource) {
        this.boundsSource = boundsSource;
        if (boundsSource == null) {
            this.the_geom = null;
        } else {
            this.the_geom = boundsSource.getSchema().getGeometryDescriptor().getLocalName();
        }
    }

    public SimpleFeatureCollection execute(ReferencedEnvelope bbox, Double radius)
            throws IOException {
        if (circularType == CircularType.Hex) {
            HexagonOperation operation = new HexagonOperation();
            operation.setOrientation(HexagonOrientation.FLAT);
            operation.setBoundsSource(boundsSource);
            operation.setGeometryBoundary(boundsGeometry);

            return new CircularFeatureCollection(operation.execute(bbox, radius), radius);
        } else {
            CoordinateReferenceSystem crs = bbox.getCoordinateReferenceSystem();
            SimpleFeatureType schema = FeatureTypes.getDefaultType(TYPE_NAME, Polygon.class, crs);
            schema = FeatureTypes.add(schema, UID, Integer.class, 19);
            IFeatureInserter featureWriter = getFeatureWriter(schema);
            try {
                int featureID = 0;
                final double diameter = radius * 2.0;

                double currentY = bbox.getMinY() + radius;
                while (currentY <= bbox.getMaxY()) {
                    double currentX = bbox.getMinX() + radius;
                    while (currentX <= bbox.getMaxX()) {
                        Point center = gf.createPoint(new Coordinate(currentX, currentY));
                        Geometry geometry = center.buffer(radius, quadrantSegments);

                        if (boundsGeometry != null) {
                            if (!boundsGeometry.intersects(geometry)) {
                                continue;
                            }
                        }

                        if (boundsSource != null) {
                            Filter filter = ff.intersects(ff.property(the_geom),
                                    ff.literal(geometry));
                            if (boundsSource.subCollection(filter).isEmpty()) {
                                continue;
                            }
                        }

                        SimpleFeature newFeature = featureWriter.buildFeature(null);
                        newFeature.setAttribute(UID, ++featureID);
                        newFeature.setDefaultGeometry(geometry);
                        featureWriter.write(newFeature);

                        currentX += diameter;
                    }
                    currentY += diameter;
                }
            } catch (Exception e) {
                featureWriter.rollback(e);
            } finally {
                featureWriter.close();
            }
            return featureWriter.getFeatureCollection();
        }
    }

    static class CircularFeatureCollection extends GXTSimpleFeatureCollection {
        protected static final Logger LOGGER = Logging.getLogger(CircularFeatureCollection.class);

        private SimpleFeatureType schema;

        private Double radius;

        public CircularFeatureCollection(SimpleFeatureCollection delegate, Double radius) {
            super(delegate);

            this.schema = FeatureTypes.build(delegate, delegate.getSchema().getTypeName());
            this.radius = radius;
        }

        @Override
        public SimpleFeatureIterator features() {
            return new CircularFeatureIterator(delegate.features(), getSchema(), radius);
        }

        @Override
        public SimpleFeatureType getSchema() {
            return schema;
        }

        @Override
        public ReferencedEnvelope getBounds() {
            return new ReferencedEnvelope(delegate.getBounds(),
                    schema.getCoordinateReferenceSystem());
        }

        static class CircularFeatureIterator implements SimpleFeatureIterator {
            private SimpleFeatureIterator delegate;

            private SimpleFeatureBuilder builder;

            private Double radius;

            public CircularFeatureIterator(SimpleFeatureIterator delegate,
                    SimpleFeatureType schema, Double radius) {
                this.delegate = delegate;
                this.builder = new SimpleFeatureBuilder(schema);
                this.radius = radius * (Math.sqrt(3) / 2);
            }

            @Override
            public void close() {
                delegate.close();
            }

            @Override
            public boolean hasNext() {
                return delegate.hasNext();
            }

            @Override
            public SimpleFeature next() throws NoSuchElementException {
                SimpleFeature feature = delegate.next();
                for (Object attribute : feature.getAttributes()) {
                    if (attribute instanceof Geometry) {
                        Geometry geometry = (Geometry) attribute;
                        builder.add(geometry.getCentroid().buffer(radius, quadrantSegments));
                    } else {
                        builder.add(attribute);
                    }
                }
                return builder.buildFeature(feature.getID());
            }
        }
    }
}