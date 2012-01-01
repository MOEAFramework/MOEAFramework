/* Copyright 2009-2012 David Hadka
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

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a PBS job.
 */
public class PBSJob {

	/**
	 * The name of this job.
	 */
	private final String name;

	/**
	 * The walltime, in hours, required by this job.
	 */
	private final int walltime;

	/**
	 * The number of nodes required by this job.
	 */
	private final int nodes;

	/**
	 * The list of commands to be executed by this job.
	 */
	private final List<String> commands;

	/**
	 * Constructs a PBS job with the specified name and walltime that requires
	 * 1 node. Equivalent to calling {@code new PBSJob(name, walltime, 1)}.
	 * 
	 * @param name the name of this job
	 * @param walltime the walltime, in hours, required by this job
	 */
	public PBSJob(String name, int walltime) {
		this(name, walltime, 1);
	}

	/**
	 * Constructs a PBS job with the specified name, walltime and number of 
	 * nodes.
	 * 
	 * @param name the name of this job
	 * @param walltime the walltime, in hours, required by this job
	 * @param nodes the number of nodes required by this job
	 */
	public PBSJob(String name, int walltime, int nodes) {
		this.name = name;
		this.walltime = walltime;
		this.nodes = nodes;

		commands = new ArrayList<String>();
	}

	/**
	 * Constructs a PBS job with the specified name, walltime, number of nodes,
	 * and command. Additional commands can be specified with the
	 * {@code addCommand} method.
	 * 
	 * @param name the name of this job
	 * @param walltime the walltime, in hours, requried by this job
	 * @param nodes the number of nodes required by this job
	 * @param command the first command to be executed by this job
	 */
	public PBSJob(String name, int walltime, int nodes, String command) {
		this(name, walltime, nodes);

		addCommand(command);
	}

	/**
	 * Appends a command to be executed by this job. Commands are executed in
	 * the sequence they are added to this job.
	 * 
	 * @param command the command to be executed by this job
	 */
	public void addCommand(String command) {
		commands.add(command);
	}

	/**
	 * Returns the name of this job.
	 * 
	 * @return the name of this job
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the walltime, in hours, required by this job.
	 * 
	 * @return the walltime, in hours, requried by this job
	 */
	public int getWalltime() {
		return walltime;
	}

	/**
	 * Returns the number of nodes required by this job.
	 * 
	 * @return the number of nodes required by this job
	 */
	public int getNodes() {
		return nodes;
	}

	/**
	 * Returns the list of commands to be executed by this job.
	 * 
	 * @return the list of commands to be executed by this job
	 */
	public List<String> getCommands() {
		return commands;
	}

}
