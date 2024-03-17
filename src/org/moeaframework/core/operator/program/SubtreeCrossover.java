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
package org.moeaframework.core.operator.program;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.configuration.Prefix;
import org.moeaframework.core.operator.TypeSafeCrossover;
import org.moeaframework.core.variable.Program;
import org.moeaframework.util.tree.Node;
import org.moeaframework.util.tree.Rules;

/**
 * Exchanges a randomly-selected subtree from one program with a compatible, randomly-selected subtree from another
 * program.
 * <p>
 * This operator is type-safe.
 */
@Prefix("stx")
public class SubtreeCrossover extends TypeSafeCrossover<Program> {
	
	/**
	 * Constructs a new subtree crossover instance with a 90% chance of being applied to each solution.
	 */
	public SubtreeCrossover() {
		this(0.9);
	}
	
	/**
	 * Constructs a new subtree crossover instance.
	 * 
	 * @param probability the probability that subtree crossover is applied to a program
	 */
	public SubtreeCrossover(double probability) {
		super(Program.class, probability);
	}
	
	@Override
	public String getName() {
		return "stx";
	}
	
	@Override
	public Solution[] evolve(Solution[] parents) {
		// only return the first offspring
		return new Solution[] { super.evolve(parents)[0] };
	}

	/**
	 * Applies subtree crossover to the programs.
	 * 
	 * @param program1 the first program, which is the parent program
	 * @param program2 the second program, which provides the replacement subtree
	 */
	public void evolve(Program program1, Program program2) {
		Node node = null;
		Node replacement = null;
		Rules rules = program1.getRules();
		
		// pick the node to be replaced (destination) from the first parent
		if (PRNG.nextDouble() <= rules.getFunctionCrossoverProbability()) {
			int size = program1.getArgument(0).getNumberOfFunctions();
			
			if (size == 0) {
				// no valid crossover, no change is made
				return;
			}
			
			node = program1.getArgument(0).getFunctionAt(PRNG.nextInt(size));
		} else {
			int size = program1.getArgument(0).getNumberOfTerminals();
			
			if (size == 0) {
				// no valid crossover, no change is made
				return;
			}
			
			node = program1.getArgument(0).getTerminalAt(PRNG.nextInt(size));
		}
		
		// pick the replacement (source) from the second parent
		if (PRNG.nextDouble() <= rules.getFunctionCrossoverProbability()) {
			int size = program2.getArgument(0).getNumberOfFunctions(node.getReturnType());
			
			if (size == 0) {
				// no valid crossover, no change is made
				return;
			}

			replacement = program2.getArgument(0).getFunctionAt(node.getReturnType(), PRNG.nextInt(size));
		} else {
			int size = program2.getArgument(0).getNumberOfTerminals(node.getReturnType());
			
			if (size == 0) {
				// no valid crossover, no change is made
				return;
			}

			replacement = program2.getArgument(0).getTerminalAt(node.getReturnType(), PRNG.nextInt(size));
		}
		
		// if either node is fixed, no change is made
		if (node.isFixed() || replacement.isFixed()) {
			return;
		}
		
		// if this replacement violates the depth limit, no change is made
		if (node.getDepth() + replacement.getMaximumHeight() > rules.getMaxVariationDepth()) {
			return;
		}
		
		// replace the node
		Node parent = node.getParent();
		
		for (int i = 0; i < parent.getNumberOfArguments(); i++) {
			if (parent.getArgument(i) == node) {
				parent.setArgument(i, replacement);
				break;
			}
		}
	}

}
