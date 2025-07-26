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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.Assume;
import org.moeaframework.Capture;
import org.moeaframework.Capture.CaptureResult;

public class InitializeTest {
	
	@Test
	public void testSh() throws IOException {
		Assume.assumePOSIX();
		Assume.assumeCommand("/bin/sh", "-c", "true");
		
		CaptureResult result = Capture.output(new ProcessBuilder("/bin/sh", "-c", getTestScript("sh")));
		result.assertSuccessful();
	}

	@Test
	public void testBash() throws IOException {
		Assume.assumePOSIX();
		Assume.assumeCommand("/bin/bash", "-c", "true");
		
		CaptureResult result = Capture.output(new ProcessBuilder("/bin/bash", "-c", getTestScript("bash")));
		result.assertSuccessful();
	}
	
	@Test
	public void testZsh() throws IOException {
		Assume.assumePOSIX();
		Assume.assumeCommand("/bin/zsh", "-c", "true");
		
		CaptureResult result = Capture.output(new ProcessBuilder("/bin/zsh", "-c", getTestScript("zsh")));
		result.assertSuccessful();
	}
	
	@Test
	public void testCmd() throws IOException {
		Assume.assumeTrue(SystemUtils.IS_OS_WINDOWS);
		
		CaptureResult result = Capture.output(new ProcessBuilder("cmd.exe", "/c", getTestScript("cmd")));
		result.assertSuccessful();
	}
	
	@Test
	public void testPowershell() throws IOException {
		Assume.assumeTrue(SystemUtils.IS_OS_WINDOWS);
		
		CaptureResult result = Capture.output(new ProcessBuilder("pwsh", "-Command", getTestScript("pwsh")));
		result.assertSuccessful();
	}
	
	private String getTestScript(String shell) throws IOException {
		CaptureResult initOutput = Capture.output(Initialize.class, "--shell", shell);
		List<String> commands = new ArrayList<>();
		
//		cp ~/.bash_profile ~/.bash_profile.bkup
//	    eval "$(./cli init --shell bash --permanent)"
//	    EXPECTED="$(./cli init --shell bash)"
//	    ACTUAL="$(diff ~/.bash_profile ~/.bash_profile.bkup | grep "<" | cut -c 3-)"
//	    [ "$EXPECTED" = "$ACTUAL" ]
		
		switch (shell) {
			case "zsh", "bash", "sh" -> {
				commands.add("OLD_PATH=\"$PATH\"");
			}
			case "pwsh" -> {
				commands.add("Remove-Alias cli -Force"); // Remove Clear-Item alias that conflicts with name
			}
			default -> {}
		}
		
		commands.add(initOutput.toString());
				
		switch (shell) {
			case "zsh", "bash", "sh" -> {
				commands.add("[ \"$MOEAFRAMEWORK_ROOT\" = \"$(pwd)\" ]");
				commands.add("[ \"$PATH\" = \"$MOEAFRAMEWORK_ROOT:$OLD_PATH\" ]");
				
				if (Files.exists(Path.of("cli"))) {
					commands.add("[ \"$(which cli)\" = \"$(pwd)/cli\" ]");
				}
			}
			case "cmd" -> {
				commands.add("where cli");
			}
			case "pwsh" -> {
				commands.add("$actual = (Get-Command cli).Path");
				commands.add("$expected = Join-Path (Get-Location) \"cli.cmd\"");
				commands.add("if ($actual -ne $expected) {");
				commands.add("    Write-Warning \"FAILED: Incorrect local path: '$($actual)', expected '$($expected)'\"");
				commands.add("    exit -1");
				commands.add("}");
			}
			default -> {}
		}
		
		return String.join(System.lineSeparator(), commands);
	}

}
