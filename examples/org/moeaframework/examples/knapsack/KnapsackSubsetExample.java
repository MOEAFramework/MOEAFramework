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
package org.moeaframework.examples.knapsack;

import java.io.IOException;
import java.io.InputStream;

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.util.io.Resources;
import org.moeaframework.util.io.Resources.ResourceOption;

/**
 * Example of binary optimization using the {@link KnapsackSubset} problem on the
 * {@code knapsack.100.2} instance.
 */
public class KnapsackSubsetExample {

	public static void main(String[] args) throws IOException {
		try (InputStream input = Resources.asStream(KnapsackSubset.class,
				"knapsack.100.2", ResourceOption.REQUIRED)) {
			KnapsackSubset problem = new KnapsackSubset(input);
			
			NSGAII algorithm = new NSGAII(problem);
			algorithm.run(50000);
			algorithm.getResult().display();
		}
	}

}
