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

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.Grammar;
import org.moeaframework.util.grammar.ContextFreeGrammar;

/**
 * The symbolic regression problem for grammatical evolution.
 */
public class GrammarSymbolicRegression extends AbstractSymbolicRegression {
	
	/**
	 * The length of the codon.
	 */
	private final int codonLength;
	
	/**
	 * The grammar.
	 */
	private final ContextFreeGrammar grammar;
	
	/**
	 * The scripting engine used to evaluate the expressions.
	 */
	private final ScriptEngine engine;

	/**
	 * Constructs a new symbolic regression problem for approximating the given function.
	 * 
	 * @param function the actual function implementation
	 * @param lowerBound the lower bound for comparing the actual and approximate functions
	 * @param upperBound the upper bound for comparing the actual and approximate functions
	 * @param steps the number of comparisons made between the actual and approximate functions
	 */
	public GrammarSymbolicRegression(UnivariateFunction function, double lowerBound, double upperBound, int steps) {
		super(function, lowerBound, upperBound, steps);
		
		// setup the grammar and encoding
		codonLength = 10;
		
		grammar = ContextFreeGrammar.load("""
				<expr> ::= <func> | (<expr> <op> <expr>) | <value>
				<func> ::= <func-name> ( <expr> )
				<func-name> ::= Math.sin | Math.cos | Math.exp | Math.log
				<op> ::= + | * | - | /
				<value> ::= x
				""");
		
		// construct the scripting engine
		ScriptEngineManager sem = new ScriptEngineManager();
		engine = sem.getEngineByExtension("js");
		
		if (engine == null) {
			throw new FrameworkException("Javascript scripting engine not found, please install Nashorn and retry");
		}
	}

	@Override
	public double[] eval(Solution solution) {
		try {
			double[] approximatedY = new double[steps];
			
			for (int i = 0; i < steps; i++) {
				Bindings bindings = new SimpleBindings();
				bindings.put(symbol, x[i]);
	
				String expression = getExpression(solution);
				
				// grammars can return null, indicating no valid grammar was produced
				if (expression != null) {
					approximatedY[i] = ((Number)engine.eval(expression, bindings)).doubleValue();
				}
			}
			
			return approximatedY;
		} catch (ScriptException e) {
			throw new FrameworkException(e);
		}
	}

	@Override
	public String getExpression(Solution solution) {
		int[] codon = ((Grammar)solution.getVariable(0)).toArray();
		return grammar.build(codon);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(1, 1);
		solution.setVariable(0, new Grammar(codonLength));
		return solution;
	}

}
