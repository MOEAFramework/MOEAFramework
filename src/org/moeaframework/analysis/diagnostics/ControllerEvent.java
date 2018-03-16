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

import java.util.EventObject;

/**
 * Identifies the type of event fired by the controller.
 */
public class ControllerEvent extends EventObject {

	private static final long serialVersionUID = 3854145085028582532L;
	
	/**
	 * Enumeration of controller event types.
	 */
	public static enum Type {
		
		/**
		 * Indicates the state of the controller changed.  The state changes
		 * when an evaluation job starts and stops.  The state can be
		 * determined by invoking {@link Controller#isRunning()}.
		 */
		STATE_CHANGED,
		
		/**
		 * Indicates the underlying data model has changed.  The model changes
		 * when new results are added or the results are cleared.
		 */
		MODEL_CHANGED,
		
		/**
		 * Indicates the progress of the evaluation has changed.  The progress
		 * can be queried through {@link Controller#getRunProgress()} and
		 * {@link Controller#getOverallProgress()}.
		 */
		PROGRESS_CHANGED,
		
		/**
		 * Indicates the viewing options changed.  These events are primarily
		 * caused by changing how the data is plotted in {@link DiagnosticTool}.
		 */
		VIEW_CHANGED
		
	}
	
	/**
	 * The type of this event.
	 */
	private final Type type;
	
	/**
	 * Constructs a new controller event of the specified type.
	 * 
	 * @param controller the controller from which this event originates
	 * @param type the type of this event
	 */
	public ControllerEvent(Controller controller, Type type) {
		super(controller);
		this.type = type;
	}

	/**
	 * Returns the type of this event.
	 * 
	 * @return the type of this event.
	 */
	public Type getType() {
		return type;
	}

	@Override
	public Controller getSource() {
		return (Controller)super.getSource();
	}

}
