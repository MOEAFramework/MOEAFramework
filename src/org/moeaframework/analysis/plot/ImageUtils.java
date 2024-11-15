/* Copyright 2009-2024 David Hadka
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
package org.moeaframework.analysis.plot;

import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.moeaframework.util.validate.Validate;

/**
 * Utility for creating images from charts.
 */
public class ImageUtils {

	/**
	 * Saves the chart to an image file.  The type of image is determined from the filename extension, which must
	 * match one of the supported file types in {@link ImageFileType}.
	 * 
	 * @param chart the chart to save
	 * @param filename the filename
	 * @throws IOException if an I/O error occurred
	 */
	public static void save(JFreeChart chart, String filename) throws IOException {
		save(chart, new File(filename));
	}

	/**
	 * Saves the chart to an image file.  The type of image is determined from the filename extension, which must
	 * match one of the supported file types in {@link ImageFileType}.
	 * 
	 * @param chart the chart to save
	 * @param file the file
	 * @throws IOException if an I/O error occurred
	 */
	public static void save(JFreeChart chart, File file) throws IOException {
		save(chart, file, 800, 600);
	}
	
	/**
	 * Saves the chart to an image file.  The format must match one of the supported file types in
	 * {@link ImageFileType}.
	 * 
	 * @param chart the chart to save
	 * @param file the file
	 * @param width the image width
	 * @param height the image height
	 * @throws IOException if an I/O error occurred
	 */
	public static void save(JFreeChart chart, File file, int width, int height) throws IOException {
		save(chart, file, ImageFileType.fromFile(file), width, height);
	}

	/**
	 * Saves the chart to an image file.
	 * 
	 * @param chart the chart to save
	 * @param file the file
	 * @param fileType the image file format
	 * @param width the image width
	 * @param height the image height
	 * @throws IOException if an I/O error occurred
	 */
	public static void save(JFreeChart chart, File file, ImageFileType fileType, int width, int height) throws IOException {
		switch (fileType) {
			case PNG -> ChartUtils.saveChartAsPNG(file, chart, width, height);
			case JPEG -> ChartUtils.saveChartAsJPEG(file, chart, width, height);
			case SVG -> {
				try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
					writer.write(generateSVG(chart, width, height));
				}
			}
			default -> Validate.that("fileType", fileType).failUnsupportedOption();
		}
	}

	/**
	 * Generates a string containing a rendering of the chart in SVG format.  This is modified from JFreeChart's
	 * ChartPanel class (version 1.0.19).
	 * 
	 * @param chart the chart to save
	 * @param width the image width
	 * @param height the image height
	 * @return a string containing an SVG document for the current chart
	 */
	private static String generateSVG(JFreeChart chart, int width, int height) {
		SVGGraphics2D g2 = new SVGGraphics2D(width, height);
		
		// Suppress shadow generation, because SVG is a vector format and the shadow effect is applied via bitmap
		// effects.
		g2.setRenderingHint(new RenderingHints.Key(0) {
	        @Override
	        public boolean isCompatibleValue(Object val) {
	            return val instanceof Boolean;
	        }
	    }, true);
		
		Rectangle2D drawArea = new Rectangle2D.Double(0, 0, width, height);
		chart.draw(g2, drawArea);
		return g2.getSVGDocument();
	}

}
