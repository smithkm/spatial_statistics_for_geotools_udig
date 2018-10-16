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
package org.geotools.process.spatialstatistics.enumeration;

import javax.measure.Unit;
import javax.measure.quantity.Area;
import si.uom.SI;
import tec.uom.se.unit.MetricPrefix;
import systems.uom.common.USCustomary;

/**
 * Area Unit
 * 
 * @author Minpa Lee, MangoSystem
 * 
 * @source $URL$
 */
public enum AreaUnit {
    /**
     * If the units are not specified, or Default is used, the linear unit of the input features' spatial reference is used.
     */
    Default(null),
    /**
     * SquareMeters
     */
    SquareMeters(SI.SQUARE_METRE),
    /**
     * SquareKilometers
     */
    @SuppressWarnings("unchecked")
    SquareKilometers((Unit<Area>)MetricPrefix.KILO(SI.METRE).pow(2)),
    /**
     * SquareFeet
     */
    SquareFeet(USCustomary.SQUARE_FOOT),
    /**
     * SquareYards
     */
    @SuppressWarnings("unchecked")
    SquareYards((Unit<Area>)USCustomary.YARD.pow(2)),
    /**
     * SquareMiles
     */
    @SuppressWarnings("unchecked")
    SquareMiles((Unit<Area>)USCustomary.MILE.pow(2)),
    /**
     * Hectare
     */
    Hectare(USCustomary.HECTARE),
    /**
     * Acre
     */
    Acre(USCustomary.ACRE);
    
    public final Unit<Area> unit;

    private AreaUnit(Unit<Area> unit) {
        this.unit = unit;
    }
    
}
