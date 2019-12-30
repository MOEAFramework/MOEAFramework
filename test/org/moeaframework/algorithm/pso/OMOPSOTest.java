/* Copyright 2009-2019 David Hadka
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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.RetryOnTravis;
import org.moeaframework.TravisRunner;
import org.moeaframework.algorithm.AlgorithmTest;

/**
 * Tests the {@link OMOPSO} class.
 */
@RunWith(TravisRunner.class)
@RetryOnTravis
public class OMOPSOTest extends AlgorithmTest {
	
	@BeforeClass
	public static void setUp() {
		OMOPSO.TESTING_MODE = true;
	}
	
	@AfterClass
	public static void tearDown() {
		OMOPSO.TESTING_MODE = false;
	}
	
	@Test
	public void testDTLZ1() throws IOException {
		test("DTLZ1_2", "OMOPSO", "OMOPSO-JMetal");
	}
	
	@Test
	public void testDTLZ2() throws IOException {
		test("DTLZ2_2", "OMOPSO", "OMOPSO-JMetal");
	}
	
	@Test
	public void testDTLZ7() throws IOException {
		test("DTLZ7_2", "OMOPSO", "OMOPSO-JMetal");
	}
	
	@Test
	public void testUF1() throws IOException {
		test("UF1", "OMOPSO", "OMOPSO-JMetal");
	}

}
