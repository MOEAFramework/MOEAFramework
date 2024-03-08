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
package org.moeaframework.analysis.sensitivity;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * Writes output files.  As they allow the {@link Evaluator} to automatically resume itself at the last known good
 * result, output writers are expected to cleanup the file and return the number of valid entries through
 * {@link #getNumberOfEntries()}.
 */
public interface OutputWriter extends Closeable {
	
	/**
	 * Returns the number of entries in the file. If the file already existed, this returns the number of complete
	 * entries in the output file. This value is incremented on every invocation to the {@link #append} method.
	 * 
	 * @return the number of entries in the file
	 */
	public int getNumberOfEntries();
	
	/**
	 * Appends the specified non-dominated population and optional attributes to the file.
	 * 
	 * @param entry the non-dominated population and optional attributes
	 * @throws IOException if an I/O error occurred
	 */
	public void append(ResultEntry entry) throws IOException;
	
	/**
	 * Common settings when creating an output writer.
	 */
	public static class OutputWriterSettings {
		
		/**
		 * {@code true} when in append mode (the default), {@code false} otherwise.
		 */
		protected final boolean append;
		
		/**
		 * The strategy used when recovering from unclean files.
		 */
		protected final CleanupStrategy cleanupStrategy;
		
		/**
		 * Constructs the default settings object.
		 */
		public OutputWriterSettings() {
			this(Optional.empty(), Optional.empty());
		}
		
		/**
		 * Constructs a new settings object.
		 * 
		 * @param append {@code true} to enable append mode, {@code false} otherwise
		 * @param cleanupStrategy the cleanup strategy
		 */
		public OutputWriterSettings(Optional<Boolean> append, Optional<CleanupStrategy> cleanupStrategy) {
			super();
			this.append = append != null && append.isPresent() ? append.get() : true;
			this.cleanupStrategy = cleanupStrategy != null && cleanupStrategy.isPresent() ? cleanupStrategy.get() :
				org.moeaframework.core.Settings.getCleanupStrategy();
		}
		
		/**
		 * Returns {@code true} when in append mode (the default), {@code false} otherwise.
		 * 
		 * @return {@code true} when in append mode (the default), {@code false} otherwise
		 */
		public boolean isAppend() {
			return append;
		}

		/**
		 * Returns the strategy used when recovering from unclean files.
		 * 
		 * @return the strategy used when recovering from unclean files
		 */
		public CleanupStrategy getCleanupStrategy() {
			return cleanupStrategy;
		}
		
		/**
		 * Returns the unclean file to use when attempting to recover previously recorded data.
		 * 
		 * @param originalFile the original file
		 * @return the unclean file
		 */
		public File getUncleanFile(File originalFile) {
			return new File(originalFile.getParent(), "." + originalFile.getName() + ".unclean");
		}
		
	}
	
	/**
	 * Defines how to recover "unclean" output files.
	 * <p>
	 * By default, output writers append to any existing files.  However, before doing so, it processes any existing
	 * output file to determine the last valid record and remove any incomplete entries.  If this recovery process
	 * is interrupted, we leak these "unclean" files and need a way to handle them.
	 */
	public static enum CleanupStrategy {
		
		/**
		 * Fail with an error, user will be required to delete the temporary file before continuing.
		 */
		ERROR,
		
		/**
		 * Attempt to restore from the temporary file.
		 */
		RESTORE,
		
		/**
		 * Do not restore from the temporary file.
		 */
		OVERWRITE

	}

}
