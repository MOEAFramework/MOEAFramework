package org.moeaframework.core.indicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.configuration.Validate;
import org.moeaframework.util.TypedProperties;
import org.moeaframework.util.format.Column;
import org.moeaframework.util.format.Formattable;
import org.moeaframework.util.format.TabularData;

/**
 * Helper for evaluating multiple performance indicators, primarily for avoiding repetitive calculations that
 * would occur if creating each indicator separately.
 */
public class Indicators implements Function<NondominatedPopulation, Indicators.IndicatorValues> {

	/**
	 * Creates an instance of this class that evaluates all performance indicators.
	 * 
	 * @param problem the problem
	 * @param referenceSet the reference set
	 * @return the constructed instance
	 */
	public static Indicators all(Problem problem, NondominatedPopulation referenceSet) {
		return from(problem, referenceSet).includeAllMetrics();
	}
	
	/**
	 * Creates an instance of this class that evaluates all standard performance indicators.  This
	 * excludes the R-indicators.
	 * 
	 * @param problem the problem
	 * @param referenceSet the reference set
	 * @return the constructed instance
	 */
	public static Indicators standard(Problem problem, NondominatedPopulation referenceSet) {
		return from(problem, referenceSet).includeStandardMetrics();
	}

	/**
	 * Creates a new instance of this class with no configured indicators.
	 * 
	 * @param problem the problem
	 * @param referenceSet the reference set
	 * @return the constructed instance
	 */
	public static Indicators from(Problem problem, NondominatedPopulation referenceSet) {
		return new Indicators(problem, referenceSet);
	}
	
	/**
	 * Creates an instance of this class mirroring the given quality indicator.  This method simplifies
	 * transitioning away from the old, deprecated {@link QualityIndicator} class.
	 * 
	 * @param qualityIndicator the quality indicator
	 * @return the constructed instance
	 */
	public static Indicators from(QualityIndicator qualityIndicator) {
		return new Indicators(qualityIndicator.getProblem(), qualityIndicator.getReferenceSet())
				.includeStandardMetrics();
	}

	/**
	 * The problem.
	 */
	private final Problem problem;

	/**
	 * The reference set for the problem.
	 */
	private final NondominatedPopulation referenceSet;

	/**
	 * The normalized reference set.
	 */
	private final NondominatedPopulation normalizedReferenceSet;

	/**
	 * The normalizer to normalize populations so that all objectives reside in the range {@code [0, 1]}.
	 */
	private final Normalizer normalizer;

	/**
	 * {@code true} if the hypervolume metric is to be computed; {@code false} otherwise.
	 */
	private boolean includeHypervolume;

	/**
	 * {@code true} if the generational distance metric is to be computed; {@code false} otherwise.
	 */
	private boolean includeGenerationalDistance;

	/**
	 * {@code true} if the inverted generational distance metric is to be computed; {@code false} otherwise.
	 */
	private boolean includeInvertedGenerationalDistance;

	/**
	 * {@code true} if the additive &epsilon;-indicator metric is to be computed; {@code false} otherwise.
	 */
	private boolean includeAdditiveEpsilonIndicator;

	/**
	 * {@code true} if the spacing metric is to be computed; {@code false} otherwise.
	 */
	private boolean includeSpacing;

	/**
	 * {@code true} if the maximum Pareto front error metric is to be computed; {@code false} otherwise.
	 */
	private boolean includeMaximumParetoFrontError;

	/**
	 * {@code true} if the contribution of each approximation set to the reference set is to be computed;
	 * {@code false} otherwise.
	 */
	private boolean includeContribution;

	/**
	 * {@code true} if the R1 indicator is to be computed; {@code false} otherwise.
	 */
	private boolean includeR1;

	/**
	 * {@code true} if the R2 indicator is to be computed; {@code false} otherwise.
	 */
	private boolean includeR2;

	/**
	 * {@code true} if the R3 indicator is to be computed; {@code false} otherwise.
	 */
	private boolean includeR3;

	/**
	 * The &epsilon; values used by the indicators.
	 */
	private double[] epsilon;
	
	/**
	 * The number of subdivisions used by the R indicators.  If unset, will use the default from
	 * {@link RIndicator#getDefaultSubdivisions(Problem)}.
	 */
	private Optional<Integer> subdivisions;

	/**
	 * The cached hypervolume indicator.
	 */
	private Hypervolume hypervolume;

	/**
	 * The cached contribution indicator.
	 */
	private Contribution contribution;

	/**
	 * The cached R1 indicator.
	 */
	private R1Indicator r1;

	/**
	 * The cached R2 indicator.
	 */
	private R2Indicator r2;

	/**
	 * The cached R3 indicator.
	 */
	private R3Indicator r3;

	/**
	 * Constructs a new Indicators object for the given problem.
	 * 
	 * @param problem the problem
	 * @param referenceSet the reference set
	 */
	public Indicators(Problem problem, NondominatedPopulation referenceSet) {
		super();
		this.problem = problem;
		this.referenceSet = referenceSet;

		normalizer = new Normalizer(problem, referenceSet);
		normalizedReferenceSet = normalizer.normalize(referenceSet);
		
		subdivisions = Optional.empty();
	}

	@Override
	public IndicatorValues apply(NondominatedPopulation approximationSet) {
		IndicatorValues result = new IndicatorValues(approximationSet);
		NondominatedPopulation normalizedApproximationSet = normalizer.normalize(approximationSet);

		if (includeHypervolume) {
			if (hypervolume == null) {
				hypervolume = new Hypervolume(problem, referenceSet);
			}

			result.hypervolume = hypervolume.evaluate(approximationSet);
		}

		if (includeGenerationalDistance) {
			result.generationalDistance = GenerationalDistance.evaluate(problem, normalizedApproximationSet,
					normalizedReferenceSet, Settings.getGDPower());
		}

		if (includeInvertedGenerationalDistance) {
			result.invertedGenerationalDistance = InvertedGenerationalDistance.evaluate(problem,
					normalizedApproximationSet, normalizedReferenceSet, Settings.getIGDPower());
		}

		if (includeAdditiveEpsilonIndicator) {
			result.additiveEpsilonIndicator = AdditiveEpsilonIndicator.evaluate(problem,
					normalizedApproximationSet, normalizedReferenceSet);
		}

		if (includeMaximumParetoFrontError) {
			result.maximumParetoFrontError = MaximumParetoFrontError.evaluate(problem,
					normalizedApproximationSet, normalizedReferenceSet);
		}

		if (includeSpacing) {
			result.spacing = Spacing.evaluate(problem, approximationSet);
		}

		if (includeContribution) {
			if (contribution == null) {
				contribution = epsilon == null ? new Contribution(referenceSet) : new Contribution(referenceSet, epsilon);
			}

			result.contribution = contribution.evaluate(approximationSet);
		}

		if (includeR1) {
			if (r1 == null) {
				r1 = new R1Indicator(problem,
						subdivisions.isPresent() ? subdivisions.get() : RIndicator.getDefaultSubdivisions(problem),
						referenceSet);
			}

			result.r1 = r1.evaluate(approximationSet);
		}

		if (includeR2) {
			if (r2 == null) {
				r2 = new R2Indicator(problem,
						subdivisions.isPresent() ? subdivisions.get() : RIndicator.getDefaultSubdivisions(problem),
						referenceSet);
			}

			result.r2 = r2.evaluate(approximationSet);
		}

		if (includeR3) {
			if (r3 == null) {
				r3 = new R3Indicator(problem,
						subdivisions.isPresent() ? subdivisions.get() : RIndicator.getDefaultSubdivisions(problem),
						referenceSet);
			}

			result.r3 = r3.evaluate(approximationSet);
		}

		return result;
	}

	/**
	 * Sets the &epsilon; values used by the indicators.
	 * 
	 * @param epsilon the &epsilon; values
	 * @return a reference to this object
	 */
	public Indicators withEpsilon(double... epsilon) {
		if ((epsilon == null) || (epsilon.length == 0)) {
			this.epsilon = null;
		} else {
			this.epsilon = epsilon;
		}

		return this;
	}
	
	/**
	 * Sets the number of subdivisions used by the R-indicators.
	 * 
	 * @param subdivisions the number of subdivisions
	 * @return a reference to this object
	 */
	public Indicators withSubdivisions(int subdivisions) {
		Validate.greaterThan("subdivisions", 1, subdivisions);
		
		r1 = null;
		r2 = null;
		r3 = null;
		
		this.subdivisions = Optional.of(subdivisions);
		return this;
	}

	/**
	 * Enables the evaluation of the hypervolume metric.
	 * 
	 * @return a reference to this object
	 */
	public Indicators includeHypervolume() {
		includeHypervolume = true;
		return this;
	}

	/**
	 * Enables the evaluation of the generational distance metric.
	 * 
	 * @return a reference to this object
	 */
	public Indicators includeGenerationalDistance() {
		includeGenerationalDistance = true;
		return this;
	}

	/**
	 * Enables the evaluation of the inverted generational distance metric.
	 * 
	 * @return a reference to this object
	 */
	public Indicators includeInvertedGenerationalDistance() {
		includeInvertedGenerationalDistance = true;
		return this;
	}

	/**
	 * Enables the evaluation of the additive &epsilon;-indicator metric.
	 * 
	 * @return a reference to this object
	 */
	public Indicators includeAdditiveEpsilonIndicator() {
		includeAdditiveEpsilonIndicator = true;
		return this;
	}

	/**
	 * Enables the evaluation of the maximum Pareto front error metric.
	 * 
	 * @return a reference to this object
	 */
	public Indicators includeMaximumParetoFrontError() {
		includeMaximumParetoFrontError = true;
		return this;
	}

	/**
	 * Enables the evaluation of the spacing metric.
	 * 
	 * @return a reference to this object
	 */
	public Indicators includeSpacing() {
		includeSpacing = true;
		return this;
	}

	/**
	 * Enables the evaluation of the contribution metric.
	 * 
	 * @return a reference to this object
	 */
	public Indicators includeContribution() {
		includeContribution = true;
		return this;
	}

	/**
	 * Enables the evaluation of the R1 indicator.
	 * 
	 * @return a reference to this object
	 */
	public Indicators includeR1() {
		includeR1 = true;
		return this;
	}

	/**
	 * Enables the evaluation of the R2 indicator.
	 * 
	 * @return a reference to this object
	 */
	public Indicators includeR2() {
		includeR2 = true;
		return this;
	}

	/**
	 * Enables the evaluation of the R3 indicator.
	 * 
	 * @return a reference to this object
	 */
	public Indicators includeR3() {
		includeR3 = true;
		return this;
	}
	
	/**
	 * Enables the evaluation of all standard metrics.  This excludes the R-indicators.
	 * 
	 * @return a reference to this object
	 */
	public Indicators includeStandardMetrics() {
		if (Settings.isHypervolumeEnabled()) {
			includeHypervolume();
		}

		includeGenerationalDistance();
		includeInvertedGenerationalDistance();
		includeAdditiveEpsilonIndicator();
		includeMaximumParetoFrontError();
		includeSpacing();
		includeContribution();
		
		return this;
	}

	/**
	 * Enables the evaluation of all metrics.
	 * 
	 * @return a reference to this object
	 */
	public Indicators includeAllMetrics() {
		includeStandardMetrics();
		includeR1();
		includeR2();
		includeR3();
		
		return this;
	}

	/**
	 * Collection of indicator results, with values defaulting to {@value Double#NaN} if not included.
	 */
	public static class IndicatorValues implements Formattable<Pair<String, Double>> {

		private final NondominatedPopulation approximationSet;

		private double hypervolume;
		private double generationalDistance;
		private double invertedGenerationalDistance;
		private double additiveEpsilonIndicator;
		private double spacing;
		private double maximumParetoFrontError;
		private double contribution;
		private double r1;
		private double r2;
		private double r3;

		public IndicatorValues(NondominatedPopulation approximationSet) {
			super();
			this.approximationSet = approximationSet;

			hypervolume = Double.NaN;
			generationalDistance = Double.NaN;
			invertedGenerationalDistance = Double.NaN;
			additiveEpsilonIndicator = Double.NaN;
			spacing = Double.NaN;
			maximumParetoFrontError = Double.NaN;
			contribution = Double.NaN;
			r1 = Double.NaN;
			r2 = Double.NaN;
			r3 = Double.NaN;
		}

		public NondominatedPopulation getApproximationSet() {
			return approximationSet;
		}

		public double getHypervolume() {
			return hypervolume;
		}

		public double getGenerationalDistance() {
			return generationalDistance;
		}

		public double getInvertedGenerationalDistance() {
			return invertedGenerationalDistance;
		}

		public double getAdditiveEpsilonIndicator() {
			return additiveEpsilonIndicator;
		}

		public double getSpacing() {
			return spacing;
		}

		public double getMaximumParetoFrontError() {
			return maximumParetoFrontError;
		}

		public double getContribution() {
			return contribution;
		}

		public double getR1() {
			return r1;
		}

		public double getR2() {
			return r2;
		}

		public double getR3() {
			return r3;
		}
		
		public TypedProperties asProperties() {
			TypedProperties properties = new TypedProperties();
			
			for (Pair<String, Double> entry : asList()) {
				properties.setDouble(entry.getKey(), entry.getValue());
			}
			
			return properties;
		}

		@Override
		public TabularData<Pair<String, Double>> asTabularData() {
			TabularData<Pair<String, Double>> data = new TabularData<Pair<String, Double>>(asList());
			data.addColumn(new Column<Pair<String, Double>, String>("Indicator", p -> p.getKey()));
			data.addColumn(new Column<Pair<String, Double>, Double>("Value", p -> p.getValue()));
			return data;
		}
		
		private Iterable<Pair<String, Double>> asList() {
			List<Pair<String, Double>> results = new ArrayList<Pair<String, Double>>();

			if (!Double.isNaN(hypervolume)) {
				results.add(Pair.of("Hypervolume", hypervolume));
			}

			if (!Double.isNaN(generationalDistance)) {
				results.add(Pair.of("GenerationalDistance", generationalDistance));
			}
			
			if (!Double.isNaN(invertedGenerationalDistance)) {
				results.add(Pair.of("InvertedGenerationalDistance", invertedGenerationalDistance));
			}
			
			if (!Double.isNaN(additiveEpsilonIndicator)) {
				results.add(Pair.of("AdditiveEpsilonIndicator", additiveEpsilonIndicator));
			}
			
			if (!Double.isNaN(spacing)) {
				results.add(Pair.of("Spacing", spacing));
			}
			
			if (!Double.isNaN(maximumParetoFrontError)) {
				results.add(Pair.of("MaximumParetoFrontError", maximumParetoFrontError));
			}
			
			if (!Double.isNaN(contribution)) {
				results.add(Pair.of("Contribution", contribution));
			}
			
			if (!Double.isNaN(r1)) {
				results.add(Pair.of("R1", r1));
			}
			
			if (!Double.isNaN(r2)) {
				results.add(Pair.of("R2", r2));
			}
			
			if (!Double.isNaN(r3)) {
				results.add(Pair.of("R3", r3));
			}
			
			return results;
		}

	}

}
