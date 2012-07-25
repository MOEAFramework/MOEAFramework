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
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.Program;
import org.moeaframework.problem.AbstractProblem;
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

public class SymbolicRegression extends AbstractProblem {
	
	private final UnivariateRealFunction function;
	
	private final double lowerBound;
	
	private final double upperBound;
	
	private final int steps;
	
	private String symbol;
	
	private Rules rules;
	
	private double[] x;
	
	private double[] y;

	public SymbolicRegression(UnivariateRealFunction function,
			double lowerBound, double upperBound, int steps)
					throws FunctionEvaluationException {
		super(1, 1);
		this.function = function;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.steps = steps;
		
		// setup the default rules
		symbol = "x";
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

		// cache the function's x and y values
		x = new double[steps];
		y = new double[steps];
		
		for (int i = 0; i < steps; i++) {
			x[i] = lowerBound + (i / (steps-1.0)) * (upperBound - lowerBound);
			y[i] = function.value(x[i]);
		}
	}

	public UnivariateRealFunction getFunction() {
		return function;
	}

	public double getLowerBound() {
		return lowerBound;
	}

	public double getUpperBound() {
		return upperBound;
	}

	public int getSteps() {
		return steps;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public Rules getRules() {
		return rules;
	}

	public void setRules(Rules rules) {
		this.rules = rules;
	}
	
	public double[] getX() {
		return x;
	}
	
	public double[] getActualY() {
		return y;
	}
	
	public double[] getApproximatedY(Solution solution) {
		Program program = (Program)solution.getVariable(0);
		double[] approximatedY = new double[steps];
		
		for (int i = 0; i < steps; i++) {
			Environment environment = new Environment();
			environment.set(symbol, x[i]);
			approximatedY[i] = ((Number)program.evaluate(environment))
					.doubleValue();
		}
		
		return approximatedY;
	}

	@Override
	public void evaluate(Solution solution) {
		double difference = 0.0;
		double[] approximatedY = getApproximatedY(solution);
		
		for (int i = 0; i < steps; i++) {
			difference += Math.pow(Math.abs(y[i] - approximatedY[i]), 2.0);
		}
		
		difference = Math.sqrt(difference);
		
		// protect against NaN
		if (Double.isNaN(difference)) {
			difference = Double.POSITIVE_INFINITY;
		}

		solution.setObjective(0, difference);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(1, 1);
		solution.setVariable(0, new Program(rules));
		return solution;
	}

}
