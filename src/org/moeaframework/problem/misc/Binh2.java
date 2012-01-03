package org.moeaframework.problem.misc;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

/**
 * The Binh (2) problem.
 * <p>
 * Properties:
 * <ul>
 *   <li>Connected Pareto set
 *   <li>Convex Pareto front
 *   <li>Constrained
 * </ul>
 * <p>
 * References:
 * <ol>
 *   <li>Van Veldhuizen, D. A (1999).  "Multiobjective Evolutionary Algorithms: 
 *       Classifications, Analyses, and New Innovations."  Air Force Institute
 *       of Technology, Ph.D. Thesis, Appendix B.
 * </ol>
 */
public class Binh2 extends AbstractProblem {

	/**
	 * Constructs the Binh (2) problem.
	 */
	public Binh2() {
		super(2, 2, 2);
	}

	@Override
	public void evaluate(Solution solution) {
		double x = ((RealVariable)solution.getVariable(0)).getValue();
		double y = ((RealVariable)solution.getVariable(1)).getValue();
		double f1 = 4.0*Math.pow(x, 2.0) + 4.0*Math.pow(y, 2.0);
		double f2 = Math.pow(x - 5.0, 2.0) + Math.pow(y - 5.0, 2.0);
		double c1 = Math.pow(x - 5.0, 2.0) + Math.pow(y, 2.0) - 25.0;
		double c2 = -Math.pow(x - 8.0, 2.0) - Math.pow(y + 3.0, 2.0) + 7.7;
		
		solution.setObjective(0, f1);
		solution.setObjective(1, f2);
		solution.setConstraint(0, c1 <= 0.0 ? 0.0 : c1);
		solution.setConstraint(1, c2 <= 0.0 ? 0.0 : c2);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(2, 2, 2);
		
		solution.setVariable(0, new RealVariable(0.0, 5.0));
		solution.setVariable(1, new RealVariable(0.0, 3.0));
		
		return solution;
	}

}
