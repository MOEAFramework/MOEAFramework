/* Copyright 2009-2022 David Hadka
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

import java.util.List;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.operator.Mutation;
import org.moeaframework.core.variable.Program;
import org.moeaframework.util.tree.Node;
import org.moeaframework.util.tree.Rules;

/**
 * Mutates a program by randomly selecting nodes in the expression tree and
 * replacing the node with a new, compatible, randomly-selected node.
 * <p>
 * This operator is type-safe.
 */
public class PointMutation implements Mutation {
	
	/**
	 * The probability of mutating a node in the tree.
	 */
	private double probability;
	
	/**
	 * Constructs a new point mutation operator with the default settings.
	 */
	public PointMutation() {
		this(1.0);
	}
	
	/**
	 * Constructs a new point mutation operator.
	 * 
	 * @param probability the probability of mutating a node in the tree
	 */
	public PointMutation(double probability) {
		super();
		this.probability = probability;
	}
	
	@Override
	public String getName() {
		return "ptm";
	}

	/**
	 * Gets the probability of mutating a node in the tree.
	 * 
	 * @return the probability
	 */
	public double getProbability() {
		return probability;
	}

	/**
	 * Sets the probability of mutating a node in the tree.
	 * 
	 * @param probability the probability (0.0 - 1.0)
	 */
	public void setProbability(double probability) {
		this.probability = probability;
	}

	@Override
	public Solution mutate(Solution parent) {
		Solution result = parent.copy();
		
		for (int i = 0; i < result.getNumberOfVariables(); i++) {
			Variable variable = result.getVariable(i);
			
			if (variable instanceof Program) {
				Program program = (Program)variable;
				mutate(program.getArgument(0), program.getRules());
			}
		}
		
		return result;
	}
	
	/**
	 * Applies point mutation to the specified node.
	 * 
	 * @param node the node undergoing point mutation
	 * @param rules the rules defining the program syntax
	 */
	protected void mutate(Node node, Rules rules) {
		if (!node.isFixed() && (PRNG.nextDouble() <= probability)) {
			// mutate this node
			List<Node> mutations = rules.listAvailableMutations(node);
			
			if (!mutations.isEmpty()) {
				Node mutation = PRNG.nextItem(mutations).copyNode();
				Node parent = node.getParent();

				for (int i = 0; i < parent.getNumberOfArguments(); i++) {
					if (parent.getArgument(i) == node) {
						parent.setArgument(i, mutation);
						break;
					}
				}
				
				for (int i = 0; i < node.getNumberOfArguments(); i++) {
					mutation.setArgument(i, node.getArgument(i));
				}
				
				node = mutation;
			}
		}
		
		// recursively mutate arguments
		for (int i = 0; i < node.getNumberOfArguments(); i++) {
			mutate(node.getArgument(i), rules);
		}
	}

}
