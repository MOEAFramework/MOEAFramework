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
package org.moeaframework.util.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.List;
import org.apache.commons.io.FilenameUtils;

/**
 * Utilities for working with resources, which are files that are embedded within Java JAR files and discoverable on
 * the classpath.
 */
public class Resources {
	
	/**
	 * Options used when locating and extracting resources.
	 */
	public enum ResourceOption {
		
		/**
		 * First search for a file referenced by the resource string.  While a resource string starting with "/"
		 * indicates an absolute path relative to the classpath, this is ignored when locating a file.  The file will
		 * be resolved relative to the base directory.
		 */
		FILE,
				
		/**
		 * Extracts the resource to a temporary file instead of the path referenced by the resource.  This option is
		 * only used by {@link Resources#getResourceAsFile(Class, String, ResourceOption...)}.
		 */
		TEMPORARY,
		
		/**
		 * Set the executable flag on the file if being extracted.  This option is only used by
		 * {@link Resources#getResourceAsFile(Class, String, ResourceOption...)}.
		 */
		EXECUTABLE,
		
		/**
		 * The resource is required and a {@link ResourceNotFoundException} is thrown if the resource could not be
		 * found.  There is no need to check for {@code null} return values when using this option.
		 */
		REQUIRED
		
	}
	
	private Resources() {
		super();
	}
	
	public static String resolvePath(String resource) {
		return Path.of(resource).normalize().toString().replaceAll("\\\\", "/");
	}
	
	public static File resolveFile(String resource) {
		return new File(resource.startsWith("/") ? resource.substring(1) : resource);
	}
	
	public static InputStream asStream(Class<?> owner, String resource, ResourceOption... options) throws IOException {
		return asStream(owner, resource, options.length == 0 ? EnumSet.noneOf(ResourceOption.class) :
				EnumSet.copyOf(List.of(options)));
	}

	public static Reader asReader(Class<?> owner, String resource, ResourceOption... options) throws IOException {
		return asReader(owner, resource, options.length == 0 ? EnumSet.noneOf(ResourceOption.class) :
				EnumSet.copyOf(List.of(options)));
	}
	
	public static CommentedLineReader asLineReader(Class<?> owner, String resource, ResourceOption... options)
			throws IOException {
		Reader reader = asReader(owner, resource, options);
		return reader == null ? null : CommentedLineReader.wrap(reader);
	}
	
	public static String readString(Class<?> owner, String resource, ResourceOption... options) throws IOException {
		return readString(owner, resource, options.length == 0 ? EnumSet.noneOf(ResourceOption.class) :
				EnumSet.copyOf(List.of(options)));
	}
	
	public static File asFile(Class<?> owner, String resource, ResourceOption... options) throws IOException {
		return asFile(owner, resource, options.length == 0 ? EnumSet.noneOf(ResourceOption.class) :
				EnumSet.copyOf(List.of(options)));
	}
	
	private static InputStream asStream(Class<?> owner, String resource, EnumSet<ResourceOption> options)
			throws IOException {
		if (options.contains(ResourceOption.FILE)) {
			try {
				return new FileInputStream(resolveFile(resource));
			} catch (FileNotFoundException e) {
				// fall through
			}
		}
		
		InputStream input = owner.getResourceAsStream(resolvePath(resource));
		
		if (input != null) {
			return input;
		}
		
		if (options.contains(ResourceOption.REQUIRED)) {
			throw new ResourceNotFoundException(owner, resource);
		}
		
		return null;
	}
	
	private static Reader asReader(Class<?> owner, String resource, EnumSet<ResourceOption> options)
			throws IOException {
		InputStream input = asStream(owner, resource, options);
		return input == null ? null : new InputStreamReader(input, StandardCharsets.UTF_8);
	}
	
	private static String readString(Class<?> owner, String resource, EnumSet<ResourceOption> options)
			throws IOException {
		try (Reader reader = asReader(owner, resource, options);
				StringWriter writer = new StringWriter()) {
			if (reader == null) {
				return null;
			}
			
			reader.transferTo(writer);
			return writer.toString();
		}
	}
	
	private static File asFile(Class<?> owner, String resource, EnumSet<ResourceOption> options) throws IOException {
		if (options.contains(ResourceOption.FILE)) {
			File file = resolveFile(resource);
			
			if (file.exists()) {
				return file;
			}
		}
		
		Path path = Path.of(resource).normalize();
		File resultFile = path.toFile();
		
		if (options.contains(ResourceOption.TEMPORARY)) {
			String extension = FilenameUtils.getExtension(path.getFileName().toString());
			
			if (extension != null && extension.length() > 0 && extension.charAt(0) != '.') {
				extension = "." + extension;
			}
			
			resultFile = File.createTempFile("temp", extension);
		}

		try (InputStream input = asStream(owner, resource, ResourceOption.REQUIRED);
				OutputStream output = new FileOutputStream(resultFile)) {
			input.transferTo(output);
		}
		
		if (options.contains(ResourceOption.EXECUTABLE)) {
			resultFile.setExecutable(true);
		}
		
		return resultFile;
	}
}
