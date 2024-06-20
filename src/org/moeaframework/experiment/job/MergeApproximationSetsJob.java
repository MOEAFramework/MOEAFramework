package org.moeaframework.experiment.job;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.Epsilons;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.experiment.Samples;
import org.moeaframework.experiment.store.DataReference;
import org.moeaframework.experiment.store.DataStore;
import org.moeaframework.experiment.store.DataType;
import org.moeaframework.experiment.store.Key;
import org.moeaframework.util.TypedProperties;

public class MergeApproximationSetsJob extends Job {
			
	private final Collection<Key> inputs;
	
	private final String problemName;
	
	public MergeApproximationSetsJob(Key key, Samples samples, String problemName) {
		this(key, samples.keySet(), problemName);
	}
	
	public MergeApproximationSetsJob(Key key, Collection<Key> inputs, String problemName) {
		super(key);
		this.inputs = inputs;
		this.problemName = problemName;
	}

	@Override
	public void execute(DataStore dataStore) throws IOException {
		try (Problem problem = ProblemFactory.getInstance().getProblem(problemName)) {
			Epsilons epsilons = ProblemFactory.getInstance().getEpsilons(problemName);
			
			NondominatedPopulation combinedApproximationSet = epsilons == null ?
					new NondominatedPopulation() :
					new EpsilonBoxDominanceArchive(epsilons);
			
			for (Key input : inputs) {
				combinedApproximationSet.addAll(JobUtils.loadApproximationSet(dataStore, input, problem));
			}
			
			JobUtils.saveApproximationSet(dataStore, key, problem, combinedApproximationSet,
					TypedProperties.of("epsilons", epsilons.toString()));
		}
	}

	@Override
	public Collection<DataReference> requires() {
		List<DataReference> requirements = new ArrayList<>();
		
		for (Key input : inputs) {
			requirements.add(DataReference.of(input, DataType.APPROXIMATION_SET));
		}
		
		return requirements;
	}

	@Override
	public Collection<DataReference> produces() {
		return List.of(DataReference.of(key, DataType.APPROXIMATION_SET));
	}

}
