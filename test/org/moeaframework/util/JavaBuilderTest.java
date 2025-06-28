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
package org.moeaframework.util;

import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TempFiles;
import org.moeaframework.TempFiles.File;

public class JavaBuilderTest {
	
	@Test
	public void testMissingBuildPath() throws IOException {
		File tempDirectory = TempFiles.createDirectory();
		FileUtils.deleteQuietly(tempDirectory);
		
		new JavaBuilder().buildPath(tempDirectory);
		
		Assert.assertFileExists(tempDirectory);
	}
	
	@Test
	public void testMissingSourcePath() throws IOException {
		File tempDirectory = TempFiles.createDirectory();
		FileUtils.deleteQuietly(tempDirectory);
		
		new JavaBuilder().sourcePath(tempDirectory);
		
		Assert.assertFileNotExists(tempDirectory);
	}
	
}
