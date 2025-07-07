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
package org.moeaframework;

import java.awt.GraphicsEnvironment;
import java.io.File;

import org.apache.commons.lang3.SystemUtils;
import org.moeaframework.core.Settings;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.util.io.RedirectStream;

public class Assume extends org.junit.Assume {
	
	@SuppressWarnings("deprecation")
	private Assume() {
		super();
	}
	
	public static void skip(String message) {
		assumeTrue(message, false);
	}
	
	public static <T> T assumeInstanceOf(Class<T> cls, Object object) {
		assumeTrue("Expected object of type " + cls + " but found " + object.getClass().getName() + ", skipping test",
				cls.isInstance(object));
		
		return cls.cast(object);
	}
	
	public static void assumeFileExists(File file) {
		assumeTrue(file + " does not exist, skipping test", file.exists());
	}
	
	public static void assumePOSIX() {
		assumeTrue("System is not POSIX-compliant, skipping test", SystemUtils.IS_OS_UNIX);
	}
	
	public static void assumeCommand(String... args) {
		try {
			ProcessBuilder processBuilder = new ProcessBuilder(args);
			RedirectStream.pipe(processBuilder, System.out);
		} catch (Exception e) {
			assumeNoException(args[0] + " is not available on this system, skipping test", e);
		}
	}
	
	public static void assumeMakeExists() {
		assumeTrue("Make is not available on this system, skipping test", Make.isMakeAvailable());
	}
	
	public static void assumeFortranExists() {
		assumeCommand("gfortran", "--version");
	}
	
	public static void assumePythonExists() {
		assumeCommand(Settings.getPythonCommand(), "--version");
	}
	
	public static void assumePythonModule(String module) {
		try {
			ProcessBuilder processBuilder = new ProcessBuilder(Settings.getPythonCommand(), "-c", "import " + module);
			RedirectStream.pipe(processBuilder, System.out);
		} catch (Exception e) {
			assumeNoException(module + " is not installed", e);
		}
	}
	
	public static void assumeMatlabExists() {
		assumeCommand("matlab", "-batch", "version");
	}
	
	public static void assumeGitHubActions() {
		assumeTrue("Expected to run on GitHub Actions, skipping test", isGitHubActions());
	}
	
	public static void assumeHasDisplay() {
		assumeTrue("Skipping test as the system has no display", !GraphicsEnvironment.isHeadless() &&
				GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().length > 0);
	}
	
	public static void assumeJMetalExists() {
		Assume.assumeTrue("JMetal-Plugin required to run test",
				AlgorithmFactory.getInstance().hasProvider("org.moeaframework.algorithm.jmetal.JMetalAlgorithms"));
	}
	
	public static boolean isGitHubActions() {
		return System.getenv("GITHUB_ACTIONS") != null;
	}

}
