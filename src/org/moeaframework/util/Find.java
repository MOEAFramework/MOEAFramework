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
package org.moeaframework.util;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import org.moeaframework.util.validate.Validate;

/**
 * Static methods for locating items in a list, either using their natural ordering (as specified by the class'
 * {@link Comparable} interface), a provided {@link Comparator}, or using a computed key.
 */
public class Find {
	
	/**
	 * Private constructor to prevent instantiation.
	 */
	private Find() {
		super();
	}
	
	/**
	 * Represents the selected item, identifying both the index and the item itself.
	 * 
	 * @param <V> the type of the item
	 */
	public static class IndexedItem<V> {
	
		private final int index;
				
		private final V value;
		
		IndexedItem(int index, V value) {
			super();
			this.index = index;
			this.value = value;
		}
		
		/**
		 * Returns the index of value in the list.
		 * 
		 * @return the index
		 */
		public int getIndex() {
			return index;
		}
		
		/**
		 * Returns the value of element in the list.
		 * 
		 * @return the value
		 */
		public V getValue() {
			return value;
		}
	
	}
	
	/**
	 * Extends {@link IndexedItem} to include the comparison key when applicable.
	 * 
	 * @param <K> the type of the key
	 * @param <V> the type of the item
	 */
	public static class KeyedItem<K, V> extends IndexedItem<V> {
		
		private final K key;
		
		KeyedItem(int index, K key, V value) {
			super(index, value);
			this.key = key;
		}

		/**
		 * Returns the key associated with this value.
		 * 
		 * @return the key
		 */
		public K getKey() {
			return key;
		}
		
	}
	
	/**
	 * Locates and returns the first item in the list matching the given predicate.  If the list is empty or no matches
	 * are identified, {@code null} is returned
	 * 
	 * @param <V> the type of element in the list
	 * @param items the items
	 * @param predicate the predicate identifying the item to be matched
	 * @return the matched item or {@code null}
	 */
	public static <V> IndexedItem<V> match(List<V> items, Predicate<? super V> predicate) {
		for (int i = 0; i < items.size(); i++) {
			V value = items.get(i);
			
			if (predicate.test(value)) {
				return new IndexedItem<>(i, value);
			}
		}
		
		return null;
	}
	
	/**
	 * Finds and returns the minimum item in the list according to the given {@link Comparator}.
	 * 
	 * @param <V> the type of element in the list
	 * @param items the items
	 * @param comparator the comparator identifying the minimum elements
	 * @return the minimum item
	 * @throws IllegalArgumentException if the list is empty
	 */
	public static <V> IndexedItem<V> find(List<V> items, Comparator<? super V> comparator) {
		return find(items, Function.<V>identity(), comparator);
	}
	
	/**
	 * Finds and returns the minimum item in the list according to their natural ordering (i.e., as defined by
	 * {@link Comparable}).
	 * 
	 * @param <V> the type of element in the list
	 * @param items the items
	 * @return the minimum item
	 * @throws IllegalArgumentException if the list is empty
	 */
	public static <V extends Comparable<? super V>> IndexedItem<V> minimum(List<V> items) {
		return find(items, Comparator.<V>naturalOrder());
	}
	
	/**
	 * Finds and returns the maximum item in the list according to their natural ordering (i.e., as defined by
	 * {@link Comparable}).
	 * 
	 * @param <V> the type of element in the list
	 * @param items the items
	 * @return the maximum item
	 * @throws IllegalArgumentException if the list is empty
	 */
	public static <V extends Comparable<? super V>> IndexedItem<V> maximum(List<V> items) {
		return find(items, Comparator.<V>naturalOrder().reversed());
	}
	
	/**
	 * Finds and returns the item in the list with the minimum key, as defined by the natural ordering of the keys.
	 * 
	 * @param <V> the type of element in the list
	 * @param <K> the type of the key
	 * @param items the items
	 * @param keyExtractor a function to extract the key for each item
	 * @return the minimum item
	 * @throws IllegalArgumentException if the list is empty
	 */
	public static <V, K extends Comparable<? super K>> KeyedItem<K, V> minimum(List<V> items,
			Function<? super V, ? extends K> keyExtractor) {
		return find(items, keyExtractor, Comparator.<K>naturalOrder());
	}
	
	/**
	 * Finds and returns the item in the list with the maximum key, as defined by the natural ordering of the keys.
	 * 
	 * @param <V> the type of element in the list
	 * @param <K> the type of the key
	 * @param items the items
	 * @param keyExtractor a function to extract the key for each item
	 * @return the minimum item
	 * @throws IllegalArgumentException if the list is empty
	 */
	public static <V, K extends Comparable<? super K>> KeyedItem<K, V> maximum(List<V> items,
			Function<? super V, ? extends K> keyExtractor) {
		return find(items, keyExtractor, Comparator.<K>naturalOrder().reversed());
	}
	
	/**
	 * Finds and returns the item in the list with the minimum key, as defined by the given {@link Comparator}.
	 * 
	 * @param <V> the type of element in the list
	 * @param <K> the type of the key
	 * @param items the items
	 * @param keyExtractor a function to extract the key for each item
	 * @param keyComparator a comparator for keys
	 * @return the minimum item
	 * @throws IllegalArgumentException if the list is empty
	 */
	public static <V, K> KeyedItem<K, V> find(List<V> items, Function<? super V, ? extends K> keyExtractor,
			Comparator<? super K> keyComparator) {
		Validate.that("items", items).isNotEmpty();
		
		int index = 0;
		V value = items.get(0);
		K key = keyExtractor.apply(value);
		
		for (int i = 1; i < items.size(); i++) {
			V otherValue = items.get(i);
			K otherKey = keyExtractor.apply(otherValue);
			
			if (keyComparator.compare(key, otherKey) > 0) {
				index = i;
				value = otherValue;
				key = otherKey;
			}
		}
		
		return new KeyedItem<K, V>(index, key, value);
	}

}
