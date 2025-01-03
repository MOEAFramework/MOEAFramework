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
import java.nio.file.Path;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SystemUtils;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.util.cli.CommandLineUtility;

/**
 * Command line utility to help setup the MOEA Framework on the host system.  This prints out the suggested commands to
 * configure the system / profile:
 * <pre>{@code
 *   ./cli init
 * }</pre>
 * <p>
 * On Linux or Mac systems, we can evaluate these commands within the shell directly
 * <pre>{@code
 *   eval "$(./cli init)"
 * }</pre>
 */
public class Initialize extends CommandLineUtility {
	
	private static final String ROOT_ENVVAR = "MOEAFRAMEWORK_ROOT";
		
	private Initialize() {
		super();
	}
	
	@Override
	public Options getOptions() {
		Options options = super.getOptions();

		options.addOption(Option.builder("p")
				.longOpt("permanent")
				.build());
		options.addOption(Option.builder("s")
				.longOpt("shell")
				.hasArg()
				.build());

		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws IOException {
		String root = System.getenv().getOrDefault(ROOT_ENVVAR, Path.of("").toAbsolutePath().toString());
		
		if (SystemUtils.IS_OS_WINDOWS) {
			if (System.getenv("PSModulePath") != null) {
				if (commandLine.hasOption("permanent")) {
					System.out.println("[Environment]::SetEnvironmentVariable(\"" + ROOT_ENVVAR + "\", \"" + root + "\", [EnvironmentVariableTarget]::Machine)");
					System.out.println("[Environment]::SetEnvironmentVariable(\"PATH\", \"$($env:PATH);$($env:" + ROOT_ENVVAR + ")\", [EnvironmentVariableTarget]::Machine)");
				} else {
					System.out.println("$env:" + ROOT_ENVVAR + "=\"" + root + "\"");
					System.out.println("$env:PATH=\"$($env:PATH);$($env:" + ROOT_ENVVAR + ")\"");
				}
			} else {
				if (commandLine.hasOption("permanent")) {
					System.out.println("setx " + ROOT_ENVVAR + "=" + root);
					System.out.println("setx PATH=%PATH%;" + root);
				} else {
					System.out.println("set " + ROOT_ENVVAR + "=" + root);
					System.out.println("set PATH=%PATH%;" + root);
				}
			}
		} else if (SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_UNIX || SystemUtils.IS_OS_MAC) {
			String shell = getShell(commandLine);
			String shellConfig = getShellConfigFile(shell);
			
			if (commandLine.hasOption("permanent") && shellConfig != null) {
				System.out.println("echo 'export " + ROOT_ENVVAR + "=\"" + root + "\"' >> " + shellConfig);
				System.out.println("echo 'export PATH=\"$" + ROOT_ENVVAR + ":$PATH\"' >> " + shellConfig);
			} else {
				System.out.println("export " + ROOT_ENVVAR + "=\"" + root + "\"");
				System.out.println("export PATH=\"$" + ROOT_ENVVAR + ":$PATH\"");
			}
		} else {
			System.err.println("Setup instructions not available for " + SystemUtils.OS_NAME);
		}
	}
	
	/**
	 * Returns the configured default shell.  Note that this can differ from the current shell used to invoke Java.
	 * 
	 * @return the shell name (e.g., {@code "bash"})
	 */
	private String getShell(CommandLine commandLine) {
		String shell = commandLine.getOptionValue("shell");
				
		if (shell == null) {
			shell = System.getenv("SHELL");
		}
		
		if (shell == null) {
			throw new FrameworkException("Unable to determine shell, please provide --shell option");
		}
		
		return FilenameUtils.getName(shell);
	}
	
	/**
	 * Returns the configuration file for the given shell.
	 * <p>
	 * Implementation note: Different configuration files are used in different scenarios, such as for logins and
	 * non-interactive shells, including for example {@code .bashrc}, {@code .bash_profile}, {@code .profile}, and
	 * {@code .bash_login}.  These configuration files are chosen to run once during login.
	 * 
	 * @param shell the shell name
	 * @return the shell config file or {@code null} if the shell is not recognized
	 */
	private String getShellConfigFile(String shell) {
		if (shell.equalsIgnoreCase("bash")) {
			return "~/.bash_profile";
		} else if (shell.equalsIgnoreCase("zsh")) {
			return "~/.zshenv";
		} else {
			return null;
		}
	}

	/**
	 * The main entry point for this command line utility.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new Initialize().start(args);
	}

}
