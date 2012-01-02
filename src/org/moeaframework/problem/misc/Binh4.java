package org.moeaframework.problem.misc;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

/**
 * The Binh (4) problem.
 * <p>
 * References:
 * <ol>
 *   <li>Van Veldhuizen, D. A (1999).  "Multiobjective Evolutionary Algorithms: 
 *       Classifications, Analyses, and New Innovations."  Air Force Institute
 *       of Technology, Ph.D. Thesis, Appendix B.
 * </ol>
 */
public class Binh4 extends AbstractProblem {

	public Binh4() {
		super(2, 3, 2);
	}

	@Override
	public void evaluate(Solution solution) {
		double x = ((RealVariable)solution.getVariable(0)).getValue();
		double y = ((RealVariable)solution.getVariable(1)).getValue();
		double f1 = 1.5 - x*(1.0 - y);
		double f2 = 2.25 - x*(1.0 - Math.pow(y, 2.0));
		double f3 = 2.625 - x*(1.0 - Math.pow(y, 3.0));
		double c1 = -Math.pow(x, 2.0) - Math.pow(y - 0.5, 2.0) + 9.0;
		double c2 = Math.pow(x - 1.0, 2.0) + Math.pow(y - 0.5, 2.0) - 6.25;
		
		solution.setObjective(0, f1);
		solution.setObjective(1, f2);
		solution.setObjective(2, f3);
		solution.setConstraint(0, c1 <= 0.0 ? 0.0 : c1);
		solution.setConstraint(1, c2 <= 0.0 ? 0.0 : c2);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(2, 3, 2);
		
		solution.setVariable(0, new RealVariable(-10.0, 10.0));
		solution.setVariable(1, new RealVariable(-10.0, 10.0));
		
		return solution;
	}

}
