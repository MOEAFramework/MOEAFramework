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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.Capture;
import org.moeaframework.Capture.CaptureResult;
import org.moeaframework.core.PropertyScope;
import org.moeaframework.core.Settings;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.util.cli.CommandLineUtility;

public class MainTest {
	
	@Before
	public void setUp() {
		Main.registerTool(CustomTool.class);
	}

	@Test
	public void testUnrecognizedCommand() throws Exception {
		CaptureResult result = Capture.output(Main.class, "Foo");
		result.assertThrows(UnrecognizedOptionException.class);
	}
	
	@Test
	public void testNoCommand() throws Exception {
		CaptureResult result = Capture.output(Main.class);
		result.assertSuccessful();
	}
	
	@Test
	public void testVersion() throws Exception {
		CaptureResult result = Capture.output(Main.class, "--version", "CustomTool");
		result.assertSuccessful();
		result.assertEqualsNormalized(TypedProperties.loadBuildProperties().getString("version"));
		result.assertNotContains("CustomTool.Run");
	}
	
	@Test
	public void testInfo() throws Exception {
		CaptureResult result = Capture.output(Main.class, "--info", "CustomTool");
		result.assertSuccessful();
		result.assertContains("Version");
		result.assertNotContains("CustomTool.Run");
	}
	
	@Test
	public void testHelp() throws Exception {
		CaptureResult result = Capture.output(Main.class, "--help", "CustomTool");
		result.assertSuccessful();
		result.assertContains("CustomTool");
		result.assertNotContains("CustomTool.Run");
	}
	
	@Test
	public void testVerbose() throws Exception {
		try (PropertyScope scope = Settings.createScope()) {
			CaptureResult result = Capture.output(Main.class, "--verbose", "CustomTool");
			result.assertSuccessful();
			result.assertEqualsNormalized("CustomTool.Run");
			Assert.assertTrue(Settings.isVerbose());
		}
	}
	
	@Test
	public void testCommand() throws Exception {
		CaptureResult result = Capture.output(Main.class, "CustomTool");
		result.assertSuccessful();
		result.assertEqualsNormalized("CustomTool.Run");
	}
	
	public static class CustomTool extends CommandLineUtility {

		@Override
		public void run(CommandLine commandLine) throws Exception {
			System.out.println("CustomTool.Run");
		}
		
		public static void main(String[] args) throws Exception {
			new CustomTool().start(args);
		}
		
	}

}
