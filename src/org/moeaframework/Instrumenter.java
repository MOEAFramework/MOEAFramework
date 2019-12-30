/* Copyright 2009-2018 David Hadka
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
package org.moeaframework;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;
import org.moeaframework.algorithm.PeriodicAction.FrequencyType;
import org.moeaframework.analysis.collector.Accumulator;
import org.moeaframework.analysis.collector.AdaptiveMultimethodVariationCollector;
import org.moeaframework.analysis.collector.AdaptiveTimeContinuationCollector;
import org.moeaframework.analysis.collector.ApproximationSetCollector;
import org.moeaframework.analysis.collector.Collector;
import org.moeaframework.analysis.collector.ElapsedTimeCollector;
import org.moeaframework.analysis.collector.EpsilonProgressCollector;
import org.moeaframework.analysis.collector.IndicatorCollector;
import org.moeaframework.analysis.collector.InstrumentedAlgorithm;
import org.moeaframework.analysis.collector.PopulationSizeCollector;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.indicator.AdditiveEpsilonIndicator;
import org.moeaframework.core.indicator.Contribution;
import org.moeaframework.core.indicator.GenerationalDistance;
import org.moeaframework.core.indicator.Hypervolume;
import org.moeaframework.core.indicator.InvertedGenerationalDistance;
import org.moeaframework.core.indicator.R1Indicator;
import org.moeaframework.core.indicator.R2Indicator;
import org.moeaframework.core.indicator.R3Indicator;
import org.moeaframework.core.indicator.Spacing;
import org.moeaframework.core.spi.ProblemFactory;

/**
 * Instruments algorithms with {@link Collector}s which record information about
 * the runtime behavior of algorithms.  First, the instrumenter walks the object
 * graph of an algorithm to determine its composition.  Upon finding objects
 * which can be instrumented, it attaches the corresponding collector.  Next,
 * the instrumenter returns an {@link InstrumentedAlgorithm}, which orchestrates
 * the collection of runtime information as the algorithm is executed.  Lastly,
 * the {@code InstrumentedAlgorithm} stores the runtime information, which can
 * subsequently be accessed and analyzed.
 * <p>
 * <pre>
 *   Instrumenter instrumenter = new Instrumenter()
 *     .withProblem(problemName)
 *     .attachAll();
 * 
 *   Executor executor = new Executor()
 *     .withProblem(problemName)
 *     .withAlgorithm(algorithmName)
 *     .withMaxEvaluations(numberOfEvaluations)
 *     .withInstrumenter(instrumenter)
 *     .run();
 * 
 *   Accumulator accumulator = instrumenter.getLastAccumulator();
 * </pre>
 * Note that the Instrumenter will not scan the contents of {@link Solution} or
 * {@link Problem}.  Instead, use the Instrumenter to enumerate these objects
 * and access their properties programmatically.
 */
public class Instrumenter extends ProblemBuilder {

	/**
	 * {@code true} if the hypervolume collector is included; {@code false}
	 * otherwise.
	 */
	private boolean includeHypervolume;
	
	/**
	 * {@code true} if the generational distance collector is included;
	 * {@code false} otherwise.
	 */
	private boolean includeGenerationalDistance;
	
	/**
	 * {@code true} if the inverted generational distance collector is included;
	 * {@code false} otherwise.
	 */
	private boolean includeInvertedGenerationalDistance;
	
	/**
	 * {@code true} if the spacing collector is included; {@code false}
	 * otherwise.
	 */
	private boolean includeSpacing;
	
	/**
	 * {@code true} if the additive &epsilon;-indicator collector is included;
	 * {@code false} otherwise.
	 */
	private boolean includeAdditiveEpsilonIndicator;
	
	/**
	 * {@code true} if the contribution collector is included; {@code false}
	 * otherwise.
	 */
	private boolean includeContribution;
	
	/**
	 * {@code true} if the R1 collector is included; {@code false} otherwise.
	 */
	private boolean includeR1;
	
	/**
	 * {@code true} if the R2 collector is included; {@code false} otherwise.
	 */
	private boolean includeR2;
	
	/**
	 * {@code true} if the R3 collector is included; {@code false} otherwise.
	 */
	private boolean includeR3;
	
	/**
	 * {@code true} if the &epsilon;-progress collector is included;
	 * {@code false} otherwise.
	 */
	private boolean includeEpsilonProgress;
	
	/**
	 * {@code true} if the adaptive multimethod variation collector is included;
	 * {@code false} otherwise.
	 */
	private boolean includeAdaptiveMultimethodVariation;
	
	/**
	 * {@code true} if the adaptive time continuation collector is included;
	 * {@code false} otherwise.
	 */
	private boolean includeAdaptiveTimeContinuation;
	
	/**
	 * {@code true} if the elapsed time collector is included; {@code false}
	 * otherwise.
	 */
	private boolean includeElapsedTime;
	
	/**
	 * {@code true} if the approximation set collector is included;
	 * {@code false} otherwise.
	 */
	private boolean includeApproximationSet;
	
	/**
	 * {@code true} if the population size collector is included; {@code false}
	 * otherwise.
	 */
	private boolean includePopulationSize;

	/**
	 * The frequency that data is collected.
	 */
	private int frequency;
	
	/**
	 * The units for the frequency, either in terms of the number of evaluations
	 * or number of steps (iterations).
	 */
	private FrequencyType frequencyType;
	
	/**
	 * The collection of custom collectors added through the 
	 * {@link #attach(Collector)} method.  This does not include built-in
	 * collectors.
	 */
	private final List<Collector> customCollectors;
	
	/**
	 * The excluded packages.  Any objects from these packages will not be
	 * walked.
	 */
	private final List<String> excludedPackages;
	
	/**
	 * The accumulator from the last instrumented algorithm.
	 */
	private Accumulator lastAccumulator;
	
	/**
	 * Constructs a new instrumenter instance, initially with no collectors.
	 */
	public Instrumenter() {
		super();
		
		frequency = 100;
		frequencyType = FrequencyType.EVALUATIONS;
		customCollectors = new ArrayList<Collector>();
		
		excludedPackages = new ArrayList<String>();
		excludedPackages.add("java");
	}
	
	/**
	 * Returns the accumulator from the last instrumented algorithm.  The
	 * accumulator will be filled with the runtime information as the algorithm
	 * is executed.
	 * 
	 * @return the accumulator from the last instrumented algorithm
	 */
	public Accumulator getLastAccumulator() {
		return lastAccumulator;
	}
	
	/**
	 * Adds an excluded package that will not be walked by this instrumenter.
	 * 
	 * @param packageName the package name
	 * @return a reference to this instrumenter
	 */
	public Instrumenter addExcludedPackage(String packageName) {
		excludedPackages.add(packageName);
		
		return this;
	}
	
	/**
	 * Removes an excluded package from this instrumenter.
	 * 
	 * @param packageName the package name
	 * @return a reference to this instrumenter
	 */
	public Instrumenter removeExcludedPackage(String packageName) {
		excludedPackages.add(packageName);
		
		return this;
	}
	
	/**
	 * Allows this instrumenter to visit classes in the given package.  Some
	 * Java classes can not be readily visited/discovered by this instrumenter,
	 * possibly resulting in a {@code NullPointerException} or a
	 * {@code SecurityException}.  Therefore, the instrumenter can only visit
	 * classes explicitly allowed by the user.  By default,
	 * {@code org.moeaframework} is allowed.
	 * 
	 * @param packageName a package name or the package name prefix
	 * @return a reference to this instrumenter
	 * @deprecated has no effect
	 */
	@Deprecated
	public Instrumenter addAllowedPackage(String packageName) {
		return this;
	}
	
	/**
	 * Removes one of the packages this instrumenter is allowed to visit.
	 * Note that the given string must match the string passed to
	 * {@link #addAllowedPackage(String)}.
	 * 
	 * @param packageName the package name or package name prefix to remove
	 * @return a reference to this instrumenter
	 * @deprecated has no effect
	 */
	@Deprecated
	public Instrumenter removeAllowedPackage(String packageName) {
		return this;
	}
	
	/**
	 * Returns all packages this instrumenter is allowed to visit.
	 * 
	 * @return the list of allowed package names
	 * @deprecated has no effect
	 */
	@Deprecated
	public List<String> getAllowedPackages() {
		return new ArrayList<String>();
	}
	
	/**
	 * Sets the frequency that data is collected.  
	 * 
	 * @param frequency the frequency
	 * @return a reference to this instrumenter
	 */
	public Instrumenter withFrequency(int frequency) {
		this.frequency = frequency;
		
		return this;
	}
	
	/**
	 * Indicates if the frequency is defined in terms of the number of
	 * evaluations or number of steps (iterations).
	 * 
	 * @param frequencyType the frequency type, either EVALUATIONS or STEPS
	 * @return a reference to this instrumenter
	 */
	public Instrumenter withFrequencyType(FrequencyType frequencyType) {
		this.frequencyType = frequencyType;
		
		return this;
	}
	
	/**
	 * Includes the specified collector when instrumenting algorithms.
	 * 
	 * @param collector the collector to include when instrumenting algorithms
	 * @return a reference to this instrumenter
	 */
	public Instrumenter attach(Collector collector) {
		customCollectors.add(collector);
		
		return this;
	}
	
	/**
	 * Includes the hypervolume collector when instrumenting algorithms.
	 * 
	 * @return a reference to this instrumenter
	 */
	public Instrumenter attachHypervolumeCollector() {
		includeHypervolume = true;
		
		return this;
	}
	
	/**
	 * Includes the generational distance collector when instrumenting 
	 * algorithms.
	 * 
	 * @return a reference to this instrumenter
	 */
	public Instrumenter attachGenerationalDistanceCollector() {
		includeGenerationalDistance = true;
		
		return this;
	}
	
	/**
	 * Includes the inverted generational distance collector when instrumenting
	 * algorithms.
	 * 
	 * @return a reference to this instrumenter
	 */
	public Instrumenter attachInvertedGenerationalDistanceCollector() {
		includeInvertedGenerationalDistance = true;
		
		return this;
	}
	
	/**
	 * Includes the spacing collector when instrumenting algorithms.
	 * 
	 * @return a reference to this instrumenter
	 */
	public Instrumenter attachSpacingCollector() {
		includeSpacing = true;
		
		return this;
	}
	
	/**
	 * Includes the additive &epsilon;-indicator collector when instrumenting
	 * algorithms.
	 * 
	 * @return a reference to this instrumenter
	 */
	public Instrumenter attachAdditiveEpsilonIndicatorCollector() {
		includeAdditiveEpsilonIndicator = true;
		
		return this;
	}
	
	/**
	 * Includes the contribution collector when instrumenting algorithms.
	 * 
	 * @return a reference to this instrumenter
	 */
	public Instrumenter attachContributionCollector() {
		includeContribution = true;
		
		return this;
	}
	
	/**
	 * Includes the R1 collector when instrumenting algorithms.
	 * 
	 * @return a reference to this instrumenter
	 */
	public Instrumenter attachR1Collector() {
		includeR1 = true;
		
		return this;
	}
	
	/**
	 * Includes the R2 collector when instrumenting algorithms.
	 * 
	 * @return a reference to this instrumenter
	 */
	public Instrumenter attachR2Collector() {
		includeR2 = true;
		
		return this;
	}
	
	/**
	 * Includes the R3 collector when instrumenting algorithms.
	 * 
	 * @return a reference to this instrumenter
	 */
	public Instrumenter attachR3Collector() {
		includeR3 = true;
		
		return this;
	}
	
	/**
	 * Includes all indicator collectors when instrumenting algorithms.  This
	 * includes hypervolume, generational distance, inverted generational
	 * distance, spacing, additive &epsilon;-indicator, contribution, and the
	 * R1, R2, and R3 indicators.
	 * 
	 * @return a reference to this instrumenter
	 */
	public Instrumenter attachAllMetricCollectors() {
		attachHypervolumeCollector();
		attachGenerationalDistanceCollector();
		attachInvertedGenerationalDistanceCollector();
		attachSpacingCollector();
		attachAdditiveEpsilonIndicatorCollector();
		attachContributionCollector();
		attachR1Collector();
		attachR2Collector();
		attachR3Collector();
		
		return this;
	}
	
	/**
	 * Includes the &epsilon;-progress collector when instrumenting algorithms.
	 * 
	 * @return a reference to this instrumenter
	 */
	public Instrumenter attachEpsilonProgressCollector() {
		includeEpsilonProgress = true;
		
		return this;
	}
	
	/**
	 * Includes the adaptive multimethod variation collector when instrumenting
	 * algorithms.
	 * 
	 * @return a reference to this instrumenter
	 */
	public Instrumenter attachAdaptiveMultimethodVariationCollector() {
		includeAdaptiveMultimethodVariation = true;
		
		return this;
	}
	
	/**
	 * Includes the adaptive time continuation collector when instrumenting
	 * algorithms.
	 * 
	 * @return a reference to this instrumenter
	 */
	public Instrumenter attachAdaptiveTimeContinuationCollector() {
		includeAdaptiveTimeContinuation = true;
		
		return this;
	}
	
	/**
	 * Includes the elapsed time collector when instrumenting algorithms.
	 * 
	 * @return a reference to this instrumenter
	 */
	public Instrumenter attachElapsedTimeCollector() {
		includeElapsedTime = true;
		
		return this;
	}
	
	/**
	 * Includes the approximation set collector when instrumenting algorithms.
	 * 
	 * @return a reference to this instrumenter
	 */
	public Instrumenter attachApproximationSetCollector() {
		includeApproximationSet = true;
		
		return this;
	}
	
	/**
	 * Includes the population size collector when instrumenting algorithms.
	 * 
	 * @return a reference to this instrumenter
	 */
	public Instrumenter attachPopulationSizeCollector() {
		includePopulationSize = true;
		
		return this;
	}
	
	/**
	 * Includes all collectors when instrumenting algorithms.
	 * 
	 * @return a reference to this instrumenter
	 */
	public Instrumenter attachAll() {
		attachAllMetricCollectors();
		attachEpsilonProgressCollector();
		attachAdaptiveMultimethodVariationCollector();
		attachAdaptiveTimeContinuationCollector();
		attachElapsedTimeCollector();
		attachApproximationSetCollector();
		attachPopulationSizeCollector();
		
		return this;
	}
	
	@Override
	public Instrumenter withSameProblemAs(ProblemBuilder builder) {
		return (Instrumenter)super.withSameProblemAs(builder);
	}
	
	@Override
	public Instrumenter usingProblemFactory(ProblemFactory problemFactory) {
		return (Instrumenter)super.usingProblemFactory(problemFactory);
	}

	@Override
	public Instrumenter withProblem(String problemName) {
		return (Instrumenter)super.withProblem(problemName);
	}
	
	@Override
	public Instrumenter withProblem(Problem problemInstance) {
		return (Instrumenter)super.withProblem(problemInstance);
	}

	@Override
	public Instrumenter withProblemClass(Class<?> problemClass, 
			Object... problemArguments) {
		return (Instrumenter)super.withProblemClass(problemClass,
				problemArguments);
	}

	@Override
	public Instrumenter withProblemClass(String problemClassName, 
			Object... problemArguments) throws ClassNotFoundException {
		return (Instrumenter)super.withProblemClass(problemClassName,
				problemArguments);
	}

	@Override
	public Instrumenter withReferenceSet(File referenceSetFile) {
		return (Instrumenter)super.withReferenceSet(referenceSetFile);
	}
	
	@Override
	public Instrumenter withEpsilon(double... epsilon) {
		return (Instrumenter)super.withEpsilon(epsilon);
	}
	
	@Override
	public NondominatedPopulation getReferenceSet() {
		return super.getReferenceSet();
	}

	/**
	 * Recursively walks the object graph to 1) determine the nesting of objects
	 * to help determine which objects are to be instrumented; and 2) attach the
	 * {@code collector}s to any matching objects.
	 * <p>
	 * In order to avoid cycles in the object graph, objects are only traversed
	 * the first time they are encountered.  If an object appears multiple
	 * times in the object graph, the {@code instrument} method will only be
	 * invoked once.
	 * <p>
	 * When generating the nesting of objects, anonymous classes are given the
	 * placeholder type {@code "(Anonymous)"}, without quotes.  While the
	 * contents of arrays and {@link Collection}s are listed in the nesting,
	 * the array/collection object itself is not listed.  For example,
	 * the nesting will show {@code CompoundVariation >> PM} instead of
	 * {@code CompoundVariation >> ArrayList >> Object[] >> PM}.
	 * <p>
	 * This method is reentrant.
	 * 
	 * @param algorithm the instrumented algorithm
	 * @param collectors the collectors to be attached
	 * @param visited the set of visited objects, which may include the current
	 *        object when traversing its superclasses
	 * @param parents the objects in which the current object is contained
	 * @param object the current object undergoing reflection
	 * @param type the superclass whose members are being reflected; or
	 *        {@code null} if the base type is to be used
	 */
	protected void instrument(InstrumentedAlgorithm algorithm, 
			List<Collector> collectors, Set<Object> visited, 
			Stack<Object> parents, Object object, Class<?> type) {
		if (object == null) {
			return;
		} else if ((type == null) || (type.equals(object.getClass()))) {
			try {
				if (visited.contains(object)) {
					return;
				}
			} catch (NullPointerException e) {
				// proxies will sometimes result in NPEs when calling hashCode
				return;
			}
				
			type = object.getClass();
		}
		
		if (type.isAnnotation() || type.isEnum() || type.isPrimitive()) {
			//ignore objects which are not classes or arrays
			return;
		} else if (object instanceof Instrumenter) {
			//well this is embarrassing
			return;
		} else if (type.isArray()) {
			//recursively walk the elements in the array
			for (int i=0; i<Array.getLength(object); i++) {
				instrument(algorithm, collectors, visited, parents, 
						Array.get(object, i), null);
			}
		} else if (object instanceof Collection) {
			//recursively walk the elements in the array
			for (Object element : (Collection<?>)object) {
				instrument(algorithm, collectors, visited, parents, element, 
						null);
			}
		} else if (type.getPackage() != null) {			
			for (String excludedPackage : excludedPackages) {
				String[] excludedPackageSegments = StringUtils.split(excludedPackage, '.');
				String[] typePackageSegments = StringUtils.split(type.getPackage().getName(), '.');
				
				if (typePackageSegments.length >= excludedPackageSegments.length) {
					boolean matches = true;
					
					for (int i = 0; i < excludedPackageSegments.length; i++) {
						if (!typePackageSegments[i].equals(excludedPackageSegments[i])) {
							matches = false;
							break;
						}
					}
					
					if (matches) {
						return;
					}
				}
			}
		}
		
		if (!visited.contains(object)) {
			//attach any matching collectors
			for (Collector collector : collectors) {
				if (collector.getAttachPoint().matches(parents, object)) {
					algorithm.addCollector(collector.attach(object));
				}
			}
			
			visited.add(object);
		}
		
		//ignore some types we should not recursively scan as these have caused
		//issues with user libraries...it's the user's responsibility to access
		//the internals of these objects.
		if ((object instanceof Solution) || (object instanceof Problem)) {
			return;
		}
		
		//recursively walk superclass to enumerate all non-public fields
		Class<?> superclass = type.getSuperclass();
		
		if (superclass != null) {
			instrument(algorithm, collectors, visited, parents, object, 
					superclass);
		}
		
		//recursively walk fields
		parents.push(object);
		
		for (Field field : type.getDeclaredFields()) {
			field.setAccessible(true);
			
			try {
				instrument(algorithm, collectors, visited, parents, 
						field.get(object), null);
			} catch (IllegalArgumentException e) {
				//should never occur since object is of the specified type
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				//should never occur after setting field.setAccessible(true)
				e.printStackTrace();
			}
		}
		
		parents.pop();
	}
	
	/**
	 * Instruments the specified algorithm, returning an 
	 * {@link InstrumentedAlgorithm} to be used to execute the algorithm and
	 * store the data produced by any attached instruments.
	 * 
	 * @param algorithm the algorithm to instrument
	 * @return the instrumented algorithm
	 * @throws IllegalArgumentException if no reference set is available or
	 *         could not be loaded
	 */
	public InstrumentedAlgorithm instrument(Algorithm algorithm) {
		List<Collector> collectors = new ArrayList<Collector>();
		
		if (includeHypervolume || includeGenerationalDistance || 
				includeInvertedGenerationalDistance || includeSpacing ||
				includeAdditiveEpsilonIndicator || includeContribution ||
				includeR1 || includeR2 || includeR3) {
			Problem problem = algorithm.getProblem();
			NondominatedPopulation referenceSet = getReferenceSet();
			EpsilonBoxDominanceArchive archive = null;
			
			if (epsilon != null) {
				archive = (EpsilonBoxDominanceArchive)newArchive();
			}
			
			if (includeHypervolume) {
				collectors.add(new IndicatorCollector(
						new Hypervolume(problem, referenceSet), archive));
			}
			
			if (includeGenerationalDistance) {
				collectors.add(new IndicatorCollector(
						new GenerationalDistance(problem, referenceSet), 
						archive));
			}
			
			if (includeInvertedGenerationalDistance) {
				collectors.add(new IndicatorCollector(
						new InvertedGenerationalDistance(problem, 
								referenceSet), archive));
			}
			
			if (includeSpacing) {
				collectors.add(new IndicatorCollector(new Spacing(problem), 
						archive));
			}
			
			if (includeAdditiveEpsilonIndicator) {
				collectors.add(new IndicatorCollector(
						new AdditiveEpsilonIndicator(problem, referenceSet),
						archive));
			}
			
			if (includeContribution) {
				collectors.add(new IndicatorCollector(
						archive == null ? new Contribution(referenceSet) :
						new Contribution(referenceSet, archive.getComparator()),
						archive));
			}
			
			if (includeR1) {
				collectors.add(new IndicatorCollector(new R1Indicator(
						problem,
						R1Indicator.getDefaultSubdivisions(problem),
						referenceSet),
						archive));
			}
			
			if (includeR2) {
				collectors.add(new IndicatorCollector(new R2Indicator(
						problem,
						R2Indicator.getDefaultSubdivisions(problem),
						referenceSet),
						archive));
			}
			
			if (includeR3) {
				collectors.add(new IndicatorCollector(new R3Indicator(
						problem,
						R3Indicator.getDefaultSubdivisions(problem),
						referenceSet),
						archive));
			}
		}
		
		if (includeEpsilonProgress) {
			collectors.add(new EpsilonProgressCollector());
		}
		
		if (includeAdaptiveMultimethodVariation) {
			collectors.add(new AdaptiveMultimethodVariationCollector());
		}
		
		if (includeAdaptiveTimeContinuation) {
			collectors.add(new AdaptiveTimeContinuationCollector());
		}
	
		if (includeElapsedTime) {
			collectors.add(new ElapsedTimeCollector());
		}
		
		if (includeApproximationSet) {
			if (epsilon == null) {
				collectors.add(new ApproximationSetCollector());
			} else {
				collectors.add(new ApproximationSetCollector(epsilon));
			}
		}
		
		if (includePopulationSize) {
			collectors.add(new PopulationSizeCollector());
		}
		
		collectors.addAll(customCollectors);
		
		InstrumentedAlgorithm instrumentedAlgorithm = new InstrumentedAlgorithm(
				algorithm, frequency, frequencyType);
		
		instrument(instrumentedAlgorithm, collectors, new HashSet<Object>(), 
				new Stack<Object>(), algorithm, null);
		
		lastAccumulator = instrumentedAlgorithm.getAccumulator();
		
		return instrumentedAlgorithm;
	}

}
