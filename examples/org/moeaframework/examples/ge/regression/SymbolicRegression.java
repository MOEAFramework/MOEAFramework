/* Copyright 2009-2019 David Hadka
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
package org.moeaframework.examples.ge.regression;

import java.io.IOException;
import java.io.InputStreamReader;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.Grammar;
import org.moeaframework.problem.AbstractProblem;
import org.moeaframework.util.grammar.ContextFreeGrammar;
import org.moeaframework.util.grammar.Parser;

/**
 * The symbolic regression problem for grammatical evolution.  Given a function,
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
	 * The length of the codon.
	 */
	private int codonLength;
	
	/**
	 * The grammar.
	 */
	private ContextFreeGrammar grammar;
	
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
			double lowerBound, double upperBound, int steps) throws IOException {
		super(1, 1);
		this.function = function;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.steps = steps;
		
		// setup the grammar and encoding.
		symbol = "x";
		codonLength = 10;
		grammar = Parser.load(new InputStreamReader(
				SymbolicRegression.class.getResourceAsStream("grammar.bnf")));
		
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
	 * @param program the generated program
	 * @return the array of y-values, the function outputs, resulting from
	 *         evaluating the approximated function
	 */
	public double[] getApproximatedY(String program) {
		try {
			ScriptEngineManager sem = new ScriptEngineManager();
			ScriptEngine engine = sem.getEngineByExtension("js");
			
			double[] approximatedY = new double[steps];
			
			for (int i = 0; i < steps; i++) {
				Bindings bindings = new SimpleBindings();
				bindings.put(symbol, x[i]);
	
				approximatedY[i] = ((Number)engine.eval(program, bindings))
						.doubleValue();
			}
			
			return approximatedY;
		} catch (ScriptException e) {
			throw new FrameworkException(e);
		}
	}
	
	/**
	 * Converts the solution into a program.
	 * 
	 * @param solution the solution
	 * @return the program generated by the solution
	 */
	public String getProgram(Solution solution) {
		int[] codon = ((Grammar)solution.getVariable(0)).toArray();
		return grammar.build(codon);
	}

	@Override
	public void evaluate(Solution solution) {
		// derive the program using the codon
		String program = getProgram(solution);

		if (program == null) {
			// the codon did not produce a valid grammar; penalize the solution
			solution.setObjective(0, Double.POSITIVE_INFINITY);
		} else {
			double difference = 0.0;
			double[] approximatedY = getApproximatedY(program);

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
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(1, 1);
		solution.setVariable(0, new Grammar(codonLength));
		return solution;
	}

}
