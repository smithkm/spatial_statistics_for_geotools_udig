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
import javax.measure.quantity.Length;
import si.uom.SI;
import tec.uom.se.unit.MetricPrefix;
import systems.uom.common.USCustomary;

/**
 * Distance Unit
 * 
 * @author Minpa Lee, MangoSystem
 * 
 * @source $URL$
 */
public enum DistanceUnit {
    /**
     * If the units are not specified, or Default is used, the linear unit of the input features' spatial reference is used.
     */
    Default(null),
    /**
     * Meters
     */
    Meters(SI.METRE),
    /**
     * Kilometers
     */
    Kilometers(MetricPrefix.KILO(SI.METRE)),
    /**
     * Inches
     */
    Inches(USCustomary.INCH),
    /**
     * Feet
     */
    Feet(USCustomary.FOOT),
    /**
     * Yards
     */
    Yards(USCustomary.YARD),
    /**
     * Miles
     */
    Miles(USCustomary.MILE),
    /**
     * NauticalMiles
     */
    NauticalMiles(USCustomary.NAUTICAL_MILE);
    
    public final Unit<Length> unit;

    private DistanceUnit(Unit<Length> unit) {
        this.unit = unit;
    }
    
}
