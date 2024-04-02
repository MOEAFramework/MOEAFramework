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
package org.moeaframework.problem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URISyntaxException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.io.FilenameUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.Assert;
import org.moeaframework.Assume;
import org.moeaframework.CIRunner;
import org.moeaframework.TempFiles;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;

@RunWith(CIRunner.class)
public class ScriptedProblemTest {
	
	private static final String RESOURCE_JAVASCRIPT = "/org/moeaframework/problem/TestJavascript.js";
	
	@SuppressWarnings("resource")
	@Test(expected = ScriptException.class)
	public void testNoExtension() throws ScriptException, IOException {
		File file = TempFiles.createFileWithContent("", "");
		new ScriptedProblem(file);
	}
	
	@SuppressWarnings("resource")
	@Test(expected = ScriptException.class)
	public void testNoEngineForExtension() throws ScriptException, IOException {
		File file = TempFiles.createFileWithContent("", ".noscriptinglang");
		new ScriptedProblem(file);
	}
	
	@SuppressWarnings("resource")
	@Test(expected = ScriptException.class)
	public void testNoEngineWithName() throws ScriptException, IOException {
		new ScriptedProblem("", "noscriptinglang");
	}
	
	@Test
	public void testJavascriptFile() throws ScriptException, IOException, URISyntaxException {
		ignoreIfScriptingNotAvailbale();
		
		File file = extractResource(RESOURCE_JAVASCRIPT);
		
		try (Problem problem = new ScriptedProblem(file)) {
			test(problem);
		}
	}
	
	@Test
	public void testJavascriptReader() throws IOException, ScriptException {
		ignoreIfScriptingNotAvailbale();
		
		try (Reader reader = new InputStreamReader(getClass().getResourceAsStream(RESOURCE_JAVASCRIPT));
				Problem problem = new ScriptedProblem(reader, "nashorn")) {
			test(problem);
		}
	}
	
	private void ignoreIfScriptingNotAvailbale() {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("nashorn");
		
		Assume.assumeTrue("nashorn scripting engine not available", engine != null);
	}
	
	private void test(Problem problem) {
		Assert.assertEquals(1, problem.getNumberOfVariables());
		Assert.assertEquals(1, problem.getNumberOfObjectives());
		Assert.assertEquals(0, problem.getNumberOfConstraints());
		Assert.assertEquals("TestScript", problem.getName());
		
		Solution solution = problem.newSolution();
		RealVariable variable = (RealVariable)solution.getVariable(0);
		
		variable.setValue(Math.PI / 10);
		problem.evaluate(solution);
		
		Assert.assertEquals(variable.getValue(), solution.getObjective(0), Settings.EPS);
	}
	
	/**
	 * Extracts the data stored in a resource, saving its contents to a temporary file.  If the resource name contains
	 * an extension, the file will be created with the extension.
	 * 
	 * @param resource the name of the resource to extract
	 * @return the temporary file containing the resource data
	 * @throws IOException if an I/O error occurred
	 */
	public static File extractResource(String resource) throws IOException {
		byte[] buffer = new byte[Settings.BUFFER_SIZE];
		int len = -1;
		
		//determine the file extension, if any
		String extension = FilenameUtils.getExtension(resource);
		File file = TempFiles.createFileWithExtension(extension);
		
		//copy the resource contents to the file
		try (InputStream input = ScriptedProblemTest.class.getResourceAsStream(resource)) {
			if (input == null) {
				throw new IOException("resource not found: " + resource);
			}
			
			try (OutputStream output = new FileOutputStream(file)) {
				while ((len = input.read(buffer)) != -1) {
					output.write(buffer, 0, len);
				}
			}
		}
		
		return file;
	}

}
