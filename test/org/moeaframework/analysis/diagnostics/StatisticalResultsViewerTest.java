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
package org.moeaframework.analysis.diagnostics;

import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.Assume;

/**
 * GUI tests have limited scope and, in general, do not validate the content being displayed.
 */
public class StatisticalResultsViewerTest {
	
	@Before
	public void setUp() {
		Assume.assumeHasDisplay();
	}
	
	@Test
	public void testWithReferenceSet() {
		DiagnosticTool tool = new DiagnosticTool();
		Controller controller = tool.getController();
		
		StatisticalResultsViewer viewer = new StatisticalResultsViewer(controller, "foo");
		viewer.setVisible(true);
		viewer.dispose();
	}
	
	@Test
	public void testLocalization() {
		DiagnosticTool tool = new DiagnosticTool();
		Controller controller = tool.getController();

		Assert.assertLocalized(new StatisticalResultsViewer(controller, "foo"), Assert::isLocalized);
	}

}
