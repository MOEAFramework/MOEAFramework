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
package org.moeaframework.util.mvc;

import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.Assume;
import org.moeaframework.Counter;
import org.moeaframework.Wait;

public class UITest {
	
	@Before
	public void setUp() {
		Assume.assumeHasDisplay();
	}
	
	@Test
	public void testShow() {
		JFrame frame = new JFrame();
		frame.setMinimumSize(new Dimension(200, 100));
		
		JFrame window = UI.showAndWait(() -> frame);
		
		Assert.assertSame(window, frame);
		Assert.assertTrue(window.isVisible());
		Assert.assertGreaterThan(window.getX(), 0);
		Assert.assertGreaterThan(window.getY(), 0);
		Assert.assertGreaterThanOrEqual(window.getWidth(), 200);
		Assert.assertGreaterThanOrEqual(window.getHeight(), 100);
		Assert.assertNotNull(window.getIconImage());
		Assert.assertNotNull(window.getIconImages());
	}
		
	@Test
	public void testClearEventQueue() {
		int N = 5;
		Counter<String> counter = new Counter<>();
		
		for (int i = 0; i < N; i++) {
			SwingUtilities.invokeLater(() -> {
				Wait.sleepFor(Duration.ofMillis(100));
				counter.incrementAndGet("call");
			});
		}
		
		Assert.assertEquals(0, counter.get("call"));
		UI.clearEventQueue();
		Assert.assertEquals(N, counter.get("call"));
	}
	
	@Test
	public void testClearEventQueueOnEventDispatchThread() throws InvocationTargetException, InterruptedException {
		SwingUtilities.invokeAndWait(() -> UI.clearEventQueue());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testHandlingExceptionFromSupplier() {
		UI.showAndWait(() -> {
			throw new IllegalArgumentException("runtime exception");
		});
	}
	
	@Test(expected = SecurityException.class)
	public void testHandlingExceptionFromWindow() {
		UI.showAndWait(() -> new JFrame() {

			private static final long serialVersionUID = -6791559691041671874L;

			@Override
			public void setVisible(boolean b) {
				throw new SecurityException();
			}
			
		});
	}
	
}
