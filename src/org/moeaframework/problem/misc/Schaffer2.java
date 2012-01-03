package org.moeaframework.problem.misc;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

/**
 * The Schaffer (2) problem.
 * <p>
 * Properties:
 * <ul>
 *   <li>Disconnected Pareto set
 *   <li>Disconnected Pareto front
 * </ul>
 * <p>
 * References:
 * <ol>
 *   <li>Van Veldhuizen, D. A (1999).  "Multiobjective Evolutionary Algorithms: 
 *       Classifications, Analyses, and New Innovations."  Air Force Institute
 *       of Technology, Ph.D. Thesis, Appendix B.
 * </ol>
 */
public class Schaffer2 extends AbstractProblem {

	/**
	 * Constructs the Schaffer (2) problem.
	 */
	public Schaffer2() {
		super(1, 2);
	}

	@Override
	public void evaluate(Solution solution) {
		double x = ((RealVariable)solution.getVariable(0)).getValue();
		
		if (x <= 1.0) {
			solution.setObjective(0, -x);
		} else if (x <= 3.0) {
			solution.setObjective(0, -2.0 + x);
		} else if (x <= 4.0) {
			solution.setObjective(0, 4.0 - x);
		} else {
			solution.setObjective(0, -4.0 + x);
		}
		
		solution.setObjective(1, Math.pow(x - 5.0, 2.0));
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(1, 2);
		
		solution.setVariable(0, new RealVariable(-5.0, 10.0));

		return solution;
	}

}
