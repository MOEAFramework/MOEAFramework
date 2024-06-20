package org.moeaframework.experiment.job;

import java.io.IOException;
import java.time.Instant;
import java.util.Collection;

import org.moeaframework.experiment.store.DataReference;
import org.moeaframework.experiment.store.DataStore;
import org.moeaframework.experiment.store.Key;

public abstract class Job {
	
	protected final Key key;
	
	public Job(Key key) {
		super();
		this.key = key;
	}
	
	public String getName() {
		return getClass().getSimpleName() + "(" + key + ")";
	}
	
	public Key getKey() {
		return key;
	}
	
	public boolean isReady(DataStore dataStore) {
		for (DataReference requirement : requires()) {
			if (!dataStore.contains(requirement)) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean isComplete(DataStore dataStore) {
		if (produces().isEmpty()) {
			return false;
		}
		
		for (DataReference product : produces()) {
			if (!dataStore.contains(product)) {
				return false;
			}
		}

		return true;
	}
	
	public boolean isStale(DataStore dataStore) {
		Instant lastModified = null;
		
		for (DataReference requirement : requires()) {
			Instant requirementLastModified = dataStore.reader(requirement).lastModified();
			
			if (requirementLastModified == null) {
				return true;
			}
			
			if (lastModified == null || lastModified.isBefore(requirementLastModified)) {
				lastModified = requirementLastModified;
			}
		}
		
		if (lastModified != null) {
			for (DataReference product : produces()) {
				if (lastModified.isAfter(dataStore.reader(product).lastModified())) {
					return true;
				}
			}
		}
		
		return false;
	}
		
	@Override
	public String toString() {
		return getName();
	}
	
	public abstract void execute(DataStore dataStore) throws IOException;
	
	public abstract Collection<DataReference> requires();
	
	public abstract Collection<DataReference> produces();

}
