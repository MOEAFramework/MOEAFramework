package org.moeaframework.problem;

import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

/**
 * Wraps a problem instance to modify or extend its functionality.  All methods
 * call the corresponding method on the wrapped problem, unless overridden by
 * a subclass.
 */
public abstract class ProblemWrapper implements Problem {

	/**
	 * The original problem instance.
	 */
	protected final Problem problem;
	
	protected ProblemWrapper(Problem problem) {
		super();
		this.problem = problem;
	}
	
	@Override
	public String getName() {
		return problem.getName();
	}

	@Override
	public int getNumberOfVariables() {
		return problem.getNumberOfVariables();
	}

	@Override
	public int getNumberOfObjectives() {
		return problem.getNumberOfObjectives();
	}

	@Override
	public int getNumberOfConstraints() {
		return problem.getNumberOfConstraints();
	}

	@Override
	public void evaluate(Solution solution) {
		problem.evaluate(solution);
	}

	@Override
	public Solution newSolution() {
		return problem.newSolution();
	}

	@Override
	public void close() {
		problem.close();
	}

}
