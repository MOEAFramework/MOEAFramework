package org.moeaframework.algorithm.jmetal;

import java.util.List;

import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.Permutation;
import org.uma.jmetal.problem.PermutationProblem;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.solution.impl.DefaultIntegerPermutationSolution;

public class PermutationProblemAdapter extends ProblemAdapter<PermutationSolution<Integer>> implements PermutationProblem<PermutationSolution<Integer>> {

	private static final long serialVersionUID = -7658974412222795821L;
	
	private final Solution schema;
	
	public PermutationProblemAdapter(Problem problem) {
		super(problem);
		schema = problem.newSolution();
		
		if (schema.getNumberOfVariables() != 1) {
			throw new FrameworkException("PermutationProblemAdapter only works with a single Permutation variable");
		}
	}
	
	@Override
	public PermutationSolution<Integer> createSolution() {
		return new DefaultIntegerPermutationSolution(this);
	}
	
	@Override
	public Solution convert(PermutationSolution<Integer> solution) {
		Solution result = getProblem().newSolution();
		List<Integer> permutationList = solution.getVariables();
		int[] permutation = new int[permutationList.size()];
		
		for (int i = 0; i < permutationList.size(); i++) {
			permutation[i] = permutationList.get(i);
		}
		
		EncodingUtils.setPermutation(result.getVariable(0), permutation);
		return result;
	}
	
	@Override
	public int getNumberOfVariables() {
		return getPermutationLength();
	}
	
	@Override
	public int getPermutationLength() {
		return ((Permutation)schema.getVariable(0)).size();
	}
	
	@Override
	public int getNumberOfMutationIndices() {
		return 1;
	}

}
