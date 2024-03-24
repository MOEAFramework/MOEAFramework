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
package org.moeaframework.core.spi;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.configuration.ConfigurationException;
import org.moeaframework.core.initialization.RandomInitialization;
import org.moeaframework.core.operator.DefaultOperators;
import org.moeaframework.core.variable.Grammar;
import org.moeaframework.mock.MockBinaryProblem;
import org.moeaframework.mock.MockMixedBinaryProblem;
import org.moeaframework.mock.MockMultiTypeProblem;
import org.moeaframework.mock.MockPermutationProblem;
import org.moeaframework.mock.MockRealProblem;
import org.moeaframework.mock.MockUnsupportedVariable;
import org.moeaframework.problem.ProblemStub;
import org.moeaframework.util.TypedProperties;

public class OperatorFactoryTest extends AbstractFactoryTest<OperatorProvider, OperatorFactory> {

	@Override
	public Class<OperatorProvider> getProviderType() {
		return OperatorProvider.class;
	}
	
	@Override
	public OperatorFactory createFactory() {
		return OperatorFactory.getInstance();
	}
	
	@Test
	public void testOperators() {
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		
		for (String operator : new DefaultOperators().getRegisteredOperators()) {
			System.out.println("Testing " + operator);
			
			try {
				Variation variation = createFactory().getVariation(operator, new TypedProperties(), problem);
				test(problem, variation);
			} catch (ConfigurationException e) {
				// this operator is renamed and displays an error
				if (!operator.equalsIgnoreCase("bx")) {
					throw e;
				}
			}
		}
	}
	
	private void test(Problem problem, Variation variation) {
		RandomInitialization initialization = new RandomInitialization(
				ProblemFactory.getInstance().getProblem("DTLZ2_2"));
		
		Solution[] parents = initialization.initialize(variation.getArity());
		Solution[] offspring = variation.evolve(parents);
		
		Assert.assertNotNull(offspring);
	}
	
	@Test
	public void testDefaultReal() {
		Problem problem = new MockRealProblem();		
		Assert.assertNotNull(createFactory().getVariation(null, new TypedProperties(), problem));
	}
	
	@Test
	public void testDefaultBinary() {
		Problem problem = new MockBinaryProblem();
		Assert.assertNotNull(createFactory().getVariation(null, new TypedProperties(), problem));
	}
	
	@Test
	public void testCombiningBinary() {
		Problem problem = new MockMixedBinaryProblem();
		Assert.assertNotNull(createFactory().getVariation(null, new TypedProperties(), problem));
	}
	
	@Test
	public void testDefaultPermutation() {
		Problem problem = new MockPermutationProblem();
		Assert.assertNotNull(createFactory().getVariation(null, new TypedProperties(), problem));
	}
	
	@Test
	public void testDefaultGrammar() {
		Problem problem = new ProblemStub(1) {
			
			@Override
			public Solution newSolution() {
				Solution solution = new Solution(1, 0);
				solution.setVariable(0, new Grammar(4));
				return solution;
			}

		};
		
		Assert.assertNotNull(createFactory().getVariation(null, new TypedProperties(), problem));
	}
	
	@Test
	public void testMixedType() {
		Problem problem = new MockMultiTypeProblem();
		Assert.assertNull(createFactory().getVariation(null, new TypedProperties(), problem));
	}
	
	@Test
	public void testUnknownType() {
		Problem problem = new ProblemStub(1) {
			
			@Override
			public Solution newSolution() {
				Solution solution = new Solution(1, 0);
				solution.setVariable(0, new MockUnsupportedVariable());
				return solution;
			}

		};
		
		Assert.assertNull(createFactory().getVariation(null, new TypedProperties(), problem));
	}
	
	@Test
	public void testEmptyType() {
		Problem problem = new ProblemStub(0);
		Assert.assertNull(createFactory().getVariation(null, new TypedProperties(), problem));
	}
	
	@Test(expected = ProviderNotFoundException.class)
	public void testNonexistentOperator() {
		Problem problem = new ProblemStub(0);
		createFactory().getVariation("sbx+test_fake_operator", new TypedProperties(), problem);
	}

}
