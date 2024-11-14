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
package org.moeaframework.analysis.viewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.FilenameUtils;
import org.moeaframework.core.Settings;
import org.moeaframework.util.Localization;
import org.moeaframework.util.mvc.RunnableAction;

/**
 * Window for displaying text.
 */
public class TextViewer extends JDialog {

	private static final long serialVersionUID = 25333840765750031L;

	/**
	 * The localization instance for producing locale-specific strings.
	 */
	private static Localization localization = Localization.getLocalization(TextViewer.class);

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
	public TextViewer(Frame owner, String text) {
		super(owner, localization.getString("title.textViewer"));
		this.text = text;
		
		layoutComponents();

		setSize(800, 600);
		setMinimumSize(new Dimension(800, 600));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImages(Settings.getIcon().getResolutionVariants());
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
				e.printStackTrace();
			}
		}
	}
	
	private void layoutComponents() {		
		JTextArea textArea = new JTextArea(text);
		textArea.setLineWrap(false);
		textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		textArea.setEditable(false);
		
		RunnableAction saveAction = new RunnableAction("save", localization, this::save);
		
		RunnableAction decreaseFontSize = new RunnableAction("decreaseFontSize", localization, () -> {
			float newSize = Math.max(2.0f, textArea.getFont().getSize2D() - 2.0f);
			textArea.setFont(textArea.getFont().deriveFont(newSize));
		});
		
		RunnableAction increaseFontSize = new RunnableAction("increaseFontSize", localization, () -> {
			float newSize = Math.min(64.0f, textArea.getFont().getSize2D() + 2.0f);
			textArea.setFont(textArea.getFont().deriveFont(newSize));
		});

		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		toolbar.add(saveAction);
		toolbar.addSeparator();
		toolbar.add(decreaseFontSize);
		toolbar.add(increaseFontSize);
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(toolbar, BorderLayout.NORTH);
		getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER);
		
		pack();
	}

}
