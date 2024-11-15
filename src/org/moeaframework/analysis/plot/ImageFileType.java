package org.moeaframework.analysis.plot;

import java.io.File;

import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.FilenameUtils;

/**
 * List of supported file types when saving a plot.
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
	
	public FileNameExtensionFilter getFilter() {
		return filter;
	}
	
	/**
	 * Determine the file type from its string representation using case-insensitive matching.
	 * 
	 * @param value the string representation of the file type
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