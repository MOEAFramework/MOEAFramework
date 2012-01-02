package org.moeaframework.problem.misc;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

/**
 * The Kita problem.
 * <p>
 * Properties:
 * <ul>
 *   <li>Connected and symmetric Pareto set
 *   <li>Convex Pareto front
 *   <li>Constrained
 *   <li>Maximization (objectives are negated)
 * </ul>
 * <p>
 * References:
 * <ol>
 *   <li>Van Veldhuizen, D. A (1999).  "Multiobjective Evolutionary Algorithms: 
 *       Classifications, Analyses, and New Innovations."  Air Force Institute
 *       of Technology, Ph.D. Thesis, Appendix B.
 * </ol>
 */
public class Obayashi extends AbstractProblem {

	public Obayashi() {
		super(2, 2, 1);
	}

	@Override
	public void evaluate(Solution solution) {
		double x = ((RealVariable)solution.getVariable(0)).getValue();
		double y = ((RealVariable)solution.getVariable(1)).getValue();
		double c = Math.pow(x, 2.0) + Math.pow(y, 2.0) - 1.0;
		
		solution.setObjective(0, -x);
		solution.setObjective(1, -y);
		solution.setConstraint(0, c <= 0.0 ? 0.0 : c);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(2, 2, 1);
		
		solution.setVariable(0, new RealVariable(0.0, 1.0));
		solution.setVariable(1, new RealVariable(0.0, 1.0));
		
		return solution;
	}

}
