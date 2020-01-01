package org.moeaframework.algorithm.jmetal;

import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;
import org.uma.jmetal.util.solutionattribute.impl.OverallConstraintViolation;

public abstract class ProblemAdapter<T extends org.uma.jmetal.solution.Solution<?>> implements org.uma.jmetal.problem.Problem<T> {

	private static final long serialVersionUID = 5625585375846735318L;
	
	private final Problem problem;
	
	public ProblemAdapter(Problem problem) {
		this.problem = problem;
	}
	
	public Problem getProblem() {
		return problem;
	}
	
	@Override
	public String getName() {
		return problem.getName();
	}
	
	@Override
	public int getNumberOfConstraints() {
		return problem.getNumberOfConstraints();
	}

	@Override
	public int getNumberOfObjectives() {
		return problem.getNumberOfObjectives();
	}
	
	@Override
	public int getNumberOfVariables() {
		return problem.getNumberOfVariables();
	}
	
	public abstract Solution convert(T solution);
	
	@Override
	public void evaluate(T solution) {
		Solution result = convert(solution);

		getProblem().evaluate(result);
		
		for (int i = 0; i < getNumberOfObjectives(); i++) {
			solution.setObjective(i, result.getObjective(i));
		}
			
		// calculate constraint violation
		double overallConstraintViolation = 0.0;
		int numberOfViolations = 0;
		
		for (int i = 0; i < getNumberOfConstraints(); i++) {
			if (result.getConstraint(i) != 0.0) {
				numberOfViolations++;
				overallConstraintViolation -= Math.abs(result.getConstraint(i));
			}
		}
		
		new OverallConstraintViolation<T>().setAttribute(solution, overallConstraintViolation);
		new NumberOfViolatedConstraints<T>().setAttribute(solution, numberOfViolations);
	}

	public int getNumberOfMutationIndices() {
		return getNumberOfVariables();
	}
	
}
