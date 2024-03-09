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
package org.moeaframework.core.indicator;

import java.io.IOException;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.problem.MockRealProblem;
import org.moeaframework.util.PropertyScope;

public class NativeHypervolumeTest {

	@Test
	public void testParseCommand() throws IOException {
		String command = "java -jar \"C:\\Program Files\\Test\\test.jar\" \"\"\"";
		String[] expected = new String[] { "java", "-jar", "C:\\Program Files\\Test\\test.jar", "\"" };
		String[] actual = NativeHypervolume.parseCommand(command);
		
		Assert.assertArrayEquals(expected, actual);
	}
	
	@Test
	public void testInvert() {
		Problem problem = new MockRealProblem(3);
		Solution solution = TestUtils.newSolution(0.0, 1.0, 0.5);
		
		NativeHypervolume.invert(problem, solution);
		
		Assert.assertArrayEquals(new double[] { 1.0, 0.0, 0.5 }, solution.getObjectives(), Settings.EPS);
	}
	
	@Test
	public void testInvokeNativeProcess() throws IOException {
		double value = NativeHypervolume.invokeNativeProcess(SystemUtils.IS_OS_WINDOWS ?
				"cmd /C \"echo hypervolume is 0.75\"" : "echo \"hypervolume is 0.75\"");
		
		Assert.assertEquals(0.75, value, Settings.EPS);
	}
	
	@Test(expected = FrameworkException.class)
	public void testNoSetting() {
		try (PropertyScope scope = Settings.createScope().without(Settings.KEY_HYPERVOLUME)) {
			NativeHypervolume.evaluate(new MockRealProblem(2), new NondominatedPopulation());
		}
	}
	
	@Test
	public void testEvaluate() {
		try (PropertyScope scope = Settings.createScope().with(Settings.KEY_HYPERVOLUME,
				SystemUtils.IS_OS_WINDOWS ? "cmd /C \"echo {0} {1} {2} {3} {4} 0.75\"" : "echo \"{0} {1} {2} {3} {4} 0.75\"")) {
			double value = NativeHypervolume.evaluate(new MockRealProblem(2), new NondominatedPopulation());
			Assert.assertEquals(0.75, value, Settings.EPS);
		}
	}

}
