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
package org.moeaframework;

import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.analysis.collector.Accumulator;
import org.moeaframework.analysis.collector.AttachPoint;
import org.moeaframework.analysis.collector.Collector;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.operator.TournamentSelection;
import org.moeaframework.core.operator.real.UM;
import org.moeaframework.problem.MockRealProblem;

/**
 * Tests the {@link Instrumenter} class.
 */
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
					
					return (type.getPackage() != null) && 
							type.getPackage().getName().startsWith(
									"org.moeaframework");
				}
				
			};
		}

		@Override
		public Collector attach(Object object) {
			instrumentedObjects.add(object);
			return this;
		}

		@Override
		public void collect(Accumulator accumulator) {
			//do nothing
		}
		
	}
	
	public static class EmptyAlgorithm implements Algorithm {

		@Override
		public Problem getProblem() {
			throw new UnsupportedOperationException();
		}

		@Override
		public NondominatedPopulation getResult() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void step() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void evaluate(Solution solution) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int getNumberOfEvaluations() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isTerminated() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void terminate() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Serializable getState() throws NotSerializableException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setState(Object state) throws NotSerializableException {
			throw new UnsupportedOperationException();
		}
		
	}
	
	public static class SimpleAlgorithm extends EmptyAlgorithm {
		
		protected Variation variation = new UM(1.0);
		
		protected TournamentSelection selection = new TournamentSelection(2,
				new ParetoDominanceComparator());
		
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

	@Test
	public void testInstrumentNull() {
		new Instrumenter().attach(collector).instrument(null);
		
		Assert.assertEquals(0, collector.getInstrumentedObjects().size());
	}
	
	@Test
	public void testInstrumentEmptyAlgorithm() {
		EmptyAlgorithm algorithm = new EmptyAlgorithm();
		new Instrumenter().attach(collector).instrument(algorithm);
		
		Set<Object> instrumentedObjects = collector.getInstrumentedObjects();
		Assert.assertEquals(1, instrumentedObjects.size());
		Assert.assertTrue(instrumentedObjects.contains(algorithm));
	}
	
	@Test
	public void testInstrumentSimpleAlgorithm() {
		SimpleAlgorithm algorithm = new SimpleAlgorithm();
		new Instrumenter().attach(collector).instrument(algorithm);
		
		Set<Object> instrumentedObjects = collector.getInstrumentedObjects();
		
		Assert.assertEquals(7, instrumentedObjects.size());
		Assert.assertTrue(instrumentedObjects.contains(algorithm));
		Assert.assertTrue(instrumentedObjects.contains(algorithm.variation));
		Assert.assertTrue(instrumentedObjects.contains(algorithm.selection));
		Assert.assertTrue(instrumentedObjects.contains(algorithm.selection.getComparator()));
		Assert.assertTrue(instrumentedObjects.contains(algorithm.problem));
	}
	
	@Test
	public void testRecursion() {
		Algorithm algorithm = new EmptyAlgorithm() {
			//anonymous class, which will contain reference to InstrumenterTest
		}; 
		
		new Instrumenter().attach(collector).instrument(algorithm);
		
		Set<Object> instrumentedObjects = collector.getInstrumentedObjects();
		Assert.assertEquals(3, instrumentedObjects.size());
		Assert.assertTrue(instrumentedObjects.contains(algorithm));
		Assert.assertTrue(instrumentedObjects.contains(this));
		Assert.assertTrue(instrumentedObjects.contains(collector));
	}
	
	@Test
	public void testExcludedPackages() {
		SimpleAlgorithm algorithm = new SimpleAlgorithm();
		new Instrumenter().addExcludedPackage("org.moeaframework").attach(collector).instrument(algorithm);
		
		Set<Object> instrumentedObjects = collector.getInstrumentedObjects();
		
		Assert.assertEquals(0, instrumentedObjects.size());
	}
	
	@Test
	public void testWithExecutor() {
		Instrumenter instrumenter = new Instrumenter()
				.withProblem("DTLZ2_2")
				.attachAll();
		
		Executor executor = new Executor()
				.withProblem("DTLZ2_2")
				.withAlgorithm("NSGAII")
				.withMaxEvaluations(1000)
				.withInstrumenter(instrumenter);
		
		executor.run();
		
		Accumulator accumulator = instrumenter.getLastAccumulator();
		
		Assert.assertEquals(13, accumulator.keySet().size());
	}
	
}
