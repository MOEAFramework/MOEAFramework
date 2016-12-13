package org.moeaframework.core.operator.real;

/**
 * This class has been renamed to {@link DifferentialEvolutionVariation}.  This
 * class is provided for backwards compatibility, but will be removed in
 * version 3.0.
 * 
 * @deprecated renamed to {@link DifferentialEvolutionVariation}
 */
@Deprecated
public class DifferentialEvolution extends DifferentialEvolutionVariation {

	/**
	 * See {@link DifferentialEvolutionVariation#DifferentialEvolutionVariation(double, double)}
	 * for details.
	 */
	@Deprecated
	public DifferentialEvolution(double CR, double F) {
		super(CR, F);
	}

}
