package org.moeaframework.experiment.job;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.moeaframework.core.indicator.StandardIndicator;
import org.moeaframework.experiment.Samples;
import org.moeaframework.experiment.store.DataReference;
import org.moeaframework.experiment.store.DataStore;
import org.moeaframework.experiment.store.DataType;
import org.moeaframework.experiment.store.Key;
import org.moeaframework.util.TypedProperties;
import org.moeaframework.util.format.Column;
import org.moeaframework.util.format.TabularData;

public class DescriptiveStatisticsJob extends Job {
			
	private final Collection<Key> inputs;
	
	public DescriptiveStatisticsJob(Key key, Samples samples) {
		this(key, samples.keySet());
	}
			
	public DescriptiveStatisticsJob(Key key, Collection<Key> inputs) {
		super(key);
		this.inputs = inputs;
	}

	@Override
	public void execute(DataStore dataStore) throws IOException {
		Map<StandardIndicator, DescriptiveStatistics> map = new HashMap<>();
		
		for (StandardIndicator indicator : StandardIndicator.values()) {
			map.put(indicator, new DescriptiveStatistics());
		}
		
		for (DataReference dataReference : requires()) {
			TypedProperties properties = JobUtils.loadProperties(dataStore, dataReference);
			
			for (StandardIndicator indicator : StandardIndicator.values()) {
				map.get(indicator).addValue(properties.getDouble(indicator.name()));
			}
		}

		TabularData<Map.Entry<StandardIndicator, DescriptiveStatistics>> data = new TabularData<>(map.entrySet());
		
		data.addColumn(new Column<Map.Entry<StandardIndicator, DescriptiveStatistics>, String>("Indicator",
				x -> x.getKey().name()));
		data.addColumn(new Column<Map.Entry<StandardIndicator, DescriptiveStatistics>, Double>("Min",
				x -> x.getValue().getMin()));
		data.addColumn(new Column<Map.Entry<StandardIndicator, DescriptiveStatistics>, Double>("Median",
				x -> x.getValue().getPercentile(50)));
		data.addColumn(new Column<Map.Entry<StandardIndicator, DescriptiveStatistics>, Double>("Max",
				x -> x.getValue().getMax()));
		data.addColumn(new Column<Map.Entry<StandardIndicator, DescriptiveStatistics>, Double>("IQR",
				x -> x.getValue().getPercentile(75) - x.getValue().getPercentile(25)));
		data.addColumn(new Column<Map.Entry<StandardIndicator, DescriptiveStatistics>, Long>("Count",
				x -> x.getValue().getN()));
		
		JobUtils.saveTabularData(dataStore, key, DataType.DESCRIPTIVE_STATISTICS, data);
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
		return List.of(DataReference.of(key, DataType.DESCRIPTIVE_STATISTICS));
	}

}
