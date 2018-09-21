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
package org.moeaframework.core;

import java.io.NotSerializableException;
import java.io.Serializable;

import org.moeaframework.algorithm.AlgorithmException;

/**
 * Interface for an optimization algorithm. An optimization algorithm operates
 * by performing a series of optimization steps, though the amount of work
 * performed by each step depends on the algorithm. For example, an algorithm
 * may completely solve a problem in one step or may require hundreds of
 * thousands of steps.
 */
public interface Algorithm {

	/**
	 * Returns the problem being solved by this algorithm.
	 * 
	 * @return the problem being solved by this algorithm
	 */
	public Problem getProblem();

	/**
	 * Returns the current best-known result.
	 * 
	 * @return the current best-known result
	 */
	public NondominatedPopulation getResult();

	/**
	 * Performs one logical step of this algorithm. The amount of work performed
	 * depends on the implementation. One invocation of this method may produce
	 * one or many trial solutions.
	 * <p>
	 * This method should not be invoked when {@link #isTerminated()} returns
	 * {@code true}.
	 */
	public void step();

	/**
	 * Evaluates the specified solution for the problem being solved by this
	 * algorithm.
	 * 
	 * @param solution the solution to be evaluated
	 * @see Problem#evaluate(Solution)
	 */
	public void evaluate(Solution solution);

	/**
	 * Returns the number of times the {@code evaluate} method was invoked. This
	 * is the primary measure of runtime for optimization algorithms.
	 * 
	 * @return the number of times the {@code evaluate} method was invoked
	 */
	public int getNumberOfEvaluations();

	/**
	 * Returns {@code true} if this algorithm is terminated; {@code false}
	 * otherwise.
	 * 
	 * @return {@code true} if this algorithm is terminated; {@code false}
	 *         otherwise
	 * @see #terminate()
	 */
	public boolean isTerminated();

	/**
	 * Terminates this algorithm. Implementations should use this method to
	 * free any underlying resources; however, the {@link #getResult()} and
	 * {@link #getNumberOfEvaluations()} methods are still required to work
	 * after termination.
	 */
	public void terminate();
	
	/**
	 * Returns a {@code Serializable} object representing the internal state of
	 * this algorithm.
	 * 
	 * @return a {@code Serializable} object representing the internal state of
	 *         this algorithm
	 * @throws NotSerializableException if this algorithm does not support
	 *         serialization
	 * @throws AlgorithmException if this algorithm has not yet been
	 *         initialized
	 */
	public Serializable getState() throws NotSerializableException;

	/**
	 * Sets the internal state of of this algorithm.
	 * 
	 * @param state the internal state of this algorithm
	 * @throws NotSerializableException if this algorithm does not support
	 *         serialization
	 * @throws AlgorithmException if this algorithm has already been
	 *         initialized
	 */
	public void setState(Object state) throws NotSerializableException;

}
