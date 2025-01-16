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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

/**
 * Collection of static methods for dealing with {@link Iterable}s and {@link Iterator}s.
 */
public class Iterators {
	
	private Iterators() {
		super();
	}
	
	/**
	 * Returns an immutable iterator containing the given values.
	 * 
	 * @param <T> the type of each value
	 * @param values the values
	 * @return the iterator over the values
	 */
	@SafeVarargs
	public static <T> Iterator<T> of(T... values) {
		return List.of(values).iterator();
	}
	
	/**
	 * Returns an iterator that joins the contents of multiple iterators.
	 * 
	 * @param <T> the type of each item
	 * @param iterators the iterators
	 * @return the joined iterator
	 */
	@SafeVarargs
	public static <T> Iterator<T> join(Iterator<T>... iterators) {
		return new JoinIterators<>(iterators);
	}
	
	/**
	 * Returns an iterable that joins the contents of multiple iterables.
	 * 
	 * @param <T> the type of each item
	 * @param iterables the iterables
	 * @return the joined iterable
	 */
	@SafeVarargs
	public static <T> Iterable<T> join(Iterable<T>... iterables) {
		return new JoinIterables<>(iterables);
	}
	
	/**
	 * Returns an iterator that tracks the index of each item.
	 * 
	 * @param <T> the type of each item
	 * @param iterator the iterator
	 * @return the indexed value
	 */
	public static <T> Iterator<IndexedValue<T>> enumerate(Iterator<T> iterator) {
		return new IndexedIterator<>(iterator);
	}
	
	/**
	 * Returns an iterable that tracks the index of each item.
	 * 
	 * @param <T> the type of each item
	 * @param iterable the iterable
	 * @return the indexed iterable
	 */
	public static <T> Iterable<IndexedValue<T>> enumerate(Iterable<T> iterable) {
		return new IndexedIterable<>(iterable);
	}
	
	/**
	 * Returns an iterable that tracks the index of each item.
	 * 
	 * @param <T> the type of each item
	 * @param array the array of items
	 * @return the indexed iterable
	 */
	public static <T> Iterable<IndexedValue<T>> enumerate(T[] array) {
		return new IndexedIterable<>(List.of(array));
	}
	
	/**
	 * Returns an iterator that returns pairs of items from two iterators.
	 * 
	 * @param <K> the type of the first iterator
	 * @param <T> the type of the second iterator
	 * @param iterator1 the first iterator
	 * @param iterator2 the second iterator
	 * @return an iterator over pairs
	 */
	public static <K, T> Iterator<Pair<K, T>> zip(Iterator<K> iterator1, Iterator<T> iterator2) {
		return new ZipIterator<>(iterator1, iterator2);
	}
	
	/**
	 * Returns an iterable that returns pairs of items from two iterables.
	 * 
	 * @param <K> the type of the first iterable
	 * @param <T> the type of the second iterable
	 * @param iterable1 the first iterable
	 * @param iterable2 the second iterable
	 * @return an iterable over pairs
	 */
	public static <K, T> Iterable<Pair<K, T>> zip(Iterable<K> iterable1, Iterable<T> iterable2) {
		return new ZipIterable<>(iterable1, iterable2);
	}
	
	/**
	 * Returns an iterable that returns pairs of items from two arrays.
	 * 
	 * @param <K> the type of the first array
	 * @param <T> the type of the second array
	 * @param array1 the first array
	 * @param array2 the second array
	 * @return an iterable over pairs
	 */
	public static <K, T> Iterable<Pair<K, T>> zip(K[] array1, T[] array2) {
		return zip(List.of(array1), List.of(array2));
	}
	
	/**
	 * Returns an iterator mapping the function to the items in the iterator.
	 * 
	 * @param <T> the source type
	 * @param <R> the result type
	 * @param iterator the iterator
	 * @param function the mapping function
	 * @return the result iterator
	 */
	public static <T, R> Iterator<R> map(Iterator<T> iterator, Function<T, R> function) {
		return new MapIterator<>(iterator, function);
	}
	
	/**
	 * Returns an iterable mapping the function to the items in the iterable.
	 * 
	 * @param <T> the source type
	 * @param <R> the result type
	 * @param iterable the iterable
	 * @param function the mapping function
	 * @return the result iterable
	 */
	public static <T, R> Iterable<R> map(Iterable<T> iterable, Function<T, R> function) {
		return new MapIterable<>(iterable, function);
	}
	
	/**
	 * Returns an iterable mapping the function to the items in the array.
	 * 
	 * @param <T> the source type
	 * @param <R> the result type
	 * @param array the array
	 * @param function the mapping function
	 * @return the result iterable
	 */
	public static <T, R> Iterable<R> map(T[] array, Function<T, R> function) {
		return new MapIterable<>(List.of(array), function);
	}
	
	/**
	 * Materializes the given iterator, returning a collection of all the items.
	 * 
	 * @param <T> the type of the iterator
	 * @param iterator the iterator
	 * @return the collection of items
	 */
	public static <T> List<T> materialize(Iterator<T> iterator) {
		List<T> result = new ArrayList<>();
		
		while (iterator.hasNext()) {
			result.add(iterator.next());
		}
		
		return result;
	}
	
	/**
	 * Materializes the given iterable, returning a collection of all the items.
	 * 
	 * @param <T> the type of the iterable
	 * @param iterable the iterable
	 * @return the collection of items
	 */
	public static <T> List<T> materialize(Iterable<T> iterable) {
		return materialize(iterable.iterator());
	}
	
	/**
	 * Returns the last element in the iterator.
	 * 
	 * @param <T> the type of the iterator
	 * @param iterator the iterator
	 * @return the last element, or {@code null} if the iterator was empty
	 */
	public static <T> T last(Iterator<T> iterator) {
		T last = null;
		
		while (iterator.hasNext()) {
			last = iterator.next();
		}
		
		return last;
	}
	
	/**
	 * Returns the last element in the iterable.
	 * 
	 * @param <T> the type of the iterable
	 * @param iterable the iterable
	 * @return the last element, or {@code null} if the iterable was empty
	 */
	public static <T> T last(Iterable<T> iterable) {
		return last(iterable.iterator());
	}
	
	/**
	 * Counts the number of elements in the iterator.
	 * 
	 * @param iterator the iterator
	 * @return the number of elements
	 */
	public static int count(Iterator<?> iterator) {
		int count = 0;
		
		while (iterator.hasNext()) {
			iterator.next();
			count++;
		}
		
		return count;
	}
	
	/**
	 * Counts the number of elements in the iterable.
	 * 
	 * @param iterable the iterable
	 * @return the number of elements
	 */
	public static int count(Iterable<?> iterable) {
		return count(iterable.iterator());
	}
	
	/**
	 * Finds and returns the minimum element using natural ordering as defined by the {@link Comparable}.
	 * 
	 * @param <V> the comparable type
	 * @param iterator the iterator
	 * @return the minimum element
	 * @throws NoSuchElementException if the iterator is empty
	 */
	public static <V extends Comparable<? super V>> V minimum(Iterator<V> iterator) {
		return minimum(iterator, Function.<V>identity(), Comparator.<V>naturalOrder()).getValue();
	}
	
	/**
	 * Finds and returns the maximum element using natural ordering as defined by the {@link Comparable}.
	 * 
	 * @param <V> the comparable type
	 * @param iterator the iterator
	 * @return the maximum element
	 * @throws NoSuchElementException if the iterator is empty
	 */
	public static <V extends Comparable<? super V>> V maximum(Iterator<V> iterator) {
		return minimum(iterator, Function.<V>identity(), Comparator.<V>naturalOrder().reversed()).getValue();
	}
	
	/**
	 * Finds and returns the minimum element using natural ordering as defined by the {@link Comparable}.
	 * 
	 * @param <V> the comparable type
	 * @param iterable the iterable
	 * @return the minimum element
	 * @throws NoSuchElementException if the iterator is empty
	 */
	public static <V extends Comparable<? super V>> V minimum(Iterable<V> iterable) {
		return minimum(iterable.iterator());
	}
	
	/**
	 * Finds and returns the maximum element using natural ordering as defined by the {@link Comparable}.
	 * 
	 * @param <V> the comparable type
	 * @param iterable the iterable
	 * @return the maximum element
	 * @throws NoSuchElementException if the iterator is empty
	 */
	public static <V extends Comparable<? super V>> V maximum(Iterable<V> iterable) {
		return maximum(iterable.iterator());
	}
	
	/**
	 * Finds and returns the minimum element based on the given key with a natural ordering.
	 * 
	 * @param <K> the type of the key used for comparison
	 * @param <V> the type of each element
	 * @param iterator the iterator
	 * @param keyExtractor function returning the key used for comparisons
	 * @return the minimum key and element
	 * @throws NoSuchElementException if the iterator is empty
	 */
	public static <K extends Comparable<? super K>, V> Pair<K, V> minimum(Iterator<V> iterator,
			Function<? super V, ? extends K> keyExtractor) {
		return minimum(iterator, keyExtractor, Comparator.<K>naturalOrder());
	}
	
	/**
	 * Finds and returns the minimum element based on the given key with a natural ordering.
	 * 
	 * @param <K> the type of the key used for comparison
	 * @param <V> the type of each element
	 * @param iterable the iterable
	 * @param keyExtractor function returning the key used for comparisons
	 * @return the minimum key and element
	 * @throws NoSuchElementException if the iterable is empty
	 */
	public static <K extends Comparable<? super K>, V> Pair<K, V> minimum(Iterable<V> iterable,
			Function<? super V, ? extends K> keyExtractor) {
		return minimum(iterable.iterator(), keyExtractor);
	}
	
	/**
	 * Finds and returns the minimum element based on the given key with the ordering defined by a comparator function.
	 * 
	 * @param <K> the type of the key used for comparison
	 * @param <V> the type of each element
	 * @param iterator the iterator
	 * @param keyExtractor function returning the key used for comparisons
	 * @param keyComparator function for ordering the keys
	 * @return the minimum key and element
	 * @throws NoSuchElementException if the iterator is empty
	 */
	public static <K, V> Pair<K, V> minimum(Iterator<V> iterator, Function<? super V, ? extends K> keyExtractor,
			Comparator<? super K> keyComparator) {
		V value = iterator.next();
		K key = keyExtractor.apply(value);

		while (iterator.hasNext()) {
			V otherValue = iterator.next();
			K otherKey = keyExtractor.apply(otherValue);

			if (keyComparator.compare(otherKey, key) < 0) {
				value = otherValue;
				key = otherKey;
			}
		}

		return Pair.of(key, value);
	}
	
	/**
	 * Associates an index to a value.
	 * 
	 * @param <T> the type of the value
	 */
	public static class IndexedValue<T> {
		
		private final int index;
		
		private final T value;
		
		/**
		 * Constructs a new indexed value.
		 * 
		 * @param index the index
		 * @param value the value
		 */
		public IndexedValue(int index, T value) {
			super();
			this.index = index;
			this.value = value;
		}
		
		/**
		 * Returns the index of this value.
		 * 
		 * @return the index
		 */
		public int getIndex() {
			return index;
		}
		
		/**
		 * Returns the value.
		 * 
		 * @return the value
		 */
		public T getValue() {
			return value;
		}
		
	}
	
	private static class JoinIterables<T> implements Iterable<T> {
		
		private final List<Iterable<T>> iterables;
		
		@SafeVarargs
		public JoinIterables(Iterable<T>... iterables) {
			this(List.of(iterables));
		}
		
		public JoinIterables(List<Iterable<T>> iterables) {
			super();
			this.iterables = iterables;
		}

		@Override
		public Iterator<T> iterator() {
			List<Iterator<T>> iterators = new ArrayList<>();
			
			for (Iterable<T> iterable : iterables) {
				iterators.add(iterable.iterator());
			}
			
			return new JoinIterators<>(iterators);
		}
		
	}

	private static class JoinIterators<T> implements Iterator<T> {
		
		private final List<Iterator<T>> iterators;
		
		private int index;
		
		@SafeVarargs
		public JoinIterators(Iterator<T>... iterators) {
			this(List.of(iterators));
		}
		
		public JoinIterators(List<Iterator<T>> iterators) {
			super();
			this.iterators = iterators;
		}
	
		@Override
		public boolean hasNext() {
			while (index < iterators.size()) {
				if (iterators.get(index).hasNext()) {
					return true;
				}
				
				index++;
			}
			
			return false;
		}
		
		@Override
		public void remove() {
			iterators.get(index).remove();
		}
	
		@Override
		public T next() {
			if (hasNext()) {
				return iterators.get(index).next();
			}
	
			throw new NoSuchElementException();
		}
		
	}
	
	private static class IndexedIterable<T> implements Iterable<IndexedValue<T>> {
		
		private final Iterable<T> iterable;
		
		public IndexedIterable(Iterable<T> iterable) {
			super();
			this.iterable = iterable;
		}

		@Override
		public Iterator<IndexedValue<T>> iterator() {
			return new IndexedIterator<>(iterable.iterator());
		}
		
	}
	
	private static class IndexedIterator<T> implements Iterator<IndexedValue<T>> {
		
		private final Iterator<T> iterator;
		
		private int index;
		
		public IndexedIterator(Iterator<T> iterator) {
			super();
			this.iterator = iterator;
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public IndexedValue<T> next() {
			return new IndexedValue<>(index++, iterator.next());
		}

		@Override
		public void remove() {
			iterator.remove();
		}
		
	}
	
	private static class ZipIterable<K, T> implements Iterable<Pair<K, T>> {
		
		private final Iterable<K> iterable1;
		
		private final Iterable<T> iterable2;
		
		public ZipIterable(Iterable<K> iterable1, Iterable<T> iterable2) {
			super();
			this.iterable1 = iterable1;
			this.iterable2 = iterable2;
		}

		@Override
		public Iterator<Pair<K, T>> iterator() {
			return new ZipIterator<>(iterable1.iterator(), iterable2.iterator());
		}
		
	}
	
	private static class ZipIterator<K, T> implements Iterator<Pair<K, T>> {
		
		private final Iterator<K> iterator1;
		
		private final Iterator<T> iterator2;
				
		public ZipIterator(Iterator<K> iterator1, Iterator<T> iterator2) {
			super();
			this.iterator1 = iterator1;
			this.iterator2 = iterator2;
		}

		@Override
		public boolean hasNext() {
			return iterator1.hasNext() && iterator2.hasNext();
		}

		@Override
		public Pair<K, T> next() {
			return Pair.of(iterator1.next(), iterator2.next());
		}

		@Override
		public void remove() {
			iterator1.remove();
			iterator2.remove();
		}
		
	}
	
	private static class MapIterator<T, R> implements Iterator<R> {
		
		private final Iterator<T> iterator;
		
		private final Function<T, R> function;
						
		public MapIterator(Iterator<T> iterator, Function<T, R> function) {
			super();
			this.iterator = iterator;
			this.function = function;
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public R next() {
			return function.apply(iterator.next());
		}

		@Override
		public void remove() {
			iterator.remove();
		}
		
	}
	
	private static class MapIterable<T, R> implements Iterable<R> {
		
		private final Iterable<T> iterable;
		
		private final Function<T, R> function;
				
		public MapIterable(Iterable<T> iterable, Function<T, R> function) {
			super();
			this.iterable = iterable;
			this.function = function;
		}

		@Override
		public Iterator<R> iterator() {
			return new MapIterator<>(iterable.iterator(), function);
		}
		
	}

}