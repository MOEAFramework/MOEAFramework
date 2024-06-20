package org.moeaframework.experiment;

import org.moeaframework.experiment.store.Key;
import org.moeaframework.experiment.store.schema.Schema;

public class PartitionedSamples extends Samples {
	
	public final Key partitionKey;
	
	public PartitionedSamples(Schema schema, Key partitionKey) {
		super(schema);
		this.partitionKey = partitionKey;
	}
	
	public Key getPartitionKey() {
		return partitionKey;
	}

}
