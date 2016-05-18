/* Copyright 2009-2016 David Hadka
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

import java.util.Properties;

import org.junit.Test;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.ProviderNotFoundException;
import org.moeaframework.problem.MockBinaryProblem;

/**
 * Tests the {@link RVEA} class.
 */
public class RVEATest {
	
	/**
	 * Ensure RVEA can not be applied to problems with fewer than two
	 * objectives.
	 */
	@Test(expected=ProviderNotFoundException.class)
	public void testInitialConditions() {
		Problem problem = new MockBinaryProblem();
		AlgorithmFactory.getInstance().getAlgorithm("RVEA", new Properties(), problem);
	}

}
