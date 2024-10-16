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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

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
	 * @param <T> the type of each value
	 * @param iterators the iterators
	 * @return the joined iterator
	 */
	@SafeVarargs
	public static <T> Iterator<T> join(Iterator<T>... iterators) {
		return new JoinIterators<T>(iterators);
	}
	
	/**
	 * Returns an iterable that joins the contents of multiple iterables.
	 * 
	 * @param <T> the type of each value
	 * @param iterables the iterables
	 * @return the joined iterable
	 */
	@SafeVarargs
	public static <T> Iterable<T> join(Iterable<T>... iterables) {
		return new JoinIterables<T>(iterables);
	}
	
	/**
	 * Returns an iterator that tracks the index of each value.
	 * 
	 * @param <T> the type of each value
	 * @param iterator the iterator
	 * @return the indexed value
	 */
	public static <T> Iterator<IndexedValue<T>> enumerate(Iterator<T> iterator) {
		return new IndexedIterator<T>(iterator);
	}
	
	/**
	 * Returns an iterable that tracks the index of each value.
	 * 
	 * @param <T> the type of each value
	 * @param iterable the iterable
	 * @return the indexed iterable
	 */
	public static <T> Iterable<IndexedValue<T>> enumerate(Iterable<T> iterable) {
		return new IndexedIterable<T>(iterable);
	}
	
	public static <T> Iterator<Pair<T, T>> zip(Iterator<T> iterator1, Iterator<T> iterator2) {
		return new ZipIterator<T>(iterator1, iterator2);
	}
	
	public static <T> Iterable<Pair<T, T>> zip(Iterable<T> iterable1, Iterable<T> iterable2) {
		return new ZipIterable<T>(iterable1, iterable2);
	}
	
	public static <T> Iterable<Pair<T, T>> zip(T[] array1, T[] array2) {
		return zip(List.of(array1), List.of(array2));
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
			List<Iterator<T>> iterators = new ArrayList<Iterator<T>>();
			
			for (Iterable<T> iterable : iterables) {
				iterators.add(iterable.iterator());
			}
			
			return new JoinIterators<T>(iterators);
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
			return new IndexedIterator<T>(iterable.iterator());
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
			return new IndexedValue<T>(index++, iterator.next());
		}

		@Override
		public void remove() {
			iterator.remove();
		}
		
	}
	
	private static class ZipIterable<T> implements Iterable<Pair<T, T>> {
		
		private final Iterable<T> iterable1;
		
		private final Iterable<T> iterable2;
		
		public ZipIterable(Iterable<T> iterable1, Iterable<T> iterable2) {
			super();
			this.iterable1 = iterable1;
			this.iterable2 = iterable2;
		}

		@Override
		public Iterator<Pair<T, T>> iterator() {
			return new ZipIterator<T>(iterable1.iterator(), iterable2.iterator());
		}
		
	}
	
	private static class ZipIterator<T> implements Iterator<Pair<T, T>> {
		
		private final Iterator<T> iterator1;
		
		private final Iterator<T> iterator2;
				
		public ZipIterator(Iterator<T> iterator1, Iterator<T> iterator2) {
			super();
			this.iterator1 = iterator1;
			this.iterator2 = iterator2;
		}

		@Override
		public boolean hasNext() {
			return iterator1.hasNext() && iterator2.hasNext();
		}

		@Override
		public Pair<T, T> next() {
			return Pair.of(iterator1.next(), iterator2.next());
		}

		@Override
		public void remove() {
			iterator1.remove();
			iterator2.remove();
		}
		
	}
}