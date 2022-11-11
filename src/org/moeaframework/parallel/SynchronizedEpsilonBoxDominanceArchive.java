package org.moeaframework.parallel;

import java.util.Comparator;
import java.util.Iterator;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.EpsilonBoxDominanceComparator;

/**
 * Synchronized (thread-safe) version of {@see EpsilonBoxDominanceArchive}.
 */
public class SynchronizedEpsilonBoxDominanceArchive extends EpsilonBoxDominanceArchive {

	public SynchronizedEpsilonBoxDominanceArchive(double epsilon,
			Iterable<? extends Solution> iterable) {
		super(epsilon, iterable);
	}

	public SynchronizedEpsilonBoxDominanceArchive(double epsilon) {
		super(epsilon);
	}

	public SynchronizedEpsilonBoxDominanceArchive(double[] epsilon,
			Iterable<? extends Solution> iterable) {
		super(epsilon, iterable);
	}

	public SynchronizedEpsilonBoxDominanceArchive(double[] epsilon) {
		super(epsilon);
	}

	public SynchronizedEpsilonBoxDominanceArchive(EpsilonBoxDominanceComparator comparator,
			Iterable<? extends Solution> iterable) {
		super(comparator, iterable);
	}

	public SynchronizedEpsilonBoxDominanceArchive(EpsilonBoxDominanceComparator comparator) {
		super(comparator);
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

	@Override
	protected synchronized boolean forceAddWithoutCheck(Solution newSolution) {
		return super.forceAddWithoutCheck(newSolution);
	}

	@Override
	public synchronized EpsilonBoxDominanceComparator getComparator() {
		return super.getComparator();
	}

	@Override
	public synchronized int getNumberOfDominatingImprovements() {
		return super.getNumberOfDominatingImprovements();
	}

	@Override
	public synchronized int getNumberOfImprovements() {
		return super.getNumberOfImprovements();
	}

}
