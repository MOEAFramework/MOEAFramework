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
package org.moeaframework.examples.experiment;

import java.io.File;
import java.io.IOException;

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.analysis.parameter.Enumeration;
import org.moeaframework.analysis.parameter.Parameter;
import org.moeaframework.analysis.parameter.ParameterSet;
import org.moeaframework.analysis.sample.Sample;
import org.moeaframework.analysis.sample.Samples;
import org.moeaframework.analysis.store.Blob;
import org.moeaframework.analysis.store.Container;
import org.moeaframework.analysis.store.DataStore;
import org.moeaframework.analysis.store.Reference;
import org.moeaframework.analysis.store.fs.FileSystemDataStore;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.indicator.Indicators;
import org.moeaframework.core.indicator.Indicators.IndicatorValues;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.core.population.Population;
import org.moeaframework.problem.DTLZ.DTLZ2;
import org.moeaframework.problem.Problem;
import org.moeaframework.util.Timer;

/**
 * Demonstrates using a data store to save results.  The data store defines "containers" and "blobs", which for a file
 * system are equivalent to directories and files.  We map each unique algorithm configuration to a separate container,
 * storing the approximation set and indicator values.
 */
public class DataStoreExample {
	
	public static void main(String[] args) throws IOException {
		Problem problem = new DTLZ2(2);
		Indicators indicators = Indicators.standard(problem, NondominatedPopulation.load("./pf/DTLZ2.2D.pf"));
		
		Enumeration<Integer> populationSize = Parameter.named("populationSize").asInt().range(10, 100, 10);
		Enumeration<Double> sbxRate = Parameter.named("sbx.rate").asDouble().range(0.0, 1.0, 0.1);
		Enumeration<Long> seed = Parameter.named("seed").asLong().rangeExclusive(0, 10);
		
		ParameterSet parameters = new ParameterSet(populationSize, sbxRate, seed);
		Samples samples = parameters.enumerate();
		
		DataStore dataStore = new FileSystemDataStore(new File("results"));
		Timer timer = Timer.startNew();
				
		for (Sample sample : samples) {
			Reference reference = sample.getReference();
			Container container = dataStore.getContainer(reference);
			
			Blob resultBlob = container.getBlob("result");
			Blob indicatorBlob = container.getBlob("indicators");
			
			System.out.println("Processing " + reference + "...");
			
			if (!resultBlob.exists()) {
				System.out.print("    Running algorithm...");
				
				PRNG.setSeed(sample.getLong("seed"));
				
				NSGAII algorithm = new NSGAII(problem);
				algorithm.applyConfiguration(sample);
				algorithm.run(10000);
				resultBlob.storePopulation(algorithm.getResult());
				
				System.out.println("done.");
			}
		
			if (!indicatorBlob.exists()) {
				System.out.print("    Evaluating indicators...");
				
				Population result = resultBlob.extractPopulation();
				IndicatorValues values = indicators.apply(new NondominatedPopulation(result));
				indicatorBlob.storeText(values);
				
				System.out.println("done.");
			}
			
			System.out.println("    Finished!");
		}
		
		timer.stop();
		System.out.println("Finished in " + timer.getElapsedTime() + "s");
	}

}
