/* Copyright 2009-2025 David Hadka
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
package org.moeaframework.analysis.sensitivity;

import org.moeaframework.analysis.parameter.Parameter;

/**
 * Interface for results supporting total-order effects.  Total-order effects measure the impact or influence of a
 * parameter, including all higher-order effects.  In other words, this is the summation of the first-order,
 * second-order, third, etc. effects.
 */
public interface TotalOrderSensitivity extends SensitivityResult {
	
	/**
	 * Returns the total-order effect for the given parameter.
	 * 
	 * @param key the parameter
	 * @return the total-order effect
	 */
	public Sensitivity<Parameter<?>> getTotalOrder(Parameter<?> key);
	
	/**
	 * Returns the total-order effect for the given parameter.
	 * 
	 * @param key the parameter name
	 * @return the total-order effect
	 */
	public default Sensitivity<Parameter<?>> getTotalOrder(String key) {
		return getTotalOrder(getParameterSet().get(key));
	}

}
