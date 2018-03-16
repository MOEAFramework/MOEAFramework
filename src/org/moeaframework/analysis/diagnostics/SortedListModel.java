/* Copyright 2009-2018 David Hadka
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
package org.moeaframework.analysis.diagnostics;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.TreeSet;
import java.util.Set;

import javax.swing.ListModel;
import javax.swing.AbstractListModel;

/**
 * A sorted {@link ListModel} which stores only unique entries.
 *
 * @param <T> the type of entry stored in this list model
 */
public class SortedListModel<T extends Comparable<T>> extends 
AbstractListModel {
	
	private static final long serialVersionUID = 833503105693282917L;
	
	/**
	 * The underlying data model.
	 */
	private final Set<T> set;
	
	/**
	 * Constructs a new sorted list model.
	 */
	public SortedListModel() {
		super();
		set = new TreeSet<T>();
	}
	
	/**
	 * Removes the specified item from this list model.
	 * 
	 * @param item the item to remove from this list model
	 */
	public void remove(T item) {
		if (set.remove(item)) {
			fireContentsChanged(this, 0, getSize()-1);
		}
	}
	
	/**
	 * Clears this list model.
	 */
	public void clear() {
		set.clear();
		fireContentsChanged(this, 0, 0);
	}
	
	/**
	 * Adds the specified item to this list model.  If the item already exists,
	 * the list model is unchanged.
	 * 
	 * @param item the item to add to this list model
	 */
	public void add(T item) {
		if (set.add(item)) {
			fireContentsChanged(this, 0, getSize()-1);
		}
	}
	
	/**
	 * Adds all items contained in this collection to this list model.  This
	 * method is implemented by invoking {@link #add(Comparable)}.
	 * 
	 * @param collection the collection of items to add to this list model
	 */
	public void addAll(Collection<? extends T> collection) {
		if (set.addAll(collection)) {
			fireContentsChanged(this, 0, getSize()-1);
		}
	}
	
	/**
	 * Returns the index of the specified item in this list model.
	 * 
	 * @param item the item whose index is returned
	 * @return the index of the specified item in this list model
	 */
	public int getIndexOf(T item) {
		Iterator<T> iterator = set.iterator();
		int index = 0;
		
		while (iterator.hasNext()) {
			if (iterator.next().equals(item)) {
				return index;
			} else {
				index++;
			}
		}
		
		throw new NoSuchElementException();
	}

	@Override
	public int getSize() {
		return set.size();
	}

	@Override
	public T getElementAt(int index) {
		Iterator<T> iterator = set.iterator();
		
		while (index > 0) {
			iterator.next();
			index--;
		}
		
		return iterator.next();
	}

}
