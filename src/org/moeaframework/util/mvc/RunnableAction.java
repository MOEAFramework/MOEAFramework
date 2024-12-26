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

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JMenuItem;

import org.moeaframework.util.Localization;

/**
 * Action that invokes a {@link Runnable}.  GUI components based on this action are typically represented by a button
 * or menu item.
 */
public class RunnableAction extends LocalizedAction {
	
	private static final long serialVersionUID = 3633238192124429111L;
	
	private final Runnable runnable;
	
	/**
	 * Constructs a new runnable action.
	 * 
	 * @param id the id for localization
	 * @param localization the source for localization strings
	 * @param runnable the callback function invoked when this action is triggered
	 */
	public RunnableAction(String id, Localization localization, Runnable runnable) {
		super(id, localization);
		this.runnable = runnable;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		runnable.run();
	}
	
	/**
	 * Convenience method to create a {@link JButton} based on this action.
	 * 
	 * @return the button
	 */
	public JButton toButton() {
		return new JButton(this);
	}
	
	/**
	 * Convenience method to create a {@link JMenuItem} based on this action.
	 * 
	 * @return the menu item
	 */
	public JMenuItem toMenuItem() {
		return new JMenuItem(this);
	}
	
}