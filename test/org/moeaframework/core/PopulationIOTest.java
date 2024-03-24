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
package org.moeaframework.core;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.TestThresholds;
import org.moeaframework.TestUtils;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.Grammar;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.mock.MockSolution;

public class PopulationIOTest {

	private Population population;

	@Before
	public void setUp() {
		population = new Population();
		
		Solution s1 = MockSolution.of()
				.withVariables(new BinaryVariable(10), new Grammar(5), new Permutation(5), new RealVariable(0.0, 1.0))
				.withObjectives(1.0, 0.0)
				.withConstraints(1.0)
				.build();

		Solution s2 = MockSolution.of().withObjectives(1.0, -1.0).build();

		population.add(s1);
		population.add(s2);
	}

	@After
	public void tearDown() {
		population = null;
	}

	@Test
	public void testWriteReadObjectives() throws IOException {
		File file = TestUtils.createTempFile();

		PopulationIO.writeObjectives(file, population);
		Population population2 = PopulationIO.readObjectives(file);

		Assert.assertEquals(population.size(), population2.size());

		for (int i = 0; i < population.size(); i++) {
			Assert.assertArrayEquals(population.get(i).getObjectives(), population2.get(i).getObjectives(),
					TestThresholds.SOLUTION_EPS);
		}
	}

	@Test
	public void testWriteRead() throws IOException {
		File file = TestUtils.createTempFile();

		PopulationIO.write(file, population);
		TestUtils.assertEquals(population, PopulationIO.read(file));
		Population population2 = PopulationIO.read(file);

		Assert.assertEquals(population.size(), population2.size());
	}
	
	@Test
	public void testReadWhitespace() throws IOException {
		File file = TestUtils.createTempFile("0   1 \t 2\n\t   3 4 5 \t\n");
		Population population = PopulationIO.readObjectives(file);
		
		Assert.assertArrayEquals(new double[] {0.0, 1.0, 2.0}, population.get(0).getObjectives(), Settings.EPS);
		Assert.assertArrayEquals(new double[] {3.0, 4.0, 5.0}, population.get(1).getObjectives(), Settings.EPS);
	}

}
