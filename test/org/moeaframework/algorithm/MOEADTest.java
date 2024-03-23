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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.CIRunner;
import org.moeaframework.Retryable;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.mock.MockRealProblem;
import org.moeaframework.util.TypedProperties;

@RunWith(CIRunner.class)
@Retryable
public class MOEADTest extends JMetalAlgorithmTest {
	
	public MOEADTest() {
		super("MOEAD", true);
	}
	
	@Test
	public void testSelection() {
		MOEAD moead = null;
		
		Problem problem = new MockRealProblem();
		TypedProperties properties = new TypedProperties();
		
		//the default is de+pm
		moead = (MOEAD)AlgorithmFactory.getInstance().getAlgorithm("MOEA/D", properties, problem);
		
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
