/* Copyright 2009-2020 David Hadka
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

import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.moeaframework.algorithm.AlgorithmException;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

/**
 * Adapter for JMetal algorithms. This allows JMetal algorithms to be used
 * within the MOEA Framework as an {@link Algorithm}.
 */
public class JMetalAlgorithmAdapter<T extends org.uma.jmetal.solution.Solution<?>> implements Algorithm {

	/**
	 * The JMetal algorithm.
	 */
	private final org.uma.jmetal.algorithm.Algorithm<List<T>> algorithm;

	/**
	 * The JMetal problem adapter.
	 */
	private final ProblemAdapter<T> problem;
	
	private final int maxEvaluations;

	/**
	 * The JMetal solution set.
	 */
	private List<T> solutionSet;

	/**
	 * Constructs an adapter for the specified JMetal algorithm.
	 * 
	 * @param algorithm the JMetal algorithm
	 * @param maxEvaluations the maximum number of evaluations this algorithm will run
	 * @param problem the problem adapter
	 */
	public JMetalAlgorithmAdapter(
			org.uma.jmetal.algorithm.Algorithm<List<T>> algorithm,
			int maxEvaluations,
			ProblemAdapter<T> problem) {
		super();
		this.algorithm = algorithm;
		this.maxEvaluations = maxEvaluations;
		this.problem = problem;
	}

	@Override
	public void evaluate(Solution solution) {
		problem.getProblem().evaluate(solution);
	}

	@Override
	public int getNumberOfEvaluations() {
		if (solutionSet == null) {
			return 0;
		} else {
			return maxEvaluations;
		}
	}

	@Override
	public Problem getProblem() {
		return problem.getProblem();
	}

	@Override
	public NondominatedPopulation getResult() {
		NondominatedPopulation result = new NondominatedPopulation();

		if (solutionSet != null) {
			for (int i = 0; i < solutionSet.size(); i++) {
				Solution solution = problem.convert(solutionSet.get(i));
				JMetalUtils.copyObjectivesAndConstraints(solutionSet.get(i), solution);
				result.add(solution);
			}
		}

		return result;
	}


	@Override
	public void step() {
		if (solutionSet == null) {
			try {
				algorithm.run();
				solutionSet = algorithm.getResult();
			} catch (Exception e) {
				throw new AlgorithmException(this, e);
			}
		}
	}

	@Override
	public boolean isTerminated() {
		return solutionSet != null;
	}

	@Override
	public void terminate() {
		if (solutionSet == null) {
			solutionSet = new ArrayList<T>();
		}
	}

	/**
	 * Throws {@code NotSerializableException} since JMetal algorithms are
	 * currently not serializable.
	 */
	@Override
	public Serializable getState() throws NotSerializableException {
		throw new NotSerializableException(getClass().getName());
	}

	/**
	 * Throws {@code NotSerializableException} since JMetal algorithms are
	 * currently not serializable.
	 */
	@Override
	public void setState(Object state) throws NotSerializableException {
		throw new NotSerializableException(getClass().getName());
	}

}
