package org.moeaframework.experiment.job;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.moeaframework.analysis.plot.Plot;
import org.moeaframework.core.indicator.StandardIndicator;
import org.moeaframework.experiment.Samples;
import org.moeaframework.experiment.store.DataReference;
import org.moeaframework.experiment.store.DataStore;
import org.moeaframework.experiment.store.DataType;
import org.moeaframework.experiment.store.Key;
import org.moeaframework.util.TypedProperties;

public class IndicatorPlotJob extends Job {

	private final Map<Key, ? extends Samples> inputs;
	
	private final String xAxis;
	
	private final StandardIndicator yAxis;
	
	public IndicatorPlotJob(Key key, Map<Key, ? extends Samples> inputs, String xAxis, StandardIndicator yAxis) {
		super(key);
		this.inputs = inputs;
		this.xAxis = xAxis;
		this.yAxis = yAxis;
	}

	@Override
	public void execute(DataStore dataStore) throws IOException {
		Plot plot = new Plot();
		
		for (Key groupKey : inputs.keySet()) {
			Samples samples = inputs.get(groupKey);
			List<Pair<Double, Double>> dataPoints = new ArrayList<>();
			
			for (Key sampleKey : samples.partition(xAxis).keySet()) {
				TypedProperties properties = JobUtils.loadProperties(dataStore, sampleKey, DataType.INDICATOR_VALUES);
				dataPoints.add(Pair.of(
						((Number)sampleKey.get(xAxis)).doubleValue(),
						properties.getDouble(yAxis.name())));
			}
			
			Collections.sort(dataPoints, Comparator.comparing(Pair::getKey));

			plot.line(groupKey.getDisplayName(),
					dataPoints.stream().map(x -> x.getKey()).toList(),
					dataPoints.stream().map(x -> x.getValue()).toList());
		}
		
		File tempFile = File.createTempFile("plot", ".png");
		
		try {
			plot.setXLabel(xAxis);
			plot.setYLabel(yAxis.name());
			plot.save(tempFile);
			
			dataStore.writer(key, DataType.of(yAxis.name() + ".png")).store(tempFile);
		} finally {
			tempFile.delete();
		}
	}

	@Override
	public Collection<DataReference> requires() {
		List<DataReference> requirements = new ArrayList<>();

		for (Key groupKey : inputs.keySet()) {
			Samples samples = inputs.get(groupKey);
			
			for (Key sampleKey : samples.partition(xAxis).keySet()) {
				requirements.add(DataReference.of(sampleKey, DataType.INDICATOR_VALUES));
			}
		}

		return requirements;
	}

	@Override
	public Collection<DataReference> produces() {
		return List.of(DataReference.of(key, DataType.of(yAxis.name() + ".png")));
	}

}
