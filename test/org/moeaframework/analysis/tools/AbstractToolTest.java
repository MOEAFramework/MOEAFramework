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
package org.moeaframework.analysis.tools;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.moeaframework.Assert;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.AlgorithmFactoryTestWrapper;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.core.spi.ProblemFactoryTestWrapper;

@Ignore("Abstract test class")
public abstract class AbstractToolTest {
	
	public static final String COMPLETE_RESULT_FILE = """
			# Problem = DTLZ2_2
			# Variables = 11
			# Objectives = 2
			//NFE=100
			//ElapsedTime=0.214
			0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.25 0.75
			1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 0.75 0.25
			#
			//NFE=200
			//ElapsedTime=0.209186
			0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.25 0.75
			1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 0.75 0.25
			#
			""";
	
	public static final String EMPTY_RESULT_FILE = """
			# Problem = DTLZ2_2
			# Variables = 11
			# Objectives = 2
			""";
	
	public static final String PARAMETER_DESCRIPTION_FILE = """
			populationSize int 10 100
			sbx.rate double 0.0 1.0
			""";
	
	public static final String PARAMETER_SAMPLES_FILE = """
			10 0.0
			10 0.5
			10 1.0
			100 0.0
			100 0.5
			100 1.0
			""";
	
	protected AlgorithmFactoryTestWrapper algorithmFactory;
	
	protected ProblemFactoryTestWrapper problemFactory;
	
	@Before
	public void setUp() {
		algorithmFactory = new AlgorithmFactoryTestWrapper();
		problemFactory = new ProblemFactoryTestWrapper();
		
		AlgorithmFactory.setInstance(algorithmFactory);
		ProblemFactory.setInstance(problemFactory);
	}
	
	@After
	public void tearDown() {
		try {
			Assert.assertEquals("Number of calls to close problem does not match number of creations",
					problemFactory.getCreateCount(), problemFactory.getCloseCount());
			
			Assert.assertEquals("Number of calls to terminate algorithm does not match number of creations",
					algorithmFactory.getCreateCount(), algorithmFactory.getTerminateCount());
		} finally {
			algorithmFactory = null;
			problemFactory = null;
			
			AlgorithmFactory.setInstance(new AlgorithmFactory());
			ProblemFactory.setInstance(new ProblemFactory());
		}
	}
	
	public void assertAlgorithmTerminationCount(int expected) {
		Assert.assertEquals("Unexpected number of calls to terminate algorithm", expected, algorithmFactory.getTerminateCount());
	}
	
	public void assertProblemCloseCount(int expected) {
		Assert.assertEquals("Unexpected number of calls to close problem", expected, problemFactory.getCloseCount());
	}
	
}
