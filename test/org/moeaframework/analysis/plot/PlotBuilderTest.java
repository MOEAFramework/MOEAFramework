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
package org.moeaframework.analysis.plot;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.Assume;
import org.moeaframework.TempFiles;

public class PlotBuilderTest extends AbstractPlotTest {

	@Test
	public void testSavePNG() throws IOException {
		File tempFile = TempFiles.createFileWithExtension(".png");
		
		new XYPlotBuilder()
			.scatter("Points", new double[] { 0, 1, 2 }, new double[] { 0, 1, 2 })
			.save(tempFile);
		
		Assert.assertFileWithContent(tempFile);
	}
	
	@Test
	public void testSaveSVG() throws IOException {
		Assume.assumeTrue("Skipping test as JFreeSVG library is not found", ImageUtils.supportsSVG());
		
		File tempFile = TempFiles.createFileWithExtension(".svg");
			
		new XYPlotBuilder()
			.scatter("Points", new double[] { 0, 1, 2 }, new double[] { 0, 1, 2 })
			.save(tempFile);
			
		Assert.assertFileWithContent(tempFile);
	}

}
