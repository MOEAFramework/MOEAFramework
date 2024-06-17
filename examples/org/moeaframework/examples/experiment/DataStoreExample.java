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
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.moeaframework.analysis.DefaultEpsilons;
import org.moeaframework.core.Epsilons;
import org.moeaframework.experiment.Experiment;
import org.moeaframework.experiment.Samples;
import org.moeaframework.experiment.job.EndOfRunJob;
import org.moeaframework.experiment.job.EvaluateIndicatorsJob;
import org.moeaframework.experiment.job.MergeApproximationSetsJob;
import org.moeaframework.experiment.job.PrintJob;
import org.moeaframework.experiment.job.StatisticsJob;
import org.moeaframework.experiment.parameter.EnumeratedParameterSet;
import org.moeaframework.experiment.parameter.Parameter;
import org.moeaframework.experiment.store.DataStore;
import org.moeaframework.experiment.store.DataType;
import org.moeaframework.experiment.store.Key;
import org.moeaframework.experiment.store.fs.FileSystemDataStore;
import org.moeaframework.experiment.store.fs.HierarchicalFileMap;
import org.moeaframework.experiment.store.schema.Field;
import org.moeaframework.experiment.store.schema.Schema;
import org.moeaframework.util.TypedProperties;

/**
 * The DataStore provides a structured way to store a large number of files from an experiment.  By default, it
 * saves to the file system, but can be extended with other storage solutions.
 */
public class DataStoreExample {
	
	private static final Logger LOGGER = Logger.getLogger(DataStoreExample.class.getSimpleName());
	
	// Locking on files to prevent duplicate writes
	// DataType defines type and I/O methods
	
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
				Parameter.named("algorithm").withValues("NSGAII", "eMOEA", "GDE3"),
				Parameter.named("populationSize").withValues(25, 50, 100, 150, 200, 250, 300, 350, 400, 450, 500),
				Parameter.named("seed").asInt().range(0, 14));

		DataStore dataStore = new FileSystemDataStore(schema, HierarchicalFileMap.at(new File("results")));
		Experiment experiment = new Experiment(dataStore, Executors.newFixedThreadPool(4), LOGGER);
		
		Samples samples = parameterSet.generate();
		samples.save(dataStore);
		
		for (TypedProperties input : samples) {
			Key key = Key.from(schema, input);
			
			experiment.submit(new EndOfRunJob(key, input));
			experiment.submit(new EvaluateIndicatorsJob(key, problemName));
		}
				
		Epsilons epsilons = DefaultEpsilons.getInstance().getEpsilons(problemName);
		
		// Partition at populationSize so we can aggregate across seeds
		Map<Key, Samples> aggregates = samples.partition("populationSize");
		
		for (Map.Entry<Key, Samples> entry : aggregates.entrySet()) {
			experiment.submit(new MergeApproximationSetsJob(entry.getKey(), entry.getValue().getKeys(), epsilons));
			experiment.submit(new EvaluateIndicatorsJob(entry.getKey(), problemName));
			experiment.submit(new StatisticsJob(entry.getKey(), entry.getValue().getKeys()));
		}

		experiment.submit(new MergeApproximationSetsJob(Key.of(), aggregates.keySet(), epsilons));
		experiment.submit(new EvaluateIndicatorsJob(Key.of(), problemName));
		experiment.submit(new PrintJob(Key.of(), DataType.INDICATOR_VALUES));

		experiment.shutdownAndWait();
	}

}
