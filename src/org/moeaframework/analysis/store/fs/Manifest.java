/* Copyright 2009-2024 David Hadka
 *
 * This file is part of the MOEA Framework.
 *
 * The MOEA Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * The MOEA Framework is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.moeaframework.analysis.store.fs;

import java.util.HashSet;
import java.util.Set;

import org.moeaframework.core.TypedProperties;

/**
 * Manifest for storing and validating the structure of the data store.
 */
public class Manifest extends TypedProperties {
	
	/**
	 * Constructs a new, empty manifest.
	 */
	public Manifest() {
		super();
	}
	
	/**
	 * Validates the two manifests are equal by comparing both contain identical keys and values.
	 * 
	 * @param expected the expected manifest
	 * @throws ManifestValidationException if any differences were detected
	 */
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
