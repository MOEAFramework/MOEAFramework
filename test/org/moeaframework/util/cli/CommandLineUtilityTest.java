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

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.Capture;
import org.moeaframework.Capture.CaptureResult;

public class CommandLineUtilityTest {
		
	public static class MockStandardUtility extends CommandLineUtility {

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
			new MockStandardUtility().start(args);
		}
		
	}
	
	public static class MockCommandBasedUtility extends CommandLineUtility {

		@Override
		public List<Command> getCommands() {
			List<Command> commands = super.getCommands();
			
			commands.add(Command.of(MockCommand.class));
			
			return commands;
		}
		
		@Override
		public void run(CommandLine commandLine) throws Exception {
			runCommand(commandLine);
		}
		
		public static void main(String[] args) throws Exception {
			new MockCommandBasedUtility().start(args);
		}
		
	}
	
	public static class MockCommand extends CommandLineUtility {

		@Override
		public void run(CommandLine commandLine) throws Exception {
			System.out.println("Run Mock Command");
		}
				
	}

	@Test
	public void testHelp() throws Exception {
		CaptureResult result = Capture.output(MockStandardUtility.class, "--help");
		result.assertSuccessful();
		result.assertNotContains("Invoked run");
	}
	
	@Test
	public void testHelpWithValidOption() throws Exception {
		CaptureResult result = Capture.output(MockStandardUtility.class, "--test", "--help");
		result.assertSuccessful();
	}
	
	@Test
	public void testInvalidOption() throws Exception {
		CaptureResult result = Capture.output(MockStandardUtility.class,  "--invalid");
		result.assertThrows(MissingOptionException.class);
	}
	
	@Test
	public void testMissingOption() throws Exception {
		CaptureResult result = Capture.output(MockStandardUtility.class);
		result.assertThrows(MissingOptionException.class);
	}
	
	@Test
	public void testNormal() throws Exception {
		CaptureResult result = Capture.output(MockStandardUtility.class, "--test");
		result.assertSuccessful();
		result.assertContains("Invoked run");
	}
	
	@Test
	public void testCommandHelp() throws Exception {
		CaptureResult result = Capture.output(MockCommandBasedUtility.class, "--help");
		result.assertSuccessful();
		result.assertContains("MockCommand");
	}

	@Test
	public void testInvalidCommand() throws Exception {
		CaptureResult result = Capture.output(MockCommandBasedUtility.class, "invalid");
		result.assertThrows(ParseException.class);
	}
	
	@Test
	public void testMissingCommand() throws Exception {
		CaptureResult result = Capture.output(MockCommandBasedUtility.class);
		result.assertThrows(ParseException.class);
	}
	
	@Test
	public void testCommandsNormal() throws Exception {
		CaptureResult result = Capture.output(MockCommandBasedUtility.class, "mock");
		result.assertSuccessful();
		result.assertContains("Run Mock Command");
	}
	
	@Test
	public void testPrompt() throws IOException {
		InputStream oldIn = System.in;
		
		try (PipedOutputStream out = new PipedOutputStream(); PipedInputStream in = new PipedInputStream(out)) {
			System.setIn(in);
			
			IOUtils.write("y\n", out, StandardCharsets.UTF_8);
			Assert.assertTrue(new MockStandardUtility().prompt("Respond"));
			
			IOUtils.write("Y\n", out, StandardCharsets.UTF_8);
			Assert.assertTrue(new MockStandardUtility().prompt("Respond"));
			
			IOUtils.write("Yes\n", out, StandardCharsets.UTF_8);
			Assert.assertTrue(new MockStandardUtility().prompt("Respond"));
			
			IOUtils.write("n\n", out, StandardCharsets.UTF_8);
			Assert.assertFalse(new MockStandardUtility().prompt("Respond"));
			
			IOUtils.write("N\n", out, StandardCharsets.UTF_8);
			Assert.assertFalse(new MockStandardUtility().prompt("Respond"));
			
			IOUtils.write("No\n", out, StandardCharsets.UTF_8);
			Assert.assertFalse(new MockStandardUtility().prompt("Respond"));
			
			IOUtils.write("yy\n", out, StandardCharsets.UTF_8);
			Assert.assertFalse(new MockStandardUtility().prompt("Respond"));
			
			IOUtils.write(" \n", out, StandardCharsets.UTF_8);
			Assert.assertFalse(new MockStandardUtility().prompt("Respond"));
			
			IOUtils.write("\n", out, StandardCharsets.UTF_8);
			Assert.assertFalse(new MockStandardUtility().prompt("Respond"));
		} finally {
			System.setIn(oldIn);
		}
	}

}
