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
package org.moeaframework.util.mvc;

import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.event.EventListenerSupport;

/**
 * The controller manages the underlying data model, settings, and handles notifications.
 */
public abstract class Controller {
	
	/**
	 * The parent window.
	 */
	private final Window window;
	
	/**
	 * The collection of listeners which are notified when the controller state changes.
	 */
	private final EventListenerSupport<ControllerListener> listeners;
	
	private final Preferences preferences;
	
	private final List<Runnable> shutdownHooks;
	
	/**
	 * Constructs a new MVC controller.
	 * 
	 * @param window the parent window
	 */
	public Controller(Window window) {
		super();
		this.window = window;
		
		listeners = EventListenerSupport.create(ControllerListener.class);
		preferences = Preferences.userNodeForPackage(getClass());
		shutdownHooks = new ArrayList<>();
		
		window.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosed(WindowEvent e) {
				shutdown();
			}
			
		});
		
		loadPreferences(preferences);
	}
		
	public void loadPreferences(Preferences preferences) {
		
	}
	
	public void savePreferences(Preferences preferences) {
		
	}
	
	/**
	 * Adds the specified listener to receive all subsequent controller events.
	 * 
	 * @param listener the listener to receive controller events
	 */
	public void addControllerListener(ControllerListener listener) {
		listeners.addListener(listener);
	}
	
	/**
	 * Removes the specified listener so it no longer receives controller events.
	 * 
	 * @param listener the listener to no longer receive controller events
	 */
	public void removeControllerListener(ControllerListener listener) {
		listeners.removeListener(listener);
	}
	
	public void addShutdownHook(Runnable hook) {
		shutdownHooks.add(hook);
	}
	
	protected void shutdown() {
		for (Runnable hook : shutdownHooks) {
			hook.run();
		}
		
		savePreferences(preferences);

	}
	
	/**
	 * Fires the specified controller event.  All listeners will receive this event on the event dispatch thread.
	 * 
	 * @param event the controller event to fire
	 */
	public synchronized void fireEvent(String eventId) {
		ControllerEvent event = new ControllerEvent(this, eventId);
		SwingUtilities.invokeLater(() -> listeners.fire().controllerStateChanged(event));
	}

	/**
	 * Handles an exception, possibly displaying a dialog box containing details of the exception.
	 * 
	 * @param e the exception
	 */
	public void handleException(Exception e) {
		e.printStackTrace();
		
		String message = e.getMessage() == null ? e.toString() : e.getMessage();
		
		if (e.getCause() != null && e.getCause().getMessage() != null) {
			message += " - " + e.getCause().getMessage();
		}
		
		JOptionPane.showMessageDialog(
				window, 
				message, 
				"Error", 
				JOptionPane.ERROR_MESSAGE);
	}

}
