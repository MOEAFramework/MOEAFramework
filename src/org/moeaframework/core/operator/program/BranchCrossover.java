package org.moeaframework.core.operator.program;

import java.util.ArrayList;
import java.util.List;

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
	
	public Node getCrossoverPoint(Node node, int count) {
		if (count == 0) {
			return node;
		} else {
			count--;
			
			for (int i = 0; i < node.getNumberOfArguments(); i++) {
				Node argument = node.getArgument(i);
				int size = argument.size();
				
				if (size > count) {
					return getCrossoverPoint(argument, count);
				} else {
					count -= size;
				}
			}
		}
		
		throw new IllegalStateException();
	}
	
	public List<Node> listCrossoverOptions(Node node, Class<?> type) {
		List<Node> result = new ArrayList<Node>();
		
		if (type.isAssignableFrom(node.getReturnType())) {
			result.add(node);
		}
		
		for (int i = 0; i < node.getNumberOfArguments(); i++) {
			result.addAll(listCrossoverOptions(node.getArgument(i), type));
		}
		
		return result;
	}
	
	public void crossover(Program program1, Program program2, Rules rules) {
		int size = program1.size();
		int count = PRNG.nextInt(size - 1) + 1; // skip the program node
		Node node = getCrossoverPoint(program1, count);
		Node parent = node.getParent();
		List<Node> options = listCrossoverOptions(program2.getArgument(0),
				node.getReturnType());
		
		if (options.isEmpty()) {
			return;
		}
		
		for (int i = 0; i < parent.getNumberOfArguments(); i++) {
			Node argument = parent.getArgument(i);
			
			if (argument == node) {
				parent.setArgument(i, PRNG.nextItem(options));
			}
		}
	}

}
