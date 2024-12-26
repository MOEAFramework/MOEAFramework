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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.function.Supplier;

import javax.swing.JButton;
import javax.swing.JPopupMenu;

import org.moeaframework.util.Localization;

/**
 * Action that displays a popup menu when triggered.  GUI components based on this action are typically buttons, with
 * the menu displayed adjacent to the button.
 */
public class PopupAction extends LocalizedAction {
		
	private static final long serialVersionUID = 4516883983249801132L;
	
	private final Supplier<JPopupMenu> menuSupplier;
	
	/**
	 * Constructs a new popup action.
	 * 
	 * @param id the id for localization
	 * @param localization the source for localization strings
	 * @param menuSupplier callback that generates the menu
	 */
	public PopupAction(String id, Localization localization, Supplier<JPopupMenu> menuSupplier) {
		super(id, localization);
		this.menuSupplier = menuSupplier;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JPopupMenu menu = menuSupplier.get();
		
		if (e.getSource() instanceof Component component) {
			menu.show(component, 0, component.getHeight());
		}
	}
	
	/**
	 * Convenience method to create a {@link JButton} based on this action.
	 * 
	 * @return the menu item
	 */
	public JButton toButton() {
		return new JButton(this);
	}

}