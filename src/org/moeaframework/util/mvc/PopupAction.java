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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.function.Supplier;

import javax.swing.JButton;
import javax.swing.JPopupMenu;

import org.moeaframework.util.Localization;

public class PopupAction extends LocalizedAction {
		
	private static final long serialVersionUID = 4516883983249801132L;
	
	private final Supplier<JPopupMenu> menuSupplier;
	
	public PopupAction(String id, Localization localization, Supplier<JPopupMenu> menuSupplier) {
		this(id, localization, new Object[0], menuSupplier);
	}
	
	public PopupAction(String id, Localization localization, Object[] args, Supplier<JPopupMenu> menuSupplier) {
		super(id, localization, args);
		this.menuSupplier = menuSupplier;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JPopupMenu menu = menuSupplier.get();
		
		if (e.getSource() instanceof Component component) {
			menu.show(component, 0, component.getHeight());
		}
	}
	
	public JButton toButton() {
		return new JButton(this);
	}

}