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

/**
 * Defines a command for use by a command line utility.  CLIs designed around commands require one position argument,
 * the command name to invoke.
 */
public class Command {
	
	private final String name;
	
	private final Class<? extends CommandLineUtility> implementation;
	
	private final boolean isVisible;
	
	private Command(String name, Class<? extends CommandLineUtility> implementation, boolean isVisible) {
		super();
		this.name = name;
		this.implementation = implementation;
		this.isVisible = isVisible;
	}

	/**
	 * Returns the display name for this command.
	 * 
	 * @return the display name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the class implementing this command.
	 * 
	 * @return the class
	 */
	public Class<? extends CommandLineUtility> getImplementation() {
		return implementation;
	}
	
	/**
	 * Returns {@code true} if this command is visible to users; {@code false} otherwise.
	 * 
	 * @return {@code true} if this command is visible to users; {@code false} otherwise
	 */
	public boolean isVisible() {
		return isVisible;
	}
	
	/**
	 * Creates a command with the given implementation.  The command name is derived from the class name.
	 * 
	 * @param implementation the command implementation
	 * @return the command
	 */
	public static Command of(Class<? extends CommandLineUtility> implementation) {
		return of(implementation.getSimpleName(), implementation);
	}
	
	/**
	 * Creates a command with the given name and implementation.
	 * 
	 * @param name the name of the command
	 * @param implementation the command implementation
	 * @return the command
	 */
	public static Command of(String name, Class<? extends CommandLineUtility> implementation) {
		return new Command(name, implementation, true);
	}
	
	/**
	 * Creates a hidden command with the given implementation.  The command name is derived from the class name.
	 * Hidden commands are not displayed in help messages but can still be invoked.
	 * 
	 * @param implementation the command implementation
	 * @return the command
	 */
	public static Command hidden(Class<? extends CommandLineUtility> implementation) {
		return hidden(implementation.getSimpleName(), implementation);
	}
	
	/**
	 * Creates a hidden command with the given name and implementation.  Hidden commands are not displayed in
	 * help messages but can still be invoked.
	 * 
	 * @param name the name of the command
	 * @param implementation the command implementation
	 * @return the command
	 */
	public static Command hidden(String name, Class<? extends CommandLineUtility> implementation) {
		return new Command(name, implementation, false);
	}
	
}