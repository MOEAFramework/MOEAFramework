package org.moeaframework.studies.borg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.moeaframework.core.CoreUtils;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.real.AdaptiveMetropolis;
import org.moeaframework.core.operator.real.DifferentialEvolution;
import org.moeaframework.core.operator.real.PCX;
import org.moeaframework.core.operator.real.PM;
import org.moeaframework.core.operator.real.SPX;
import org.moeaframework.core.operator.real.SBX;
import org.moeaframework.core.operator.real.UNDX;
import org.moeaframework.core.operator.real.UM;
import org.moeaframework.core.variable.RealVariable;

public class OperatorPlots {

	public static void main(String[] args) throws IOException {
		Solution s1 = new Solution(2, 0);
		Solution s2 = new Solution(2, 0);
		Solution s3 = new Solution(2, 0);

		s1.setVariable(0, new RealVariable(-0.25, -1.0, 1.0));
		s1.setVariable(1, new RealVariable(-0.25, -1.0, 1.0));
		s2.setVariable(0, new RealVariable(0.25, -1.0, 1.0));
		s2.setVariable(1, new RealVariable(-0.25, -1.0, 1.0));
		s3.setVariable(0, new RealVariable(0.0, -1.0, 1.0));
		s3.setVariable(1, new RealVariable(0.25, -1.0, 1.0));

		Solution[] solutions = null;
		
		AdaptiveMetropolis am = new AdaptiveMetropolis(3, 10000, 1.0);
		solutions = am.evolve(new Solution[] { s1, s2, s3 });
		mutate(solutions);
		write("AM.dat", solutions);

		PCX pcx = new PCX(3, 10000);
		solutions = pcx.evolve(new Solution[] { s1, s2, s3 });
		mutate(solutions);
		write("PCX.dat", solutions);

		UNDX undx = new UNDX(3, 10000);
		solutions = undx.evolve(new Solution[] { s1, s2, s3 });
		mutate(solutions);
		write("UNDX.dat", solutions);

		SPX spx = new SPX(3, 10000);
		solutions = spx.evolve(new Solution[] { s1, s2, s3 });
		mutate(solutions);
		write("SPX.dat", solutions);

		SBX sbx = new SBX(1.0, 20.0);
		solutions = new Solution[0];
		for (int i = 0; i < 5000; i++) {
			solutions = CoreUtils.merge(solutions, sbx.evolve(new Solution[] {
					s1, s3 }));
		}
		mutate(solutions);
		write("SBX.dat", solutions);

		UM um = new UM(0.5);
		solutions = new Solution[0];
		for (int i = 0; i < 10000; i++) {
			solutions = CoreUtils.merge(solutions, um
					.evolve(new Solution[] { s1 }));
		}
		write("UM.dat", solutions);

		DifferentialEvolution de = new DifferentialEvolution(1.0, 1.0);
		solutions = new Solution[0];
		for (int i = 0; i < 10000; i++) {
			solutions = CoreUtils.merge(solutions, de.evolve(new Solution[] {
					s3, s2, s1, s3 }));
		}
		mutate(solutions);
		write("DE.dat", solutions);
	}

	public static void mutate(Solution[] solutions) {
		PM pm = new PM(0.5, 250.0);
		for (int i = 0; i < solutions.length; i++) {
			solutions[i] = pm.evolve(new Solution[] { solutions[i] })[0];
		}
	}

	public static void write(String filename, Solution[] solutions)
			throws IOException {
		File file = new File(filename);
		PrintWriter writer = null;

		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));

			for (Solution solution : solutions) {
				writer
						.print(((RealVariable)solution.getVariable(0))
								.getValue());
				for (int i = 1; i < solution.getNumberOfVariables(); i++) {
					writer.print(' ');
					writer.print(((RealVariable)solution.getVariable(i))
							.getValue());
				}
				writer.println();
			}
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

}
