/* Copyright 2009-2011 David Hadka
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
package org.moeaframework.problem;

import java.io.IOException;
import java.io.StringReader;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.Grammar;
import org.moeaframework.util.Timing;
import org.moeaframework.util.grammar.ContextFreeGrammar;
import org.moeaframework.util.grammar.Parser;

/**
 * A problem for finding the closest-matching univariate function to a given
 * univariate function. The variable {@code x} is the input to both functions.
 * The following grammar is used to find the matching function:
 * <p>
 * 
 * <pre>
 * {@code
 * <expr> ::= <func> | (<expr> <op> <expr>) | <value>
 * <func> ::= <func-name> ( <expr> )
 * <func-name> ::= 'Math.sin' | 'Math.cos' | 'Math.exp' | 'Math.sqrt'
 * <op> ::= + | * | - | **
 * <value> ::= '1.0' | '2.0' | '0.5' | x
 * }
 * </pre>
 */
public class FunctionMatcher extends AbstractProblem {

	/**
	 * The context free grammar defining the space of matching functions.
	 */
	private final ContextFreeGrammar grammar;

	/**
	 * The string representation of the target function.
	 */
	private final String targetFunction;

	/**
	 * The lower bound of the {@code x} variable.
	 */
	private final double lowerBound;

	/**
	 * The upper bound of the {@code x} variable.
	 */
	private final double upperBound;

	/**
	 * The number of samples taken when comparing two functions. More samples
	 * increases accuracy but decreases performance.
	 */
	private final int numberOfSamples;

	/**
	 * The script engine for evaluating the functions.
	 */
	private final ScriptEngine engine;

	/**
	 * Constructs a function matcher problem.
	 * 
	 * @param targetFunction the string representation of the target function
	 * @param lowerBound the lower bound of the {@code x} variable
	 * @param upperBound the upper bound of the {@code x} variable
	 * @param numberOfSamples the number of samples taken when comparing two
	 *        functions
	 * @throws IOException if an I/O error occurred
	 */
	public FunctionMatcher(String targetFunction, double lowerBound,
			double upperBound, int numberOfSamples) throws IOException {
		super(1, 1);
		this.targetFunction = targetFunction;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.numberOfSamples = numberOfSamples;

		grammar = Parser
				.load(new StringReader(
						"<expr> ::= <func> | (<expr> <op> <expr>) | <value>\n"
								+ "<func> ::= <func-name> ( <expr> )\n"
								+ "<func-name> ::= 'Math.sin' | 'Math.cos' | 'Math.exp' | 'Math.sqrt'\n"
								+ "<op> ::= + | * | - | **\n"
								+ "<value> ::= '1.0' | '2.0' | '0.5' | x"));

		ScriptEngineManager sem = new ScriptEngineManager();
		engine = sem.getEngineByName("groovy");
	}

	/**
	 * Returns the context free grammar defining the space of matching
	 * functions.
	 * 
	 * @return the context free grammar defining the space of matching functions
	 */
	public ContextFreeGrammar getGrammar() {
		return grammar;
	}

	/**
	 * Returns the string representation of the target function.
	 * 
	 * @return the string representation of the target function
	 */
	public String getTargetFunction() {
		return targetFunction;
	}

	/**
	 * Returns the script engine for evaluating the functions.
	 * 
	 * @return the script engine for evaluating the functions
	 */
	public ScriptEngine getEngine() {
		return engine;
	}

	/**
	 * Returns the lower bound of the {@code x} variable.
	 * 
	 * @return the lower bound of the {@code x} variable
	 */
	public double getLowerBound() {
		return lowerBound;
	}

	/**
	 * Returns the upper bound of the {@code x} variable.
	 * 
	 * @return the upper bound of the {@code x} variable
	 */
	public double getUpperBound() {
		return upperBound;
	}

	/**
	 * Returns the number of samples taken when comparing two functions.
	 * 
	 * @return the number of samples taken when comparing two functions
	 */
	public int getNumberOfSamples() {
		return numberOfSamples;
	}

	@Override
	public void evaluate(Solution solution) {
		try {
			int[] codon = ((Grammar)solution.getVariable(0)).toArray();
			String grammarFunction = grammar.build(codon);
			double diff = 0.0;

			if (grammarFunction == null) {
				diff = Double.POSITIVE_INFINITY;
			} else {
				Timing.startTimer("eval");
				for (double i = lowerBound; i <= upperBound; i += 
						(upperBound - lowerBound) / numberOfSamples) {
					Bindings b = new SimpleBindings();
					b.put("x", i);

					double v1 = ((Number)engine.eval(targetFunction, b))
							.doubleValue();
					double v2 = ((Number)engine.eval(grammarFunction, b))
							.doubleValue();

					diff += Math.pow(v1 - v2, 2.0);
				}
				Timing.stopTimer("eval");
			}

			if (Double.isNaN(diff)) {
				diff = Double.POSITIVE_INFINITY;
			}

			solution.setObjective(0, diff);
		} catch (ScriptException e) {
			throw new ProblemException(this, e);
		}
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(1, 1);
		solution.setVariable(0, new Grammar(10));
		return solution;
	}

}
