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
package org.moeaframework.problem;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

/**
 * Wraps a problem to collect objective function evaluation timing data.  Precision and accuracy of the timing data
 * is dependent on {@link System#nanoTime()}.
 */
public class TimingProblem extends ProblemWrapper {
	
	/**
	 * The time, in nanoseconds, expended on objective function evaluation.
	 */
	private AtomicLong time;
	
	/**
	 * The number of times the function was evaluated.
	 */
	private AtomicInteger counter;
	
	/**
	 * Decorates the specified problem to collecting objective function evaluation timing data.
	 * 
	 * @param problem the problem to decorate
	 */
	public TimingProblem(Problem problem) {
		super(problem);
		
		time = new AtomicLong();
		counter = new AtomicInteger();
	}

	@Override
	public void evaluate(Solution solution) {
		long start = System.nanoTime();
		problem.evaluate(solution);
		long end = System.nanoTime();
		
		time.addAndGet(end - start);
		counter.incrementAndGet();
	}
	
	/**
	 * Clears any timing data collected.  
	 */
	public void clear() {
		time.set(0);
	}
	
	/**
	 * Returns the time, in nanoseconds, expended on objective function evaluation.
	 * 
	 * @return the time, in nanoseconds, expended on objective function evaluation
	 */
	public long getNanoseconds() {
		return time.get();
	}
	
	/**
	 * Returns the time, in seconds, expended on objective function evaluation.
	 * 
	 * @return the time, in seconds, expended on objective function evaluation
	 */
	public double getSeconds() {
		return time.get() / 1e9;
	}
	
	/**
	 * Returns the number of times the objective function was evaluated.
	 * 
	 * @return the number of times the objective function was evaluated
	 */
	public int getNFE() {
		return counter.get();
	}

}
