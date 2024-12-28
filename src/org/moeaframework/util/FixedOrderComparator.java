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
package org.moeaframework.util;

import java.util.Comparator;
import java.util.Map;

/**
 * Sorts elements according to a fixed order.  The order is defined by mapping elements to integer and sorting based
 * on the numeric value.  Elements not defined in the mapping are always sorted last.
 * 
 * @param <T> the type of elements being sorted
 */
public class FixedOrderComparator<T> implements Comparator<T> {
	
	/**
	 * Constant used to place an element at the end of the sorted collection.  If multiple elements map to this value,
	 * their ordering is undefined.
	 */
	public static final int LAST = Integer.MAX_VALUE;
	
	private final Map<T, Integer> order;
		
	/**
	 * Creates a new fixed order comparator.
	 * 
	 * @param order mapping defining the ordering of elements (smaller values appearing first)
	 */
	public FixedOrderComparator(Map<T, Integer> order) {
		super();
		this.order = order;
	}
	
	@Override
	public int compare(T obj1, T obj2) {
		if (obj1.equals(obj2)) {
			return 0;
		}
		
		int order1 = order.getOrDefault(obj1, LAST);
		int order2 = order.getOrDefault(obj2, LAST);
		
		return Integer.compare(order1, order2);
	}

}
