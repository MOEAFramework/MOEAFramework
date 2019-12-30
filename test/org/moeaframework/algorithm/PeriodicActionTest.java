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
package org.moeaframework.algorithm;

import java.io.NotSerializableException;
import java.io.Serializable;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.algorithm.PeriodicAction.FrequencyType;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

/**
 * Tests the {@link PeriodicAction} class.
 */
public class PeriodicActionTest {
	
	public static class MockAlgorithm implements Algorithm {
		
		private int numberOfEvaluations;

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
			numberOfEvaluations += 10;
		}

		@Override
		public void evaluate(Solution solution) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int getNumberOfEvaluations() {
			return numberOfEvaluations;
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
	
	public static class TestPeriodicAction extends PeriodicAction {
		
		private int numberOfActions;

		public TestPeriodicAction(int frequency, FrequencyType frequencyType) {
			super(new MockAlgorithm(), frequency, frequencyType);
		}

		@Override
		public void doAction() {
			numberOfActions++;
		}

		public int getNumberOfActions() {
			return numberOfActions;
		}
		
	}

	@Test
	public void testEvaluationsExact() {
		test(10, FrequencyType.EVALUATIONS, 1000);
	}
	
	@Test
	public void testEvaluationsLarger() {
		test(100, FrequencyType.EVALUATIONS, 100);
	}
	
	@Test
	public void testEvaluationsSmaller() {
		test(1, FrequencyType.EVALUATIONS, 1000);
	}
	
	@Test
	public void testEvaluationsMax() {
		test(10000, FrequencyType.EVALUATIONS, 1);
	}
	
	@Test
	public void testEvaluationsUneven() {
		test(34, FrequencyType.EVALUATIONS, 250);
	}
	
	@Test
	public void testStepsExact() {
		test(1, FrequencyType.STEPS, 1000);
	}
	
	@Test
	public void testStepsLarger() {
		test(10, FrequencyType.STEPS, 100);
	}
	
	@Test
	public void testStepsMax() {
		test(1000, FrequencyType.STEPS, 1);
	}
	
	public void test(int frequency, FrequencyType type, int expected) {
		TestPeriodicAction algorithm = new TestPeriodicAction(frequency, type);
		
		for (int i=0; i<1000; i++) {
			algorithm.step();
		}
		
		Assert.assertEquals(expected, algorithm.getNumberOfActions());
	}
	
}
