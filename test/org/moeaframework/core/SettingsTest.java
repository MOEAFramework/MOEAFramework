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
package org.moeaframework.core;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.NondominatedPopulation.DuplicateMode;

/**
 * Tests the {@link Settings} class.  These tests ensure that valid settings
 * are provided, and that there should be no errors when accessing these
 * settings.
 */
public class SettingsTest {

	@Test
	public void testContinuityCorrection() {
		Settings.isContinuityCorrection();
	}

	@Test
	public void testHypervolumeDelta() {
		Assert.assertTrue(Settings.getHypervolumeDelta() >= 0.0);
	}

	@Test
	public void testHypervolume() {
		Settings.getHypervolume();
	}

	@Test
	public void testHypervolumeEnabled() {
		Settings.isHypervolumeEnabled();
	}

	@Test
	public void testPISAAlgorithms() {
		Assert.assertTrue(Settings.getPISAAlgorithms().length >= 0);
	}

	@Test
	public void testPISAPollRate() {
		Assert.assertTrue(Settings.getPISAPollRate() >= 0);
	}

	@Test
	public void testPISACommand() {
		for (String algorithm : Settings.getPISAAlgorithms()) {
			Assert.assertNotNull(Settings.getPISACommand(algorithm));
		}
	}		

	@Test
	public void testPISAConfiguration() {
		for (String algorithm : Settings.getPISAAlgorithms()) {
			Assert.assertNotNull(Settings.getPISAConfiguration(algorithm));
		}
	}
	
	@Test
	public void testPISAParameters() {
		for (String algorithm : Settings.getPISAAlgorithms()) {
			for (String parameter : Settings.getPISAParameters(algorithm)) {
				Assert.assertNotNull(Settings.getPISAParameterDefaultValue(
						algorithm, parameter));
			}
		}
	}
	
	@Test
	public void testDiagnosticToolAlgorithms() {
		Assert.assertNotNull(Settings.getDiagnosticToolAlgorithms());
	}
	
	@Test
	public void testDiagnosticToolProblems() {
		Assert.assertNotNull(Settings.getDiagnosticToolProblems());
	}
	
	@Test
	public void testParseCommand() throws IOException {
		String command = "java -jar \"C:\\Program Files\\Test\\test.jar\" \"\"\"";
		String[] expected = new String[] { "java", "-jar", 
				"C:\\Program Files\\Test\\test.jar", "\"" };
		String[] actual = Settings.parseCommand(command);
		
		Assert.assertArrayEquals(expected, actual);
	}
	
	@Test
	public void testDuplicateMode() {
		Assert.assertEquals(DuplicateMode.NO_DUPLICATE_OBJECTIVES,
				Settings.getDuplicateMode());
	}
	
	@Test
	public void testDuplicateModeCaseSensitivity() {
		Settings.PROPERTIES.setString(Settings.KEY_DUPLICATE_MODE,
				DuplicateMode.ALLOW_DUPLICATES.name().toLowerCase());
		
		Assert.assertEquals(DuplicateMode.ALLOW_DUPLICATES,
				Settings.getDuplicateMode());
		
		Assert.assertEquals(DuplicateMode.ALLOW_DUPLICATES,
				new NondominatedPopulation().duplicateMode);
		
		Settings.PROPERTIES.remove(Settings.KEY_DUPLICATE_MODE);
	}

}
