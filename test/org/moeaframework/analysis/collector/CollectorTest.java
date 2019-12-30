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
package org.moeaframework.analysis.collector;

import java.util.Properties;

import org.junit.Assert;
import org.moeaframework.Instrumenter;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.ProblemFactory;

/**
 * Helper functions for testing collectors.  This primarily ensures that the
 * collectors attach to the correct algorithms, and that a single collector
 * does not attach to multiple objects.
 */
public class CollectorTest {
	
	private static class TestCollector implements Collector {
		
		private final Collector collector;
		
		private int numberOfAttachments;
		
		public TestCollector(Collector collector) {
			super();
			this.collector = collector;
		}

		@Override
		public AttachPoint getAttachPoint() {
			return collector.getAttachPoint();
		}

		@Override
		public Collector attach(Object object) {
			numberOfAttachments++;
			return collector.attach(object);
		}

		@Override
		public void collect(Accumulator accumulator) {
			collector.collect(accumulator);
		}

		public int getNumberOfAttachments() {
			return numberOfAttachments;
		}
		
	}
	
	protected void test(String algorithmName, Collector collector, 
			boolean willAttach) {
		Problem problem = null;
		Accumulator accumulator = null;
		int numberOfEvaluations = 1000;
		String problemName = "DTLZ2_2";
		TestCollector testCollector = new TestCollector(collector);
		
		Assert.assertNotNull(collector.getAttachPoint());

		try {
			problem = ProblemFactory.getInstance().getProblem(problemName);

			Instrumenter instrumenter = new Instrumenter()
					.withFrequency(100)
					.attach(testCollector);

			Properties properties = new Properties();
			properties.setProperty("maxEvaluations", 
					Integer.toString(numberOfEvaluations));

			Algorithm algorithm = null;

			try {
				algorithm = AlgorithmFactory.getInstance().getAlgorithm(
						algorithmName, properties, problem);

				InstrumentedAlgorithm instrumentedAlgorithm = 
					instrumenter.instrument(algorithm);

				while (instrumentedAlgorithm.getNumberOfEvaluations() < 
						numberOfEvaluations) {
					instrumentedAlgorithm.step();
				}
				
				accumulator = instrumentedAlgorithm.getAccumulator();
			} finally {
				if (algorithm != null) {
					algorithm.terminate();
				}
			}
		} finally {
			if (problem != null) {
				problem.close();
			}
		}
		
		Assert.assertEquals(willAttach ? 1 : 0, 
				testCollector.getNumberOfAttachments());
		Assert.assertNotNull(accumulator);
		
		if (willAttach) {
			for (String key : accumulator.keySet()) {
				Assert.assertTrue(accumulator.size(key) > 0);
				Assert.assertEquals(accumulator.size("NFE"), 
						accumulator.size(key));
			}
		}
	}

}
