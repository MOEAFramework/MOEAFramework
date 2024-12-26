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

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.moeaframework.util.Localization;

/**
 * An action whose display settings are derived from localization.
 * <ul>
 *   <li>{@code action.<id>.name} - The display name (required)
 *   <li>{@code action.<id>.description} - The description, typically shown in a tooltip (required)
 *   <li>{@code action.<id>.icon} - The icon resource name (optional)
 * </ul>
 */
public abstract class LocalizedAction extends AbstractAction {

	private static final long serialVersionUID = 4030882078395416151L;

	/**
	 * Constructs a new localized action.
	 * 
	 * @param id the id for localization
	 * @param localization the source of localization strings
	 * @param args any arguments used for localization
	 */
	public LocalizedAction(String id, Localization localization, Object... args) {
		super();
		
		putValue(Action.NAME, localization.getString("action." + id + ".name", args));
		putValue(Action.SHORT_DESCRIPTION, localization.getString("action." + id + ".description", args));
		putValue(Action.SMALL_ICON, localization.getIcon("action." + id + ".icon"));
	}

}