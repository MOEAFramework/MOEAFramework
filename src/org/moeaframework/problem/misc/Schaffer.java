package org.moeaframework.problem.misc;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

/**
 * The Schaffer problem.
 * <p>
 * Properties:
 * <ul>
 *   <li>Connected Pareto set
 *   <li>Convex Pareto front
 * </ul>
 * <p>
 * References:
 * <ol>
 *   <li>Van Veldhuizen, D. A (1999).  "Multiobjective Evolutionary Algorithms: 
 *       Classifications, Analyses, and New Innovations."  Air Force Institute
 *       of Technology, Ph.D. Thesis, Appendix B.
 * </ol>
 */
public class Schaffer extends AbstractProblem {

	/**
	 * Constructs the Schaffer problem.
	 */
	public Schaffer() {
		super(1, 2);
	}

	@Override
	public void evaluate(Solution solution) {
		double x = ((RealVariable)solution.getVariable(0)).getValue();
		
		solution.setObjective(0, Math.pow(x, 2.0));
		solution.setObjective(1, Math.pow(x - 2.0, 2.0));
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(1, 2);
		
		solution.setVariable(0, new RealVariable(0, 2));

		return solution;
	}

}
