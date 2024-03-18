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
package org.moeaframework.examples.misc;

import java.io.IOException;
import java.time.Duration;

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.termination.MaxElapsedTime;
import org.moeaframework.problem.misc.Srinivas;

/**
 * Demonstrates using a different termination condition, such as the max evaluation time.
 */
public class MaxTimeTerminationExample {
	
	public static void main(String[] args) throws IOException {
		int seconds = 5;
		
		NSGAII algorithm = new NSGAII(new Srinivas());
		algorithm.run(new MaxElapsedTime(Duration.ofSeconds(seconds)));
		
		System.out.println("Performed " + algorithm.getNumberOfEvaluations() + " NFE in " + seconds + " seconds");
	}

}
