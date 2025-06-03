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
package org.moeaframework.analysis.diagnostics;

import org.apache.commons.cli.CommandLine;
import org.moeaframework.util.cli.CommandLineUtility;
import org.moeaframework.util.mvc.UI;

/**
 * Command line utility for launching the diagnostic tool.
 */
public class LaunchDiagnosticTool extends CommandLineUtility {

	/**
	 * Constructs the command line utility for launching the diagnostic tool.
	 */
	public LaunchDiagnosticTool() {
		super();
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		UI.show(() -> new DiagnosticTool());
	}
	
	/**
	 * Starts the command line utility for launching the diagnostic tool.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new LaunchDiagnosticTool().start(args);
	}

}
