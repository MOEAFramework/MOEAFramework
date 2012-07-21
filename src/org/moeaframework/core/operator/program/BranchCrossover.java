package org.moeaframework.core.operator.program;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.Variation;
import org.moeaframework.core.variable.Program;
import org.moeaframework.util.tree.Node;
import org.moeaframework.util.tree.Rules;

public class BranchCrossover implements Variation {
	
	private double probability;
	
	public BranchCrossover(double probability) {
		super();
		this.probability = probability;
	}

	@Override
	public int getArity() {
		return 2;
	}

	@Override
	public Solution[] evolve(Solution[] parents) {
		Solution result1 = parents[0].copy();
		Solution result2 = parents[1].copy();
		
		for (int i = 0; i < result1.getNumberOfVariables(); i++) {
			Variable variable1 = result1.getVariable(i);
			Variable variable2 = result2.getVariable(i);
			
			if ((PRNG.nextDouble() <= probability) &&
					(variable1 instanceof Program) &&
					(variable2 instanceof Program)) {
				Program program1 = (Program)variable1;
				Program program2 = (Program)variable2;
				
				crossover(program1, program2, program1.getRules());
			}
		}
		
		return new Solution[] { result1 };
	}
	
	protected void crossover(Program program1, Program program2, Rules rules) {
		Node node = null;
		Node replacement = null;
		
		// pick the node to be replaced (destination) from the first parent
		if (PRNG.nextDouble() <= rules.getFunctionCrossoverProbability()) {
			int size = program1.getArgument(0).getNumberOfFunctions();
			node = program1.getArgument(0).getFunctionAt(PRNG.nextInt(size));
		} else {
			int size = program1.getArgument(0).getNumberOfTerminals();
			node = program1.getArgument(0).getTerminalAt(PRNG.nextInt(size));
		}
		
		// pick the replacement (source) from the second parent
		if (PRNG.nextDouble() <= rules.getFunctionCrossoverProbability()) {
			int size = program1.getArgument(0).getNumberOfFunctions(
					node.getReturnType());
			
			if (size == 0) {
				// no valid crossover, no change is made
				return;
			}

			replacement = program1.getArgument(0).getFunctionAt(
					node.getReturnType(), PRNG.nextInt(size));
		} else {
			int size = program1.getArgument(0).getNumberOfTerminals(
					node.getReturnType());
			
			if (size == 0) {
				// no valid crossover, no change is made
				return;
			}

			replacement = program1.getArgument(0).getTerminalAt(
					node.getReturnType(), PRNG.nextInt(size));
		}
		
		// if this replacement violates the depth limit, no change is made
		if (node.getDepth() + replacement.getMaximumHeight() > 
				rules.getMaxCrossoverDepth()) {
			return;
		}
		
		// replace the node
		Node parent = node.getParent();
		
		for (int i = 0; i < parent.getNumberOfArguments(); i++) {
			Node argument = parent.getArgument(i);
			
			if (argument == node) {
				parent.setArgument(i, replacement);
			}
		}
	}

}
