/* Copyright 2009-2016 David Hadka
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
package org.moeaframework.examples.ga.tsplib;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Panel for displaying TSPLIB problem instances and tours.
 */
public class TSPPanel extends JPanel {

	private static final long serialVersionUID = -9001874665477567840L;

	/**
	 * The TSPLIB problem instance.
	 */
	private final TSPInstance problem;
	
	/**
	 * The displayed tours and their display settings.
	 */
	private final Map<Tour, TourDisplaySetting> tours;
	
	/**
	 * The width of nodes in the graphical display.
	 */
	private double nodeWidth;
	
	/**
	 * The border around the graphical display.  This border should be at least
	 * half the node width to ensure nodes are fully contained inside the panel.
	 */
	private Insets insets;
	
	/**
	 * {@code true} if this graphical display should automatically repaint when
	 * the displayed tours are changed; {@code false} otherwise.
	 */
	private boolean autoRepaint;
	
	/**
	 * Constructs a new panel for displaying a TSPLIB problem instance.
	 * 
	 * @param problem the TSPLIB problem instance
	 */
	public TSPPanel(TSPInstance problem) {
		super();
		this.problem = problem;
		
		if (DisplayDataType.NO_DISPLAY.equals(problem.getDisplayDataType())) {
			throw new IllegalArgumentException("problem instance does not support a graphical display");
		}
		
		tours = new LinkedHashMap<Tour, TourDisplaySetting>();
		nodeWidth = 4.0;
		insets = new Insets((int)nodeWidth, (int)nodeWidth, (int)nodeWidth, (int)nodeWidth);
		autoRepaint = true;
		
		setBackground(Color.WHITE);
		setForeground(Color.BLACK);
	}
	
	/**
	 * Set to {@code true} if this graphical display should automatically
	 * repaint when the displayed tours are changed; {@code false} otherwise.
	 * When {@code false}, the display will only change when {@link #repaint()}
	 * is invoked or the component automatically repaints the panel.  This is
	 * used to avoid unnecessary repaints when making many changes to this
	 * display.
	 * 
	 * @param autoRepaint {@code true} if this graphical display should
	 *        automatically repaint when the displayed tours are changed;
	 *        {@code false} otherwise
	 */
	public void setAutoRepaint(boolean autoRepaint) {
		this.autoRepaint = autoRepaint;
	}

	/**
	 * Adds a tour to this graphical display.  The tour will be displayed using
	 * the default color.
	 * 
	 * @param tour the tour to display
	 */
	public void displayTour(Tour tour) {
		synchronized (tours) {
			tours.put(tour, new TourDisplaySetting());
		}
		
		if (autoRepaint) {
			repaint();
		}
	}
	
	/**
	 * Adds a tour to this graphical display with the specified paint settings.
	 * 
	 * @param tour the tour to display
	 * @param paint the paint settings
	 */
	public void displayTour(Tour tour, Paint paint) {
		synchronized (tours) {
			tours.put(tour, new TourDisplaySetting(paint));
		}
		
		if (autoRepaint) {
			repaint();
		}
	}
	
	/**
	 * Adds a tour to this graphical display with the specified paint and
	 * stroke settings.
	 * 
	 * @param tour the tour to display
	 * @param paint the paint settings
	 * @param stroke the line stroke settings
	 */
	public void displayTour(Tour tour, Paint paint, Stroke stroke) {
		synchronized (tours) {
			tours.put(tour, new TourDisplaySetting(paint, stroke));
		}
		
		if (autoRepaint) {
			repaint();
		}
	}
	
	/**
	 * Removes all tours shown in this display.
	 */
	public void clearTours() {
		synchronized (tours) {
			tours.clear();
		}
		
		if (autoRepaint) {
			repaint();
		}
	}
	
	/**
	 * Removes the specified tour from this display.
	 * 
	 * @param tour the tour to remove
	 */
	public void removeTour(Tour tour) {
		synchronized (tours) {
			tours.remove(tour);
		}
		
		if (autoRepaint) {
			repaint();
		}
	}
	
	/**
	 * Sets the width of nodes in the graphical display.
	 * 
	 * @param nodeWidth the width of nodes in the graphical display
	 */
	public void setNodeWidth(double nodeWidth) {
		this.nodeWidth = nodeWidth;
		
		if (autoRepaint) {
			repaint();
		}
	}
	
	/**
	 * Sets the border around the graphical display.  This border should be at
	 * least half the node width to ensure nodes are fully contained inside the
	 * panel.
	 * 
	 * @param insets the border around the graphical display
	 */
	public void setInsets(Insets insets) {
		this.insets = insets;
		
		if (autoRepaint) {
			repaint();
		}
	}
	
	/**
	 * Converts the node coordinates into display coordinates on the screen.
	 * If this problem uses geographical weights, then the latitude/longitude
	 * coordinates are projected on the screen using the Mercator projection.
	 * 
	 * @param node the node whose display coordinates are calculated
	 * @param isGeographical {@code true} if the coordinates are geographical;
	 *        {@code false} otherwise
	 * @return the node coordinates into display coordinates on the screen
	 */
	private double[] toDisplayCoordinates(Node node, boolean isGeographical) {
		double[] position = node.getPosition();
		double x = position[1];
		double y = position[0];
		
		if (isGeographical) {
			x = GeographicalDistance.toGeographical(x);
			y = GeographicalDistance.toGeographical(y);
			x = 0.5 * Math.log((1.0 + Math.sin(x)) / (1.0 - Math.sin(x)));
		}
		
		return new double[] { x, y };
	}

	@Override
	public void paint(Graphics g) {
		synchronized(tours) {
			super.paint(g);
		}
	}

	@Override
	protected synchronized void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// get the display data
		NodeCoordinates displayData = null;

		if (DisplayDataType.COORD_DISPLAY.equals(problem.getDisplayDataType())) {
			displayData = (NodeCoordinates)problem.getDistanceTable();
		} else {
			displayData = problem.getDisplayData();
		}

		// first determine bounds of the data
		boolean isGeographical = EdgeWeightType.GEO.equals(problem.getEdgeWeightType());
		double left = Double.POSITIVE_INFINITY;
		double right = Double.NEGATIVE_INFINITY;
		double bottom = Double.POSITIVE_INFINITY;
		double top = Double.NEGATIVE_INFINITY;

		for (int i = 1; i <= displayData.size(); i++) {
			Node node = displayData.get(i);
			double[] position = toDisplayCoordinates(node, isGeographical);

			left = Math.min(left, position[0]);
			right = Math.max(right, position[0]);
			bottom = Math.min(bottom, position[1]);
			top = Math.max(top, position[1]);
		}

		// calculate the bounds of the drawing
		int displayWidth = getWidth();
		int displayHeight = getHeight();
		double scaleX = (displayWidth - insets.right - insets.left) / (right - left);
		double scaleY = (displayHeight - insets.top - insets.bottom) / (top - bottom);
		double scale = Math.min(scaleX, scaleY);
		double offsetX = (displayWidth - insets.right - insets.left - scale * (right - left)) / 2.0;
		double offsetY = (displayHeight - insets.top - insets.bottom - scale * (top - bottom)) / 2.0;

		// draw the tours
		for (Entry<Tour, TourDisplaySetting> entry : tours.entrySet()) {
			Tour tour = entry.getKey();
			TourDisplaySetting displaySettings = entry.getValue();

			g2.setPaint(displaySettings.getPaint());
			g2.setStroke(displaySettings.getStroke());

			for (int i = 0; i < tour.size(); i++) {
				Node node1 = displayData.get(tour.get(i));
				Node node2 = displayData.get(tour.get(i+1));
				double[] position1 = toDisplayCoordinates(node1, isGeographical);
				double[] position2 = toDisplayCoordinates(node2, isGeographical);

				Line2D line = new Line2D.Double(
						displayWidth - (offsetX + scale * (position1[0] - left) + insets.left), 
						displayHeight - (offsetY + scale * (position1[1] - bottom) + insets.bottom),
						displayWidth - (offsetX + scale * (position2[0] - left) + insets.left),
						displayHeight - (offsetY + scale * (position2[1] - bottom) + insets.bottom));

				g2.draw(line);
			}
		}

		// draw the nodes
		g2.setColor(getForeground());

		for (int i = 1; i <= displayData.size(); i++) {
			Node node = displayData.get(i);
			double[] position = toDisplayCoordinates(node, isGeographical);

			Ellipse2D point = new Ellipse2D.Double(
					displayWidth - (offsetX + scale * (position[0] - left) + insets.left) - (nodeWidth / 2.0),
					displayHeight - (offsetY + scale * (position[1] - bottom) + insets.bottom) - (nodeWidth / 2.0),
					nodeWidth,
					nodeWidth);

			g2.fill(point);
			g2.draw(point);
		}
	}
	
	public static void main(String[] args) throws IOException {
		TSPInstance problem = new TSPInstance(new File("./data/tsp/gr120.tsp"));
		problem.addTour(new File("./data/tsp/gr120.opt.tour"));
		
		TSPPanel panel = new TSPPanel(problem);
		
		for (int i=0; i<20; i++) {
			panel.displayTour(Tour.createRandomTour(problem.getDimension()), new Color(128, 128, 128, 64));
		}
		
		panel.displayTour(problem.getTours().get(0), Color.RED, new BasicStroke(2.0f));
		
		JFrame frame = new JFrame(problem.getName());
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(500, 400);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	/**
	 * The inner class storing tour display settings.  These settings control
	 * the paint and line stroke when rendering the tour.
	 */
	private class TourDisplaySetting {
		
		/**
		 * The paint/color used when rendering the tour.
		 */
		private final Paint paint;
		
		/**
		 * The line stroke used when rendering the tour.
		 */
		private final Stroke stroke;
		
		/**
		 * Constructs a new, default tour display setting.
		 */
		public TourDisplaySetting() {
			this(Color.RED);
		}
		
		/**
		 * Constructs a new tour display setting with the specified paint.
		 * 
		 * @param paint the paint/color used when rendering the tour
		 */
		public TourDisplaySetting(Paint paint) {
			this(paint, new BasicStroke());
		}
		
		/**
		 * Constructs a new tour display setting with the specified paint and
		 * line stroke.
		 * 
		 * @param paint the paint/color used when rendering the tour
		 * @param stroke the line stroke used when rendering the tour
		 */
		public TourDisplaySetting(Paint paint, Stroke stroke) {
			super();
			this.paint = paint;
			this.stroke = stroke;
		}

		/**
		 * Returns the paint/color used when rendering the tour.
		 * 
		 * @return the paint/color used when rendering the tour
		 */
		public Paint getPaint() {
			return paint;
		}

		/**
		 * Returns the line stroke used when rendering the tour.
		 * 
		 * @return the line stroke used when rendering the tour
		 */
		public Stroke getStroke() {
			return stroke;
		}
		
	}
	
}
