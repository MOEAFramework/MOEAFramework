/* Copyright 2009-2016 David Hadka
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
package org.moeaframework.examples.gui;

/**
 * An example that runs on the command line and prints its results to standard
 * output.  Any standard output will be displayed in a special GUI that mimics
 * a terminal window.
 */
public class TerminalExample extends Example {
	
	/**
	 * Constructs a new example that runs on the command line.
	 * 
	 * @param name the name of this example
	 * @param description the description of this example
	 * @param mainClass the class defining the main method that is invoked to
	 *        start this example
	 * @param resources any resources, such as source code or data files, that
	 *        should be displayed in the examples GUI
	 */
	public TerminalExample(String name, String description, Class<?> mainClass,
			String... resources) {
		super(name, description, mainClass, resources);
	}
	
	@Override
	public void run() {
		new TerminalGUI(this);
	}
	
}