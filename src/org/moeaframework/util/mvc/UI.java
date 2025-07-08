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

import java.awt.Taskbar;
import java.awt.Window;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.commons.lang3.SystemUtils;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Settings;

/**
 * Utilities for creating UIs in a consistent manner.  For best results, the constructor of the window should be called
 * within {@link #show(Supplier)} or {@link #showAndWait(Supplier)}.  Within the constructor:
 * <ol>
 *   <li>Set the window title.
 *   <li>Layout all components.
 *   <li>Set {@link Window#setPreferredSize(java.awt.Dimension)}
 * </ol>
 * 
 * <h2>OS-Specific Details</h2>
 * <ul>
 *   <li>Mac (Darwin) - Call {@code java} with {@code -Xdock:name=...} to set the application title.  Otherwise, the
 *       class name will appear in the title bar.
 * </ul>
 */
public class UI {
	
	static {
		configureLookAndFeel();
	}
	
	private UI() {
		super();
	}
	
	/**
	 * Configures the system look and feel.
	 * <p>
	 * <strong>Must be called before displaying any UI components!</strong>  Note that it is not necessary to call this
	 * method explicitly so long as you are displaying windows using the methods in this class, since this method is
	 * called automatically when this class is first loaded.
	 */
	public static void configureLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			if (Settings.isVerbose()) {
				System.err.println("ERROR: Unable to set system look and feel: " + e);
			}
		}
		
		if (SystemUtils.IS_OS_MAC) {
			try {
				Taskbar taskbar = Taskbar.getTaskbar();
				taskbar.setIconImage(Settings.getIcon().getResolutionVariant(256, 256));
			} catch (Exception e) {
				if (Settings.isVerbose()) {
					System.err.println("ERROR: Unable to set taskbar icon: " + e);
				}
			}
		}
	}
	
	/**
	 * Creates a window asynchronously, scheduling the window to be created and displayed on the event dispatch thread.
	 * The returned future can be used to await this task, blocking until the window is displayed.  If invoked from the
	 * event dispatch thread, the window is created immediately.  Any exceptions will be thrown by the future wrapped
	 * in an {@link ExecutionException}.
	 * 
	 * @param <T> the type of window
	 * @param supplier a function that creates the window
	 * @return the future used to access the displayed window
	 */
	public static <T extends Window> CompletableFuture<T> show(Supplier<T> supplier) {		
		if (SwingUtilities.isEventDispatchThread()) {
			try {
				T window = supplier.get();
				
				window.setIconImages(Settings.getIcon().getResolutionVariants());
				window.pack();
	
				if (window instanceof JFrame frame) {
					frame.setLocationRelativeTo(null);
					frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				} else if (window instanceof JDialog dialog) {
					dialog.setLocationRelativeTo(dialog.getOwner());
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				}
	
				window.setVisible(true);
				return CompletableFuture.completedFuture(window);
			} catch (Exception e) {
				return CompletableFuture.failedFuture(e);
			}
		} else {
			CompletableFuture<T> future = new CompletableFuture<>();

			SwingUtilities.invokeLater(() -> {
				try {
					future.complete(show(supplier).get());
				} catch (ExecutionException e) {
					future.completeExceptionally(e.getCause());
				} catch (Exception e) {
					future.completeExceptionally(e);
				}
			});
			
			return future;
		}
	}
	
	/**
	 * Creates a window synchronously.
	 * 
	 * @param <T> the type of window
	 * @param supplier a function that creates the window
	 * @return the window after it is displayed
	 */
	public static <T extends Window> T showAndWait(Supplier<T> supplier) {
		try {
			return show(supplier).get();
		} catch (Exception e) {
			Throwable cause = e;
			
			if (cause instanceof ExecutionException ex) {
				cause = ex.getCause();
			}
			
			if (cause instanceof RuntimeException re) {
				throw re;
			}
			
			throw new FrameworkException("Failed to create or show window", cause);
		}
	}
	
	/**
	 * Blocks until all events scheduled in the event queue are processed.  This method no-ops if run from the event
	 * dispatch thread.
	 */
	public static void clearEventQueue() {
		if (SwingUtilities.isEventDispatchThread()) {
			System.err.println("WARNING: Unable to clear event queue, already running on event dispatch thread");
		} else {
			try {
				SwingUtilities.invokeAndWait(() -> {});
			} catch (InvocationTargetException | InterruptedException e) {
				// ignore
			}
		}
	}

}
