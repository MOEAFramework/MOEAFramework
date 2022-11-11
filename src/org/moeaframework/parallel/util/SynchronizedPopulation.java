package org.moeaframework.parallel.util;

import java.util.Comparator;
import java.util.Iterator;
import org.moeaframework.core.Population;
import org.moeaframework.core.Solution;

/**
 * Synchronized (thread-safe) version of {@see Population}.
 */
public class SynchronizedPopulation extends Population {

	public SynchronizedPopulation() {
		super();
	}

	public SynchronizedPopulation(Iterable<? extends Solution> iterable) {
		super(iterable);
	}

	public <T extends Solution> SynchronizedPopulation(T[] solutions) {
		super(solutions);
	}

	@Override
	public synchronized boolean add(Solution solution) {
		return super.add(solution);
	}

	@Override
	public synchronized void replace(int index, Solution solution) {
		super.replace(index, solution);
	}

	@Override
	public synchronized Solution get(int index) {
		return super.get(index);
	}

	@Override
	public synchronized void remove(int index) {
		super.remove(index);
	}

	@Override
	public synchronized boolean remove(Solution solution) {
		return super.remove(solution);
	}

	@Override
	public synchronized void clear() {
		super.clear();
	}

	@Override
	public synchronized Iterator<Solution> iterator() {
		return super.iterator();
	}

	@Override
	public synchronized void sort(Comparator<? super Solution> comparator) {
		super.sort(comparator);
	}

	@Override
	public synchronized void truncate(int size, Comparator<? super Solution> comparator) {
		super.truncate(size, comparator);
	}

	@Override
	public synchronized int indexOf(Solution solution) {
		return super.indexOf(solution);
	}

	@Override
	public synchronized boolean addAll(Iterable<? extends Solution> iterable) {
		return super.addAll(iterable);
	}

	@Override
	public synchronized <T extends Solution> boolean addAll(T[] solutions) {
		return super.addAll(solutions);
	}

	@Override
	public synchronized boolean contains(Solution solution) {
		return super.contains(solution);
	}

	@Override
	public synchronized boolean containsAll(Iterable<? extends Solution> iterable) {
		return super.containsAll(iterable);
	}

	@Override
	public synchronized <T extends Solution> boolean containsAll(T[] solutions) {
		return super.containsAll(solutions);
	}

	@Override
	public synchronized boolean isEmpty() {
		return super.isEmpty();
	}

	@Override
	public synchronized boolean removeAll(Iterable<? extends Solution> iterable) {
		return super.removeAll(iterable);
	}

	@Override
	public synchronized <T extends Solution> boolean removeAll(T[] solutions) {
		return super.removeAll(solutions);
	}

	@Override
	public synchronized int size() {
		return super.size();
	}

}
