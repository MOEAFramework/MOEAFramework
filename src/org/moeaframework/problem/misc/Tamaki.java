package org.moeaframework.problem.misc;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

/**
 * The Tamaki problem.
 * <p>
 * Properties:
 * <ul>
 *   <li>Connected and curved Pareto set
 *   <li>Curved Pareto front
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
public class Tamaki extends AbstractProblem {

	public Tamaki() {
		super(3, 3, 1);
	}

	@Override
	public void evaluate(Solution solution) {
		double x = ((RealVariable)solution.getVariable(0)).getValue();
		double y = ((RealVariable)solution.getVariable(1)).getValue();
		double z = ((RealVariable)solution.getVariable(2)).getValue();
		double c = Math.pow(x, 2.0) + Math.pow(y, 2.0) + Math.pow(z, 2.0) - 1.0;
		
		solution.setObjective(0, -x);
		solution.setObjective(1, -y);
		solution.setObjective(2, -z);
		solution.setConstraint(0, c <= 0.0 ? 0.0 : c);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(3, 3, 1);
		
		solution.setVariable(0, new RealVariable(0.0, Double.POSITIVE_INFINITY));
		solution.setVariable(1, new RealVariable(0.0, Double.POSITIVE_INFINITY));
		solution.setVariable(2, new RealVariable(0.0, Double.POSITIVE_INFINITY));
		
		return solution;
	}

}
