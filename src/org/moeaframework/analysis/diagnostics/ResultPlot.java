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

import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;

import javax.swing.JPanel;

/**
 * Abstract Swing component for displaying results.  The specific implementation
 * determines the type of plot.  The results displayed in the plot are
 * specified by {@link DiagnosticTool#getSelectedResults()}.
 */
public abstract class ResultPlot extends JPanel implements ControllerListener, 
HierarchyListener {

	private static final long serialVersionUID = -4484341164088815299L;

	/**
	 * The {@link DiagnosticTool} instance containing this plot.
	 */
	protected final DiagnosticTool frame;
	
	/**
	 * The {@link Controller} this plot uses to access result data.
	 */
	protected final Controller controller;
	
	/**
	 * The metric to display.
	 */
	protected final String metric;
	
	/**
	 * Constructs a new Swing component for displaying results.
	 * 
	 * @param frame the {@code DiagnosticTool} instance containing this plot
	 * @param metric the metric to display
	 */
	public ResultPlot(DiagnosticTool frame, String metric) {
		super();
		this.frame = frame;
		this.metric = metric;
		this.controller = frame.getController();
		
		addHierarchyListener(this);
	}
	
	/**
	 * Updates the contents of this plot.  This method is automatically invoked
	 * when the data model is changed, and will always be executed on the
	 * event dispatch thread.
	 */
	protected abstract void update();

	@Override
	public void hierarchyChanged(HierarchyEvent e) {
		if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
			if (isShowing()) {
				controller.addControllerListener(this);
				update();
			} else {
				controller.removeControllerListener(this);
			}
		}
	}

	@Override
	public void controllerStateChanged(ControllerEvent e) {
		if (e.getType().equals(ControllerEvent.Type.MODEL_CHANGED)) {
			update();
		}
	}

}
