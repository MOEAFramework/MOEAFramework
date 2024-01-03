package org.moeaframework.problem;

import org.moeaframework.core.Solution;

/**
 * Base class for defining mock problems.
 */
public abstract class MockProblem extends AbstractProblem {
	
	public MockProblem(int numberOfVariables, int numberOfObjectives) {
		this(numberOfVariables, numberOfObjectives, 0);
	}

	public MockProblem(int numberOfVariables, int numberOfObjectives, int numberOfConstraints) {
		super(numberOfVariables, numberOfObjectives, numberOfConstraints);
	}
	
	@Override
	public void evaluate(Solution solution) {
		// Simple way to make the objective values variable but deterministic
		double f = solution.getVariable(0).hashCode();
				
		for (int i = 0; i < getNumberOfObjectives(); i++) {
			solution.setObjective(i, f);
		}
	}

	@Override
	public Solution newSolution() {
		return new Solution(getNumberOfVariables(), getNumberOfObjectives(), getNumberOfConstraints());
	}

}
