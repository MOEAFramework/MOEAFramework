/* Copyright 2009-2018 David Hadka
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

import java.util.EventListener;

/**
 * Listens for events fired by the controller.  See {@link ControllerEvent} for
 * details on the type of events which can be fired.
 */
public interface ControllerListener extends EventListener {
	
	/**
	 * Invoked by the controller to indicate its state changed.  The
	 * {@code ControllerEvent} indicates the type of event which has occurred.
	 * 
	 * @param event details of the controller event
	 */
	public void controllerStateChanged(ControllerEvent event);

}
