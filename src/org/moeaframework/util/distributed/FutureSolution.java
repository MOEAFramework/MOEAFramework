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
import java.util.Map;
import java.util.concurrent.Future;

import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Solution;

/**
 * Represents a {@link Solution} evaluated asynchronously. Calls to
 * <ul>
 *   <li>{@link #getObjective(int)}
 *   <li>{@link #getObjectives()}
 *   <li>{@link #getConstraint(int)}
 *   <li>{@link #getConstraints()}
 * </ul>
 * will block until the asynchronous evaluation completes. This behavior is achieved by assigning a {@link Future}
 * through the {@link #setFuture(Future)} method prior to submitting this solution for evaluation.
 */
public class FutureSolution extends Solution {

	private static final long serialVersionUID = 4101855209843150767L;

	/**
	 * The {@code Future} for the asynchronous evaluation of this solution, or {@code null} if no asynchronous
	 * evaluation is underway.
	 */
	private transient Future<Solution> future;

	/**
	 * The unique identifier for this solution.
	 */
	private long distributedEvaluationID;
	
	/**
	 * Constructs a future solution. This future solution replaces the nested solution; there should exist no direct
	 * access to the nested solution.
	 * 
	 * @param solution the nested solution
	 */
	FutureSolution(Solution solution) {
		super(solution);
	}

	/**
	 * Sets the {@code Future} for this solution.
	 * 
	 * @param future the future
	 */
	synchronized void setFuture(Future<Solution> future) {
		this.future = future;
	}

	/**
	 * Sets the unique identifier for this solution. This is assigned automatically by {@link DistributedProblem} when
	 * evaluating the solution.
	 * 
	 * @param distributedEvaluationID a unique identifier for this solution
	 */
	synchronized void setDistributedEvaluationID(long distributedEvaluationID) {
		this.distributedEvaluationID = distributedEvaluationID;		
	}
	
	/** 
	 * Returns the unique identifier for this solution. This can be used to:
	 * <ol>
	 *   <li>Configure the random number generator (RNG) seed on stochastic problems, allowing reproducible results.
	 *   <li>Tracking files or other resources associated with the solution, such as writing program inputs to disk.
	 * </ol>
	 * 
	 * @return the unique identifier that was associated with this solution
	 */
	public long getDistributedEvaluationID() {
		return this.distributedEvaluationID;		
	}
	
	/**
	 * Updates this solution with the result of the {@code Future}, or blocks until the result is available. Since the
	 * result is a serialized copy of this solution, the objectives, constraints, and attributes must be copied.
	 */
	synchronized void update() {
		if (future != null) {
			try {
				Solution solution = future.get();
				future = null;
				
				setObjectives(solution.getObjectives());
				setConstraints(solution.getConstraints());
				
				for (Map.Entry<String, Serializable> entry : solution.getAttributes().entrySet()) {
					setAttribute(entry.getKey(), entry.getValue());
				}
			} catch (Exception e) {
				throw new FrameworkException(e);
			}
		}
	}
	
	@Override
	public FutureSolution copy() {
		update();
		return new FutureSolution(this);
	}

	@Override
	public double[] getObjectives() {
		update();
		return super.getObjectives();
	}

	@Override
	public double[] getConstraints() {
		update();
		return super.getConstraints();
	}

	@Override
	public double getObjective(int index) {
		update();
		return super.getObjective(index);
	}
	
	@Override
	public double getConstraint(int index) {
		update();
		return super.getConstraint(index);
	}
	
	@Override
	public boolean violatesConstraints() {
		update();
		return super.violatesConstraints();
	}
	
	@Override
	public Object getAttribute(String key) {
		update();
		return super.getAttribute(key);
	}
	
	@Override
	public boolean hasAttribute(String key) {
		update();
		return super.hasAttribute(key);
	}
	
	@Override
	public Map<String, Serializable> getAttributes() {
		update();
		return super.getAttributes();
	}

}
