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
package org.moeaframework.analysis.viewer;

import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.Assume;

/**
 * GUI tests have limited scope and, in general, do not validate the content being displayed.
 */
public class TextViewerTest {
	
	@Before
	public void setUp() {
		Assume.assumeHasDisplay();
	}
	
	@Test
	public void test() {
		TextViewer viewer = new TextViewer();
		viewer.setText("foo");
		viewer.setVisible(true);
		viewer.dispose();
	}
	
	@Test
	public void testLocalization() {
		Assert.assertLocalized(new TextViewer(), Assert::isLocalized);
	}

}
