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
package org.moeaframework.core.spi;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Supplier;

import org.moeaframework.core.Epsilons;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;

/**
 * Problem provider that lets callers register problems by name.  For example:
 * <pre>
 *    RegisteredProblemProvider myProvider = new RegisteredProblemProvider();
 *    myProvider.register("MyProblem", () -> new MyProblem(), "pf/myProbem.pf");
 *    
 *    ProblemFactory.getInstance().addProvider(myProvider);
 * </pre>
 */
public class RegisteredProblemProvider extends ProblemProvider {
	
	/**
	 * Mapping of problem names to a constructor function.
	 */
	private final TreeMap<String, Supplier<Problem>> constructorMap;
	
	/**
	 * Mapping of problem names to their reference set.
	 */
	private final TreeMap<String, String> referenceSetMap;
	
	/**
	 * Mapping of problem names to their epsilons.
	 */
	private final TreeMap<String, Epsilons> epsilonsMap;
	
	/**
	 * Collection of problems to appear in the diagnostic tool.
	 */
	private final TreeSet<String> diagnosticToolProblems;
	
	/**
	 * Creates a new, empty problem provider.
	 */
	public RegisteredProblemProvider() {
		super();
		constructorMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		referenceSetMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		epsilonsMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		diagnosticToolProblems = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
	}
	
	/**
	 * Registers a new problem with this provider.
	 * 
	 * @param name the problem name
	 * @param constructor the function that creates a new instance of the problem
	 * @param referenceSet the path of the file containing the reference set
	 */
	protected final void register(String name, Supplier<Problem> constructor, String referenceSet) {
		constructorMap.put(name, constructor);
		referenceSetMap.put(name, referenceSet);
	}
	
	/**
	 * Registers the &epsilon; values for the given problem, used as the defaults when constructing an algorithm,
	 * archive, or other object that uses &epsilon;-based dominance.
	 * 
	 * @param name the problem name
	 * @param epsilons the &epsilon; values
	 */
	protected final void registerEpsilons(String name, Epsilons epsilons) {
		epsilonsMap.put(name, epsilons);
	}
	
	/**
	 * Registers the given problem with the diagnostic tool.
	 * 
	 * @param name the problem name
	 */
	protected final void registerDiagnosticToolProblem(String name) {
		diagnosticToolProblems.add(name);
	}
	
	/**
	 * Registers all of the given problems with the diagnostic tool.
	 * 
	 * @param names the problem names
	 */
	protected final void registerDiagnosticToolProblems(Collection<String> names) {
		diagnosticToolProblems.addAll(names);
	}
	
	/**
	 * Returns all problems that have been registered with this provider.  Note that this does not necessarily include
	 * all problems that can be instantiated by the provider, only those that have been explicitly registered.
	 * 
	 * @return the problem names
	 */
	public Set<String> getRegisteredProblems() {
		return constructorMap.keySet();
	}
	
	@Override
	public Set<String> getDiagnosticToolProblems() {
		return Collections.unmodifiableSet(diagnosticToolProblems);
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
				return NondominatedPopulation.loadReferenceSet(referenceSet);
			} catch (IOException e) {
				return null;
			}
		}
		
		return null;
	}
	
	@Override
	public Epsilons getEpsilons(String name) {
		return epsilonsMap.get(name);
	}

}
