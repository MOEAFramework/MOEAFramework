package org.moeaframework.core.operator.program;

import java.util.List;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.Variation;
import org.moeaframework.core.variable.Program;
import org.moeaframework.util.tree.Node;
import org.moeaframework.util.tree.Rules;

public class PointMutation implements Variation {
	
	private double probability;
	
	public PointMutation(double probability) {
		super();
		this.probability = probability;
	}

	@Override
	public int getArity() {
		return 1;
	}

	@Override
	public Solution[] evolve(Solution[] parents) {
		Solution result = parents[0].copy();
		
		for (int i = 0; i < result.getNumberOfVariables(); i++) {
			Variable variable = result.getVariable(i);
			
			if (variable instanceof Program) {
				Program program = (Program)variable;
				mutate(program, program.getRules());
			}
		}
		
		return new Solution[] { result };
	}
	
	public void mutate(Node node, Rules rules) {
		for (int i = 0; i < node.getNumberOfArguments(); i++) {
			if (PRNG.nextDouble() <= probability) {
				Node argument = node.getArgument(i);
				List<Node> mutations = rules.listAvailableMutations(argument);
				
				if (!mutations.isEmpty()) {
					//apply the mutation
					Node mutation = PRNG.nextItem(mutations);
					node.setArgument(i, mutation);
					
					for (int j = 0; j < argument.getNumberOfArguments(); j++) {
						mutation.setArgument(j, argument.getArgument(j));
					}
					
					argument = mutation;
				}
				
				for (int j = 0; j < argument.getNumberOfArguments(); j++) {
					mutate(argument.getArgument(j), rules);
				}
			}
		}
	}

}
