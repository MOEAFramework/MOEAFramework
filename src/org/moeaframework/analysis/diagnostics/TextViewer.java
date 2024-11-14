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
package org.moeaframework.analysis.diagnostics;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.FilenameUtils;
import org.moeaframework.core.Settings;
import org.moeaframework.util.Localization;
import org.moeaframework.util.mvc.RunnableAction;

/**
 * Window for displaying text.
 */
public class TextViewer extends JFrame {

	private static final long serialVersionUID = 25333840765750031L;

	/**
	 * The localization instance for produce locale-specific strings.
	 */
	private static Localization localization = Localization.getLocalization(TextViewer.class);
	
	/**
	 * The controller instance.
	 */
	private final DiagnosticToolController controller;
	
	/**
	 * The text to display.
	 */
	private final String text;

	/**
	 * Constructs a new window to display text.
	 * 
	 * @param controller the controller instance
	 * @param text the text to display
	 */
	public TextViewer(DiagnosticToolController controller, String text) {
		super(localization.getString("title.textViewer"));
		this.controller = controller;
		this.text = text;
		
		initialize();
		layoutMenu();
		layoutComponents();

		setSize(800, 600);
		setMinimumSize(new Dimension(800, 600));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImages(Settings.getIcon().getResolutionVariants());
	}
	
	private void initialize() {
		
	}
	
	private void save() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter("Text File (*.txt)", "txt"));
		
		int result = fileChooser.showSaveDialog(TextViewer.this);
		
		if (result == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
				
			if (!FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("txt")) {
				file = new File(file.getParent(), file.getName() + ".txt");
			}
			
			try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
				writer.print(text);
			} catch (IOException e) {
				controller.handleException(e);
			}
		}
	}
	
	private void layoutMenu() {
		JMenu file = new JMenu(localization.getString("menu.file"));
		file.add(new RunnableAction("save", localization, this::save).toMenuItem());
		file.addSeparator();
		file.add(new RunnableAction("exit", localization, this::dispose).toMenuItem());
		
		JMenuBar menu = new JMenuBar();
		menu.add(file);
		
		setJMenuBar(menu);
	}
	
	private void layoutComponents() {
		JTextArea textArea = new JTextArea(text);
		textArea.setLineWrap(false);
		textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER);
	}

}
