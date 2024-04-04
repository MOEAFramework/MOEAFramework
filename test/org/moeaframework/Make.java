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
package org.moeaframework;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.moeaframework.util.io.RedirectStream;

public class Make {
	
	private Make() {
		super();
	}
	
	/**
	 * Run make in the given folder.
	 * 
	 * @param folder the folder in which make is executed
	 * @param args any additional arguments passed to make
	 * @return the captured output from make
	 */
	public static String runMake(File folder, String... args) throws IOException {
		List<String> command = new ArrayList<String>();
		command.add("make");
		command.addAll(Arrays.asList(args));
		
		System.out.println("Running '" + command.stream().collect(Collectors.joining(" ")) + "' from folder '" +
				folder + "'");
		
		try {
			ProcessBuilder processBuilder = new ProcessBuilder(command);
			processBuilder.directory(folder);
			
			return RedirectStream.capture(processBuilder);
		} catch (InterruptedException e) {
			throw new IOException(e);
		}
	}
	
	/**
	 * Returns {@code true} if the 'make' command exists on the system.
	 * 
	 * @return {@code true} if the 'make' command exists; {@code false} otherwise
	 */
	public static boolean verifyMakeExists() {
		try {
			runMake(new File("."), "--version");
			return true;
		} catch (IOException e) {
			System.err.println(e);
			return false;
		}
	}

}
