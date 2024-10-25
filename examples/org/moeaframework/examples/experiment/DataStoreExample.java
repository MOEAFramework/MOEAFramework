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
import org.moeaframework.analysis.io.ResultEntry;
import org.moeaframework.analysis.io.ResultFileWriter;
import org.moeaframework.analysis.parameter.EnumeratedParameterSet;
import org.moeaframework.analysis.parameter.Enumeration;
import org.moeaframework.analysis.parameter.Parameter;
import org.moeaframework.analysis.sample.Sample;
import org.moeaframework.analysis.sample.Samples;
import org.moeaframework.analysis.store.Container;
import org.moeaframework.analysis.store.DataStore;
import org.moeaframework.analysis.store.Key;
import org.moeaframework.analysis.store.fs.FileSystemDataStore;
import org.moeaframework.analysis.store.fs.HierarchicalFileMap;
import org.moeaframework.core.indicator.Indicators;
import org.moeaframework.core.indicator.Indicators.IndicatorValues;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.problem.DTLZ.DTLZ2;
import org.moeaframework.problem.Problem;

/**
 * Demonstrates running an algorithm with different parameter samples.
 */
public class DataStoreExample {
	
	public static void main(String[] args) throws Exception {
		Problem problem = new DTLZ2(2);
		Indicators indicators = Indicators.standard(problem, NondominatedPopulation.loadReferenceSet("./pf/DTLZ2.2D.pf"));
		
		Enumeration<Integer> populationSize = Parameter.named("populationSize").asInt().range(10, 100, 10);
		Enumeration<Double> sbxRate = Parameter.named("sbx.rate").asDecimal().range(0.0, 1.0, 0.1);
		
		EnumeratedParameterSet parameters = new EnumeratedParameterSet(populationSize, sbxRate);
		Samples samples = parameters.generate();
		
		DataStore dataStore = new FileSystemDataStore(HierarchicalFileMap.at(new File("results")));
		
		for (Sample sample : samples) {
			Key key = Key.getKey(sample).extend("algorithm", "NSGAII");
			Container container = dataStore.getContainer(key);
			
			if (container.exists()) {
				System.out.println("Found existing container for " + key);
			}
			
			NSGAII algorithm = new NSGAII(problem);
			algorithm.applyConfiguration(sample);
			algorithm.run(10000);
			
			container.getBlob("configuration").store((Writer out) -> {
				algorithm.getConfiguration().display(out);
			});
			
			container.getBlob("result").store((Writer out) -> {
				try (ResultFileWriter writer = new ResultFileWriter(problem, out)) {
					writer.write(new ResultEntry(algorithm.getResult()));
				}
			});
			
			container.getBlob("indicators").store((Writer out) -> {
				IndicatorValues values = indicators.apply(algorithm.getResult());
				values.display(out);
			});
		}
	}

}
