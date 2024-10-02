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
package org.moeaframework.snippet;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.moeaframework.Analyzer;
import org.moeaframework.Executor;
import org.moeaframework.Instrumenter;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.analysis.collector.InstrumentedAlgorithm;
import org.moeaframework.analysis.collector.Observations;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.mock.MockRealProblem;
import org.moeaframework.problem.CEC2009.UF1;

@SuppressWarnings("unused")
public class ExperimentSnippet {

	@Test
	public void executor() {
		NondominatedPopulation result = new Executor()
		        .withProblem("UF1")
		        .withAlgorithm("NSGAII")
		        .withMaxEvaluations(10000)
		        .run();
	}
	
	@Test
	public void executorWithCustomProblem() {
		NondominatedPopulation result = new Executor()
		        .withProblem(new MyProblem())
		        .withAlgorithm("NSGAII")
		        .withMaxEvaluations(10000)
		        .run();
	}
	
	@Test
	public void executorWithProperties() {
		NondominatedPopulation result = new Executor()
		        .withProblem("UF1")
		        .withAlgorithm("NSGAII")
		        .withProperty("populationSize", 250)
		        .withProperty("operator", "pcx+um")
		        .withMaxEvaluations(10000)
		        .run();
	}
	
	@Test
	public void analyzer() {
		Executor executor = new Executor()
		        .withProblem("UF1")
		        .withAlgorithm("NSGAII")
		        .withMaxEvaluations(10000);

		Analyzer analyzer = new Analyzer()
		        .withSameProblemAs(executor)
		        .includeHypervolume()
		        .includeGenerationalDistance();
				
		analyzer.addAll("NSGAII", executor.runSeeds(50));
		analyzer.display();
	}
	
	@Test
	public void saveCSV() throws IOException {
		Problem problem = new UF1();
		
		Instrumenter instrumenter = new Instrumenter()
		    .withProblem(problem)
		    .withReferenceSet(new File("./pf/UF1.dat"))
		    .withFrequency(100)
		    .attachHypervolumeCollector()
		    .attachGenerationalDistanceCollector();
				
		NSGAII algorithm = new NSGAII(problem);
				
		InstrumentedAlgorithm<NSGAII> instrumentedAlgorithm = instrumenter.instrument(algorithm);
		instrumentedAlgorithm.run(10000);
				
		Observations observations = instrumentedAlgorithm.getObservations();
		
		instrumenter.getObservations().saveCSV(new File("NSGAII_UF1_Runtime.csv"));
	}
	
	private class MyProblem extends MockRealProblem {
		
	}
	
}
