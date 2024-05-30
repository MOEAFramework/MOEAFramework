package org.moeaframework.core.indicator;

/**
 * Enumeration of standard indicators provided by the MOEA Framework.  These constants should match the corresponding
 * class names.
 */
public enum StandardIndicator {

	/**
	 * Hypervolume.  The ideal approximation set has a value matching the hypervolume of the reference set.  Since the
	 * sets are normalized, the largest possible hypervolume value is {@code 1.0}, but in practice the Pareto front
	 * typically will not dominate the entire space resulting in a hypervolume {@code <= 1.0}.
	 */
	Hypervolume(true, true, true),
	
	/**
	 * Generational distance (GD).  The ideal approximation set has a distance of {@code 0.0}.
	 */
	GenerationalDistance(false, false, true),
	
	/**
	 * Inverted generational distance (IGD).  The ideal approximation set has a distance of {@code 0.0}.
	 */
	InvertedGenerationalDistance(false, false, true),
	
	/**
	 * Additive epsilon-indicator (AEI).  The ideal approximation set has a value of {@code 0.0}.
	 */
	AdditiveEpsilonIndicator(false, true, true),
	
	/**
	 * Spacing.
	 */
	Spacing(true, false, false),
	
	/**
	 * Maximum Pareto front error.  The ideal approximation set has a value of {@code 0.0}.
	 */
	MaximumParetoFrontError(false, false, true),
	
	/**
	 * Pareto set contribution.  The ideal approximation set has a value of {@code 1.0}, meaning each point in the
	 * reference set is "covered" by a point in the approximation set.
	 */
	Contribution(true, true, true),
	
	/**
	 * R1 indicator.  The ideal approximation set has a value of {@code 1.0}.
	 */
	R1Indicator(true, false, true),
	
	/**
	 * R2 indicator.  The ideal approximation set has a value of {@code -1.0}.
	 */
	R2Indicator(false, false, true),
	
	/**
	 * R3 indicator.  The ideal approximation set has a value of {@value Double#NEGATIVE_INFINITY}.
	 */
	R3Indicator(false, false, true);
	
	private boolean largerValuesPreferred;
	private boolean paretoCompliant;
	private boolean referenceSetRequired;
	
	private StandardIndicator(boolean largerValuesPreferred, boolean paretoCompliant, boolean referenceSetRequired) {
		this.largerValuesPreferred = largerValuesPreferred;
		this.paretoCompliant = paretoCompliant;
		this.referenceSetRequired = referenceSetRequired;
	}
	
	/**
	 * Returns {@code true} if larger indicator values are preferred; {@code false} if smaller values are preferred.
	 * Note that many of these indicators, especially those that are normalized, have bounded ranges.
	 * 
	 * @return {@code true} if larger indicator values are preferred; {@code false} otherwise
	 */
	public boolean areLargerValuesPreferred() {
		return largerValuesPreferred;
	}
	
	/**
	 * Returns {@code true} if the indicator is Pareto compliant, meaning if one approximation set strictly
	 * dominates another set, the indicator value will be lower.
	 * 
	 * @return {@code true} if Pareto compliant; {@code false} otherwise
	 */
	public boolean isParetoCompliant() {
		return paretoCompliant;
	}
	
	/**
	 * Returns {@code true} if a reference set is required to calculate the indicator.  All normalized indicators
	 * require a reference set (unless normalization has been disabled).
	 * 
	 * @return {@code true} if a reference set is required; {@code false} otherwise
	 */
	public boolean isReferenceSetRequired() {
		return referenceSetRequired;
	}
	
}