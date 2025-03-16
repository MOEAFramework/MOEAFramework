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
package org.moeaframework.util.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.junit.Test;
import org.moeaframework.Capture;
import org.moeaframework.Capture.CaptureResult;

public class CommandLineUtilityTest {
		
	public static class MockCommandLineUtility extends CommandLineUtility {

		@Override
		public Options getOptions() {
			Options options = super.getOptions();
			
			options.addOption(Option.builder("t")
					.required()
					.longOpt("test")
					.build());
			
			return options;
		}

		@Override
		public void run(CommandLine commandLine) throws Exception {
			System.out.println("Invoked run");
		}
		
		public static void main(String[] args) throws Exception {
			new MockCommandLineUtility().start(args);
		}
		
	}

	@Test
	public void testHelp() throws Exception {
		CaptureResult result = Capture.output(MockCommandLineUtility.class, "--help");
		result.assertSuccessful();
		result.assertNotContains("Invoked run");
	}
	
	@Test
	public void testHelpWithValidOption() throws Exception {
		CaptureResult result = Capture.output(MockCommandLineUtility.class, "--test", "--help");
		result.assertSuccessful();
	}
	
	@Test
	public void testInvalidOption() throws Exception {
		CaptureResult result = Capture.output(MockCommandLineUtility.class,  "--invalid");
		result.assertThrows(MissingOptionException.class);
	}
	
	@Test
	public void testMissingOption() throws Exception {
		CaptureResult result = Capture.output(MockCommandLineUtility.class);
		result.assertThrows(MissingOptionException.class);
	}
	
	@Test
	public void testNormal() throws Exception {
		CaptureResult result = Capture.output(MockCommandLineUtility.class, "--test");
		result.assertSuccessful();
		result.assertContains("Invoked run");
	}

}
