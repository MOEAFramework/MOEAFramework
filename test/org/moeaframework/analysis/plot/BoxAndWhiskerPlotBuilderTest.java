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
package org.moeaframework.analysis.plot;

import java.io.IOException;

import org.junit.Test;
import org.moeaframework.algorithm.Algorithm;
import org.moeaframework.analysis.IndicatorStatistics;
import org.moeaframework.core.indicator.Hypervolume;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.problem.Problem;

public class BoxAndWhiskerPlotBuilderTest extends AbstractPlotTest {
	
	@Test
	public void testEmpty() {
		new BoxAndWhiskerPlotBuilder().show();
	}

	@Test
	public void test() {
		Problem problem = ProblemFactory.getInstance().getProblem("ZDT1");
		NondominatedPopulation referenceSet = ProblemFactory.getInstance().getReferenceSet("ZDT1");
		
		String[] algorithms = { "NSGAII", "eMOEA", "OMOPSO" };
		
		Hypervolume hypervolume = new Hypervolume(problem, referenceSet);
		IndicatorStatistics statistics = new IndicatorStatistics(hypervolume);
		
		for (String name : algorithms) {
			for (int i = 0; i < 10; i++) {
				Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm(name, problem);
				algorithm.run(10000);
				statistics.add(name, algorithm.getResult());
			}
		}

		new BoxAndWhiskerPlotBuilder()
				.add(statistics)
				.show();
	}
	
	public static void main(String[] args) throws IOException {
		new BoxAndWhiskerPlotBuilderTest().testEmpty();
		new BoxAndWhiskerPlotBuilderTest().test();
	}

}
