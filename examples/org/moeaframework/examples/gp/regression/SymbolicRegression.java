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

/**
 * The symbolic regression problem for genetic programming.  Given a function,
 * the symbolic regression problem attempts to find an expression for closely
 * approximating the output of the function.
 */
public class SymbolicRegression extends AbstractProblem {
	
	/**
	 * The actual function implementation.
	 */
	private final UnivariateFunction function;
	
	/**
	 * The lower bound for comparing the actual and approximate functions.
	 */
	private final double lowerBound;
	
	/**
	 * The upper bound for comparing the actual and approximate functions.
	 */
	private final double upperBound;
	
	/**
	 * The number of comparisons made between the actual and approximate
	 * functions.
	 */
	private final int steps;
	
	/**
	 * The name of the input variable for the expression tree.
	 */
	private String symbol;
	
	/**
	 * The rules for building expression trees for symbolic regression.
	 */
	private Rules rules;
	
	/**
	 * The cached x values.
	 */
	private double[] x;
	
	/**
	 * The cached actual y values.
	 */
	private double[] y;

	/**
	 * Constructs a new symbolic regression problem for approximating the
	 * given function.
	 * 
	 * @param function the actual function implementation
	 * @param lowerBound the lower bound for comparing the actual and
	 *        approximate functions
	 * @param upperBound the upper bound for comparing the actual and
	 *        approximate functions
	 * @param steps the number of comparisons made between the actual and
	 *        approximate functions
	 */
	public SymbolicRegression(UnivariateFunction function,
			double lowerBound, double upperBound, int steps) {
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

	/**
	 * Returns the actual function implementation.
	 * 
	 * @return the actual function implementation
	 */
	public UnivariateFunction getFunction() {
		return function;
	}

	/**
	 * Returns the lower bound for comparing the actual and approximate
	 * functions.
	 * 
	 * @return the lower bound for comparing the actual and approximate
	 *         functions
	 */
	public double getLowerBound() {
		return lowerBound;
	}

	/**
	 * Returns the upper bound for comparing the actual and approximate
	 * functions.
	 * 
	 * @return the upper bound for comparing the actual and approximate
	 *         functions
	 */
	public double getUpperBound() {
		return upperBound;
	}

	/**
	 * Returns the number of comparisons made between the actual and
	 * approximate functions.
	 * 
	 * @return the number of comparisons made between the actual and
	 *         approximate functions
	 */
	public int getSteps() {
		return steps;
	}

	/**
	 * Returns the name of the input variable to the specified function.
	 * 
	 * @return the name of the input variable to the specified function
	 */
	public String getSymbol() {
		return symbol;
	}

	/**
	 * Sets the name of the input variable to the approximated function.  The
	 * default is {@code "x"}.  When changing the symbol, be sure to add the
	 * rule {@code new Get(Number.class, symbol)} so the input variable value
	 * can be accessed in the approximated function.
	 * 
	 * @param symbol the name of the input variable to the approximated
	 *        function
	 */
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	/**
	 * Returns the rules used to construct the approximated function.
	 * 
	 * @return the rules used to construct the approximated function
	 */
	public Rules getRules() {
		return rules;
	}

	/**
	 * Sets the rules used to construct the approximated function.
	 * 
	 * @param rules the rules used to construct the approximated function
	 */
	public void setRules(Rules rules) {
		this.rules = rules;
	}
	
	/**
	 * Returns the array of x-values, the function inputs, used when comparing
	 * the actual and approximated functions.
	 * 
	 * @return the array of x-values, the function inputs, used when comparing
	 *         the actual and approximated functions
	 */
	public double[] getX() {
		return x;
	}
	
	/**
	 * Returns the array of y-values, the function outputs, resulting from
	 * evaluating the actual function using the x-values from {@link #getX()}.
	 * 
	 * @return the array of y-values, the function outputs, resulting from
	 *         evaluating the actual function
	 */
	public double[] getActualY() {
		return y;
	}
	
	/**
	 * Returns the array of y-values, the function outputs, resulting from
	 * evaluating the approximated function using the x-values from
	 * {@link #getX()}.
	 * 
	 * @param solution the solution whose approximated function is being
	 *        evaluated
	 * @return the array of y-values, the function outputs, resulting from
	 *         evaluating the approximated function
	 */
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
