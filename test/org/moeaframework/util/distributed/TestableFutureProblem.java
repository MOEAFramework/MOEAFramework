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

import java.time.Duration;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.moeaframework.Assert;
import org.moeaframework.Wait;
import org.moeaframework.core.Solution;
import org.moeaframework.mock.MockConstraintProblem;

public class TestableFutureProblem extends MockConstraintProblem {
	
	private static final int TIMEOUT_SECONDS = 60;
	
	private static final Duration EVALUATE_TIME = Duration.ofMillis(100);
	
	private Semaphore semaphore;
	
	public TestableFutureProblem() {
		super();
	}
	
	// pass in 0 to immediately block all evaluate calls
	public TestableFutureProblem(int permits) {
		super();
		setSemaphore(new Semaphore(permits));
	}

	@Override
	public void evaluate(Solution solution) {
		if (semaphore != null) {
			try {
				if (!semaphore.tryAcquire(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
					Assert.fail("Acquire timed out, indicating the problem blocked the main thread");
				}
			} catch (InterruptedException e) {
				Assert.fail("Acquire was interrupted");
			}
		}
		
		Wait.sleepFor(EVALUATE_TIME);
		
		super.evaluate(solution);
	}
	
	@Override
	public TestableFutureSolution newSolution() {
		return new TestableFutureSolution(super.newSolution());
	}
	
	public void setSemaphore(Semaphore semaphore) {
		this.semaphore = semaphore;
	}
	
	public Semaphore getSemaphore() {
		return semaphore;
	}
	
	public void releaseOrFailIfBlocked(int permits) {
		semaphore.release(permits);
	}

}