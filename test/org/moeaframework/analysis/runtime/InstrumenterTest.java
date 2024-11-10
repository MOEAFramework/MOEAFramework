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
package org.moeaframework.analysis.runtime;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.algorithm.Algorithm;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.indicator.StandardIndicator;
import org.moeaframework.core.operator.Variation;
import org.moeaframework.core.operator.real.UM;
import org.moeaframework.core.population.Population;
import org.moeaframework.core.selection.TournamentSelection;
import org.moeaframework.mock.MockAlgorithm;
import org.moeaframework.mock.MockAlgorithmWithExtensions;
import org.moeaframework.mock.MockRealProblem;
import org.moeaframework.problem.Problem;
import org.moeaframework.problem.DTLZ.DTLZ2;

public class InstrumenterTest {
	
	public static class TestCollector implements Collector {

		private final Set<Object> instrumentedObjects;
		
		public TestCollector() {
			super();
			instrumentedObjects = new HashSet<Object>();
		}

		public Set<Object> getInstrumentedObjects() {
			return instrumentedObjects;
		}

		@Override
		public AttachPoint getAttachPoint() {
			return new AttachPoint() {

				@Override
				public boolean matches(Stack<Object> parents, Object object) {
					Class<?> type = object.getClass();
					return (type.getPackage() != null) && type.getPackage().getName().startsWith("org.moeaframework");
				}
				
			};
		}

		@Override
		public Collector attach(Object object) {
			instrumentedObjects.add(object);
			return this;
		}

		@Override
		public void collect(Observation observation) {
			//do nothing
		}
		
	}
	
	public static class SimpleAlgorithm extends MockAlgorithmWithExtensions {
		
		protected Variation variation = new UM(1.0);
		
		protected TournamentSelection selection = new TournamentSelection(2, new ParetoDominanceComparator());
		
		protected Population population = null;
		
		protected int integer = 5;
		
		protected Problem problem = new TestProblem();
		
	}
	
	public static class TestProblem extends MockRealProblem {
		
		protected Object testObject = new Object();
		
	}
	
	private TestCollector collector;
	
	@Before
	public void setUp() {
		collector = new TestCollector();
	}
	
	@After
	public void tearDown() {
		collector = null;
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInstrumentNull() {
		new Instrumenter().attach(collector).instrument(null);
	}
	
	@Test
	public void testInstrumentEmptyAlgorithm() {
		MockAlgorithm algorithm = new MockAlgorithmWithExtensions();
		new Instrumenter().attach(collector).instrument(algorithm);
		
		Set<Object> instrumentedObjects = collector.getInstrumentedObjects();
		Assert.assertSize(2, instrumentedObjects);
		Assert.assertContains(instrumentedObjects, algorithm);
		Assert.assertContains(instrumentedObjects, algorithm.getExtensions());
	}
	
	@Test
	public void testInstrumentSimpleAlgorithm() {
		SimpleAlgorithm algorithm = new SimpleAlgorithm();
		new Instrumenter().attach(collector).instrument(algorithm);
		
		Set<Object> instrumentedObjects = collector.getInstrumentedObjects();
		
		Assert.assertSize(8, instrumentedObjects);
		Assert.assertContains(instrumentedObjects, algorithm);
		Assert.assertContains(instrumentedObjects, algorithm.variation);
		Assert.assertContains(instrumentedObjects, algorithm.selection);
		Assert.assertContains(instrumentedObjects, algorithm.selection.getComparator());
		Assert.assertContains(instrumentedObjects, algorithm.problem);
		Assert.assertContains(instrumentedObjects, algorithm.getExtensions());
	}
	
	@Test
	public void testSynthetic() {
		Algorithm algorithm = new MockAlgorithmWithExtensions() {
			//anonymous class, which will contain a synthetic field "this" pointing to InstrumenterTest
		}; 
		
		new Instrumenter().attach(collector).instrument(algorithm);
		
		Set<Object> instrumentedObjects = collector.getInstrumentedObjects();
		Assert.assertSize(2, instrumentedObjects);
		Assert.assertContains(instrumentedObjects, algorithm);
		Assert.assertContains(instrumentedObjects, algorithm.getExtensions());
	}
	
	@Test
	public void testExcludedPackages() {
		SimpleAlgorithm algorithm = new SimpleAlgorithm();
		new Instrumenter().addExcludedPackage("org.moeaframework").attach(collector).instrument(algorithm);
		
		Set<Object> instrumentedObjects = collector.getInstrumentedObjects();
		
		Assert.assertSize(0, instrumentedObjects);
	}
	
	@Test
	public void test() {
		Problem problem = new DTLZ2(2);
		
		Instrumenter instrumenter = new Instrumenter()
				.withReferenceSet(new File("pf/DTLZ2.2D.pf"))
				.attachAll();
		
		NSGAII algorithm = new NSGAII(problem);
		InstrumentedAlgorithm<?> instrumentedAlgorithm = instrumenter.instrument(algorithm);
		
		instrumentedAlgorithm.run(1000);
		
		Observations observations = instrumentedAlgorithm.getObservations();
				
		Assert.assertSize(16, observations.keys());

		for (StandardIndicator indicator : StandardIndicator.values()) {
			Assert.assertTrue("Missing observation for " + indicator.name(), observations.keys().contains(indicator.name()));
		}
	}
	
}
