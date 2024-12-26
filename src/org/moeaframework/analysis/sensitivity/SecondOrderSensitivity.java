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

import org.apache.commons.lang3.tuple.Pair;
import org.moeaframework.analysis.parameter.Parameter;

/**
 * Interface for results supporting second-order effects.  Second-order effects measure the impact or influence of each
 * pair of parameters.  Results are symmetric, so the order of the parameters does not matter.
 */
public interface SecondOrderSensitivity extends SensitivityResult {
	
	/**
	 * Returns the second-order effect for the given parameters.
	 * 
	 * @param left the first parameter
	 * @param right the second parameter
	 * @return the second-order effect
	 */
	public Sensitivity<Pair<Parameter<?>, Parameter<?>>> getSecondOrder(Parameter<?> left, Parameter<?> right);
	
	/**
	 * Returns the second-order effect for the given parameters.
	 * 
	 * @param left the first parameter name
	 * @param right the second parameter name
	 * @return the second-order effect
	 */
	public default Sensitivity<Pair<Parameter<?>, Parameter<?>>> getSecondOrder(String left, String right) {
		return getSecondOrder(getParameterSet().get(left), getParameterSet().get(right));
	}

}
