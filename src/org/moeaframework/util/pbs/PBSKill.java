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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.moeaframework.core.Settings;
import org.moeaframework.util.CommandLineUtility;

/**
 * Command line utility for killing all PBS jobs belonging to a specified user.
 * Command line options allow only queued jobs or those with job ids within a
 * user-specified range to be killed.
 */
public class PBSKill extends CommandLineUtility {

	/**
	 * The regular expression pattern for parsing the job id from the
	 * {@code qstat} command.
	 */
	private static Pattern idPattern = Pattern.compile(
			Settings.getPBSJobIdRegex());
	
	/**
	 * The regular expression pattern for determining if the job is queued.
	 */
	private static Pattern queuedPattern = Pattern.compile(
			Settings.getPBSQueuedRegex());

	/**
	 * Private constructor to prevent instantiation.
	 */
	private PBSKill() {
		super();
	}

	/**
	 * Returns the list of all jobs in the PBS queue for the specified user
	 * using the {@code qstat} command.
	 * 
	 * @param user the user name
	 * @param queued {@code true} if this method should only return queued jobs;
	 *        {@code false otherwise}
	 * @return the list of all jobs in the PBS queue for the specified user
	 * @throws IOException if an I/O error occurred
	 */
	private List<String> getQueuedJobs(String user, boolean queued) 
	throws IOException {
		Process process = Runtime.getRuntime().exec(MessageFormat.format(
				Settings.getPBSQstatCommand(), user));
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new InputStreamReader(process
					.getInputStream()));

			List<String> jobs = new ArrayList<String>();

			String line = null;
			while ((line = reader.readLine()) != null) {
				Matcher idMatcher = idPattern.matcher(line);
				Matcher queuedMatcher = queuedPattern.matcher(line);
				
				if (idMatcher.matches() && 
						(!queued || queuedMatcher.matches())) {
					jobs.add(idMatcher.group(1));
				}
			}

			return jobs;
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * Kills the PBS job with the specified jod id using the {@code qdel
	 * <job-id>} command.
	 * 
	 * @param job the job id
	 * @throws IOException if an I/O error occurred
	 */
	private void killJob(String job) throws IOException {
		System.out.print("Killing " + job + "...");

		Process qdelProcess = Runtime.getRuntime().exec(MessageFormat.format(
				Settings.getPBSQdelCommand(), job));

		try {
			if (qdelProcess.waitFor() == 0) {
				System.out.println("success!");
			} else {
				System.out.println("failed!");
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	@SuppressWarnings("static-access")
	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		
		options.addOption(OptionBuilder
				.withLongOpt("user")
				.hasArg()
				.withArgName("userid")
				.withDescription("userid of the jobs to kill")
				.create('u'));
		options.addOption(OptionBuilder
				.withLongOpt("range")
				.hasArg()
				.withArgName("min:max")
				.withDescription("range of job ids to kill")
				.create('r'));
		options.addOption(OptionBuilder
				.withLongOpt("queued")
				.withDescription("kill only queued jobs")
				.create('q'));
		
		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws IOException {
		String userid = null;
		
		if (commandLine.hasOption("user")) {
			userid = commandLine.getOptionValue("user");
		} else {
			userid = System.getProperty("user.name");
		}
		
		boolean queued = commandLine.hasOption("queued");
		
		List<String> jobs = getQueuedJobs(userid, queued);
		
		if (commandLine.hasOption("range")) {
			String range = commandLine.getOptionValue("range");
			int colon = range.indexOf(':');
			int min = 0;
			int max = Integer.MAX_VALUE;
			
			if (colon < 0) {
				min = max = Integer.parseInt(range);
			} else if (colon == 0) {
				max = Integer.parseInt(range.substring(1));
			} else if (colon == range.length()-1) {
				min = Integer.parseInt(range.substring(0, colon));
			} else {
				min = Integer.parseInt(range.substring(0, colon));
				max = Integer.parseInt(range.substring(colon+1));
			}
			
			for (String job : jobs) {
				int jobId = Integer.parseInt(job);
				
				if ((min <= jobId) && (jobId <= max)) {
					killJob(job);
				}
			}
		} else {
			for (String job : jobs) {
				killJob(job);
			}
		}
	}

	/**
	 * Command line utility for killing all PBS jobs belonging to a specified
	 * user.
	 * 
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		new PBSKill().start(args);
	}

}
