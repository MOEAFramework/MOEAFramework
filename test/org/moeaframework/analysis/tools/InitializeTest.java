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

import java.io.File;
import java.nio.file.Path;

import org.junit.Test;
import org.moeaframework.Capture;
import org.moeaframework.Capture.CaptureResult;

public class InitializeTest {

	@Test
	public void testBash() throws Exception {
		CaptureResult result = Capture.output(Initialize.class, "--shell", "bash");
		result.assertContains("export MOEAFRAMEWORK_ROOT=\"" + Path.of("").toAbsolutePath().toString() + "\"");
		result.assertContains("export PATH=\"$MOEAFRAMEWORK_ROOT" + File.pathSeparator + "$PATH\"");
		result.assertNotContains("~/.bash_profile");
	}
	
	@Test
	public void testBashPermanent() throws Exception {
		CaptureResult result = Capture.output(Initialize.class, "--shell", "bash", "--permanent");
		result.assertContains("export MOEAFRAMEWORK_ROOT=\"" + Path.of("").toAbsolutePath().toString() + "\"");
		result.assertContains("export PATH=\"$MOEAFRAMEWORK_ROOT" + File.pathSeparator + "$PATH\"");
		result.assertContains(">> ~/.bash_profile");
	}
	
	@Test
	public void testZsh() throws Exception {
		CaptureResult result = Capture.output(Initialize.class, "--shell", "zsh");
		result.assertContains("export MOEAFRAMEWORK_ROOT=\"" + Path.of("").toAbsolutePath().toString() + "\"");
		result.assertContains("export PATH=\"$MOEAFRAMEWORK_ROOT" + File.pathSeparator + "$PATH\"");
		result.assertNotContains("~/.zshenv");
	}
	
	@Test
	public void testZshPermanent() throws Exception {
		CaptureResult result = Capture.output(Initialize.class, "--shell", "zsh", "--permanent");
		result.assertContains("export MOEAFRAMEWORK_ROOT=\"" + Path.of("").toAbsolutePath().toString() + "\"");
		result.assertContains("export PATH=\"$MOEAFRAMEWORK_ROOT" + File.pathSeparator + "$PATH\"");
		result.assertContains(">> ~/.zshenv");
	}
	
	@Test
	public void testCmd() throws Exception {
		CaptureResult result = Capture.output(Initialize.class, "--shell", "cmd");
		result.assertContains("set MOEAFRAMEWORK_ROOT=" + Path.of("").toAbsolutePath().toString());
		result.assertContains("set PATH=" + Path.of("").toAbsolutePath().toString() + File.pathSeparator + "%PATH%");
	}
	
	@Test
	public void testPowershell() throws Exception {
		CaptureResult result = Capture.output(Initialize.class, "--shell", "pwsh");
		result.assertContains("$env:MOEAFRAMEWORK_ROOT=\"" + Path.of("").toAbsolutePath().toString() + "\"");
		result.assertContains("$env:PATH=\"$($env:MOEAFRAMEWORK_ROOT)" + File.pathSeparator + "$($env:PATH)\"");
	}
	
	@Test
	public void testPowershellPermanent() throws Exception {
		CaptureResult result = Capture.output(Initialize.class, "--shell", "pwsh", "--permanent");
		result.assertContains("[Environment]::SetEnvironmentVariable(\"MOEAFRAMEWORK_ROOT\", \"" +
				Path.of("").toAbsolutePath().toString() + "\"");
		result.assertContains("[Environment]::SetEnvironmentVariable(\"PATH\", \"" +
				Path.of("").toAbsolutePath().toString() + File.pathSeparator + "$($MachinePath)\"");
	}
	
}
