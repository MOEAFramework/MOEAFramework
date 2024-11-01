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
package org.moeaframework.analysis.store;

import java.util.Set;
import java.util.TreeSet;

/**
 * A key that identifies or addresses as specific {@link Container}.
 */
public interface Key {

	/**
	 * Returns the indices defined by this key.
	 * 
	 * @return the indices
	 */
	public Set<String> indices();
	
	/**
	 * Returns the value associated with the given index.
	 * 
	 * @param index the index
	 * @return the value associated with the index
	 */
	public Comparable<?> get(String index);
	
	public default Key extend(String name, Comparable<?> value) {
		return new ExtendedKey(this, name, value);
	}
	
	static class ExtendedKey extends AbstractKey {
		
		private final Key innerKey;
		
		private final String index;
		
		private final Comparable<?> value;
		
		public ExtendedKey(Key innerKey, String index, Comparable<?> value) {
			super();
			this.innerKey = innerKey;
			this.index = index;
			this.value = value;
		}
		
		public Set<String> indices() {
			Set<String> indices = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
			indices.addAll(innerKey.indices());
			indices.add(index);
			return indices;
		}
		
		public Comparable<?> get(String index) {
			if (this.index.equalsIgnoreCase(index)) {
				return value;
			}
			
			return innerKey.get(index);
		}
		
	}

}