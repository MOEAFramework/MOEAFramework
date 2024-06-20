package org.moeaframework.experiment.job;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.moeaframework.core.indicator.StandardIndicator;
import org.moeaframework.experiment.Sample;
import org.moeaframework.experiment.Samples;
import org.moeaframework.experiment.store.DataReference;
import org.moeaframework.experiment.store.DataStore;
import org.moeaframework.experiment.store.DataType;
import org.moeaframework.experiment.store.Key;
import org.moeaframework.util.TypedProperties;
import org.moeaframework.util.format.Column;
import org.moeaframework.util.format.TabularData;
import org.moeaframework.util.statistics.KruskalWallisTest;
import org.moeaframework.util.statistics.MannWhitneyUTest;

public class StatisticalSignificanceJob extends Job {

	private final Map<Key, ? extends Samples> inputs;
	
	private final double significanceLevel = 0.05;
	
	public StatisticalSignificanceJob(Key key, Map<Key, ? extends Samples> inputs) {
		super(key);
		this.inputs = inputs;
	}

	@Override
	public void execute(DataStore dataStore) throws IOException {
		// Set up the groups.
		Map<Key, Integer> keyToGroup = new HashMap<>();
		Map<Integer, Key> groupToKey = new HashMap<>();
		int group = 0;
		
		for (Key key : inputs.keySet()) {
			keyToGroup.put(key, group);
			groupToKey.put(group, key);
			group += 1;
		}
		
		// Set up the Kruskal-Wallis tests so we can load all data in one pass.
		Map<StandardIndicator, KruskalWallisTest> kwTests = new HashMap<>();
		
		for (StandardIndicator indicator : StandardIndicator.values()) {
			kwTests.put(indicator, new KruskalWallisTest(inputs.size()));
		}
		
		// Load the data.
		for (Key groupKey : inputs.keySet()) {
			Samples samples = inputs.get(groupKey);
			
			for (Sample sample : inputs.get(groupKey)) {
				Key sampleKey = Key.from(samples.getSchema(), sample);
				TypedProperties properties = JobUtils.loadProperties(dataStore, sampleKey, DataType.INDICATOR_VALUES);
					
				for (StandardIndicator indicator : StandardIndicator.values()) {
					kwTests.get(indicator).add(properties.getDouble(indicator.name()), keyToGroup.get(groupKey));
				}
			}
		}
		
		// Evaluate the statistical tests.
		List<Object[]> results = new ArrayList<>();
		
		for (StandardIndicator indicator : StandardIndicator.values()) {
			KruskalWallisTest kwTest = kwTests.get(indicator);

			if (kwTest.test(significanceLevel)) {
				// Difference detected, test each pair of algorithms
				for (int i = 0; i < inputs.size() - 1; i++) {
					for (int j = i + 1; j < inputs.size(); j++) {
						MannWhitneyUTest mwTest = new MannWhitneyUTest();
						mwTest.addAll(kwTest.getStatistics(i).getValues(), 0);
						mwTest.addAll(kwTest.getStatistics(j).getValues(), 1);

						results.add(new Object[] { indicator, groupToKey.get(i), groupToKey.get(j),
								mwTest.test(significanceLevel) });
					}
				}
			} else {
				// No difference, add all pairs as indifferent
				for (int i = 0; i < inputs.size() - 1; i++) {
					for (int j = i + 1; j < inputs.size(); j++) {
						results.add(new Object[] { indicator, groupToKey.get(i), groupToKey.get(j), false });
					}
				}
			}
		}
		
		TabularData<Object[]> data = new TabularData<>(results);
		
		data.addColumn(new Column<Object[], String>("Indicator",
				x -> x[0].toString()));
		data.addColumn(new Column<Object[], String>("Group 1",
				x -> x[1].toString()));
		data.addColumn(new Column<Object[], String>("Group 2",
				x -> x[2].toString()));
		data.addColumn(new Column<Object[], String>("Statistically Different (a=" + significanceLevel + ")",
				x -> x[3].toString()));
		
		JobUtils.saveTabularData(dataStore, key, DataType.STATISTICAL_COMPARISON, data);
	}

	@Override
	public Collection<DataReference> requires() {
		List<DataReference> requirements = new ArrayList<>();

		for (Key groupKey : inputs.keySet()) {
			Samples samples = inputs.get(groupKey);
			
			for (Sample sample : inputs.get(groupKey)) {
				Key sampleKey = Key.from(samples.getSchema(), sample);
				requirements.add(DataReference.of(sampleKey, DataType.INDICATOR_VALUES));
			}
		}

		return requirements;
	}

	@Override
	public Collection<DataReference> produces() {
		return List.of(DataReference.of(key, DataType.STATISTICAL_COMPARISON));
	}

}
