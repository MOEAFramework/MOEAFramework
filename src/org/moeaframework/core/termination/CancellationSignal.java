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
package org.moeaframework.core.termination;

import java.util.concurrent.atomic.AtomicBoolean;

import org.moeaframework.algorithm.Algorithm;

/**
 * Terminates a run when cancellation signal is received, by calling {@link #cancel()}.
 */
public class CancellationSignal implements TerminationCondition {
	
	private final AtomicBoolean cancelled;
	
	/**
	 * Constructs a new termination condition that allows cancellation.
	 */
	public CancellationSignal() {
		super();
		this.cancelled = new AtomicBoolean();
	}

	/**
	 * Cancels the current run.
	 */
	public void cancel() {
		cancelled.set(true);
	}
	
	/**
	 * Returns {@code true} if cancelled; {@code false} otherwise.
	 * 
	 * @return {@code true} if cancelled; {@code false} otherwise
	 */
	public boolean isCancelled() {
		return cancelled.get();
	}
	
	@Override
	public void initialize(Algorithm algorithm) {
		cancelled.set(false);
	}

	@Override
	public boolean shouldTerminate(Algorithm algorithm) {
		return cancelled.get();
	}
	
	@Override
	public double getPercentComplete(Algorithm algorithm) {
		return Double.NaN;
	}

}
