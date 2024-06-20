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
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.moeaframework.core.indicator.StandardIndicator;
import org.moeaframework.experiment.Experiment;
import org.moeaframework.experiment.OutputHandler;
import org.moeaframework.experiment.PartitionedSamples;
import org.moeaframework.experiment.Sample;
import org.moeaframework.experiment.Samples;
import org.moeaframework.experiment.job.DescriptiveStatisticsJob;
import org.moeaframework.experiment.job.EndOfRunJob;
import org.moeaframework.experiment.job.EvaluateIndicatorsJob;
import org.moeaframework.experiment.job.IndicatorPlotJob;
import org.moeaframework.experiment.job.MergeApproximationSetsJob;
import org.moeaframework.experiment.job.StatisticalSignificanceJob;
import org.moeaframework.experiment.parameter.EnumeratedParameterSet;
import org.moeaframework.experiment.parameter.Parameter;
import org.moeaframework.experiment.store.DataStore;
import org.moeaframework.experiment.store.Key;
import org.moeaframework.experiment.store.fs.FileSystemDataStore;
import org.moeaframework.experiment.store.fs.HierarchicalFileMap;
import org.moeaframework.experiment.store.schema.Field;
import org.moeaframework.experiment.store.schema.Schema;

/**
 * The DataStore provides a structured way to store a large number of files from an experiment.  By default, it
 * saves to the file system, but can be extended with other storage solutions.
 */
public class DataStoreExample {
	
	private static final Logger LOGGER = OutputHandler.getLogger(DataStoreExample.class.getName());
	
	public static void main(String[] args) throws Exception {
		String problemName = "DTLZ2_2";
		
		Schema schema = Schema.of(
				Field.named("algorithm").asString(),
				Field.named("populationSize").asInt(),
				Field.named("seed").asInt());
		
		EnumeratedParameterSet parameterSet = new EnumeratedParameterSet(
				schema,
				Parameter.named("problem").asConstant(problemName),
				Parameter.named("maxEvaluations").asConstant(25000),
				Parameter.named("algorithm").withValues("NSGAII", "GDE3", "MOEA/D"),
				Parameter.named("populationSize").asInt().range(20, 500, 10),
				Parameter.named("seed").asInt().range(0, 10));

		DataStore dataStore = new FileSystemDataStore(schema, HierarchicalFileMap.at(new File("results")));
		Experiment experiment = new Experiment(dataStore, Executors.newFixedThreadPool(2), LOGGER);
		
		Samples samples = parameterSet.generate();
		samples.save(dataStore);
		samples.load(dataStore);
		
		for (Sample sample : samples) {
			Key key = Key.from(schema, sample);
			
			experiment.submit(new EndOfRunJob(key, sample));
			experiment.submit(new EvaluateIndicatorsJob(key, problemName));
		}

		for (PartitionedSamples partition : samples.partition("populationSize")) {
			experiment.submit(new MergeApproximationSetsJob(partition.getPartitionKey(), partition, problemName));
			experiment.submit(new EvaluateIndicatorsJob(partition.getPartitionKey(), problemName));
			experiment.submit(new DescriptiveStatisticsJob(partition.getPartitionKey(), partition));
		}
		
		for (PartitionedSamples algorithm : samples.partition("algorithm")) {
			experiment.submit(new StatisticalSignificanceJob(algorithm.getPartitionKey(),
					algorithm.partition("populationSize")));
		}
		
		for (StandardIndicator indicator : StandardIndicator.values()) {
			experiment.submit(new IndicatorPlotJob(Key.of(),
					samples.partition("algorithm"),
					"populationSize",
					indicator));
		}
		
		experiment.shutdownAndWait();
	}

}
