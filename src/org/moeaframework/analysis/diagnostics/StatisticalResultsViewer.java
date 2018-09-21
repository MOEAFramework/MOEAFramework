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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.moeaframework.util.Localization;

/**
 * Window for displaying statistical results.
 */
public class StatisticalResultsViewer extends JFrame {

	private static final long serialVersionUID = 25333840765750031L;

	/**
	 * The localization instance for produce locale-specific strings.
	 */
	private static Localization localization = Localization.getLocalization(
			StatisticalResultsViewer.class);
	
	/**
	 * The controller instance.
	 */
	private final Controller controller;
	
	/**
	 * The statistical results to display.
	 */
	private final String results;
	
	/**
	 * The action to save the statistical results to a file.
	 */
	private Action saveAction;
	
	/**
	 * The action to close the statistical results.
	 */
	private Action exitAction;

	/**
	 * Constructs a new window to display statistical results.
	 * 
	 * @param controller the controller instance
	 * @param results the statistical results to display
	 */
	public StatisticalResultsViewer(Controller controller, String results) {
		super(localization.getString("title.statisticalResults"));
		this.controller = controller;
		this.results = results;
		
		initialize();
		layoutComponents();

		setSize(800, 600);
		setMinimumSize(new Dimension(800, 600));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	/**
	 * Initializes the components of this window.
	 */
	private void initialize() {
		saveAction = new AbstractAction() {

			private static final long serialVersionUID = -4467620869954438555L;

			{
				putValue(Action.NAME,
						localization.getString("action.saveStatistics.name"));
				putValue(Action.SHORT_DESCRIPTION,
						localization.getString("action.saveStatistics.description"));
			}

			@Override
			public void actionPerformed(ActionEvent event) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(new FileNameExtensionFilter(
						"Text File (*.txt)", "txt"));
				
				int result = fileChooser.showSaveDialog(
						StatisticalResultsViewer.this);
				
				if (result == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					PrintWriter writer = null;
						
					if (!file.getName().toLowerCase().endsWith(".txt")) {
						file = new File(file.getParent(), file.getName() + 
								".txt");
					}
					
					try {
						writer = new PrintWriter(new FileWriter(file));
						writer.print(results);
					} catch (IOException e) {
						controller.handleException(e);
					} finally {
						if (writer != null) {
							writer.close();
						}
					}
				}
			}
			
		};
		
		exitAction = new AbstractAction() {

			private static final long serialVersionUID = -4467620869954438555L;

			{
				putValue(Action.NAME,
						localization.getString("action.exit.name"));
				putValue(Action.SHORT_DESCRIPTION,
						localization.getString("action.exit.description"));
			}

			@Override
			public void actionPerformed(ActionEvent event) {
				dispose();
			}
			
		};
	}
	
	/**
	 * Layout the components on this window.
	 */
	private void layoutComponents() {
		//layout the menus
		JMenu file = new JMenu(localization.getString("menu.file"));
		file.add(new JMenuItem(saveAction));
		file.addSeparator();
		file.add(new JMenuItem(exitAction));
		
		JMenuBar menu = new JMenuBar();
		menu.add(file);
		
		setJMenuBar(menu);
		
		//layout the body
		JTextArea textArea = new JTextArea(results);
		textArea.setWrapStyleWord(true);
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER);
	}

}
