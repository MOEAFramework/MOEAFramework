package org.moeaframework.experiment.job;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.moeaframework.core.Epsilons;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.indicator.Indicators;
import org.moeaframework.core.indicator.Indicators.IndicatorValues;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.experiment.Sample;
import org.moeaframework.experiment.store.DataReference;
import org.moeaframework.experiment.store.DataStore;
import org.moeaframework.experiment.store.DataType;
import org.moeaframework.experiment.store.Key;

public class EvaluateIndicatorsJob extends Job {
	
	private static final Map<String, WeakReference<Indicators>> CACHE = Collections.synchronizedMap(new HashMap<>());
	
	private final String problemName;
	
	public EvaluateIndicatorsJob(Key key, Sample sample) {
		this(key, JobUtils.getProblemName(key, sample));
	}
	
	public EvaluateIndicatorsJob(Key key) {
		this(key, JobUtils.getProblemName(key, null));
	}
	
	public EvaluateIndicatorsJob(Key key, String problemName) {
		super(key);
		this.problemName = problemName;
	}

	@Override
	public void execute(DataStore dataStore) throws IOException {
		try (Problem problem = ProblemFactory.getInstance().getProblem(problemName)) {
			NondominatedPopulation approximationSet = JobUtils.loadApproximationSet(dataStore, key, problem);
			
			WeakReference<Indicators> cachedIndicator = CACHE.get(problemName);
			Indicators indicators = cachedIndicator != null ? cachedIndicator.get() : null;
			
			if (indicators == null) {
				NondominatedPopulation referenceSet = ProblemFactory.getInstance().getReferenceSet(problemName);
				Epsilons epsilons = ProblemFactory.getInstance().getEpsilons(problemName);
				
				indicators = Indicators.all(problem, referenceSet).withEpsilons(epsilons);
				
				CACHE.put(problemName, new WeakReference<>(indicators));
			}

			IndicatorValues values = indicators.apply(approximationSet);
			JobUtils.saveProperties(dataStore, key, DataType.INDICATOR_VALUES, values.asProperties());
		}
	}

	@Override
	public Collection<DataReference> requires() {
		return List.of(DataReference.of(key, DataType.APPROXIMATION_SET));
	}

	@Override
	public Collection<DataReference> produces() {
		return List.of(DataReference.of(key, DataType.INDICATOR_VALUES));
	}

}
