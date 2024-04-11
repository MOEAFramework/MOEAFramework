/* Copyright 2009-2024 David Hadka
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
package org.moeaframework.core.indicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;
import org.moeaframework.core.Epsilons;
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
 * would occur if creating each indicator separately.  Normalized indicators are, by default, normalized using the
 * provided reference set.  See {@link DefaultNormalizer} for ways to customize normalization.
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
		return of(problem, referenceSet).includeAllMetrics();
	}
	
	/**
	 * Creates an instance of this class that evaluates all standard performance indicators.  This excludes the
	 * R-indicators.
	 * 
	 * @param problem the problem
	 * @param referenceSet the reference set
	 * @return the constructed instance
	 */
	public static Indicators standard(Problem problem, NondominatedPopulation referenceSet) {
		return of(problem, referenceSet).includeStandardMetrics();
	}

	/**
	 * Creates a new instance of this class with no configured indicators.
	 * 
	 * @param problem the problem
	 * @param referenceSet the reference set
	 * @return the constructed instance
	 */
	public static Indicators of(Problem problem, NondominatedPopulation referenceSet) {
		return new Indicators(problem, referenceSet);
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
	private Epsilons epsilons;
	
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

		normalizer = DefaultNormalizer.getInstance().getNormalizer(problem, referenceSet);
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
			result.additiveEpsilonIndicator = AdditiveEpsilonIndicator.evaluate(problem, normalizedApproximationSet,
					normalizedReferenceSet);
		}

		if (includeMaximumParetoFrontError) {
			result.maximumParetoFrontError = MaximumParetoFrontError.evaluate(problem, normalizedApproximationSet,
					normalizedReferenceSet);
		}

		if (includeSpacing) {
			result.spacing = Spacing.evaluate(problem, approximationSet);
		}

		if (includeContribution) {
			if (contribution == null) {
				contribution = epsilons == null ? new Contribution(referenceSet) :
					new Contribution(referenceSet, epsilons);
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
	 * Calculates the indicators for a list of approximation sets.
	 * 
	 * @param approximationSets the approximation sets
	 * @return the indicator values
	 */
	public List<IndicatorValues> applyAll(List<NondominatedPopulation> approximationSets) {
		List<IndicatorValues> result = new ArrayList<IndicatorValues>();
		
		for (NondominatedPopulation approximationSet : approximationSets) {
			result.add(apply(approximationSet));
		}
		
		return result;
	}

	/**
	 * Sets the &epsilon; values used by the indicators.
	 * 
	 * @param epsilons the &epsilon; values
	 * @return a reference to this object so calls can be chained together
	 */
	public Indicators withEpsilons(Epsilons epsilons) {
		this.epsilons = epsilons;
		return this;
	}
	
	/**
	 * Sets the number of subdivisions used by the R-indicators.
	 * 
	 * @param subdivisions the number of subdivisions
	 * @return a reference to this object so calls can be chained together
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
	 * @return a reference to this object so calls can be chained together
	 */
	public Indicators includeHypervolume() {
		includeHypervolume = true;
		return this;
	}
	
	/**
	 * Configures the hypervolume metric using the given reference point.  The hypervolume is then measured
	 * between the Pareto front and this reference point.
	 * 
	 * @param referencePoint the reference point
	 * @return a reference to this object so calls can be chained together
	 */
	public Indicators withHypervolume(double[] referencePoint) {
		includeHypervolume();
		hypervolume = new Hypervolume(problem, referenceSet, referencePoint);
		return this;
	}
	
	/**
	 * Configures the hypervolume metric using the given minimum and maximum bounds of the Pareto set.
	 * 
	 * @param minimum the minimum bounds
	 * @param maximum the maximum bounds
	 * @return a reference to this object so calls can be chained together
	 */
	public Indicators withHypervolume(double[] minimum, double[] maximum) {
		includeHypervolume();
		hypervolume = new Hypervolume(problem, minimum, maximum);
		return this;
	}

	/**
	 * Enables the evaluation of the generational distance metric.
	 * 
	 * @return a reference to this object so calls can be chained together
	 */
	public Indicators includeGenerationalDistance() {
		includeGenerationalDistance = true;
		return this;
	}

	/**
	 * Enables the evaluation of the inverted generational distance metric.
	 * 
	 * @return a reference to this object so calls can be chained together
	 */
	public Indicators includeInvertedGenerationalDistance() {
		includeInvertedGenerationalDistance = true;
		return this;
	}

	/**
	 * Enables the evaluation of the additive &epsilon;-indicator metric.
	 * 
	 * @return a reference to this object so calls can be chained together
	 */
	public Indicators includeAdditiveEpsilonIndicator() {
		includeAdditiveEpsilonIndicator = true;
		return this;
	}

	/**
	 * Enables the evaluation of the maximum Pareto front error metric.
	 * 
	 * @return a reference to this object so calls can be chained together
	 */
	public Indicators includeMaximumParetoFrontError() {
		includeMaximumParetoFrontError = true;
		return this;
	}

	/**
	 * Enables the evaluation of the spacing metric.
	 * 
	 * @return a reference to this object so calls can be chained together
	 */
	public Indicators includeSpacing() {
		includeSpacing = true;
		return this;
	}

	/**
	 * Enables the evaluation of the contribution metric.
	 * 
	 * @return a reference to this object so calls can be chained together
	 */
	public Indicators includeContribution() {
		includeContribution = true;
		return this;
	}

	/**
	 * Enables the evaluation of the R1 indicator.
	 * 
	 * @return a reference to this object so calls can be chained together
	 */
	public Indicators includeR1() {
		includeR1 = true;
		return this;
	}

	/**
	 * Enables the evaluation of the R2 indicator.
	 * 
	 * @return a reference to this object so calls can be chained together
	 */
	public Indicators includeR2() {
		includeR2 = true;
		return this;
	}

	/**
	 * Enables the evaluation of the R3 indicator.
	 * 
	 * @return a reference to this object so calls can be chained together
	 */
	public Indicators includeR3() {
		includeR3 = true;
		return this;
	}
	
	/**
	 * Enables the evaluation of all standard metrics.  This excludes the R-indicators.
	 * 
	 * @return a reference to this object so calls can be chained together
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
	 * @return a reference to this object so calls can be chained together
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

		/**
		 * Constructs a new indicator result object for the given approximation set.  All indicator values are
		 * defaulted to {@value Double#NaN}.
		 * 
		 * @param approximationSet the approximation set used to compute these indicator values
		 */
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

		/**
		 * Returns the approximation set used when computing these indicators values.
		 * 
		 * @return the approximation set
		 */
		public NondominatedPopulation getApproximationSet() {
			return approximationSet;
		}

		/**
		 * Returns the hypervolume value, or {@value Double#NaN} if not configured to compute this metric.
		 * 
		 * @return the hypervolume value
		 */
		public double getHypervolume() {
			return hypervolume;
		}

		/**
		 * Returns the generational distance value, or {@value Double#NaN} if not configured to compute this metric.
		 * 
		 * @return the generational distance value
		 */
		public double getGenerationalDistance() {
			return generationalDistance;
		}

		/**
		 * Returns the inverted generational distance value, or {@value Double#NaN} if not configured to compute this
		 * metric
		 * 
		 * @return the inverted generational distance value
		 */
		public double getInvertedGenerationalDistance() {
			return invertedGenerationalDistance;
		}

		/**
		 * Returns the additive epsilon indicator value, or {@value Double#NaN} if not configured to compute this
		 * metric
		 * 
		 * @return the additive epsilon indicator value
		 */
		public double getAdditiveEpsilonIndicator() {
			return additiveEpsilonIndicator;
		}

		/**
		 * Returns the spacing value, or {@value Double#NaN} if not configured to compute this metric.
		 * 
		 * @return the spacing value
		 */
		public double getSpacing() {
			return spacing;
		}

		/**
		 * Returns the maximum Pareto front error value, or {@value Double#NaN} if not configured to compute this
		 * metric
		 * 
		 * @return the maximum Pareto front error value
		 */
		public double getMaximumParetoFrontError() {
			return maximumParetoFrontError;
		}

		/**
		 * Returns the contribution value, or {@value Double#NaN} if not configured to compute this metric.
		 * 
		 * @return the contribution value
		 */
		public double getContribution() {
			return contribution;
		}

		/**
		 * Returns the R1 indicator value, or {@value Double#NaN} if not configured to compute this metric.
		 * 
		 * @return the R1 indicator value
		 */
		public double getR1() {
			return r1;
		}

		/**
		 * Returns the R2 indicator value, or {@value Double#NaN} if not configured to compute this metric.
		 * 
		 * @return the R2 indicator value
		 */
		public double getR2() {
			return r2;
		}

		/**
		 * Returns the R3 indicator value, or {@value Double#NaN} if not configured to compute this metric.
		 * 
		 * @return the R3 indicator value
		 */
		public double getR3() {
			return r3;
		}
		
		/**
		 * Returns the indicator values as a typed properties object.  This is useful for storing the data, such as
		 * with {@link org.moeaframework.analysis.io.ResultFileWriter}.
		 * 
		 * @return the indicator values as typed properties
		 */
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
