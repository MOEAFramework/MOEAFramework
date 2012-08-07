/* Copyright 2009-2012 David Hadka
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

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;

/**
 * The Quartic function as introduced by Koza [1].  The function is
 * {@code f(x) = x^4 + x^3 + x^2 + x}.
 * 
 * References:
 * <ol>
 *   <li>Koza, J.R.  "Genetic Programming: On the Programming of Computers by
 *       Means of Natural Selection."  MIT Press, Cambridge, MA, USA, 1992.
 * </ol>
 */
public class QuarticExample implements UnivariateRealFunction {
	
	/**
	 * Runs the Quartic demo problem.
	 * 
	 * @param args the command line arguments
	 * @throws FunctionEvaluationException if an error occurred while
	 *         evaluating the function (should not occur in practice)
	 */
	public static void main(String[] args) throws FunctionEvaluationException {
		SymbolicRegressionGUI.runDemo(new SymbolicRegression(
				new QuarticExample(), -1.0, 1.0, 100));
	}

	@Override
	public double value(double x) throws FunctionEvaluationException {
		return x*x*x*x + x*x*x + x*x + x;
	}
	
}
