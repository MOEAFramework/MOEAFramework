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
package org.moeaframework.analysis.sample;

import java.util.Set;

import org.moeaframework.analysis.store.AbstractKey;
import org.moeaframework.analysis.store.Key;
import org.moeaframework.analysis.store.Keyed;
import org.moeaframework.core.TypedProperties;

/**
 * A single parameter sample.
 * 
 * @see Parameter
 * @see Samples
 */
public class Sample extends TypedProperties implements Keyed {

	public Sample() {
		super(TypedProperties.DEFAULT_SEPARATOR, true);
	}

	public Sample copy() {
		Sample copy = new Sample();
		copy.addAll(this);
		return copy;
	}
	
	@Override
	public Key getKey() {
		return new SampleKey();
	}
	
	private class SampleKey extends AbstractKey {

		@Override
		public Set<String> indices() {
			return keySet();
		}

		@Override
		public Comparable<?> get(String index) {
			return getString(index);
		}
		
	}

}