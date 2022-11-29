package org.moeaframework.core.operator;

import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;

/**
 * Identifies mutation operators that evolve exactly one parent.
 */
public interface Mutation extends Variation {
	
	/**
	 * Mutates the given parent to produce an offspring.
	 * 
	 * @param parent the parent solution
	 * @return the offspring
	 */
	public Solution mutate(Solution parent);
	
	@Override
	default public int getArity() {
		return 1;
	}

	@Override
	default public Solution[] evolve(Solution[] parents) {
		return new Solution[] { mutate(parents[0]) };
	}

}
