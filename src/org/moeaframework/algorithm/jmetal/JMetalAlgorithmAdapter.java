/* Copyright 2009-2018 David Hadka
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

import org.moeaframework.algorithm.AlgorithmException;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

/**
 * Adapter for JMetal algorithms. This allows JMetal algorithms to be used
 * within the MOEA Framework as an {@link Algorithm}.
 */
public class JMetalAlgorithmAdapter implements Algorithm {

	/**
	 * The JMetal algorithm.
	 */
	private final jmetal.core.Algorithm algorithm;

	/**
	 * The JMetal problem adapter.
	 */
	private final JMetalProblemAdapter problem;

	/**
	 * The JMetal solution set.
	 */
	private jmetal.core.SolutionSet solutionSet;

	/**
	 * Constructs an adapter for the specified JMetal algorithm.
	 * 
	 * @param algorithm the JMetal algorithm
	 */
	public JMetalAlgorithmAdapter(jmetal.core.Algorithm algorithm,
			JMetalProblemAdapter problem) {
		super();
		this.algorithm = algorithm;
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
			Integer result = (Integer)algorithm.getInputParameter(
					"maxEvaluations");

			if (result == null) {
				// probably a PSO or GDE3 variant
				Integer maxIterations = (Integer)algorithm.getInputParameter(
						"maxIterations");
				Integer populationSize = (Integer)algorithm.getInputParameter(
						"populationSize");
				
				if (populationSize == null) {
					populationSize = (Integer)algorithm.getInputParameter(
							"swarmSize");
				}
				
				result = maxIterations*populationSize;
			}

			return result;
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
				result.add(problem.translate(solutionSet.get(i)));
			}
		}

		return result;
	}

	@Override
	public void step() {
		if (solutionSet == null) {
			try {
				solutionSet = algorithm.execute();
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
			solutionSet = new jmetal.core.SolutionSet();
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
