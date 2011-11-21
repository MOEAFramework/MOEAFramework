/* Copyright 2009-2011 David Hadka
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
package org.moeaframework.util.pbs;

import java.io.IOException;
import java.io.PrintStream;
import java.text.MessageFormat;

import org.moeaframework.core.Settings;
import org.moeaframework.util.io.RedirectStream;

/**
 * Translates individual {@link PBSJob}s to valid PBS scripts and submits the
 * scripts using {@code qsub}.
 */
public class PBSSubmitter {

	/**
	 * Constructs a PBS submitter.
	 */
	private PBSSubmitter() {
		super();
	}

	/**
	 * Translates the specified PBS job to a valid PBS script which can be
	 * submitted to the system's {@code qsub} program.
	 * 
	 * @param job the PBS job
	 * @return the PBS script sued to execute the specified PBS job
	 */
	protected static String toPBSScript(PBSJob job) {
		StringBuilder sb = new StringBuilder();
		
		for (String command : job.getCommands()) {
			sb.append(command).append(Settings.NEW_LINE);
		}
		
		return MessageFormat.format(Settings.getPBSScript(), 
				job.getName(),
				job.getNodes(),
				job.getWalltime(),
				sb.toString());
	}

	/**
	 * Submits the specified PBS job to the PBS queue using the {@code qsub}
	 * program.
	 * 
	 * @param job the PBS job to be submitted
	 * @throws IOException if an error occurred while invoking {@code qsub}
	 */
	public static void submit(PBSJob job) throws IOException {
		Process process = Runtime.getRuntime().exec(
				Settings.getPBSQsubCommand());

		RedirectStream.redirect(process.getInputStream(), System.out);
		RedirectStream.redirect(process.getErrorStream(), System.err);

		String script = toPBSScript(job);
		System.out.println(script);

		PrintStream ps = new PrintStream(process.getOutputStream());
		ps.print(script);
		ps.close();

		try {
			int exitStatus = process.waitFor();

			if (exitStatus != 0) {
				throw new IOException("qsub terminated with exit status "
						+ exitStatus);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

}
