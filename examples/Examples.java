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
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.UIManager;

import org.moeaframework.analysis.diagnostics.LaunchDiagnosticTool;
import org.moeaframework.examples.ga.LOTZ.LOTZExample;
import org.moeaframework.examples.ga.knapsack.KnapsackExample;
import org.moeaframework.examples.ga.onemax.OneMaxExample;
import org.moeaframework.examples.ga.tsplib.A280Example;
import org.moeaframework.examples.ga.tsplib.PR76Example;
import org.moeaframework.examples.gp.ant.LosAltosExample;
import org.moeaframework.examples.gp.ant.SantaFeExample;
import org.moeaframework.examples.gp.regression.QuarticExample;
import org.moeaframework.examples.gp.regression.QuinticExample;
import org.moeaframework.examples.gp.regression.SexticExample;
import org.moeaframework.examples.gui.Example;
import org.moeaframework.examples.gui.ExamplesGUI;
import org.moeaframework.examples.gui.TerminalExample;

/**
 * Launches the demo application allowing users to learn about and experiment
 * with the MOEA Framework using several examples.
 */
public class Examples {
	
	/**
	 * The resource bundle containing the localized example descriptions.
	 */
	private static ResourceBundle resourceBundle;
	
	static {
		try {
			resourceBundle = ResourceBundle.getBundle(
					"org.moeaframework.examples.gui.LocalStrings", 
					Locale.getDefault());
		} catch (MissingResourceException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Private constructor to prevent instantiation.
	 */
	private Examples() {
		super();
	}
	
	/**
	 * Starts the demo application.
	 * 
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(
			        UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// couldn't set system look and feel, continue with default
		}
		
		Vector<Example> examples = new Vector<Example>();
		
		examples.add(new Example(
				resourceBundle.getString("introduction.name"),
				resourceBundle.getString("introduction.description"),
				null));
		
		examples.add(new TerminalExample(
				resourceBundle.getString("example1.name"),
				resourceBundle.getString("example1.description"),
				Example1.class,
				"examples/Example1.java"));
		
		examples.add(new TerminalExample(
				resourceBundle.getString("example2.name"),
				resourceBundle.getString("example2.description"),
				Example2.class,
				"examples/Example2.java"));
		
		examples.add(new TerminalExample(
				resourceBundle.getString("example3.name"),
				resourceBundle.getString("example3.description"),
				Example3.class,
				"examples/Example3.java"));
		
		examples.add(new TerminalExample(
				resourceBundle.getString("example4.name"),
				resourceBundle.getString("example4.description"),
				Example4.class,
				"examples/Example4.java"));
		
		examples.add(new Example(
				resourceBundle.getString("regression1.name"),
				resourceBundle.getString("regression1.description"),
				QuarticExample.class,
				"examples/org/moeaframework/examples/gp/regression/QuarticExample.java",
				"examples/org/moeaframework/examples/gp/regression/SymbolicRegression.java",
				"examples/org/moeaframework/examples/gp/regression/SymbolicRegressionGUI.java"));
		
		examples.add(new Example(
				resourceBundle.getString("regression2.name"),
				resourceBundle.getString("regression2.description"),
				QuinticExample.class,
				"examples/org/moeaframework/examples/gp/regression/QuinticExample.java",
				"examples/org/moeaframework/examples/gp/regression/SymbolicRegression.java",
				"examples/org/moeaframework/examples/gp/regression/SymbolicRegressionGUI.java"));
		
		examples.add(new Example(
				resourceBundle.getString("regression3.name"),
				resourceBundle.getString("regression3.description"),
				SexticExample.class,
				"examples/org/moeaframework/examples/gp/regression/SexticExample.java",
				"examples/org/moeaframework/examples/gp/regression/SymbolicRegression.java",
				"examples/org/moeaframework/examples/gp/regression/SymbolicRegressionGUI.java"));
		
		examples.add(new TerminalExample(
				resourceBundle.getString("lotz.name"),
				resourceBundle.getString("lotz.description"),
				LOTZExample.class,
				"examples/org/moeaframework/examples/ga/LOTZ/LOTZExample.java",
				"examples/org/moeaframework/examples/ga/LOTZ/LOTZ.java"));
		
		examples.add(new TerminalExample(
				resourceBundle.getString("onemax.name"),
				resourceBundle.getString("onemax.description"),
				OneMaxExample.class,
				"examples/org/moeaframework/examples/ga/onemax/OneMaxExample.java",
				"examples/org/moeaframework/examples/ga/onemax/OneMax.java"));
		
		examples.add(new TerminalExample(
				resourceBundle.getString("knapsack.name"),
				resourceBundle.getString("knapsack.description"),
				KnapsackExample.class,
				"examples/org/moeaframework/examples/ga/knapsack/KnapsackExample.java",
				"examples/org/moeaframework/examples/ga/knapsack/Knapsack.java",
				"examples/org/moeaframework/examples/ga/knapsack/knapsack.100.2"));
		
		examples.add(new TerminalExample(
				resourceBundle.getString("ant1.name"),
				resourceBundle.getString("ant1.description"),
				SantaFeExample.class,
				"examples/org/moeaframework/examples/gp/ant/SantaFeExample.java",
				"examples/org/moeaframework/examples/gp/ant/AntProblem.java",
				"examples/org/moeaframework/examples/gp/ant/santafe.trail"));
		
		examples.add(new TerminalExample(
				resourceBundle.getString("ant2.name"),
				resourceBundle.getString("ant2.description"),
				LosAltosExample.class,
				"examples/org/moeaframework/examples/gp/ant/SantaFeExample.java",
				"examples/org/moeaframework/examples/gp/ant/AntProblem.java",
				"examples/org/moeaframework/examples/gp/ant/losaltos.trail"));
		
		examples.add(new Example(
				resourceBundle.getString("tsp1.name"),
				resourceBundle.getString("tsp1.description"),
				PR76Example.class,
				"examples/org/moeaframework/examples/ga/tsplib/PR76Example.java",
				"examples/org/moeaframework/examples/ga/tsplib/TSPExample.java",
				"examples/org/moeaframework/examples/ga/tsplib/pr76.tsp"));
		
		examples.add(new Example(
				resourceBundle.getString("tsp2.name"),
				resourceBundle.getString("tsp2.description"),
				A280Example.class,
				"examples/org/moeaframework/examples/ga/tsplib/A280Example.java",
				"examples/org/moeaframework/examples/ga/tsplib/TSPExample.java",
				"examples/org/moeaframework/examples/ga/tsplib/a280.tsp"));
		
		examples.add(new Example(
				resourceBundle.getString("diagnostic.name"),
				resourceBundle.getString("diagnostic.description"),
				LaunchDiagnosticTool.class));
		
		examples.add(new Example(
				resourceBundle.getString("conclusion.name"),
				resourceBundle.getString("conclusion.description"),
				null));
		
		new ExamplesGUI(examples);
	}

}
