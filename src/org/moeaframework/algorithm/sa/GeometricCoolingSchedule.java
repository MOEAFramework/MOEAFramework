/* Copyright 2009-2024 David Hadka
 *
 * This file is part of the MOEA Framework.
 *
 * The MOEA Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * The MOEA Framework is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.moeaframework.algorithm.sa;

import org.moeaframework.core.configuration.Property;
import org.moeaframework.core.configuration.Validate;

/**
 * Geometric reduction rule for simulated annealing.  The next temperature is calculated as
 * <pre>
 *   t = t * a
 * </pre>
 */
public class GeometricCoolingSchedule implements CoolingSchedule {
	
	/**
	 * The cooling rate.
	 */
	private double alpha;
	
	/**
	 * Constructs a new geometric cooling schedule.
	 */
	public GeometricCoolingSchedule() {
		this(0.8);
	}
	
	/**
	 * Constructs a new geometric cooling schedule.
	 * 
	 * @param alpha the cooling rate
	 */
	public GeometricCoolingSchedule(double alpha) {
		super();
		setAlpha(alpha);
	}

	/**
	 * Returns the cooling rate.
	 * 
	 * @return the cooling rate
	 */
	public double getAlpha() {
		return alpha;
	}

	/**
	 * Sets the cooling rate.
	 * 
	 * @param alpha the cooling rate
	 */
	@Property
	public void setAlpha(double alpha) {
		Validate.inclusiveBetween("alpha", Math.nextUp(0.0), Math.nextDown(1.0), alpha);
		this.alpha = alpha;
	}

	@Override
	public double nextTemperature(double currentTemperature) {
		return currentTemperature * alpha;
	}

}
