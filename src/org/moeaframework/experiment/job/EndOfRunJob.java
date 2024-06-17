package org.moeaframework.experiment.job;

import java.util.Collection;
import java.util.List;

import org.moeaframework.Executor;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.experiment.store.DataReference;
import org.moeaframework.experiment.store.DataStore;
import org.moeaframework.experiment.store.DataType;
import org.moeaframework.experiment.store.Key;
import org.moeaframework.experiment.store.TransactionalOutputStream;
import org.moeaframework.experiment.store.TransactionalWriter;
import org.moeaframework.experiment.store.schema.Field;
import org.moeaframework.util.PropertyScope;
import org.moeaframework.util.TypedProperties;

public class EndOfRunJob extends Job {
	
	private final TypedProperties properties;
		
	public EndOfRunJob(Key key, TypedProperties properties) {
		super(key);
		this.properties = properties.copy(); // copy to avoid threading issues
	}

	@Override
	public void execute(DataStore dataStore) {
		try (TransactionalWriter out = dataStore.writer(key, DataType.INPUTS).asText()) {
			properties.store(out);
			out.commit();
		} catch (Exception e) {
			throw new FrameworkException(e);
		}
		
		try (PropertyScope scope = properties.createScope()) {
			String algorithmName = properties.getString(Field.ALGORITHM.getName());
			String problemName = properties.getString(Field.PROBLEM.getName());
			
			scope.without(Field.ALGORITHM.getName());
			scope.without(Field.PROBLEM.getName());
			
			if (properties.contains(Field.SEED.getName())) {
				PRNG.setSeed(properties.getLong(Field.SEED.getName()));
				scope.without(Field.SEED.getName());
			}
						
			NondominatedPopulation result = new Executor()
					.withProblem(problemName)
					.withAlgorithm(algorithmName)
					.withProperties(properties)
					.run();
			
			try (TransactionalOutputStream out = dataStore.writer(key, DataType.APPROXIMATION_SET).asBinary()) {
				result.saveBinary(out);
				out.commit();
			} catch (Exception e) {
				throw new FrameworkException(e);
			}
		}
	}

	@Override
	public Collection<DataReference> requires() {
		return List.of();
	}
	
	@Override
	public Collection<DataReference> produces() {
		return List.of(
				DataReference.of(key, DataType.INPUTS),
				DataReference.of(key, DataType.APPROXIMATION_SET));
	}

}
