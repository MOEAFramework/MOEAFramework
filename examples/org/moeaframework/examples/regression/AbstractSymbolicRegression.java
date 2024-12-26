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
package org.moeaframework.examples.regression;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.moeaframework.core.Solution;
import org.moeaframework.problem.AbstractProblem;

/**
 * The symbolic regression problem.  Given a function, the symbolic regression problem attempts to find an expression
 * for closely approximating the output of the function.
 * <p>
 * This abstract class is designed to allow either grammar-based or program-based solvers.
 */
public abstract class AbstractSymbolicRegression extends AbstractProblem {
	
	/**
	 * The actual function implementation.
	 */
	protected final UnivariateFunction function;
	
	/**
	 * The lower bound for comparing the actual and approximate functions.
	 */
	protected final double lowerBound;
	
	/**
	 * The upper bound for comparing the actual and approximate functions.
	 */
	protected final double upperBound;
	
	/**
	 * The number of comparisons made between the actual and approximate functions.
	 */
	protected final int steps;
	
	/**
	 * The name of the input variable.
	 */
	protected final String symbol;
	
	/**
	 * The cached x values.
	 */
	protected double[] x;
	
	/**
	 * The cached y values.
	 */
	protected double[] y;

	/**
	 * Constructs a new symbolic regression problem for approximating the given function.
	 * 
	 * @param function the actual function implementation
	 * @param lowerBound the lower bound for comparing the actual and approximate functions
	 * @param upperBound the upper bound for comparing the actual and approximate functions
	 * @param steps the number of comparisons made between the actual and approximate functions
	 */
	public AbstractSymbolicRegression(UnivariateFunction function, double lowerBound, double upperBound, int steps) {
		super(1, 1);
		this.function = function;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.steps = steps;
		this.symbol = "x";

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
	 * Returns the lower bound for comparing the actual and approximate functions.
	 * 
	 * @return the lower bound for comparing the actual and approximate functions
	 */
	public double getLowerBound() {
		return lowerBound;
	}

	/**
	 * Returns the upper bound for comparing the actual and approximate functions.
	 * 
	 * @return the upper bound for comparing the actual and approximate functions
	 */
	public double getUpperBound() {
		return upperBound;
	}

	/**
	 * Returns the number of comparisons made between the actual and approximate functions.
	 * 
	 * @return the number of comparisons made between the actual and approximate functions
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
	 * Returns the array of x-values, the function inputs, used when comparing the actual and approximated functions.
	 * 
	 * @return the array of x-values, the function inputs, used when comparing the actual and approximated functions
	 */
	public double[] getX() {
		return x;
	}
	
	/**
	 * Returns the array of y-values, the function outputs, resulting from evaluating the actual function using the
	 * x-values from {@link #getX()}.
	 * 
	 * @return the array of y-values, the function outputs, resulting from evaluating the actual function
	 */
	public double[] getY() {
		return y;
	}
	
	@Override
	public void evaluate(Solution solution) {
		double difference = 0.0;
		double[] approximatedY = eval(solution);

		for (int i = 0; i < steps; i++) {
			difference += Math.pow(Math.abs(y[i] - approximatedY[i]), 2.0);
		}

		difference = Math.sqrt(difference);

		// protect against NaN
		if (Double.isNaN(difference)) {
			difference = Double.POSITIVE_INFINITY;
		}

		solution.setObjectiveValue(0, difference);
	}
	
	/**
	 * Converts the grammar or program into a string for display.
	 * 
	 * @param solution the solution
	 * @return the expression
	 */
	public abstract String getExpression(Solution solution);
	
	/**
	 * Evaluates the grammar or program and returns the approximated y-values.
	 * 
	 * @param program the generated program
	 * @return the array of y-values, the function outputs, resulting from evaluating the grammar or program
	 */
	public abstract double[] eval(Solution solution);

}
