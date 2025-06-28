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

import java.util.EventObject;

/**
 * An event fired by a controller.  Use the event type to identify different types of changes that are occurring,
 * limiting the scope of changes to the UI.
 */
public class ControllerEvent extends EventObject {

	private static final long serialVersionUID = 3854145085028582532L;
	
	/**
	 * The type of this event.
	 */
	private final String eventType;
	
	/**
	 * Constructs a new controller event of the specified type.
	 * 
	 * @param controller the controller from which this event originates
	 * @param eventType the type of this event
	 */
	public ControllerEvent(Controller controller, String eventType) {
		super(controller);
		this.eventType = eventType;
	}

	/**
	 * Returns the type of this event.
	 * 
	 * @return the type of this event.
	 */
	public String getEventType() {
		return eventType;
	}

	@Override
	public Controller getSource() {
		return (Controller)super.getSource();
	}

}
