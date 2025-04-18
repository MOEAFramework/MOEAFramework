/* Copyright 2009-2025 David Hadka
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

import org.moeaframework.algorithm.Algorithm;
import org.moeaframework.core.Named;

/**
 * Interface used to implement conditions for when an algorithm should terminate.  The {@code initialize} method is
 * invoked when the algorithm is first created to collect any initial conditions, such as the starting time,
 * and {@code shouldTerminate} is invoked every step to check if the algorithm should terminate.
 */
public interface TerminationCondition extends Named {
	
	/**
	 * Invoked when the algorithm is created to collect any initial conditions.  Note that the algorithm may not
	 * have been initialized at this point.
	 * 
	 * @param algorithm the algorithm
	 */
	public void initialize(Algorithm algorithm);
	
	/**
	 * Invoked after every step to check if the algorithm should terminate.
	 * 
	 * @param algorithm the algorithm
	 * @return {@code true} if the algorithm should terminate; {@code false} otherwise
	 */
	public boolean shouldTerminate(Algorithm algorithm);
	
	/**
	 * Returns the percentage that this condition is complete, as a value between {@code 0.0} and {@code 100.0}.  If
	 * the percentage can not be determined, returns {@value Double#NaN}.
	 * 
	 * @param algorithm the algorithm
	 * @return the percentage completion
	 */
	public double getPercentComplete(Algorithm algorithm);
	
	@Override
	public default String getName() {
		return getClass().getSimpleName();
	}

}
