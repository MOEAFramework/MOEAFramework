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
package org.moeaframework.snippet;

import java.io.IOException;
import java.util.BitSet;

import org.junit.Test;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.Grammar;
import org.moeaframework.core.variable.Program;
import org.moeaframework.util.grammar.ContextFreeGrammar;
import org.moeaframework.util.tree.Add;
import org.moeaframework.util.tree.Constant;
import org.moeaframework.util.tree.Divide;
import org.moeaframework.util.tree.Environment;
import org.moeaframework.util.tree.Get;
import org.moeaframework.util.tree.Multiply;
import org.moeaframework.util.tree.Rules;
import org.moeaframework.util.tree.Subtract;

public class VariableSnippet {

	@Test
	public void realSnippet() {
		Solution solution = new Solution(1, 1);
		int i = 0;
		double lowerBound = 0.0;
		double upperBound = 1.0;
		
		// begin-example: real-variable
		// Creating a real-valued variable:
		solution.setVariable(i, EncodingUtils.newReal(lowerBound, upperBound));

		// Reading and writing a single variable:
		double value = EncodingUtils.getReal(solution.getVariable(i));
		EncodingUtils.setReal(solution.getVariable(i), value);

		// Reading and writing all variables (when all variables in the solution are real-valued):
		double[] values = EncodingUtils.getReal(solution);
		EncodingUtils.setReal(solution, values);
		// end-example: real-variable
	}
	
	@Test
	public void binarySnippet() {
		Solution solution = new Solution(1, 1);
		int i = 0;
		int length = 10;
		
		// begin-example: binary-variable
		// Creating a binary variable:
		solution.setVariable(i, EncodingUtils.newBinary(length));

		// Reading the values as an array or BitSet:
		boolean[] bits = EncodingUtils.getBinary(solution.getVariable(i));
		BitSet bitSet = EncodingUtils.getBitSet(solution.getVariable(i));

		// Updating the bits:
		EncodingUtils.setBinary(solution.getVariable(i), bits);
		EncodingUtils.setBitSet(solution.getVariable(i), bitSet);
		// end-example: binary-variable
	}
	
	@Test
	public void integerSnippet() {
		Solution solution = new Solution(1, 1);
		int i = 0;
		int lowerBound = 0;
		int upperBound = 10;
		
		// begin-example: integer-variable
		// Creating an integer variable:
		solution.setVariable(i, EncodingUtils.newInt(lowerBound, upperBound));
		solution.setVariable(i, EncodingUtils.newBinaryInt(lowerBound, upperBound));

		// Reading and writing a single variable:
		int value = EncodingUtils.getInt(solution.getVariable(i));
		EncodingUtils.setInt(solution.getVariable(i), value);

		// Reading and writing all variables (when all variables in the solution are integers):
		int[] values = EncodingUtils.getInt(solution);
		EncodingUtils.setInt(solution, values);
		// end-example: integer-variable
	}
	
	@Test
	public void permutationSnippet() {
		Solution solution = new Solution(1, 1);
		int i = 0;
		int length = 10;
		
		// begin-example: permutation-variable
		// Creating a permutation:
		solution.setVariable(i, EncodingUtils.newPermutation(length));

		// Reading and writing a permutation:
		int[] permutation = EncodingUtils.getPermutation(solution.getVariable(i));
		EncodingUtils.setPermutation(solution.getVariable(i), permutation);
		// end-example: permutation-variable
	}
	
	@Test
	public void subsetSnippet() {
		Solution solution = new Solution(1, 1);
		int i = 0;
		int fixedSize = 10;
		int minSize = 5;
		int maxSize = 10;
		int numberOfElements = 20;
		
		// begin-example: subset-variable
		// Creating a fixed and variable-length subset:
		solution.setVariable(i, EncodingUtils.newSubset(fixedSize, numberOfElements));
		solution.setVariable(i, EncodingUtils.newSubset(minSize, maxSize, numberOfElements));

		// Reading and writing the sets
		int[] subset = EncodingUtils.getSubset(solution.getVariable(i));
		EncodingUtils.setSubset(solution.getVariable(i), subset);
		// end-example: subset-variable
	}
	
	@Test
	public void grammarSnippet() throws IOException {
		Solution solution = new Solution(1, 1);
		int i = 0;
		
		// begin-example: grammar-definition
		ContextFreeGrammar cfg = ContextFreeGrammar.load("""
				<expr> ::= '(' <expr> <op> <expr> ')' | <val>
				<val> ::= x | y
				<op> ::= + | - | * | /
				""");
		// end-example: grammar-definition
		
		// begin-example: grammar-variable
		// Creating a grammar with a codon length of 10
		solution.setVariable(i, new Grammar(10));
		
		// Build an expression from the context-free grammar
		Grammar grammar = (Grammar)solution.getVariable(i);
		String expression = grammar.build(cfg);
		
		solution.setObjectiveValue(0, evaluate(expression));
		// end-example: grammar-variable
	}
	
	private double evaluate(String str) {
		return 0.0;
	}
	
	@Test
	public void programSnippet() {
		Solution solution = new Solution(1, 1);
		int i = 0;
		
		// begin-example: program-definition
		// Creating a program
		Rules rules = new Rules();
		rules.add(new Add());
		rules.add(new Multiply());
		rules.add(new Subtract());
		rules.add(new Divide());
		rules.add(new Get(Number.class, "x"));
		rules.add(new Get(Number.class, "y"));
		rules.add(new Constant(1.0));
		rules.setReturnType(Number.class);
		rules.setMaxVariationDepth(10);
		// end-example: program-definition
		
		// begin-example: program-variable
		// Define the variable
		solution.setVariable(i, new Program(rules));
		// end-example: program-variable
		
		// Randomize the program
		solution.getVariable(i).randomize();
		
		// begin-example: program-evaluate
		// Reading and evaluating the program
		Environment environment = new Environment();
		environment.set("x", 5.0);
		environment.set("y", 10.0);
		
		Program program = (Program)solution.getVariable(i);
		Number result = (Number)program.evaluate(environment);
		
		solution.setObjectiveValue(0, result.doubleValue());
		// end-example: program-evaluate
	}
	
}
