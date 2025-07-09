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

import java.awt.Window;

import javax.swing.SwingUtilities;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.moeaframework.Assume;

@Ignore("Abstract test class")
public abstract class AbstractPlotBuilderTest {
	
	@Before
	public void setUp() {
		Assume.assumeHasDisplay();
	}
	
	@After
	public void tearDown() {
		if (isJUnitTest()) {
			disposeAll();
		}
	}
	
	public static boolean isJUnitTest() {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

		for (StackTraceElement element : stackTrace) {
			if (element.getClassName().startsWith("org.junit.")) {
				return true;
			}
		}

		return false;
	}
	
	public static void disposeAll() {
		SwingUtilities.invokeLater(() -> {
			for (Window window : Window.getWindows()) {
				if (window.isShowing()) {
					window.dispose();
				}
			}
		});
	}

}
