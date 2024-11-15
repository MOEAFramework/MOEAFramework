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
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;
import org.moeaframework.core.Epsilons;
import org.moeaframework.core.Settings;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.problem.Problem;
import org.moeaframework.util.format.Column;
import org.moeaframework.util.format.Formattable;
import org.moeaframework.util.format.TabularData;
import org.moeaframework.util.validate.Validate;

/**
 * Helper for evaluating multiple performance indicators, primarily for avoiding repetitive calculations that
 * would occur if creating each indicator separately.  Normalized indicators are, by default, normalized using the
 * provided reference set.  See {@link DefaultNormalizer} for ways to customize normalization.
 */
public class Indicators implements Function<NondominatedPopulation, Indicators.IndicatorValues> {

	/**
	 * The problem.
	 */
	private final Problem problem;

	/**
	 * The reference set for the problem.
	 */
	private final NondominatedPopulation referenceSet;

	/**
	 * The normalizer to normalize populations so that all objectives reside in the range {@code [0, 1]}.  Can be
	 * {@code null}, in which case each indicator is responsible for configuring its normalization.
	 */
	private final Normalizer normalizer;
	
	/**
	 * The indicators that have been selected.
	 */
	private final EnumSet<StandardIndicator> selectedIndicators;

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
	 * The cached generational distance (GD) indicator.
	 */
	private GenerationalDistance generationalDistance;
	
	/**
	 * The cached generational distance plus (GD+) indicator.
	 */
	private GenerationalDistancePlus generationalDistancePlus;
	
	/**
	 * The cached inverted generational distance (IGD) indicator.
	 */
	private InvertedGenerationalDistance invertedGenerationalDistance;
	
	/**
	 * The cached inverted generational distance plus (IGD+) indicator.
	 */
	private InvertedGenerationalDistancePlus invertedGenerationalDistancePlus;
	
	/**
	 * The cached additive epsilon indicator.
	 */
	private AdditiveEpsilonIndicator additiveEpsilonIndicator;
	
	/**
	 * The cached maximum Pareto front error indicator.
	 */
	private MaximumParetoFrontError maximumParetoFrontError;
	
	/**
	 * The cached spacing indicator.
	 */
	private Spacing spacing;

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
		this(problem, referenceSet, null);
	}
	
	/**
	 * Constructs a new Indicators object for the given problem.
	 * 
	 * @param problem the problem
	 * @param referenceSet the reference set
	 * @param normalizer the user-provided normalizer, or {@code null} if the default is used
	 */
	public Indicators(Problem problem, NondominatedPopulation referenceSet, Normalizer normalizer) {
		super();
		this.problem = problem;
		this.referenceSet = referenceSet;
		this.normalizer = normalizer;
		
		Validate.that("problem", problem).isNotNull();
		Validate.that("referenceSet", referenceSet).isNotNull();
		
		selectedIndicators = EnumSet.noneOf(StandardIndicator.class);
		subdivisions = Optional.empty();
	}
	
	private void initialize() {
		if (selectedIndicators.contains(StandardIndicator.Hypervolume) && hypervolume == null) {
			hypervolume = normalizer == null ? new Hypervolume(problem, referenceSet) : new Hypervolume(problem, normalizer);
		}
		
		if (selectedIndicators.contains(StandardIndicator.GenerationalDistance) && generationalDistance == null) {
			generationalDistance = new GenerationalDistance(problem, referenceSet, normalizer, Settings.getGDPower());
		}
		
		if (selectedIndicators.contains(StandardIndicator.GenerationalDistancePlus) && generationalDistancePlus == null) {
			generationalDistancePlus = new GenerationalDistancePlus(problem, referenceSet, normalizer);
		}
		
		if (selectedIndicators.contains(StandardIndicator.InvertedGenerationalDistance) && invertedGenerationalDistance == null) {
			invertedGenerationalDistance = new InvertedGenerationalDistance(problem, referenceSet, normalizer, Settings.getIGDPower());
		}
		
		if (selectedIndicators.contains(StandardIndicator.InvertedGenerationalDistancePlus) && invertedGenerationalDistancePlus == null) {
			invertedGenerationalDistancePlus = new InvertedGenerationalDistancePlus(problem, referenceSet, normalizer);
		}
		
		if (selectedIndicators.contains(StandardIndicator.AdditiveEpsilonIndicator) && additiveEpsilonIndicator == null) {
			additiveEpsilonIndicator = new AdditiveEpsilonIndicator(problem, referenceSet, normalizer);
		}
		
		if (selectedIndicators.contains(StandardIndicator.MaximumParetoFrontError) && maximumParetoFrontError == null) {
			maximumParetoFrontError = new MaximumParetoFrontError(problem, referenceSet, normalizer);
		}
		
		if (selectedIndicators.contains(StandardIndicator.Spacing) && spacing == null) {
			spacing = new Spacing(problem);
		}
		
		if (selectedIndicators.contains(StandardIndicator.Contribution) && contribution == null) {
			contribution = epsilons == null ? new Contribution(referenceSet) : new Contribution(referenceSet, epsilons);
		}
		
		if (selectedIndicators.contains(StandardIndicator.R1Indicator) && r1 == null) {
			r1 = new R1Indicator(
					problem,
					subdivisions.isPresent() ? subdivisions.get() : RIndicator.getDefaultSubdivisions(problem),
					referenceSet,
					normalizer);
		}
		
		if (selectedIndicators.contains(StandardIndicator.R2Indicator) && r2 == null) {
			r2 = new R2Indicator(
					problem,
					subdivisions.isPresent() ? subdivisions.get() : RIndicator.getDefaultSubdivisions(problem),
					referenceSet,
					normalizer);
		}
		
		if (selectedIndicators.contains(StandardIndicator.R3Indicator) && r3 == null) {
			r3 = new R3Indicator(
					problem,
					subdivisions.isPresent() ? subdivisions.get() : RIndicator.getDefaultSubdivisions(problem),
					referenceSet,
					normalizer);
		}
	}
	
	@Override
	public IndicatorValues apply(NondominatedPopulation approximationSet) {
		initialize();
		
		IndicatorValues result = new IndicatorValues(approximationSet);

		if (selectedIndicators.contains(StandardIndicator.Hypervolume)) {
			result.hypervolume = hypervolume.evaluate(approximationSet);
		}

		if (selectedIndicators.contains(StandardIndicator.GenerationalDistance)) {
			result.generationalDistance = generationalDistance.evaluate(approximationSet);
		}
		
		if (selectedIndicators.contains(StandardIndicator.GenerationalDistancePlus)) {
			result.generationalDistancePlus = generationalDistancePlus.evaluate(approximationSet);
		}

		if (selectedIndicators.contains(StandardIndicator.InvertedGenerationalDistance)) {
			result.invertedGenerationalDistance = invertedGenerationalDistance.evaluate(approximationSet);
		}
		
		if (selectedIndicators.contains(StandardIndicator.InvertedGenerationalDistancePlus)) {
			result.invertedGenerationalDistancePlus = invertedGenerationalDistancePlus.evaluate(approximationSet);
		}

		if (selectedIndicators.contains(StandardIndicator.AdditiveEpsilonIndicator)) {
			result.additiveEpsilonIndicator = additiveEpsilonIndicator.evaluate(approximationSet);
		}

		if (selectedIndicators.contains(StandardIndicator.MaximumParetoFrontError)) {
			result.maximumParetoFrontError = maximumParetoFrontError.evaluate(approximationSet);
		}

		if (selectedIndicators.contains(StandardIndicator.Spacing)) {
			result.spacing = Spacing.evaluate(problem, approximationSet);
		}

		if (selectedIndicators.contains(StandardIndicator.Contribution)) {
			result.contribution = contribution.evaluate(approximationSet);
		}

		if (selectedIndicators.contains(StandardIndicator.R1Indicator)) {
			result.r1 = r1.evaluate(approximationSet);
		}

		if (selectedIndicators.contains(StandardIndicator.R2Indicator)) {
			result.r2 = r2.evaluate(approximationSet);
		}

		if (selectedIndicators.contains(StandardIndicator.R3Indicator)) {
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
		Validate.that("subdivisions", subdivisions).isGreaterThan(1);
		
		r1 = null;
		r2 = null;
		r3 = null;
		
		this.subdivisions = Optional.of(subdivisions);
		return this;
	}
	
	/**
	 * Returns the performance indicators that will be evaluated.
	 * 
	 * @return the selected performance indicators
	 */
	public EnumSet<StandardIndicator> getSelectedIndicators() {
		return selectedIndicators.clone();
	}
	
	/**
	 * Enables the evaluation of the given performance indicator.
	 * 
	 * @param indicator the indicator to enable
	 * @return a reference to this object so calls can be chained together
	 */
	public Indicators include(StandardIndicator indicator) {
		selectedIndicators.add(indicator);
		return this;
	}
	
	/**
	 * Enables the evaluation of the selected indicators.
	 * 
	 * @param indicators the indicators to enable
	 * @return a reference to this object so calls can be chained together
	 */
	public Indicators include(EnumSet<StandardIndicator> indicators) {
		for (StandardIndicator indicator : indicators) {
			include(indicator);
		}
		
		return this;
	}
	
	/**
	 * Enables the evaluation of the hypervolume metric.
	 * 
	 * @return a reference to this object so calls can be chained together
	 */
	public Indicators includeHypervolume() {
		include(StandardIndicator.Hypervolume);
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
		include(StandardIndicator.GenerationalDistance);
		return this;
	}
	
	/**
	 * Enables the evaluation of the generational distance plus metric.
	 * 
	 * @return a reference to this object so calls can be chained together
	 */
	public Indicators includeGenerationalDistancePlus() {
		include(StandardIndicator.GenerationalDistancePlus);
		return this;
	}

	/**
	 * Enables the evaluation of the inverted generational distance metric.
	 * 
	 * @return a reference to this object so calls can be chained together
	 */
	public Indicators includeInvertedGenerationalDistance() {
		include(StandardIndicator.InvertedGenerationalDistance);
		return this;
	}
	
	/**
	 * Enables the evaluation of the inverted generational distance plus metric.
	 * 
	 * @return a reference to this object so calls can be chained together
	 */
	public Indicators includeInvertedGenerationalDistancePlus() {
		include(StandardIndicator.InvertedGenerationalDistancePlus);
		return this;
	}

	/**
	 * Enables the evaluation of the additive &epsilon;-indicator metric.
	 * 
	 * @return a reference to this object so calls can be chained together
	 */
	public Indicators includeAdditiveEpsilonIndicator() {
		include(StandardIndicator.AdditiveEpsilonIndicator);
		return this;
	}

	/**
	 * Enables the evaluation of the maximum Pareto front error metric.
	 * 
	 * @return a reference to this object so calls can be chained together
	 */
	public Indicators includeMaximumParetoFrontError() {
		include(StandardIndicator.MaximumParetoFrontError);
		return this;
	}

	/**
	 * Enables the evaluation of the spacing metric.
	 * 
	 * @return a reference to this object so calls can be chained together
	 */
	public Indicators includeSpacing() {
		include(StandardIndicator.Spacing);
		return this;
	}

	/**
	 * Enables the evaluation of the contribution metric.
	 * 
	 * @return a reference to this object so calls can be chained together
	 */
	public Indicators includeContribution() {
		include(StandardIndicator.Contribution);
		return this;
	}

	/**
	 * Enables the evaluation of the R1 indicator.
	 * 
	 * @return a reference to this object so calls can be chained together
	 */
	public Indicators includeR1() {
		include(StandardIndicator.R1Indicator);
		return this;
	}

	/**
	 * Enables the evaluation of the R2 indicator.
	 * 
	 * @return a reference to this object so calls can be chained together
	 */
	public Indicators includeR2() {
		include(StandardIndicator.R2Indicator);
		return this;
	}

	/**
	 * Enables the evaluation of the R3 indicator.
	 * 
	 * @return a reference to this object so calls can be chained together
	 */
	public Indicators includeR3() {
		include(StandardIndicator.R3Indicator);
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
		includeGenerationalDistancePlus();
		includeInvertedGenerationalDistancePlus();
		includeR1();
		includeR2();
		includeR3();
		
		return this;
	}
	
	/**
	 * Returns the specified indicator.
	 * 
	 * @param indicator the indicator
	 * @return the indicator
	 */
	public Indicator getIndicator(StandardIndicator indicator) {
		initialize();
		
		return switch (indicator) {
			case Hypervolume -> hypervolume;
			case GenerationalDistance -> generationalDistance;
			case GenerationalDistancePlus -> generationalDistancePlus;
			case InvertedGenerationalDistance -> invertedGenerationalDistance;
			case InvertedGenerationalDistancePlus -> invertedGenerationalDistancePlus;
			case AdditiveEpsilonIndicator -> additiveEpsilonIndicator;
			case MaximumParetoFrontError -> maximumParetoFrontError;
			case Contribution -> contribution;
			case Spacing -> spacing;
			case R1Indicator -> r1;
			case R2Indicator -> r2;
			case R3Indicator -> r3;
		};
	}

	/**
	 * Collection of indicator results, with values defaulting to {@value Double#NaN} if not included.
	 */
	public static class IndicatorValues implements Formattable<Pair<StandardIndicator, Double>> {

		private final NondominatedPopulation approximationSet;

		private double hypervolume;
		private double generationalDistance;
		private double generationalDistancePlus;
		private double invertedGenerationalDistance;
		private double invertedGenerationalDistancePlus;
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
			generationalDistancePlus = Double.NaN;
			invertedGenerationalDistance = Double.NaN;
			invertedGenerationalDistancePlus = Double.NaN;
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
		 * Returns the indicator value, or {@value Double#NaN} if not configured to compute this metric.
		 * 
		 * @param indicator the indicator
		 * @return the indicator value
		 */
		public double get(StandardIndicator indicator) {
			return switch (indicator) {
				case Hypervolume -> getHypervolume();
				case GenerationalDistance -> getGenerationalDistance();
				case GenerationalDistancePlus -> getGenerationalDistancePlus();
				case InvertedGenerationalDistance -> getInvertedGenerationalDistance();
				case InvertedGenerationalDistancePlus -> getInvertedGenerationalDistancePlus();
				case AdditiveEpsilonIndicator -> getAdditiveEpsilonIndicator();
				case Spacing -> getSpacing();
				case MaximumParetoFrontError -> getMaximumParetoFrontError();
				case Contribution -> getContribution();
				case R1Indicator -> getR1();
				case R2Indicator -> getR2();
				case R3Indicator -> getR3();
			};
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
		 * Returns the generational distance plus value, or {@value Double#NaN} if not configured to compute this
		 * metric.
		 * 
		 * @return the generational distance plus value
		 */
		public double getGenerationalDistancePlus() {
			return generationalDistancePlus;
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
		 * Returns the inverted generational distance plus value, or {@value Double#NaN} if not configured to compute
		 * this metric
		 * 
		 * @return the inverted generational distance plus value
		 */
		public double getInvertedGenerationalDistancePlus() {
			return invertedGenerationalDistancePlus;
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
			
			for (Pair<StandardIndicator, Double> entry : asList()) {
				properties.setDouble(entry.getKey().name(), entry.getValue());
			}
			
			return properties;
		}

		@Override
		public TabularData<Pair<StandardIndicator, Double>> asTabularData() {
			TabularData<Pair<StandardIndicator, Double>> data = new TabularData<Pair<StandardIndicator, Double>>(asList());
			data.addColumn(new Column<Pair<StandardIndicator, Double>, String>("Indicator", p -> p.getKey().name()));
			data.addColumn(new Column<Pair<StandardIndicator, Double>, Double>("Value", p -> p.getValue()));
			return data;
		}
		
		private Iterable<Pair<StandardIndicator, Double>> asList() {
			List<Pair<StandardIndicator, Double>> results = new ArrayList<Pair<StandardIndicator, Double>>();
			
			for (StandardIndicator indicator : StandardIndicator.values()) {
				double value = get(indicator);
				
				if (!Double.isNaN(value)) {
					results.add(Pair.of(indicator, value));
				}
			}
			
			return results;
		}

	}
	
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
	
}
