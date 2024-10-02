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
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.Assert;
import org.moeaframework.Assume;
import org.moeaframework.CIRunner;
import org.moeaframework.TempFiles;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.util.io.Resources;
import org.moeaframework.util.io.Resources.ResourceOption;

@RunWith(CIRunner.class)
public class ScriptedProblemTest {
	
	private static final String RESOURCE_JAVASCRIPT = "/org/moeaframework/problem/TestJavascript.js";
	
	@SuppressWarnings("resource")
	@Test(expected = ScriptException.class)
	public void testNoExtension() throws ScriptException, IOException {
		File file = TempFiles.createFileWithExtension("").withContent("");
		new ScriptedProblem(file);
	}
	
	@SuppressWarnings("resource")
	@Test(expected = ScriptException.class)
	public void testNoEngineForExtension() throws ScriptException, IOException {
		File file = TempFiles.createFileWithExtension(".noscriptinglang").withContent("");
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
		
		File file = Resources.asFile(getClass(), RESOURCE_JAVASCRIPT, ResourceOption.REQUIRED, ResourceOption.TEMPORARY);
		
		try (Problem problem = new ScriptedProblem(file)) {
			test(problem);
		}
		
		file.delete();
	}
	
	@Test
	public void testJavascriptReader() throws IOException, ScriptException {
		ignoreIfScriptingNotAvailbale();
		
		try (Reader reader = Resources.asReader(getClass(), RESOURCE_JAVASCRIPT, ResourceOption.REQUIRED);
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
		
		Assert.assertEquals(variable.getValue(), solution.getObjective(0), TestThresholds.HIGH_PRECISION);
	}

}
