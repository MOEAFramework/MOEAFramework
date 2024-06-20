package org.moeaframework.experiment.job;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.experiment.Sample;
import org.moeaframework.experiment.store.DataReference;
import org.moeaframework.experiment.store.DataStore;
import org.moeaframework.experiment.store.DataType;
import org.moeaframework.experiment.store.Key;
import org.moeaframework.experiment.store.schema.Field;

public class EndOfRunJob extends Job {
	
	private final Sample sample;
		
	public EndOfRunJob(Key key, Sample sample) {
		super(key);
		this.sample = sample;
	}

	@Override
	public void execute(DataStore dataStore) throws IOException {
		Sample properties = sample.copy();
		
		String algorithmName = Field.ALGORITHM.valueOf(properties);
		String problemName = Field.PROBLEM.valueOf(properties);

		properties.remove(Field.ALGORITHM.getName());
		properties.remove(Field.PROBLEM.getName());

		if (Field.SEED.isDefined(properties)) {
			PRNG.setSeed(Field.SEED.valueOf(properties));
			properties.remove(Field.SEED.getName());
		}
		
		try (Problem problem = ProblemFactory.getInstance().getProblem(problemName)) {
			NondominatedPopulation result = new Executor()
					.withProblem(problem)
					.withAlgorithm(algorithmName)
					.withProperties(properties)
					.run();
			
			JobUtils.saveApproximationSet(dataStore, key, problem, result, sample);
		}
	}

	@Override
	public Collection<DataReference> requires() {
		return List.of();
	}
	
	@Override
	public Collection<DataReference> produces() {
		return List.of(DataReference.of(key, DataType.APPROXIMATION_SET));
	}

}
