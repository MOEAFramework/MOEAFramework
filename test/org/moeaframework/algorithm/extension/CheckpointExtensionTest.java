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
package org.moeaframework.algorithm.extension;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TempFiles;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.mock.MockRealProblem;

public class CheckpointExtensionTest {
	
	@Test
	public void test() throws IOException {
		File file = TempFiles.createFile();
		file.delete();
		
		NSGAII original = new NSGAII(new MockRealProblem(2));
		original.addExtension(new CheckpointExtension(file, 100));
		
		Assert.assertFalse(file.exists());
		Assert.assertEquals(0, original.getNumberOfEvaluations());
		
		original.step();
		
		Assert.assertTrue(file.exists());
		Assert.assertEquals(100, original.getNumberOfEvaluations());
		
		NSGAII restored = new NSGAII(new MockRealProblem(2));
		restored.addExtension(new CheckpointExtension(file, 100));
		
		Assert.assertTrue(file.exists());
		Assert.assertEquals(100, restored.getNumberOfEvaluations());
	}

	@Test
	public void testInvalidStateFileSuppressesError() throws IOException {
		File file = TempFiles.createFile().withContent("foo");
		
		NSGAII algorithm = new NSGAII(new MockRealProblem(2));
		algorithm.addExtension(new CheckpointExtension(file, 100));
		algorithm.step();
		
		Assert.assertNotNull(algorithm);
		Assert.assertEquals(100, algorithm.getNumberOfEvaluations());
	}

}
