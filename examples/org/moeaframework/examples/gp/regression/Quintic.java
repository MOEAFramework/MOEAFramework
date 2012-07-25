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

import java.util.Properties;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.spi.AlgorithmFactory;

/**
 * The Quintic function as introduced in [1].  The function is
 * {@code f(x) = x^5 - 2x^3 + x}.
 * 
 * References:
 * <ol>
 *   <li>Koza, J.R.  "Genetic Programming II: Automatic Discovery of Reusable
 *       Programs."  MIT Press, Cambridge, MA, 1994.
 * </ol>
 */
public class Quintic implements UnivariateRealFunction {
	
	public static void main(String[] args) throws FunctionEvaluationException {
		// setup the problem and GUI
		SymbolicRegression problem = new SymbolicRegression(new Quintic(),
				-1.0, 1.0, 100);
		SymbolicRegressionGUI gui = new SymbolicRegressionGUI(problem);
		
		// setup and construct the GP solver
		int generation = 0;
		int maxGenerations = 1000;
		Algorithm algorithm = null;
		Properties properties = new Properties();
		properties.setProperty("populationSize", "500");
		
		try {
			algorithm = AlgorithmFactory.getInstance().getAlgorithm(
					"NSGAII", properties, problem);
			
			// run the GP solver
			while ((generation < maxGenerations) && !gui.isCanceled()) {
				algorithm.step();
				generation++;
				
				gui.update(algorithm.getResult().get(0), generation,
						maxGenerations);
			}
		} finally {
			if (algorithm != null) {
				algorithm.terminate();
			}
		}
	}

	@Override
	public double value(double x) throws FunctionEvaluationException {
		return x*x*x*x*x* - 2.0*x*x*x + x;
	}
	
}
