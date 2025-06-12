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
package org.moeaframework.examples.runtime;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.algorithm.extension.Frequency;
import org.moeaframework.analysis.plot.PlotBuilder;
import org.moeaframework.analysis.plot.Style;
import org.moeaframework.analysis.runtime.InstrumentedAlgorithm;
import org.moeaframework.analysis.runtime.Instrumenter;
import org.moeaframework.analysis.series.IndexedResult;
import org.moeaframework.problem.DTLZ.DTLZ2;
import org.moeaframework.problem.Problem;

/**
 * Uses ImageMagick (must be installed separately) to produce an animated GIF showing the population convergence.
 * <p>
 * Linux: sudo apt install imagemagick
 * MacOS: brew install imagemagick
 */
public class AnimatedGIFExample {

	public static void main(String[] args) throws IOException, InterruptedException {
		// Setup the problem and algorithm
		Problem problem = new DTLZ2(2);
		NSGAII algorithm = new NSGAII(problem);
		
		// Instrument the algorithm to collect the populations
		Instrumenter instrumenter = new Instrumenter()
				.withReferenceSet("pf/DTLZ2.2D.pf")
				.withFrequency(Frequency.ofEvaluations(100));
		
		InstrumentedAlgorithm<NSGAII> instrumentedAlgorithm = instrumenter.instrument(algorithm);
		instrumentedAlgorithm.run(10000);
		
		// Generate images and a manifest file listing the images in their correct order
		File tempDirectory = Files.createTempDirectory("animate").toFile();
		File imageList = new File(tempDirectory, "imageList.txt");
		
		try (PrintWriter writer = new PrintWriter(imageList)) {
			for (IndexedResult result : instrumentedAlgorithm.getSeries()) {
				File imgFile = new File(tempDirectory, "img" + result.getIndex() + ".png");
				
				PlotBuilder.xy()
						.scatter("Population", result.getPopulation())
						.scatter("Reference Set", instrumenter.getReferenceSet(), Style.black())
						.xLim(0.0, 1.5)
						.yLim(0.0, 1.5)
						.title("NSGAII on DTLZ2")
						.subtitle(result.getIndex() + " NFE")
						.save(imgFile);
				
				writer.println(imgFile.getAbsolutePath());
			}
		}
				
		// Invoke ImageMagik to combine the images into an animated GIF
		new ProcessBuilder()
				.command("convert", "-delay", "10", "-loop", "0", "@" + imageList.getAbsolutePath(), "combined.gif")
				.inheritIO()
				.start()
				.waitFor();
	}

}
