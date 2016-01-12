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
package org.moeaframework.examples.gp.regression;

import org.apache.commons.math3.analysis.UnivariateFunction;

/**
 * The Quintic function as introduced by Koza [1].  The function is
 * {@code f(x) = x^5 - 2x^3 + x}.
 * 
 * References:
 * <ol>
 *   <li>Koza, J.R.  "Genetic Programming II: Automatic Discovery of Reusable
 *       Programs."  MIT Press, Cambridge, MA, 1994.
 * </ol>
 */
public class QuinticExample implements UnivariateFunction {
	
	/**
	 * Runs the Quintic demo problem.
	 * 
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		SymbolicRegressionGUI.runDemo(new SymbolicRegression(
				new QuinticExample(), -1.0, 1.0, 100));
	}

	@Override
	public double value(double x) {
		return x*x*x*x*x* - 2.0*x*x*x + x;
	}
	
}
