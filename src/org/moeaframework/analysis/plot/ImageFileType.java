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

import java.io.File;

import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.FilenameUtils;

/**
 * List of supported image file types.
 */
public enum ImageFileType {
	
	/**
	 * Portable Network Graphic (PNG) format, a raster graphics file with lossless compression.
	 */
	PNG(new FileNameExtensionFilter("PNG Image (*.png)", "png")),
	
	/**
	 * Joint Photographic Experts Group (JPEG) format, which often produces smaller file sizes but is lossy.
	 */
	JPEG(new FileNameExtensionFilter("JPEG Image (*.jpg, *.jpeg)", "jpg", "jpeg")),
	
	/**
	 * Scalable Vector Graphics (SVG) format, which stores images in a vector-based format instead of raster
	 * (pixel-based) format, allowing the image to scale to any dimension without artifacts.
	 */
	SVG(new FileNameExtensionFilter("SVG Image (*.svg)", "svg"));
	
	private FileNameExtensionFilter filter;
	
	private ImageFileType(FileNameExtensionFilter filter) {
		this.filter = filter;
	}
	
	/**
	 * Returns the file filter for this image file type.
	 * 
	 * @return the file filter
	 */
	public FileNameExtensionFilter getFilter() {
		return filter;
	}
	
	/**
	 * Determine the image file type from a file using its extension.
	 * 
	 * @param file the file
	 * @return the file type
	 * @throws IllegalArgumentException if the file type is not supported
	 */
	public static ImageFileType fromFile(File file) {
		for (ImageFileType fileType : ImageFileType.values()) {
			if (fileType.getFilter().accept(file)) {
				return fileType;
			}
		}
		
		throw new IllegalArgumentException("'" + FilenameUtils.getExtension(file.getName()) +
				"' is not a supported image type");
	}
}