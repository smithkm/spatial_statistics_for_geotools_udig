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
package org.geotools.process.spatialstatistics.core;

import java.util.logging.Logger;

import javax.measure.Quantity;
import javax.measure.quantity.Area;
import javax.measure.quantity.Length;
import tec.uom.se.quantity.Quantities;

import javax.measure.Unit;

import org.geotools.process.spatialstatistics.enumeration.AreaUnit;
import org.geotools.process.spatialstatistics.enumeration.DistanceUnit;
import org.geotools.referencing.CRS;
import org.geotools.util.logging.Logging;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;

/**
 * Utility class for unit conversion
 * 
 * @author Minpa Lee, MangoSystem
 * 
 * @source $URL$
 */
public class UnitConverter {
    protected static final Logger LOGGER = Logging.getLogger(UnitConverter.class);

    public static double convertDistance(double value, DistanceUnit valueUnit,
            Unit<Length> targetUnit) {
        Quantity<Length> measure = Quantities.getQuantity(value, DistanceUnit.Default.unit);
        
        return measure.to(targetUnit).getValue().doubleValue();
    }

    public static double convertDistance(Quantity<Length> measure, DistanceUnit targetUnit) {
        if(targetUnit==DistanceUnit.Default) {
            return measure.getValue().doubleValue();
        } else {
            return measure.to(targetUnit.unit).getValue().doubleValue();
        }
    }

    public static double convertArea(Quantity<Area> measure, AreaUnit targetUnit) {
        if(targetUnit==AreaUnit.Default) {
            return measure.getValue().doubleValue();
        } else {
            return measure.to(targetUnit.unit).getValue().doubleValue();
        }
    }

    @SuppressWarnings("unchecked")
    public static Unit<Length> getLengthUnit(CoordinateReferenceSystem crs) {
        CoordinateReferenceSystem horCRS = CRS.getHorizontalCRS(crs);
        if (horCRS instanceof GeographicCRS) {
            return DistanceUnit.Default.unit;
        } else {
            return (Unit<Length>) horCRS.getCoordinateSystem().getAxis(0).getUnit();
        }
    }

    @SuppressWarnings("unchecked")
    public static Unit<Area> getAreaUnit(CoordinateReferenceSystem crs) {
        CoordinateReferenceSystem horCRS = CRS.getHorizontalCRS(crs);
        if (horCRS instanceof GeographicCRS) {
            return AreaUnit.Default.unit;
        } else {
            Unit<?> distUnit = horCRS.getCoordinateSystem().getAxis(0).getUnit();
            return (Unit<Area>) distUnit.multiply(distUnit);
        }
    }
}
