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

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.Grammar;
import org.moeaframework.util.grammar.ContextFreeGrammar;
import org.moeaframework.util.grammar.Parser;

/**
 * The problem of generating a function whose output is maximized. The context
 * free grammar for the space of valid functions is
 * <p>
 * 
 * <pre>
 * {@code
 * <prog> ::= <expr>
 * <expr> ::=  <expr> <op> <expr> | <var>
 * <op> ::= + | *\n" + "<var> ::= 0.5
 * }
 * </pre>
 */
public class MaxFunction extends AbstractProblem {

	/**
	 * The context free grammar defining space of valid functions.
	 */
	private final ContextFreeGrammar grammar;

	/**
	 * The script engine for evaluating the function.
	 */
	private final ScriptEngine engine;

	/**
	 * Constructs the problem of generating a function whose output is 
	 * maximized.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	public MaxFunction() throws IOException {
		super(1, 1);

		grammar = Parser.load(new StringReader("<prog> ::= <expr>\n"
				+ "<expr> ::=  <expr> <op> <expr> | <var>\n"
				+ "<op> ::= + | *\n" + "<var> ::= 0.5"));

		ScriptEngineManager sem = new ScriptEngineManager();
		engine = sem.getEngineByName("groovy");
	}

	@Override
	public void evaluate(Solution solution) {
		try {
			int[] codon = ((Grammar)solution.getVariable(0)).toArray();
			String script = grammar.build(codon);
			double value = 0;

			if (script != null) {
				value = ((Number)engine.eval(script)).doubleValue();
			}

			solution.setObjective(0, -value);
		} catch (ScriptException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(1, 1);
		solution.setVariable(0, new Grammar(5));
		return solution;
	}

}
