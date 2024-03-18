/* Copyright 2009-2024 David Hadka
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
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.problem.ProblemException;
import org.moeaframework.problem.ProblemWrapper;

/**
 * Distributes the {@link #evaluate(Solution)} method across multiple threads, cores or compute nodes using the
 * provided {@link ExecutorService}.  The {@code ExecutorService} defines the type and method of distribution. The
 * problem must be {@link Serializable} if executing on remote nodes.
 */
public class DistributedProblem extends ProblemWrapper {

	/**
	 * The {@code ExecutorService} for distributing jobs across multiple threads, cores or compute nodes.
	 */
	private final ExecutorService executor;
	
	/**
	 * If {@code true}, the {@code ExecutorService} will be shutdown when this problem is closed; if {@code false},
	 * it's the caller's responsibility to manage the lifecycle of the executor.
	 */
	private final boolean shutdownWhenClosed;

	/**
	 * By assigning a unique id to each {@code FutureSolution} that this {@code DistributedProblem} creates, problems
	 * with stochastic evaluation functions (e.g., certain types of simulations) can use this as a random seed
	 * in order to get replicability of results even when run in parallel.      
	 */
	private final AtomicLong nextDistributedEvaluationID;
	
	/**
	 * Creates a distributed problem using the number of available processors on the local computer.  Callers should
	 * ensure the returned problem is closed, preferably using a try-with-resources block, to clean up the underlying
	 * resources.
	 * 
	 * @param problem the problem to distribute
	 * @return the distributed problem
	 */
	public static DistributedProblem from(Problem problem) {
		return from(problem, Runtime.getRuntime().availableProcessors());
	}
	
	/**
	 * Creates a distributed problem using a fixed number of threads.  Callers should ensure the returned problem is
	 * closed, preferably using a try-with-resources block, to clean up the underlying resources.
	 * 
	 * @param problem the problem to distribute
	 * @param numberOfThreads the number of threads
	 * @return the distributed problem
	 */
	public static DistributedProblem from(Problem problem, int numberOfThreads) {
		return new DistributedProblem(problem, Executors.newFixedThreadPool(numberOfThreads), true);
	}
	
	/**
	 * Decorates a problem for distributing the evaluation of the problem across multiple threads, cores or compute
	 * nodes as defined by the specified {@code ExecutorService}.  This will not shutdown the executor and is
	 * equivalent to calling the constructor with {@code shutdownWhenClosed} set to {@code false}.
	 * 
	 * @param problem the problem being distributed
	 * @param executor the {@code ExecutorService} for distributing jobs across multiple threads, cores or compute nodes
	 */
	public DistributedProblem(Problem problem, ExecutorService executor) {
		this(problem, executor, false);
	}
	
	/**
	 * Decorates a problem for distributing the evaluation of the problem across multiple threads, cores or compute
	 * nodes as defined by the specified {@code ExecutorService}.
	 * 
	 * @param problem the problem being distributed
	 * @param executor the {@code ExecutorService} for distributing jobs across multiple threads, cores or compute nodes
	 * @param shutdownWhenClosed {@code true} to shutdown the executor when this problem instance is closed;
	 *        {@code false} otherwise
	 */
	public DistributedProblem(Problem problem, ExecutorService executor, boolean shutdownWhenClosed) {
		super(problem);
		this.executor = executor;
		this.shutdownWhenClosed = shutdownWhenClosed;
		
		nextDistributedEvaluationID = new AtomicLong(0);
	}
	
	/**
	 * The {@link Callable} sent to the {@code ExecutorService} for distributed processing. Note that serialization may
	 * result in the solution being evaluated and returned may be a different instance than provided to the
	 * constructor. It is therefore necessary to ensure the required fields are copied when appropriate.
	 */
	private static class ProblemEvaluator implements Callable<Solution>, Serializable {

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
		if (solution instanceof FutureSolution futureSolution) {
			futureSolution.setDistributedEvaluationID(nextDistributedEvaluationID.getAndIncrement());
			
			Future<Solution> future = executor.submit(new ProblemEvaluator(problem, futureSolution));
			futureSolution.setFuture(future);
		} else {
			throw new ProblemException(this, "must provide FutureSolution to DistributedProblem");
		}
	}

	@Override
	public Solution newSolution() {
		return new FutureSolution(super.newSolution());
	}
	
	@Override
	public void close() {
		try {
			super.close();
		} finally {
			if (shutdownWhenClosed && !executor.isShutdown()) {
				executor.shutdown();
			}
		}
	}

}
