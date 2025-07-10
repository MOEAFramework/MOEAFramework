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
package org.moeaframework.util.mvc;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.Assume;
import org.moeaframework.Wait;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.problem.CEC2009.UF1;
import org.moeaframework.problem.Problem;

public class ExampleUITest {
	
	static class TestExampleUI extends ExampleUI<NSGAII> {
		
		private static final long serialVersionUID = 9061186330605849493L;
		
		private int updateCalls;
		
		private final JLabel label;

		public TestExampleUI(String title, NSGAII algorithm) {
			super(title, algorithm);
			label = new JLabel("Iteration: <Not Started>", SwingConstants.CENTER);
			
			setMinimumSize(new Dimension(100, 100));
			getContentPane().setLayout(new BorderLayout());
			getContentPane().add(label, BorderLayout.CENTER);
		}

		@Override
		public void update(NSGAII algorithm, int iteration) {
			label.setText("Iteration: " + iteration);
			updateCalls++;
		}
		
		public int getUpdateCalls() {
			return updateCalls;
		}
		
	}
	
	@Test
	public void test() throws InterruptedException {
		Assume.assumeHasDisplay();
		
		Problem problem = new UF1();
		NSGAII algorithm = new NSGAII(problem);
		
		TestExampleUI example = UI.showAndWait(() -> new TestExampleUI("Test", algorithm));
		Assert.assertTrue(example.isVisible());
		
		example.start();
		
		Wait.sleepUntil(() -> algorithm.getNumberOfEvaluations() >= 1000);
		
		example.stop();
		example.dispose();
		
		Wait.sleepUntil(() -> algorithm.isTerminated());
		
		UI.clearEventQueue();
		Assert.assertEquals(algorithm.getNumberOfEvaluations() / algorithm.getInitialPopulationSize() , example.getUpdateCalls());
	}
	
}
