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
package org.moeaframework.core.spi;

import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.analysis.sensitivity.ProblemStub;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.Variation;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.Grammar;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.core.variable.RealVariable;

/**
 * Tests the {@link OperatorFactory} class.
 */
public class OperatorFactoryTest {

	private static final String[] operators = { "sbx+pm", "hux+bf", 
		"pmx+insertion+swap", "de", "de+pm", "pcx", "spx", "undx", "pm", "um", 
		"1x+um", "2x+um", "ux+um", "gx+gm", "bx", "ptm", "bx+ptm" };
	
	private Problem problem;
	
	@Before
	public void setUp() {
		problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
	}
	
	@After
	public void tearDown() {
		problem = null;
	}
	
	@Test
	public void testCommonOperators() {
		for (String operator : operators) {
			Variation variation = OperatorFactory.getInstance().getVariation(
					operator, new Properties(), problem);
			
			test(variation);
		}
	}
	
	private void test(Variation variation) {
		RandomInitialization initialization = new RandomInitialization(problem, 
				variation.getArity());
		Solution[] parents = initialization.initialize();
		Solution[] offspring = variation.evolve(parents);
		
		Assert.assertNotNull(offspring);
	}
	
	@Test
	public void testDefaultReal() {
		Problem problem = new ProblemStub(1) {
			
			@Override
			public Solution newSolution() {
				Solution solution = new Solution(1, 0);
				solution.setVariable(0, new RealVariable(0, 1));
				return solution;
			}

		};
		
		Assert.assertNotNull(OperatorFactory.getInstance().getVariation(null, 
				new Properties(), problem));
	}
	
	@Test
	public void testDefaultBinary() {
		Problem problem = new ProblemStub(1) {
			
			@Override
			public Solution newSolution() {
				Solution solution = new Solution(1, 0);
				solution.setVariable(0, new BinaryVariable(10));
				return solution;
			}

		};
		
		Assert.assertNotNull(OperatorFactory.getInstance().getVariation(null, 
				new Properties(), problem));
	}
	
	@Test
	public void testDefaultPermutation() {
		Problem problem = new ProblemStub(1) {
			
			@Override
			public Solution newSolution() {
				Solution solution = new Solution(1, 0);
				solution.setVariable(0, new Permutation(4));
				return solution;
			}

		};
		
		Assert.assertNotNull(OperatorFactory.getInstance().getVariation(null, 
				new Properties(), problem));
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
		
		Assert.assertNotNull(OperatorFactory.getInstance().getVariation(null, 
				new Properties(), problem));
	}
	
	@Test(expected = ProviderLookupException.class)
	public void testMixedType() {
		Problem problem = new ProblemStub(5) {
			
			@Override
			public Solution newSolution() {
				Solution solution = new Solution(5, 0);
				solution.setVariable(0, new RealVariable(0, 1));
				solution.setVariable(1, new BinaryVariable(10));
				solution.setVariable(2, new Permutation(4));
				solution.setVariable(3, new Grammar(4));
				return solution;
			}

		};
		
		OperatorFactory.getInstance().getVariation(null, new Properties(), 
				problem);
	}
	
	@Test(expected = ProviderLookupException.class)
	public void testUnknownType() {
		Problem problem = new ProblemStub(1) {
			
			@Override
			public Solution newSolution() {
				Solution solution = new Solution(1, 0);
				
				solution.setVariable(0, new Variable() {

					private static final long serialVersionUID = -5453570189207466169L;

					@Override
					public Variable copy() {
						throw new UnsupportedOperationException();
					}

					@Override
					public void randomize() {
						throw new UnsupportedOperationException();
					}

				});
				
				return solution;
			}

		};
		
		OperatorFactory.getInstance().getVariation(null, new Properties(), 
				problem);
	}
	
	@Test(expected = ProviderLookupException.class)
	public void testEmptyType() {
		Problem problem = new ProblemStub(0);
		
		OperatorFactory.getInstance().getVariation(null, new Properties(), 
				problem);
	}
	
	@Test(expected = ProviderNotFoundException.class)
	public void testNonexistentOperator() {
		Problem problem = new ProblemStub(0);
		
		OperatorFactory.getInstance().getVariation("sbx+test_fake_operator", 
				new Properties(), problem);
	}

}
