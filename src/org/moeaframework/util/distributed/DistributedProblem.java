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
package org.moeaframework.util.distributed;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.problem.ProblemException;

/**
 * Distributes the {@link #evaluate(Solution)} method across multiple threads,
 * cores or compute nodes using the provided {@link ExecutorService}. The
 * {@code ExecutorService} defines the type and method of distribution. The
 * problem must be {@link Serializable} if executing on remote nodes.
 */
public class DistributedProblem implements Problem {

	/**
	 * The {@code ExecutorService} for distributing jobs across multiple
	 * threads, cores or compute nodes.
	 */
	private final ExecutorService executor;

	/**
	 * The problem.
	 */
	private final Problem innerProblem;

	/**
	 * By assigning a unique id to each FutureSolution that this 
	 * DistributedProblem creates, problems with stochastic evaluation functions
	 * (e.g., certain types of simulations) can use this as a random seed
	 * in order to get replicability of results even when run in parallel.      
	 */
	private long nextDistributedEvaluationID = 0;
	
	/**
	 * Decorates a problem for distributing the evaluation of the problem across
	 * multiple threads, cores or compute nodes as defined by the specified
	 * {@code ExecutorService}.
	 * 
	 * @param problem the problem being distributed
	 * @param executor the {@code ExecutorService} for distributing jobs across
	 *        multiple threads, cores or compute nodes
	 */
	public DistributedProblem(Problem problem, ExecutorService executor) {
		super();
		this.innerProblem = problem;
		this.executor = executor;
	}

	/**
	 * The {@link Callable} sent to the {@code ExecutorService} for distributed
	 * processing. Note that serialization may result in the solution being
	 * evaluated and returned may be a different instance than provided to the
	 * constructor. It is therefore necessary to ensure the required fields are
	 * copied when appropriate.
	 */
	private static class ProblemEvaluator implements Callable<Solution>,
			Serializable {

		private static final long serialVersionUID = -4812427470992224532L;

		/**
		 * The problem.
		 */
		private final Problem problem;

		/**
		 * The solution being evaluated.
		 */
		private final Solution solution;

		/**
		 * Constructs a distributed job to evaluate the specified solution.
		 * 
		 * @param problem the problem
		 * @param solution the solution to be evaluated
		 */
		public ProblemEvaluator(Problem problem, Solution solution) {
			super();
			this.problem = problem;
			this.solution = solution;
		}

		@Override
		public Solution call() throws Exception {
			problem.evaluate(solution);
			return solution;
		}

	}

	@Override
	public void evaluate(Solution solution) {
		if (solution instanceof FutureSolution) {
			FutureSolution futureSolution = (FutureSolution)solution;
			futureSolution.setDistributedEvaluationID(nextDistributedEvaluationID());
			Future<Solution> future = executor.submit(new ProblemEvaluator(
					innerProblem, futureSolution));
			futureSolution.setFuture(future);
		} else {
			throw new ProblemException(this, "requires FutureSolution");
		}
	}

	synchronized long nextDistributedEvaluationID() {
		return nextDistributedEvaluationID++;
	}


	@Override
	public String getName() {
		return innerProblem.getName();
	}

	@Override
	public int getNumberOfConstraints() {
		return innerProblem.getNumberOfConstraints();
	}

	@Override
	public int getNumberOfObjectives() {
		return innerProblem.getNumberOfObjectives();
	}

	@Override
	public int getNumberOfVariables() {
		return innerProblem.getNumberOfVariables();
	}

	@Override
	public Solution newSolution() {
		return new FutureSolution(innerProblem.newSolution());
	}
	
	@Override
	public void close() {
		innerProblem.close();
	}

}
