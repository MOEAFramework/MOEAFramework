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
package org.moeaframework.examples.experiment;

import java.io.File;
import java.io.Writer;

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.algorithm.NSGAIII;
import org.moeaframework.analysis.parameter.Enumeration;
import org.moeaframework.analysis.parameter.Parameter;
import org.moeaframework.analysis.parameter.ParameterSet;
import org.moeaframework.analysis.sample.Sample;
import org.moeaframework.analysis.sample.Samples;
import org.moeaframework.analysis.store.Container;
import org.moeaframework.analysis.store.DataStore;
import org.moeaframework.analysis.store.Reference;
import org.moeaframework.analysis.store.fs.FileSystemDataStore;
import org.moeaframework.core.indicator.Indicators;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.problem.DTLZ.DTLZ2;
import org.moeaframework.problem.Problem;
import org.moeaframework.util.format.TableFormat;

/**
 * Demonstrates using a data store to save results.
 */
public class DataStoreExample {
	
	public static void main(String[] args) throws Exception {
		Problem problem = new DTLZ2(2);
		Indicators indicators = Indicators.standard(problem, NondominatedPopulation.load("./pf/DTLZ2.2D.pf"));
		
		Enumeration<Integer> populationSize = Parameter.named("populationSize").asInt().range(10, 100, 10);
		Enumeration<Double> sbxRate = Parameter.named("sbx.rate").asDecimal().range(0.0, 1.0, 0.1);
		
		ParameterSet parameters = new ParameterSet(populationSize, sbxRate);
		Samples samples = parameters.enumerate();
		
		DataStore dataStore = new FileSystemDataStore(new File("results"));
		
		for (Sample sample : samples) {
			NSGAII algorithm = new NSGAIII(problem);
			algorithm.applyConfiguration(sample);
			
			// Locate the container for this algorithm
			Reference reference = Reference.of(algorithm.getConfiguration());
			Container container = dataStore.getContainer(reference);

			if (!container.exists()) {
				System.out.println("Evaluating " + reference);
				algorithm.run(10000);
								
				container.getBlob("result").store((Writer out) -> {
					algorithm.getResult().save(out);
				});
				
				container.getBlob("indicators").store((Writer out) -> {
					indicators.apply(algorithm.getResult()).save(TableFormat.Plaintext, out);
				});
			} else {
				System.out.println("Skipping " + reference + ", found in data store");
			}
		}
	}

}
