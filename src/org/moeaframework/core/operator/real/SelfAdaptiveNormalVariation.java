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
package org.moeaframework.core.operator.real;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.attribute.Attribute;
import org.moeaframework.core.operator.Mutation;
import org.moeaframework.core.variable.RealVariable;

/**
 * Self adaptive variation based on the normal distribution.  The self adaptive parameter {@code sigma} is stored as
 * the {@link Sigma} attribute.  Each call to {@link #evolve(Solution[])} performs the following changes:
 * <pre>
 *   sigma = sigma * e^(tau * N(0,1))
 *   vars = vars + sigma * N(0,I)
 * </pre>
 * where {@code N(0,1)} and {@code N(0,I)} are normally-distributed random numbers with mean {@code 0} and standard
 * deviation {@code 1}.
 */
public class SelfAdaptiveNormalVariation implements Mutation {
	
	/**
	 * Constructs a new instance of the self-adaptive variation based on the normal distribution.
	 */
	public SelfAdaptiveNormalVariation() {
		super();
	}
	
	@Override
	public String getName() {
		return "selfadaptive";
	}

	@Override
	public Solution mutate(Solution parent) {
		Solution child = parent.copy();
		double sigma = 1.0;
		double tau = 1.0 / Math.sqrt(2.0 * child.getNumberOfVariables());
		
		// copy attribute from parent
		if (Sigma.hasAttribute(parent)) {
			sigma = Sigma.getAttribute(parent);
		}
		
		sigma *= Math.exp(tau*PRNG.nextGaussian());
		Sigma.setAttribute(child, sigma);
		
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
		
		return child;
	}
	
	private static final class Sigma implements Attribute {
		
		public static final String ATTRIBUTE_NAME = "sigma";
		
		private Sigma() {
			super();
		}
		
		public static final boolean hasAttribute(Solution solution) {
			return solution.hasAttribute(ATTRIBUTE_NAME);
		}
		
		public static final void setAttribute(Solution solution, double value) {
			solution.setAttribute(ATTRIBUTE_NAME, value);
		}
		
		public static final double getAttribute(Solution solution) {
			return (Double)solution.getAttribute(ATTRIBUTE_NAME);
		}
		
	}

}
