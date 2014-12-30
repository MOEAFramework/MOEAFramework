/* Copyright 2009-2015 David Hadka
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
package org.moeaframework.core.operator;

import org.moeaframework.core.Initialization;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.Grammar;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.core.variable.Program;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.util.tree.Node;
import org.moeaframework.util.tree.Rules;

/**
 * Initializes all built-in decision variables randomly. The
 * {@link #initialize(Variable)} method can be extended to provide support for
 * other types.
 */
public class RandomInitialization implements Initialization {

	/**
	 * The problem.
	 */
	private final Problem problem;

	/**
	 * The initial population size.
	 */
	private final int populationSize;

	/**
	 * Constructs a random initialization operator.
	 * 
	 * @param problem the problem
	 * @param populationSize the initial population size
	 */
	public RandomInitialization(Problem problem, int populationSize) {
		super();
		this.problem = problem;
		this.populationSize = populationSize;
	}

	@Override
	public Solution[] initialize() {
		Solution[] initialPopulation = new Solution[populationSize];

		for (int i = 0; i < populationSize; i++) {
			Solution solution = problem.newSolution();

			for (int j = 0; j < solution.getNumberOfVariables(); j++) {
				Variable variable = solution.getVariable(j);
				initialize(variable);
			}

			initialPopulation[i] = solution;
		}

		return initialPopulation;
	}

	/**
	 * Initializes the specified decision variable randomly. This method
	 * supports all built-in types, and can be extended to support custom types.
	 * 
	 * @param variable the variable to be initialized
	 */
	protected void initialize(Variable variable) {
		if (variable instanceof RealVariable) {
			RealVariable real = (RealVariable)variable;
			real.setValue(PRNG.nextDouble(real.getLowerBound(), real
					.getUpperBound()));
		} else if (variable instanceof BinaryVariable) {
			BinaryVariable binary = (BinaryVariable)variable;

			for (int i = 0; i < binary.getNumberOfBits(); i++) {
				binary.set(i, PRNG.nextBoolean());
			}
		} else if (variable instanceof Permutation) {
			Permutation permutation = (Permutation)variable;

			int[] array = permutation.toArray();
			PRNG.shuffle(array);
			permutation.fromArray(array);
		} else if (variable instanceof Grammar) {
			Grammar grammar = (Grammar)variable;

			int[] array = grammar.toArray();
			for (int i = 0; i < array.length; i++) {
				array[i] = PRNG.nextInt(grammar.getMaximumValue());
			}
			grammar.fromArray(array);
		} else if (variable instanceof Program) {
			// ramped half-and-half initialization
			Program program = (Program)variable;
			Rules rules = program.getRules();
			int depth = PRNG.nextInt(2, rules.getMaxInitializationDepth());
			boolean isFull = PRNG.nextBoolean();
			Node root = null;
			
			if (isFull) {
				if (rules.getScaffolding() == null) {
					root = rules.buildTreeFull(rules.getReturnType(), depth);
				} else {
					root = rules.buildTreeFull(rules.getScaffolding(), depth);
				}
			} else {
				if (rules.getScaffolding() == null) {
					root = rules.buildTreeGrow(rules.getReturnType(), depth);
				} else {
					root = rules.buildTreeGrow(rules.getScaffolding(), depth);
				}
			}
			
			program.setArgument(0, root);
		} else {
			System.err.println("can not initialize unknown type");
		}
	}

}
