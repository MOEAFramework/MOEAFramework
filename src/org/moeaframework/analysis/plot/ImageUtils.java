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

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.util.validate.Validate;

/**
 * Utility for creating images from charts.
 */
public class ImageUtils {

	private ImageUtils() {
		super();
	}

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
				try (PrintWriter writer = new PrintWriter(file)) {
					writer.write(generateSVG(chart, width, height));
				}
			}
			default -> Validate.that("fileType", fileType).failUnsupportedOption();
		}
	}
	
	/**
	 * Returns {@code true} if saving to the SVG format is supported.  This requires the JFreeSVG library to be
	 * setup on the classpath.
	 * 
	 * @return {@code true} if saving to the SVG format is supported; {@code false} otherwise
	 */
	public static boolean supportsSVG() {
		return createSVGGraphics2D(100, 100) != null;
	}
	
	/**
	 * Returns a list of supported image formats.
	 * 
	 * @return a list of supported image formats
	 */
	public static List<ImageFileType> getSupportedImageFormats() {
		List<ImageFileType> formats = new ArrayList<>();
		
		for (ImageFileType format : ImageFileType.values()) {
			if (format.equals(ImageFileType.SVG) && !supportsSVG()) {
				continue;
			}
			
			formats.add(format);
		}
		
		return formats;
	}

	/**
	 * Generates a string containing a rendering of the chart in SVG format.
	 * <p>
	 * This is derived from JFreeChart's ChartPanel class.  JFreeSVG is not available by default since it is licensed
	 * under the GPL, and must be manually installed by the end user.
	 * 
	 * @param chart the chart to save
	 * @param width the image width
	 * @param height the image height
	 * @return a string containing an SVG document for the current chart
	 */
	private static String generateSVG(JFreeChart chart, int width, int height) {
		Graphics2D g2 = createSVGGraphics2D(width, height);
		
		if (g2 == null) {
			throw new FrameworkException("JFreeSVG library is not present, please add to classpath");
		}
		
		// Disable shadow generation since bitmap effects are not supported in SVG
		g2.setRenderingHint(JFreeChart.KEY_SUPPRESS_SHADOW_GENERATION, true);

		Rectangle2D drawArea = new Rectangle2D.Double(0, 0, width, height);
		chart.draw(g2, drawArea);
		
		try {
			Method method = g2.getClass().getMethod("getSVGElement");
			
			StringBuilder sb = new StringBuilder();
			sb.append("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n");
			sb.append(method.invoke(g2));
			sb.append("\n");
			
			return sb.toString();
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException |
				InvocationTargetException e) {
			throw new FrameworkException("Failed to generate SVG", e);
		}
	}
	
	/**
	 * Uses reflection to create an instance of the SVG graphics object.
	 * 
	 * @return the graphics object, or {@code null} if not available
	 */
	private static Graphics2D createSVGGraphics2D(int width, int height) {
		try {
			Class<?> svgGraphics2d = Class.forName("org.jfree.graphics2d.svg.SVGGraphics2D");
			Constructor<?> ctor = svgGraphics2d.getConstructor(int.class, int.class);
			return (Graphics2D)ctor.newInstance(width, height);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException |
				IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			return null;
		}
	}

}
