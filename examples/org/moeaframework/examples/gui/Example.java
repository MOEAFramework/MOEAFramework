/* Copyright 2009-2014 David Hadka
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

public class Example {
	
	private final String name;
	
	private final String description;
	
	private final Class<?> mainClass;
	
	private final String[] resources;
	
	public Example(String name, String description, Class<?> mainClass,
			String... resources) {
		super();
		this.name = name;
		this.description = description;
		this.mainClass = mainClass;
		this.resources = resources;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Class<?> getMainClass() {
		return mainClass;
	}

	public String[] getResources() {
		return resources;
	}

	@Override
	public String toString() {
		return name;
	}
	
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
