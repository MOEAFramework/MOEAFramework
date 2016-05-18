/* Copyright 2009-2016 David Hadka
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
package org.moeaframework.algorithm.single;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.variable.RealVariable;

/**
 * Self adaptive variation based on the normal distribution.  The self adaptive
 * parameter {@code sigma} is stored as the {@value SIGMA} attribute.  Each
 * call to {@link #evolve(Solution[])} performs the following changes:
 * <pre>
 *   sigma = sigma * e^(tau * N(0,1))
 *   vars = vars + sigma * N(0,I)
 * </pre>
 * where {@code N(0,1)} and {@code N(0,I)} are normally-distributed random
 * numbers with mean {@code 0} and standard deviation {@code 1}.
 */
public class SelfAdaptiveNormalVariation implements Variation {
	
	/**
	 * The attribute for storing the self adaptive parameter.
	 */
	public static final String SIGMA = "sigma";

	@Override
	public int getArity() {
		return 1;
	}

	@Override
	public Solution[] evolve(Solution[] parents) {
		Solution child = parents[0].copy();
		double sigma = 1.0;
		double tau = 1.0 / Math.sqrt(2.0 * child.getNumberOfVariables());
		
		if (child.hasAttribute(SIGMA)) {
			sigma = (Double)child.getAttribute(SIGMA);
		}
		
		sigma *= Math.exp(tau*PRNG.nextGaussian());
		child.setAttribute(SIGMA, sigma);
		
		for (int i = 0; i < child.getNumberOfVariables(); i++) {
			RealVariable variable = (RealVariable)child.getVariable(i);
			double value = variable.getValue();
			value += sigma * PRNG.nextGaussian();
			
			if (value < variable.getLowerBound()) {
				value = variable.getLowerBound();
			} else if (value > variable.getUpperBound()) {
				value = variable.getUpperBound();
			}
			
			variable.setValue(value);
		}
		
		return new Solution[] { child };
	}

}
