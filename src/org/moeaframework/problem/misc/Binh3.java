package org.moeaframework.problem.misc;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

/**
 * The Binh (3) problem.
 * <p>
 * Properties:
 * <ul>
 *   <li>Connected Pareto set
 * </ul>
 * <p>
 * References:
 * <ol>
 *   <li>Van Veldhuizen, D. A (1999).  "Multiobjective Evolutionary Algorithms: 
 *       Classifications, Analyses, and New Innovations."  Air Force Institute
 *       of Technology, Ph.D. Thesis, Appendix B.
 * </ol>
 */
public class Binh3 extends AbstractProblem {

	public Binh3() {
		super(2, 3);
	}

	@Override
	public void evaluate(Solution solution) {
		double x = ((RealVariable)solution.getVariable(0)).getValue();
		double y = ((RealVariable)solution.getVariable(1)).getValue();
		
		solution.setObjective(0, x - 1e6);
		solution.setObjective(1, y - 2e-6);
		solution.setObjective(2, x*y - 2.0);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(2, 3);
		
		solution.setVariable(0, new RealVariable(1e-6, 1e6));
		solution.setVariable(1, new RealVariable(1e-6, 1e6));
		
		return solution;
	}

}
