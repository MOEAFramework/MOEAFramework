/* Copyright 2009-2011 David Hadka
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

import java.util.Collections;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.event.EventListenerList;

/**
 * Stores a set of {@link EventListener}s. This aims to be more typesafe than
 * creating lists or other collections of listeners while being more convenient
 * and lightweight than {@link EventListenerList}.
 * 
 * @param <T> the type of listener; must extend {@link EventListener}
 */
public class Listeners<T extends EventListener> implements Iterable<T> {

	/**
	 * Internal collection of listeners.
	 */
	private List<T> listeners;

	/**
	 * Constructs an empty collection of listeners.
	 */
	public Listeners() {
		listeners = new Vector<T>();
	}

	/**
	 * Returns an unmodifiable iterator over the listeners.  The iterator
	 * traverses a copy of the internal list, allowing the listeners to be
	 * modified without causing a {@code ConcurrentModificationException}.
	 * 
	 * @return an unmodifiable iterator over the listeners
	 */
	@Override
	public Iterator<T> iterator() {
		return Collections.unmodifiableList(new Vector<T>(listeners))
				.iterator();
	}

	/**
	 * Adds the specified listener to this collection of listeners.
	 * 
	 * @param listener the listener
	 */
	public void add(T listener) {
		listeners.add(listener);
	}

	/**
	 * Removes the specified listener from this collection of listeners.
	 * 
	 * @param listener the listener
	 */
	public void remove(T listener) {
		listeners.remove(listener);
	}

	/**
	 * Removes all listeners from this collection of listeners.
	 */
	public void clear() {
		listeners.clear();
	}

}
