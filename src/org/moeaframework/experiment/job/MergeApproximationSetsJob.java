package org.moeaframework.experiment.job;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.Epsilons;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.experiment.store.DataReference;
import org.moeaframework.experiment.store.DataStore;
import org.moeaframework.experiment.store.DataType;
import org.moeaframework.experiment.store.Key;
import org.moeaframework.experiment.store.TransactionalOutputStream;

public class MergeApproximationSetsJob extends Job {
			
	private final Collection<Key> inputs;
	
	private final Epsilons epsilons;
	
	public MergeApproximationSetsJob(Key key, Collection<Key> inputs, Epsilons epsilons) {
		super(key);
		this.inputs = inputs;
		this.epsilons = epsilons;
	}

	@Override
	public void execute(DataStore dataStore) {
		NondominatedPopulation combinedApproximationSet = epsilons == null ? new NondominatedPopulation() :
				new EpsilonBoxDominanceArchive(epsilons);
		
		for (Key input : inputs) {
			try (InputStream in = dataStore.reader(input, DataType.APPROXIMATION_SET).asBinary()) {
				combinedApproximationSet.addAll(Population.loadBinary(in));
			} catch (IOException e) {
				throw new FrameworkException(e);
			}
		}
		
		try (TransactionalOutputStream out = dataStore.writer(key, DataType.APPROXIMATION_SET).asBinary()) {
			combinedApproximationSet.saveBinary(out);
			out.commit();
		} catch (Exception e) {
			throw new FrameworkException(e);
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
