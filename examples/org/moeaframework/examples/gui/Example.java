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
 * An example for use in the {@link ExamplesGUI}.
 */
public class Example {
	
	/**
	 * The name of this example.
	 */
	private final String name;
	
	/**
	 * The description of this example.
	 */
	private final String description;
	
	/**
	 * The class defining the main method that is invoked to start this
	 * example.
	 */
	private final Class<?> mainClass;
	
	/**
	 * Any resources, such as source code or data files, that should be
	 * displayed in the examples GUI.
	 */
	private final String[] resources;
	
	/**
	 * Constructs a new example.
	 * 
	 * @param name the name of this example
	 * @param description the description of this example
	 * @param mainClass the class defining the main method that is invoked to
	 *        start this example
	 * @param resources any resources, such as source code or data files, that
	 *        should be displayed in the examples GUI
	 */
	public Example(String name, String description, Class<?> mainClass,
			String... resources) {
		super();
		this.name = name;
		this.description = description;
		this.mainClass = mainClass;
		this.resources = resources;
	}

	/**
	 * Returns the name of this example.
	 * 
	 * @return the name of this example
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the description of this example.
	 * 
	 * @return the description of this example
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the class defining the main method that is invoked to start this
	 * example.
	 * 
	 * @return the class defining the main method that is invoked to start this
	 *         example
	 */
	public Class<?> getMainClass() {
		return mainClass;
	}

	/**
	 * Returns any resources, such as source code or data files, that should be
	 * displayed in the examples GUI.
	 * 
	 * @return any resources, such as source code or data files, that should be
	 *         displayed in the examples GUI
	 */
	public String[] getResources() {
		return resources;
	}

	@Override
	public String toString() {
		return name;
	}
	
	/**
	 * Runs the example.  The example itself is responsible for providing any
	 * GUI to display the results.
	 */
	public void run() {
		try {
			Runtime.getRuntime().exec(new String[] { 
					"java",
					"-classpath",
					System.getProperty("java.class.path"),
					getMainClass().getName() });
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
}
