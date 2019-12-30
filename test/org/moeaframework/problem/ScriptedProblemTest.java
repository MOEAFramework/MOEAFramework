/* Copyright 2009-2019 David Hadka
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
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;

import javax.script.ScriptException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.moeaframework.IgnoreOnTravis;
import org.moeaframework.TestUtils;
import org.moeaframework.TravisRunner;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;

/**
 * Tests the {@link ScriptedProblem} class.
 */
@RunWith(TravisRunner.class)
@IgnoreOnTravis("the scripting engine may not be available")
public class ScriptedProblemTest {
	
	private static final String RESOURCE_JAVASCRIPT = 
			"/org/moeaframework/problem/TestJavascript.js";
	
	@Test(expected = ScriptException.class)
	public void testNoExtension() throws ScriptException, IOException {
		File file = File.createTempFile("test", "");
		file.deleteOnExit();

		new ScriptedProblem(file);
	}
	
	@Test(expected = ScriptException.class)
	public void testNoEngineForExtension() throws ScriptException, IOException {
		File file = File.createTempFile("test", ".noscriptinglang");
		file.deleteOnExit();

		new ScriptedProblem(file);
	}
	
	@Test(expected = ScriptException.class)
	public void testNoEngineWithName() throws ScriptException, IOException {
		new ScriptedProblem("", "noscriptinglang");
	}
	
	@Test
	public void testJavascriptFile() throws ScriptException, IOException, 
	URISyntaxException {
		File file = TestUtils.extractResource(RESOURCE_JAVASCRIPT);
		
		test(new ScriptedProblem(file));
	}
	
	@Test
	public void testJavascriptReader() throws IOException, ScriptException {
		Reader reader = null;
		
		try {
			reader = new InputStreamReader(getClass().getResourceAsStream(
					RESOURCE_JAVASCRIPT));
			
			test(new ScriptedProblem(reader, "rhino"));
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
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
		
		Assert.assertEquals(variable.getValue(), solution.getObjective(0), 
				Settings.EPS);
		
		problem.close();
	}

}
