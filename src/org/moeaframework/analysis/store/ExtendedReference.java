/* Copyright 2009-2025 David Hadka
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
 * Reference adding or overwriting a field in an existing reference.
 */
class ExtendedReference extends AbstractReference {
	
	private final Reference reference;
	
	private final String name;
	
	private final String value;
	
	public ExtendedReference(Reference reference, String name, String value) {
		super();
		this.reference = reference;
		this.name = name;
		this.value = value;
	}
	
	@Override
	public Set<String> fields() {
		Set<String> result = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		result.addAll(reference.fields());
		result.add(name);
		return result;
	}
	
	@Override
	public String get(String name) {
		if (this.name.equalsIgnoreCase(name)) {
			return value;
		}
		
		return reference.get(name);
	}
	
}