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
package org.moeaframework.algorithm.pso;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.Retryable;
import org.moeaframework.CIRunner;
import org.moeaframework.Flaky;
import org.moeaframework.algorithm.AlgorithmTest;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.problem.MockRealProblem;
import org.moeaframework.util.TypedProperties;

@RunWith(CIRunner.class)
@Retryable
public class OMOPSOTest extends AlgorithmTest {
	
	@Test
	public void testDTLZ1() throws IOException {
		assumeJMetalExists();
		test("DTLZ1_2", "OMOPSO", "OMOPSO-JMetal");
	}
	
	@Test
	@Flaky("need to investigate - differences showing up after upgrading to JMetal 5.9")
	public void testDTLZ2() throws IOException {
		assumeJMetalExists();
		test("DTLZ2_2", "OMOPSO", "OMOPSO-JMetal");
	}
	
	@Test
	@Flaky
	public void testDTLZ7() throws IOException {
		assumeJMetalExists();
		test("DTLZ7_2", "OMOPSO", "OMOPSO-JMetal");
	}
	
	@Test
	public void testUF1() throws IOException {
		assumeJMetalExists();
		test("UF1", "OMOPSO", "OMOPSO-JMetal");
	}
	
	@Test
	public void testConfiguration() {
		Problem problem = new MockRealProblem(2);	
		OMOPSO algorithm = new OMOPSO(problem, 100);
		
		Assert.assertArrayEquals(algorithm.getArchive().getComparator().getEpsilons().toArray(),
				algorithm.getConfiguration().getDoubleArray("epsilon"),
				Settings.EPS);
		
		algorithm.applyConfiguration(TypedProperties.withProperty("epsilon", "0.1"));
		Assert.assertArrayEquals(new double[] { 0.1 },
				algorithm.getArchive().getComparator().getEpsilons().toArray(),
				Settings.EPS);
		
		Assert.assertArrayEquals(new double[] { 0.1 },
				algorithm.getConfiguration().getDoubleArray("epsilon"),
				Settings.EPS);

		algorithm.applyConfiguration(TypedProperties.withProperty("epsilon", "0.1, 0.2"));
		Assert.assertArrayEquals(new double[] { 0.1, 0.2 },
				algorithm.getArchive().getComparator().getEpsilons().toArray(),
				Settings.EPS);
		
		Assert.assertArrayEquals(new double[] { 0.1, 0.2 },
				algorithm.getConfiguration().getDoubleArray("epsilon"),
				Settings.EPS);
	}

}
