package org.moeaframework.algorithm.pso;

import java.util.Comparator;
import java.util.Iterator;

import org.moeaframework.core.FastNondominatedSorting;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.CrowdingComparator;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;

/**
 * Non-dominated population that maintains the crowding distance attribute
 * for its solutions.  This class is essentially the same as a 
 * {@link NondominatedSortingPopulation}, but only stores non-dominated
 * solutions.
 */
public class CrowdingPopulation extends NondominatedPopulation {

	/**
	 * {@code true} if the population has been modified but the crowding
	 * indicator has not been updated; {@code false} otherwise.
	 */
	private boolean modified;
	
	/**
	 * The maximum capacity of this population.
	 */
	private int capacity;

	/**
	 * The fast non-dominated sorting implementation.
	 */
	private final FastNondominatedSorting fastNondominatedSorting;

	/**
	 * Constructs an empty population that maintains the
	 * {@code crowdingDistance} attribute for its solutions.
	 */
	public CrowdingPopulation(int capacity) {
		this(new ParetoDominanceComparator(), capacity);
	}

	/**
	 * Constructs an empty population that maintains the
	 * {@code crowdingDistance} attribute for its solutions.
	 * 
	 * @param comparator the dominance comparator
	 */
	public CrowdingPopulation(DominanceComparator comparator, int capacity) {
		super();
		this.capacity = capacity;

		modified = false;
		fastNondominatedSorting = new FastNondominatedSorting(comparator);
	}

	/**
	 * Constructs a population initialized with the specified solutions that 
	 * maintains the {@code crowdingDistance} attribute for its solutions.
	 * 
	 * @param comparator the dominance comparator
	 * @param iterable the solutions used to initialize this population
	 */
	public CrowdingPopulation(DominanceComparator comparator, int capacity,
			Iterable<? extends Solution> iterable) {
		this(comparator, capacity);
		addAll(iterable);
	}

	/**
	 * Constructs a population initialized with the specified solutions that 
	 * maintains {@code crowdingDistance} attribute for its solutions.
	 * 
	 * @param iterable the solutions used to initialize this population
	 */
	public CrowdingPopulation(int capacity, Iterable<? extends Solution> iterable) {
		this(new ParetoDominanceComparator(), capacity, iterable);
	}

	@Override
	public boolean add(Solution solution) {
		boolean result = super.add(solution);
		
		if (result) {
			modified = true;
			
			if (size() > capacity) {
				truncate(capacity);
			}
		}
		
		return result;
	}

	@Override
	public Solution get(int index) {
		if (modified) {
			update();
		}

		return super.get(index);
	}

	@Override
	public void remove(int index) {
		modified = true;
		super.remove(index);
	}

	@Override
	public boolean remove(Solution solution) {
		modified = true;
		return super.remove(solution);
	}

	@Override
	public void clear() {
		modified = true;
		super.clear();
	}

	@Override
	public Iterator<Solution> iterator() {
		if (modified) {
			update();
		}

		return super.iterator();
	}

	@Override
	public void sort(Comparator<? super Solution> comparator) {
		if (modified) {
			update();
		}

		super.sort(comparator);
	}

	@Override
	public void truncate(int size, Comparator<? super Solution> comparator) {
		if (modified) {
			update();
		}

		super.truncate(size, comparator);
		modified = true;
	}

	/**
	 * Equivalent to calling {@code truncate(size, new CrowdingComparator())}.
	 * 
	 * @param size the target population size after truncation
	 */
	public void truncate(int size) {
		truncate(size, new CrowdingComparator());
	}

	/**
	 * Updates the crowding distance.
	 */
	public void update() {
		modified = false;
		fastNondominatedSorting.updateCrowdingDistance(this);
	}

}
