package org.moeaframework.core.operator.real;

import org.moeaframework.core.Variation;

/**
 * Abstract class for operators that can take a variable number of parents
 * and produce a variable number of offspring.
 */
public abstract class MultiParentVariation implements Variation {

	/**
	 * The number of parents required by this operator.
	 */
	protected int numberOfParents;

	/**
	 * The number of offspring produced by this operator.
	 */
	protected int numberOfOffspring;

	/**
	 * Creates a new multi-parent variation operator.
	 * 
	 * @param numberOfParents the number of parents required by this operator
	 * @param numberOfOffspring the number of offspring produced by this operator
	 */
	public MultiParentVariation(int numberOfParents, int numberOfOffspring) {
		super();
		this.numberOfParents = numberOfParents;
		this.numberOfOffspring = numberOfOffspring;
	}
	

	/**
	 * Returns the number of parents required by this operator.
	 * 
	 * @return the number of parents required by this operator
	 */
	public int getNumberOfParents() {
		return numberOfParents;
	}
	
	/**
	 * Sets the number of parents required by this operator.
	 * 
	 * @param numberOfParents the number of parents required by this operator
	 */
	public void setNumberOfParents(int numberOfParents) {
		this.numberOfParents = numberOfParents;
	}

	/**
	 * Returns the number of offspring produced by this operator.
	 * 
	 * @return the number of offspring produced by this operator
	 */
	public int getNumberOfOffspring() {
		return numberOfOffspring;
	}
	
	/**
	 * Sets the number of offspring produced by this operator.
	 * 
	 * @param numberOfOffspring the number of offspring produced by this operator
	 */
	public void setNumberOfOffspring(int numberOfOffspring) {
		this.numberOfOffspring = numberOfOffspring;
	}
	
	@Override
	public int getArity() {
		return numberOfParents;
	}
	
}
