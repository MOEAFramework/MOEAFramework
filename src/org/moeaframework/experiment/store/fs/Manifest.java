package org.moeaframework.experiment.store.fs;

import java.util.HashSet;
import java.util.Set;

import org.moeaframework.util.TypedProperties;

public class Manifest extends TypedProperties {
	
	public static final String FILENAME = ".manifest";
	
	public void validate(Manifest expected) {
		Set<String> keys = new HashSet<String>();
		keys.addAll(keySet());
		keys.addAll(expected.keySet());
		
		for (String key : keys) {
			if (!contains(key)) {
				throw new ManifestValidationException("Manifest missing '" + key + "'");
			}
			
			if (!expected.contains(key)) {
				throw new ManifestValidationException("Manifest missing '" + key + "'");
			}
			
			if (!getString(key).equals(expected.getString(key))) {
				throw new ManifestValidationException("Manifests contain different values for '" + key +
						"', expected '" + expected.getString(key) + "' but was '" + getString(key) + "'");
			}
		}
	}

}
