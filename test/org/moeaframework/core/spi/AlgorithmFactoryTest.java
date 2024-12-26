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
package org.moeaframework.core.spi;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.mock.MockRealProblem;

/**
 * Note that most of the functionality is indirectly tested by other test functions.
 */
public class AlgorithmFactoryTest extends AbstractFactoryTest<AlgorithmProvider, AlgorithmFactory> {
	
	@Override
	public Class<AlgorithmProvider> getProviderType() {
		return AlgorithmProvider.class;
	}
	
	@Override
	public AlgorithmFactory createFactory() {
		return AlgorithmFactory.getInstance();
	}

	@Test
	public void testCustomProvider() {
		AlgorithmProvider provider = new TestAlgorithmProvider();
		
		AlgorithmFactory factory = new AlgorithmFactory();
		factory.addProvider(provider);
		
		Assert.assertNotNull(factory.getAlgorithm("testAlgorithm",
				new TypedProperties(),
				new MockRealProblem()));
	}
	
	@Test
	public void testNoProvider() {
		AlgorithmFactory factory = new AlgorithmFactory();
		
		Assert.assertThrows(ProviderNotFoundException.class, () -> factory.getAlgorithm(
				"testAlgorithm",
				new TypedProperties(),
				new MockRealProblem()));
	}
	
	@Test
	public void testDiagnosticToolAlgorithms() {
		for (String name : createFactory().getAllDiagnosticToolAlgorithms()) {
			System.out.println("Testing AlgorithmFactory on " + name);
			Assert.assertNotNull(AlgorithmFactory.getInstance().getAlgorithm(name, new MockRealProblem(2)));
			
			String swapCaseName = StringUtils.swapCase(name);
			Assert.assertNotNull(AlgorithmFactory.getInstance().getAlgorithm(swapCaseName, new MockRealProblem(2)));
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetNullInstance() {
		AlgorithmFactory.setInstance(null);
	}
	
}
