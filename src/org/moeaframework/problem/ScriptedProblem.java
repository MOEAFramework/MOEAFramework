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
package org.moeaframework.problem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

/**
 * Permits interfacing with problems implemented by one of the many scripting
 * languages supported by the Java Scripting APIs.
 * <p>
 * Currently, only scripting engines which support the {@link Invocable}
 * interface are supported.  The script itself should contain methods
 * equivalent to the methods in {@link Problem}, with the same names, arguments
 * and return values.
 */
public class ScriptedProblem implements Problem {
	
	/**
	 * The invocable {@code Problem} interface produced by 
	 * {@link Invocable#getInterface(Class)}.
	 */
	private final Problem internalProblem;
	
	/**
	 * The scripting engine used by this instance.
	 */
	private final ScriptEngine engine;
	
	/**
	 * Constructs a new problem implemented in a scripting language.
	 * 
	 * @param script the script
	 * @param name the name of the scripting engine
	 * @throws ScriptException if an error occurred in the Scripting APIs
	 */
	public ScriptedProblem(String script, String name) throws ScriptException {
		this(new StringReader(script), name);
	}
	
	/**
	 * Constructs a new problem implemented in a scripting language.
	 * 
	 * @param reader the reader for loading the script contents
	 * @param name the name of the scripting engine
	 * @throws ScriptException if an error occurred in the Scripting APIs
	 */
	public ScriptedProblem(Reader reader, String name) throws ScriptException {
		super();
		
		engine = newScriptEngine(name);
		internalProblem = createInvocableInstance(reader);
	}
	
	/**
	 * Constructs a new problem implemented in a scripting language.
	 * 
	 * @param file the file containing the script contents, and whose file
	 *        extension identifies the scripting language
	 * @throws ScriptException if an error occurred in the Scripting APIs
	 * @throws IOException if an I/O error occurred
	 */
	public ScriptedProblem(File file) throws ScriptException, IOException {
		super();
		
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(file));
			engine = newScriptEngine(file);
			internalProblem = createInvocableInstance(reader);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
	
	/**
	 * Returns a new scripting engine for the specified scripting language.
	 * 
	 * @param name the name of the scripting language
	 * @return a new scripting engine for the specified scripting language
	 * @throws ScriptException if an error occurred in the Scripting APIs
	 */
	private ScriptEngine newScriptEngine(String name) throws ScriptException {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName(name);
		
		if (engine == null) {
			throw new ScriptException("no scripting engine for " + name);
		}
		
		return engine;
	}
	
	/**
	 * Returns a new scripting engine for the scripting language identified by
	 * the file name extension.
	 * 
	 * @param file the file containing the script
	 * @return a new scripting engine for the scripting language
	 * @throws ScriptException if an error occurred in the Scripting APIs
	 */
	private ScriptEngine newScriptEngine(File file) throws ScriptException {
		String filename = file.getName();
		int index = filename.lastIndexOf('.');
		
		if ((index < 0) || (index >= filename.length()-1)) {
			throw new ScriptException("file has no extension");
		}

		String extension = filename.substring(index+1);
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByExtension(extension);
		
		if (engine == null) {
			throw new ScriptException("no scripting engine for extension ." +
					extension);
		}
		
		return engine;
	}
	
	/**
	 * Returns a {@code Problem} instance whose methods invoke the underlying
	 * scripting engine; or {@code null} if the scripting engine does not
	 * support invocable methods/functions.
	 * 
	 * @param reader the reader containing the script
	 * @return a {@link Problem} instance whose methods invoke the underlying
	 *         scripting engine; or {@code null} if the scripting engine does 
	 *         not support invocable methods/functions
	 * @throws ScriptException if an error occurred in the Scripting APIs
	 */
	private Problem createInvocableInstance(Reader reader) 
	throws ScriptException {
		Problem problem = null;
		
		if (engine instanceof Invocable) {
			engine.eval(reader);
			problem = ((Invocable)engine).getInterface(Problem.class);
		}
		
		if (problem == null) {
			throw new ScriptException("scripting engine not invocable");
		}
		
		return problem;
	}

	@Override
	public String getName() {
		return internalProblem.getName();
	}

	@Override
	public int getNumberOfVariables() {
		return internalProblem.getNumberOfVariables();
	}

	@Override
	public int getNumberOfObjectives() {
		return internalProblem.getNumberOfObjectives();
	}

	@Override
	public int getNumberOfConstraints() {
		return internalProblem.getNumberOfConstraints();
	}

	@Override
	public void evaluate(Solution solution) {
		internalProblem.evaluate(solution);
	}

	@Override
	public Solution newSolution() {
		return internalProblem.newSolution();
	}

	@Override
	public void close() {
		internalProblem.close();
	}

}
