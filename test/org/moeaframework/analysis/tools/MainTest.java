/* Copyright 2009-2025 David Hadka
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
package org.moeaframework.analysis.tools;

import org.apache.commons.cli.ParseException;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.Capture;
import org.moeaframework.Capture.CaptureResult;
import org.moeaframework.core.PropertyScope;
import org.moeaframework.core.Settings;
import org.moeaframework.core.TypedProperties;

public class MainTest {

	@Test
	public void testUnrecognizedCommand() throws Exception {
		CaptureResult result = Capture.output(Main.class, "Foo");
		result.assertThrows(ParseException.class);
	}
	
	@Test
	public void testNoCommand() throws Exception {
		CaptureResult result = Capture.output(Main.class);
		result.assertSuccessful();
	}
	
	@Test
	public void testVersion() throws Exception {
		CaptureResult result = Capture.output(Main.class, "--version");
		result.assertSuccessful();
		result.assertEqualsNormalized(TypedProperties.loadBuildProperties().getString("version"));
	}
	
	@Test
	public void testInfo() throws Exception {
		CaptureResult result = Capture.output(Main.class, "--info");
		result.assertSuccessful();
		result.assertContains("Version");
	}
	
	@Test
	public void testHelp() throws Exception {
		CaptureResult result = Capture.output(Main.class, "--help");
		result.assertSuccessful();
		result.assertContains("Select one of the available commands:");
	}
	
	@Test
	public void testVerbose() throws Exception {
		try (PropertyScope scope = Settings.createScope()) {
			CaptureResult result = Capture.output(Main.class, "--verbose");
			result.assertSuccessful();
			Assert.assertTrue(Settings.isVerbose());
		}
	}

}
