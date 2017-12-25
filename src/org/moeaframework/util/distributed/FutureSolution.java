/* Copyright 2009-2016 David Hadka
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

import java.util.concurrent.Future;

import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Solution;

/**
 * Represents a {@link Solution} evaluated asynchronously. Calls to
 * <ul>
 * <li>{@link #getObjective(int)}
 * <li>{@link #getObjectives()}
 * <li>{@link #getConstraint(int)}
 * <li>{@link #getConstraints()}
 * </ul>
 * will block until the asynchronous evaluation completes. This behavior is
 * achieved by assigning a {@link Future} through the {@link #setFuture(Future)}
 * method prior to submitting this solution for evaluation.
 */
public class FutureSolution extends Solution {

	private static final long serialVersionUID = 4101855209843150767L;

	/**
	 * The {@code Future} for the asynchronous evaluation of this solution, or
	 * {@code null} if no asynchronous evaluation is underway.
	 */
	private transient Future<Solution> future;

	/**
	 * Constructs a future solution. This future solution replaces the nested
	 * solution; there should exist no direct access to the nested solution.
	 * 
	 * @param solution the nested solution
	 */
	FutureSolution(Solution solution) {
		super(solution);
	}

	@Override
	public FutureSolution copy() {
		update();
		return new FutureSolution(this);
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
	 * Updates this solution with the result of the {@code Future}, or blocks
	 * until the result is available. Since the result is a serialized copy of
	 * this solution, the objectives and constraints must be copied.
	 */
	private synchronized void update() {
		if (future != null) {
			try {
				Solution solution = future.get();
				future = null;
				setObjectives(solution.getObjectives());
				setConstraints(solution.getConstraints());
				//Add attributes copy
				for (Map.Entry<String, Serializable> entry : solution.getAttributes().entrySet()) {
				    setAttribute(
					    entry.getKey(),
					    SerializationUtils.clone(entry.getValue()));
				}
			} catch (Exception e) {
				throw new FrameworkException(e);
			}
		}
	}

	@Override
	public double[] getObjectives() {
		update();
		return super.getObjectives();
	}

	@Override
	public double getConstraint(int index) {
		update();
		return super.getConstraint(index);
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

}
