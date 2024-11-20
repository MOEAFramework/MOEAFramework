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
package org.moeaframework.examples.regression;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.Program;
import org.moeaframework.util.tree.Add;
import org.moeaframework.util.tree.Cos;
import org.moeaframework.util.tree.Divide;
import org.moeaframework.util.tree.Environment;
import org.moeaframework.util.tree.Exp;
import org.moeaframework.util.tree.Get;
import org.moeaframework.util.tree.Log;
import org.moeaframework.util.tree.Multiply;
import org.moeaframework.util.tree.Rules;
import org.moeaframework.util.tree.Sin;
import org.moeaframework.util.tree.Subtract;

/**
 * The symbolic regression problem for genetic programming.
 */
public class ProgramSymbolicRegression extends AbstractSymbolicRegression {
	
	/**
	 * The rules for building expression trees for symbolic regression.
	 */
	private Rules rules;

	/**
	 * Constructs a new symbolic regression problem for approximating the given function.
	 * 
	 * @param function the actual function implementation
	 * @param lowerBound the lower bound for comparing the actual and approximate functions
	 * @param upperBound the upper bound for comparing the actual and approximate functions
	 * @param steps the number of comparisons made between the actual and approximate functions
	 */
	public ProgramSymbolicRegression(UnivariateFunction function, double lowerBound, double upperBound, int steps) {
		super(function, lowerBound, upperBound, steps);
		
		// setup the program rules
		rules = new Rules();
		rules.add(new Add());
		rules.add(new Multiply());
		rules.add(new Subtract());
		rules.add(new Divide());
		rules.add(new Sin());
		rules.add(new Cos());
		rules.add(new Exp());
		rules.add(new Log());
		rules.add(new Get(Number.class, symbol));
		rules.setReturnType(Number.class);
		rules.setMaxVariationDepth(10);
	}
	
	@Override
	public String getExpression(Solution solution) {
		return ((Program)solution.getVariable(0)).getBody().toString();
	}
	
	@Override
	public double[] eval(Solution solution) {
		Program program = (Program)solution.getVariable(0);
		double[] approximatedY = new double[steps];
		
		for (int i = 0; i < steps; i++) {
			Environment environment = new Environment();
			environment.set(symbol, x[i]);
			approximatedY[i] = ((Number)program.evaluate(environment)).doubleValue();
		}
		
		return approximatedY;
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(1, 1);
		solution.setVariable(0, new Program(rules));
		return solution;
	}

}
