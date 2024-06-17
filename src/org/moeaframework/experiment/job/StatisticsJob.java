package org.moeaframework.experiment.job;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.indicator.StandardIndicator;
import org.moeaframework.experiment.store.DataReference;
import org.moeaframework.experiment.store.DataStore;
import org.moeaframework.experiment.store.DataType;
import org.moeaframework.experiment.store.Key;
import org.moeaframework.experiment.store.TransactionalWriter;
import org.moeaframework.util.TypedProperties;

public class StatisticsJob extends Job {
			
	private final Collection<Key> inputs;
			
	public StatisticsJob(Key key, Collection<Key> inputs) {
		super(key);
		this.inputs = inputs;
	}

	@Override
	public void execute(DataStore dataStore) {
		Map<StandardIndicator, DescriptiveStatistics> map = new HashMap<>();
		
		for (StandardIndicator indicator : StandardIndicator.values()) {
			map.put(indicator, new DescriptiveStatistics());
		}
		
		for (Key input : inputs) {
			try (Reader in = dataStore.reader(input, DataType.INDICATOR_VALUES).asText()) {
				TypedProperties properties = new TypedProperties();
				properties.load(in);
				
				for (StandardIndicator indicator : StandardIndicator.values()) {
					map.get(indicator).addValue(properties.getDouble(indicator.name()));
				}
			} catch (IOException e) {
				throw new FrameworkException(e);
			}
		}
		
		TypedProperties result = new TypedProperties();
		
		for (StandardIndicator indicator : StandardIndicator.values()) {
			DescriptiveStatistics statistics = map.get(indicator);
			
			result.setDouble(indicator.name() + "-" + "Min", statistics.getMin());
			result.setDouble(indicator.name() + "-" + "Mean", statistics.getMean());
			result.setDouble(indicator.name() + "-" + "Max", statistics.getMax());
			result.setDouble(indicator.name() + "-" + "Median", statistics.getPercentile(50));
			result.setDouble(indicator.name() + "-" + "IQR", (statistics.getPercentile(75) - statistics.getPercentile(25)));
		}
		
		try (TransactionalWriter out = dataStore.writer(key, DataType.STATISTICS).asText()) {
			result.store(out);
			out.commit();
		} catch (Exception e) {
			throw new FrameworkException(e);
		}
	}

	@Override
	public Collection<DataReference> requires() {
		List<DataReference> requirements = new ArrayList<>();
		
		for (Key input : inputs) {
			requirements.add(DataReference.of(input, DataType.INDICATOR_VALUES));
		}
		
		return requirements;
	}

	@Override
	public Collection<DataReference> produces() {
		return List.of(DataReference.of(key, DataType.STATISTICS));
	}

}
