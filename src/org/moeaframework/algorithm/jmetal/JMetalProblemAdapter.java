/* Copyright 2009-2011 David Hadka
 * 
 * This file is part of the MOEA Framework.
 * 
 * The MOEA Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or (at your 
 * option) any later version.
 * 
 * The MOEA Framework is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public 
 * License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.moeaframework.algorithm.jmetal;

import jmetal.base.SolutionType;
import jmetal.base.solutionType.BinarySolutionType;
import jmetal.base.solutionType.PermutationSolutionType;
import jmetal.base.solutionType.RealSolutionType;

import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.ProblemException;
import org.moeaframework.util.ArrayMath;

/**
 * Adapter for JMetal problems. This allows MOEA Framework {@link Problem}s to
 * be used within the JMetal library.
 */
public class JMetalProblemAdapter extends jmetal.base.Problem {

	private static final long serialVersionUID = 668044250314229409L;

	/**
	 * The problem.
	 */
	private final Problem problem;

	/**
	 * Constructs a JMetal problem adapter.
	 * 
	 * @param problem the problem
	 */
	public JMetalProblemAdapter(Problem problem) {
		super();
		this.problem = problem;
		
		if (problem.getNumberOfConstraints() > 1) {
			System.err.println("multiple constraints not supported, aggregating into first constraint");
		}

		numberOfVariables_ = problem.getNumberOfVariables();
		numberOfObjectives_ = problem.getNumberOfObjectives();
		numberOfConstraints_ = problem.getNumberOfConstraints();
		problemName_ = problem.getName();
		lowerLimit_ = new double[numberOfVariables_];
		upperLimit_ = new double[numberOfVariables_];
		length_ = new int[numberOfVariables_];

		Solution solution = problem.newSolution();
		SolutionType solutionType = null;

		try {
			for (int i = 0; i < numberOfVariables_; i++) {
				Variable variable = solution.getVariable(i);
	
				if (variable instanceof RealVariable) {
					RealVariable real = (RealVariable)variable;
					lowerLimit_[i] = real.getLowerBound();
					upperLimit_[i] = real.getUpperBound();
	
					if (solutionType == null) {
						solutionType = new RealSolutionType(this);
					} else if (!(solutionType instanceof RealSolutionType)) {
						throw new ProblemException(problem, 
								"mixed types not supported");
					}
				} else if (variable instanceof BinaryVariable) {
					BinaryVariable binary = (BinaryVariable)variable;
					length_[i] = binary.getNumberOfBits();
	
					if (solutionType == null) {
						solutionType = new BinarySolutionType(this);
					} else if (!(solutionType instanceof BinarySolutionType)) {
						throw new ProblemException(problem, 
								"mixed types not supported");
					}
				} else if (variable instanceof Permutation) {
					Permutation permutation = (Permutation)variable;
					length_[i] = permutation.size();
	
					if (solutionType == null) {
						solutionType = new PermutationSolutionType(this);
					} else if (!(solutionType instanceof PermutationSolutionType)) {
						throw new ProblemException(problem, 
								"mixed types not supported");
					}
				} else {
					throw new ProblemException(problem, "type not supported");
				}
			}
		} catch (ClassNotFoundException e) {
			throw new ProblemException(problem, e);
		}
	}

	@Override
	public void evaluate(jmetal.base.Solution solution)
			throws jmetal.util.JMException {
		Solution result = translate(solution);

		problem.evaluate(result);

		for (int i = 0; i < solution.numberOfObjectives(); i++) {
			solution.setObjective(i, result.getObjective(i));
		}

		solution.setOverallConstraintViolation(ArrayMath.sum(result
				.getConstraints()));
	}
	
	/**
	 * Translates the specified JMetal solution into a {@link Solution}. Only
	 * works with {@link RealVariable}, {@link BinaryVariable} and
	 * {@link Permutation} decision variables. Since JMetal uses aggregate
	 * constraints, constraint values are only copied if there is a single
	 * constraint.
	 * 
	 * @param solution the JMetal solution to be translated
	 * @return the translated solution
	 */
	public Solution translate(jmetal.base.Solution solution) {
		Solution result = problem.newSolution();

		for (int i = 0; i < solution.numberOfVariables(); i++) {
			jmetal.base.Variable variable = solution.getDecisionVariables()[i];
			if (variable instanceof jmetal.base.variable.Real) {
				jmetal.base.variable.Real real = 
						(jmetal.base.variable.Real)variable;
				result.setVariable(i, new RealVariable(real.getValue(), real
						.getLowerBound(), real.getUpperBound()));
			} else if (variable instanceof jmetal.base.variable.Binary) {
				jmetal.base.variable.Binary binary = 
						(jmetal.base.variable.Binary)variable;
				BinaryVariable bv = new BinaryVariable(binary.getNumberOfBits());
				for (int j = 0; j < binary.getNumberOfBits(); j++) {
					bv.set(j, binary.getIth(j));
				}
				result.setVariable(i, bv);
			} else if (variable instanceof jmetal.base.variable.Permutation) {
				jmetal.base.variable.Permutation permutation = 
						(jmetal.base.variable.Permutation)variable;
				Permutation p = new Permutation(permutation.vector_);
				result.setVariable(i, p);
			} else {
				throw new IllegalStateException();
			}
		}

		for (int i = 0; i < solution.numberOfObjectives(); i++) {
			result.setObjective(i, solution.getObjective(i));
		}

		if (problem.getNumberOfConstraints() == 1) {
			result.setConstraint(0, solution.getOverallConstraintViolation());
		}

		return result;
	}
	
	/**
	 * Returns the problem underlying by this adapter.
	 * 
	 * @return the problem underlying by this adapter
	 */
	public Problem getProblem() {
		return problem;
	}

}
