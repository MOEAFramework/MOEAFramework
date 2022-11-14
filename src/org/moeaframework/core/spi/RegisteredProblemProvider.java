package org.moeaframework.core.spi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PopulationIO;
import org.moeaframework.core.Problem;
import org.moeaframework.util.io.CommentedLineReader;

public class RegisteredProblemProvider extends ProblemProvider {
	
	private final TreeMap<String, Supplier<Problem>> constructorMap;
	
	private final TreeMap<String, String> referenceSetMap;
	
	public RegisteredProblemProvider() {
		super();
		constructorMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		referenceSetMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	}
	
	public final void register(String name, Supplier<Problem> constructor, String referenceSet) {
		constructorMap.put(name, constructor);
		referenceSetMap.put(name, referenceSet);
	}
	
	public Set<String> getRegisteredNames() {
		return constructorMap.keySet();
	}

	@Override
	public Problem getProblem(String name) {
		Supplier<Problem> constructor = constructorMap.get(name);
		
		if (constructor != null) {
			return constructor.get();
		}
		
		return null;
	}

	@Override
	public NondominatedPopulation getReferenceSet(String name) {
		String referenceSet = referenceSetMap.get(name);
		
		if (referenceSet != null) {
			try {
				return loadReferenceSet(referenceSet);
			} catch (IOException e) {
				return null;
			}
		}
		
		return null;
	}
	
	private NondominatedPopulation loadReferenceSet(String resource) throws IOException {
		File file = new File(resource);
		
		if (file.exists()) {
			return new NondominatedPopulation(PopulationIO.readObjectives(file));
		} else {
			try (InputStream input = getClass().getResourceAsStream("/" + resource)) {
				if (input == null) {
					throw new FileNotFoundException(resource);
				} else {
					return new NondominatedPopulation(PopulationIO.readObjectives(
							new CommentedLineReader(new InputStreamReader(input))));
				}
			}
		}
	}

}
