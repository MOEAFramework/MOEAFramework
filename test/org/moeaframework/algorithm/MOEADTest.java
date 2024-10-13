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
package org.moeaframework.algorithm;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.Assert;
import org.moeaframework.CIRunner;
import org.moeaframework.Retryable;
import org.moeaframework.core.Problem;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.mock.MockRealProblem;

@RunWith(CIRunner.class)
@Retryable
public class MOEADTest extends JMetalAlgorithmTest {
	
	private boolean testDRA;
	
	public MOEADTest() {
		super("MOEAD", true);
	}
	
	@Before
	public void setUp() {
		testDRA = false;
	}
	
	@Override
	public void test(String problem, String algorithm1, String algorithm2, boolean allowBetterPerformance) {
		TypedProperties properties1 = new TypedProperties();
		
		// Override JMetal's default neighborhoodSelectionProbability.  MOEADBuilder defaults to 0.1, which differs
		// from Li and Zhang's original MOEA/D paper specifying a delta of 0.9 (see section IV.A.6).
		TypedProperties properties2 = new TypedProperties();
		properties2.setDouble("neighborhoodSelectionProbability", 0.9);
		
		if (testDRA) {
			properties1.setInt("updateUtility", 30); // Match JMetal's hard-coded value
			properties2.setString("variant", "MOEADDRA");
		}
		
		test(problem, algorithm1, properties1, algorithm2, properties2,
				allowBetterPerformance, AlgorithmFactory.getInstance());
	}
	
	@Test
	public void testDTLZ1_MOEADDRA() throws IOException {
		assumeJMetalExists();
		testDRA = true;
		test("DTLZ1_2", algorithmName, algorithmName + "-JMetal", allowBetterPerformance);
	}
	
	@Test
	public void testDTLZ2_MOEADDRA() throws IOException {
		assumeJMetalExists();
		testDRA = true;
		test("DTLZ2_2", algorithmName, algorithmName + "-JMetal", allowBetterPerformance);
	}
	
	@Test
	public void testDTLZ7_MOEADDRA() throws IOException {
		assumeJMetalExists();
		testDRA = true;
		test("DTLZ7_2", algorithmName, algorithmName + "-JMetal", allowBetterPerformance);
	}
	
	@Test
	public void testUF1_MOEADDRA() throws IOException {
		assumeJMetalExists();
		testDRA = true;
		test("UF1", algorithmName, algorithmName + "-JMetal", allowBetterPerformance);
	}
	
	@Test
	public void testSelection() {
		Problem problem = new MockRealProblem();
		TypedProperties properties = new TypedProperties();
		
		//the default is de+pm
		MOEAD moead = (MOEAD)AlgorithmFactory.getInstance().getAlgorithm("MOEA/D", properties, problem);
		Assert.assertTrue(moead.useDE);
		
		//test with just de
		properties.setString("operator", "de");
		moead = (MOEAD)AlgorithmFactory.getInstance().getAlgorithm("MOEA/D", properties, problem);
		Assert.assertTrue(moead.useDE);
		
		//test with a different operator
		properties.setString("operator", "sbx+pm");
		moead = (MOEAD)AlgorithmFactory.getInstance().getAlgorithm("MOEA/D", properties, problem);
		Assert.assertFalse(moead.useDE);
	}

}
