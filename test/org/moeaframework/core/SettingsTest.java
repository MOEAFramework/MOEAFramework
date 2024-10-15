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
package org.moeaframework.core;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TempFiles;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.core.population.NondominatedPopulation.DuplicateMode;

/**
 * These tests ensure that valid default settings are provided and there are no errors parsing settings.
 */
public class SettingsTest {

	@Test
	public void testContinuityCorrection() {
		Assert.assertFalse(Settings.isContinuityCorrection());
	}
	
	@Test
	public void testProtectedFunctions() {
		Assert.assertTrue(Settings.isProtectedFunctions());
	}

	@Test
	public void testHypervolumeDelta() {
		Assert.assertGreaterThanOrEqual(Settings.getHypervolumeDelta(), 0.0);
	}

	@Test
	public void testHypervolume() {
		Assert.assertNull(Settings.getHypervolume());
	}

	@Test
	public void testHypervolumeEnabled() {
		Assert.assertTrue(Settings.isHypervolumeEnabled());
	}
	
	@Test
	public void testDiagnosticToolAlgorithmsDefault() {
		Assert.assertGreaterThan(Settings.getDiagnosticToolAlgorithms().size(), 0);
	}
	
	@Test
	public void testDiagnosticToolProblemsDefault() {
		Assert.assertGreaterThan(Settings.getDiagnosticToolProblems().size(), 0);
	}
	
	@Test
	public void testDiagnosticToolAlgorithmsOverride() {
		try (PropertyScope scope = Settings.createScope().with(Settings.KEY_DIAGNOSTIC_TOOL_ALGORITHMS, "foo")) {
			Set<String> result = Settings.getDiagnosticToolAlgorithms();
			Assert.assertSize(1, result);
			Assert.assertContains(result, "foo");
		}
	}
	
	@Test
	public void testDiagnosticToolProblemsOverride() {
		try (PropertyScope scope = Settings.createScope().with(Settings.KEY_DIAGNOSTIC_TOOL_PROBLEMS, "bar")) {
			Set<String> result = Settings.getDiagnosticToolProblems();
			Assert.assertSize(1, result);
			Assert.assertContains(result, "bar");
		}
	}
	
	@Test
	public void testDuplicateMode() {
		Assert.assertEquals(DuplicateMode.NO_DUPLICATE_OBJECTIVES, Settings.getDuplicateMode());
	}
	
	@Test
	public void testDuplicateModeCaseSensitivity() {
		try (PropertyScope scope = Settings.createScope()
				.with(Settings.KEY_DUPLICATE_MODE, DuplicateMode.ALLOW_DUPLICATES.name().toLowerCase())) {
			Assert.assertEquals(DuplicateMode.ALLOW_DUPLICATES, Settings.getDuplicateMode());
			Assert.assertEquals(DuplicateMode.ALLOW_DUPLICATES, new NondominatedPopulation().getDuplicateMode());
		}
	}
	
	@Test
	public void testCreateKey() {
		Assert.assertEquals("", Settings.createKey(""));
		
		Assert.assertEquals("foo", Settings.createKey("foo"));
		Assert.assertEquals("foo", Settings.createKey("foo."));
		
		Assert.assertEquals("foo.bar", Settings.createKey("foo", "bar"));
		Assert.assertEquals("foo.bar", Settings.createKey("foo.", "bar"));
		
		Assert.assertEquals("foo.bar.1.2", Settings.createKey("foo", "bar", "1", "2"));
		Assert.assertEquals("foo.bar.1.2", Settings.createKey("foo.", "bar", "1", "2"));
	}
	
	@Test
	public void testReloadClearsProperties() {
		Assert.assertFalse(Settings.PROPERTIES.contains("foo"));
		
		Settings.PROPERTIES.setString("foo", "bar");
		Assert.assertTrue(Settings.PROPERTIES.contains("foo"));
		Settings.reload();
		
		Assert.assertFalse(Settings.PROPERTIES.contains("foo"));
	}
	
	@Test
	public void testSystemProperty() {
		Assert.assertFalse(Settings.PROPERTIES.contains("org.moeaframework.test.test_property"));
		
		System.setProperty("org.moeaframework.test.test_property", "foo");
		Settings.reload();
		
		Assert.assertTrue(Settings.PROPERTIES.contains("org.moeaframework.test.test_property"));
	}
	
	@Test
	public void testPropertiesFile() throws IOException {
		Assert.assertFalse(Settings.PROPERTIES.contains("org.moeaframework.test.test_property_in_file"));
		
		File file = TempFiles.createFile().withContent("org.moeaframework.test.test_property_in_file=foo");
		System.setProperty(Settings.KEY_CONFIGURATION_FILE, file.getAbsolutePath());
		Settings.reload();
		
		Assert.assertTrue(Settings.PROPERTIES.contains("org.moeaframework.test.test_property_in_file"));		
	}
	
	@Test
	public void testScope() {
		Assert.assertFalse(Settings.PROPERTIES.contains("foo"));
		
		try (PropertyScope scope = Settings.createScope().with("foo", "bar")) {
			Assert.assertTrue(Settings.PROPERTIES.contains("foo"));
			
			Settings.PROPERTIES.setInt("number", 5);
			Assert.assertEquals(5, Settings.PROPERTIES.getInt("number"));
		}
		
		Assert.assertFalse(Settings.PROPERTIES.contains("foo"));
		Assert.assertFalse(Settings.PROPERTIES.contains("number"));
	}

}
