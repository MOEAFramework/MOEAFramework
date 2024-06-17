package org.moeaframework.experiment.job;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.indicator.Indicators;
import org.moeaframework.core.indicator.Indicators.IndicatorValues;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.experiment.store.DataReference;
import org.moeaframework.experiment.store.DataStore;
import org.moeaframework.experiment.store.DataType;
import org.moeaframework.experiment.store.Key;
import org.moeaframework.experiment.store.TransactionalWriter;
import org.moeaframework.experiment.store.schema.Field;
import org.moeaframework.util.TypedProperties;

public class EvaluateIndicatorsJob extends Job {
	
	private final String problemName;
	
	public EvaluateIndicatorsJob(Key key, TypedProperties properties) {
		this(key, getProblemName(key, properties));
	}
	
	public EvaluateIndicatorsJob(Key key) {
		this(key, getProblemName(key, null));
	}
	
	public EvaluateIndicatorsJob(Key key, String problemName) {
		super(key);
		this.problemName = problemName;
	}
	
	private static String getProblemName(Key key, TypedProperties properties) {
		if (key != null && key.contains(Field.PROBLEM)) {
			return key.get(Field.PROBLEM);
		}
		
		if (properties != null && properties.contains(Field.PROBLEM.getName())) {
			return properties.getString(Field.PROBLEM.getName());
		}
		
		throw new IllegalArgumentException("Must provide key or properties that defines the field '" +
				Field.PROBLEM.getName() + "'");
	}

	@Override
	public void execute(DataStore dataStore) {
		NondominatedPopulation approximationSet;
		
		try (InputStream in = dataStore.reader(key, DataType.APPROXIMATION_SET).asBinary()) {
			approximationSet = new NondominatedPopulation(Population.loadBinary(in));
		} catch (IOException e) {
			throw new FrameworkException(e);
		}
		
		try (Problem problem = ProblemFactory.getInstance().getProblem(problemName)) {
			NondominatedPopulation referenceSet = ProblemFactory.getInstance().getReferenceSet(problemName);
			
			Indicators indicators = Indicators.all(problem, referenceSet);
			IndicatorValues values = indicators.apply(approximationSet);
			
			try (TransactionalWriter out = dataStore.writer(key, DataType.INDICATOR_VALUES).asText()) {
				values.asProperties().store(out);
				out.commit();
			} catch (IOException e) {
				throw new FrameworkException(e);
			}
		}
	}

	@Override
	public Collection<DataReference> requires() {
		return List.of(DataReference.of(key, DataType.APPROXIMATION_SET));
	}

	@Override
	public Collection<DataReference> produces() {
		return List.of(DataReference.of(key, DataType.INDICATOR_VALUES));
	}

}
