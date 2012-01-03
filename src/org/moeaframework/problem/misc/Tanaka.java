package org.moeaframework.problem.misc;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

/**
 * The Tanaka problem.
 * <p>
 * Properties:
 * <ul>
 *   <li>Connected Pareto set
 *   <li>Disconnected and convoluted Pareto front
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
public class Tanaka extends AbstractProblem {

	/**
	 * Constructs the Tanaka problem.
	 */
	public Tanaka() {
		super(2, 2, 2);
	}

	@Override
	public void evaluate(Solution solution) {
		double x = ((RealVariable)solution.getVariable(0)).getValue();
		double y = ((RealVariable)solution.getVariable(1)).getValue();
		double c1 = -Math.pow(x,  2.0) - Math.pow(y, 2.0) + 1.0 +
				0.1*Math.cos(16.0*Math.atan(x / y));
		double c2 = Math.pow(x - 0.5, 2.0) + Math.pow(y - 0.5, 2.0) - 0.5;
		
		solution.setObjective(0, x);
		solution.setObjective(1, y);
		solution.setConstraint(0, c1 <= 0.0 ? 0.0 : c1);
		solution.setConstraint(1, c2 <= 0.0 ? 0.0 : c2);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(2, 2, 2);
		
		solution.setVariable(0, new RealVariable(Math.nextUp(0.0), Math.PI));
		solution.setVariable(1, new RealVariable(Math.nextUp(0.0), Math.PI));
		
		return solution;
	}

}
