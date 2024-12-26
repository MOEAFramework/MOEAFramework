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

import org.junit.Test;
import org.moeaframework.Assert;

public class ImageFileTypeTest {
	
	@Test
	public void test() {
		Assert.assertEquals(ImageFileType.PNG, ImageFileType.fromFile(new File("image.png")));
		Assert.assertEquals(ImageFileType.JPEG, ImageFileType.fromFile(new File("image.jpg")));
		Assert.assertEquals(ImageFileType.JPEG, ImageFileType.fromFile(new File("image.jpeg")));
		Assert.assertEquals(ImageFileType.SVG, ImageFileType.fromFile(new File("image.svg")));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void tesUnsupportedExtension() {
		ImageFileType.fromFile(new File("image.foo"));
	}

}
