/* Copyright 2018-2019 Ibrahim DEMIR
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

import org.moeaframework.algorithm.AbstractAlgorithm;
import org.moeaframework.core.Problem;

public abstract class AbstractSimulatedAnnealingAlgorithm extends AbstractAlgorithm{

	protected final double tMin;
	protected final double tMax;
	protected double temperature;
	
	public AbstractSimulatedAnnealingAlgorithm(Problem problem, double tMin, double tMax) {
		super(problem);
		this.tMin = tMin;
		this.tMax = tMax;
	}

	public double getTemperature() {
		return temperature;
	}

	public double gettMin() {
		return tMin;
	}

	public double gettMax() {
		return tMax;
	}
		
}
