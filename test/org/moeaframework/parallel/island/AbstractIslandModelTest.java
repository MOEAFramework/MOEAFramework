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
package org.moeaframework.parallel.island;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.mock.MockRealProblem;

@Ignore("Abstract test class")
public abstract class AbstractIslandModelTest {

	protected List<Island> createIslands(int count) {
		List<Island> result = new ArrayList<>();
		
		for (int i = 0; i < count; i++) {
			result.add(createIsland());
		}
		
		return result;
	}
	
	protected Island createIsland() {
		NSGAII algorithm = new NSGAII(new MockRealProblem());
		return new Island(algorithm, algorithm.getPopulation());
	}

}
