package org.moeaframework.problem.misc;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

/**
 * The Osyczka (2) problem.
 * <p>
 * Properties:
 * <ul>
 *   <li>Disconnected Pareto set
 *   <li>Disconnected Pareto front
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
public class Osyczka2 extends AbstractProblem {

	/**
	 * Constructs the Osyczka (2) problem.
	 */
	public Osyczka2() {
		super(6, 2, 6);
	}

	@Override
	public void evaluate(Solution solution) {
		double x1 = ((RealVariable)solution.getVariable(0)).getValue();
		double x2 = ((RealVariable)solution.getVariable(1)).getValue();
		double x3 = ((RealVariable)solution.getVariable(2)).getValue();
		double x4 = ((RealVariable)solution.getVariable(3)).getValue();
		double x5 = ((RealVariable)solution.getVariable(4)).getValue();
		double x6 = ((RealVariable)solution.getVariable(5)).getValue();
		double f1 = -(25.0*Math.pow(x1 - 2.0, 2.0) + Math.pow(x2 - 2.0, 2.0) +
				Math.pow(x3 - 1.0, 2.0) + Math.pow(x4 - 4.0, 2.0) +
				Math.pow(x5 - 1.0, 2.0));
		double f2 = Math.pow(x1, 2.0) + Math.pow(x2, 2.0) + Math.pow(x3, 2.0) +
				Math.pow(x4, 2.0) + Math.pow(x5, 2.0) + Math.pow(x6, 2.0);
		double c1 = x1 + x2 - 2.0;
		double c2 = 6.0 - x1 - x2;
		double c3 = 2.0 - x2 + x1;
		double c4 = 2.0 - x1 + 3.0*x2;
		double c5 = 4.0 - Math.pow(x3 - 3.0, 2.0) - x4;
		double c6 = Math.pow(x5 - 3.0, 2.0) + x6 - 4.0;
		
		solution.setObjective(0, f1);
		solution.setObjective(1, f2);
		solution.setConstraint(0, c1 >= 0.0 ? 0.0 : c1);
		solution.setConstraint(1, c2 >= 0.0 ? 0.0 : c2);
		solution.setConstraint(2, c3 >= 0.0 ? 0.0 : c3);
		solution.setConstraint(3, c4 >= 0.0 ? 0.0 : c4);
		solution.setConstraint(4, c5 >= 0.0 ? 0.0 : c5);
		solution.setConstraint(5, c6 >= 0.0 ? 0.0 : c6);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(6, 2, 6);
		
		solution.setVariable(0, new RealVariable(0.0, 10.0));
		solution.setVariable(1, new RealVariable(0.0, 10.0));
		solution.setVariable(2, new RealVariable(1.0, 5.0));
		solution.setVariable(3, new RealVariable(0.0, 6.0));
		solution.setVariable(4, new RealVariable(1.0, 5.0));
		solution.setVariable(5, new RealVariable(0.0, 10.0));
		
		return solution;
	}

}
