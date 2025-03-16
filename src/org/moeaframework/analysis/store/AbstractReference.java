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

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Abstract implementation of a reference.
 */
abstract class AbstractReference implements Reference {
	
	public AbstractReference() {
		super();
	}
	
	@Override
	public int hashCode() {
		return toNormalizedMap(this).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		
		if (obj == this) {
			return true;
		}
		
		if (obj instanceof Reference) {
			Map<String, String> lhs = toNormalizedMap(this);
			Map<String, String> rhs = toNormalizedMap(((Reference)obj));
			return lhs.equals(rhs);
		}
		
		return false;
	}

	@Override
	public String toString() {
		return "Reference" + fields().stream().map(x -> x + "=" + get(x)).collect(Collectors.joining(",", "(", ")"));
	}
	
	private static Map<String, String> toNormalizedMap(Reference reference) {
		return reference.fields().stream().collect(Collectors.toMap(
				x -> Reference.normalize(x),
				x -> Reference.normalize(reference.get(x))));
	}
	
}