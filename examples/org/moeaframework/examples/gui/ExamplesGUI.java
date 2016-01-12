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
package org.moeaframework.examples.gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.moeaframework.core.Settings;

/**
 * GUI for displaying and running the examples.
 */
public class ExamplesGUI extends JFrame {
	
	private static final long serialVersionUID = -3101518902404130963L;

	/**
	 * The examples to display.
	 */
	private final Vector<Example> examples;
	
	/**
	 * The list displaying all available examples.
	 */
	private JList list;
	
	/**
	 * The button for running the currently-selected example.
	 */
	private JButton runButton;
	
	/**
	 * The button for closing the window.
	 */
	private JButton exitButton;
	
	/**
	 * The tabbed pane for containing the description and resources for the
	 * currently-selected example.
	 */
	private JTabbedPane tabbedPane;
	
	/**
	 * Constructs and displays a new example GUI.
	 * 
	 * @param examples the examples to display
	 */
	public ExamplesGUI(Vector<Example> examples) {
		super("MOEA Framework Demos");
		this.examples = examples;
		
		layoutComponents();
		setupActions();
		
		setVisible(true);
		list.setSelectedIndex(0);
	}
	
	/**
	 * Returns the contents of the specified resource.  This resource can
	 * reference a file on the local system or a resource contained on the
	 * classpath.
	 * 
	 * @param resource the resource to load
	 * @return the contents of the resource
	 * @throws IOException if an I/O error occurred
	 */
	private String load(String resource) throws IOException {
		File file = new File(resource);
		
		if (file.exists()) {
			return load(file);
		} else {
			InputStream input = getClass().getResourceAsStream("/" + resource);
			
			if (input == null) {
				throw new FileNotFoundException(resource);
			} else {
				try {
					return load(new BufferedReader(new InputStreamReader(
							input)));
				} finally {
					input.close();
				}
			}
		}
	}
	
	/**
	 * Return the contents of the specified reader.  This method does not close
	 * the reader.
	 * 
	 * @param reader the reader to read
	 * @return the contents of the reader
	 * @throws IOException if an I/O error occurred
	 */
	private String load(BufferedReader reader) throws IOException {
		String line = null;
		StringBuilder builder = new StringBuilder();
		
		while ((line = reader.readLine()) != null) {
			builder.append(line);
			builder.append(Settings.NEW_LINE);
		}
		
		return builder.toString();
	}
	
	/**
	 * Returns the contents of the specified file.
	 * 
	 * @param file the file to read
	 * @return the contents of the file
	 * @throws IOException if an I/O error occurred
	 */
	private String load(File file) throws IOException {
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(file));
			return load(reader);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
	
	/**
	 * Layout the components on the GUI.
	 */
	public void layoutComponents() {
		list = new JList(examples);
		runButton = new JButton("Run Example");
		exitButton = new JButton("Exit");
		tabbedPane = new JTabbedPane();
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonPane.add(runButton);
		buttonPane.add(exitButton);
		
		JPanel examplePane = new JPanel();
		examplePane.setLayout(new BorderLayout());
		examplePane.add(tabbedPane, BorderLayout.CENTER);
		examplePane.add(buttonPane, BorderLayout.SOUTH);
		
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(new JScrollPane(list), BorderLayout.WEST);
		contentPane.add(examplePane, BorderLayout.CENTER);
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(contentPane, BorderLayout.CENTER);
		setSize(840, 600);
		setIconImages(Settings.getIconImages());
	}
	
	/**
	 * Register any event listeners.
	 */
	private void setupActions() {
		list.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					Example example = (Example)list.getSelectedValue();
					display(example);
				}
			}
			
		});
		
		runButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Example example = (Example)list.getSelectedValue();
				example.run();
			}
			
		});
		
		exitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
			
		});
	}
	
	/**
	 * Update the display to show the information about the specified example.
	 * 
	 * @param example the currently-selected example
	 */
	private void display(Example example) {
		if (example == null) {
			tabbedPane.removeAll();
			runButton.setEnabled(false);
		} else {
			tabbedPane.removeAll();
			
			JEditorPane description = new JEditorPane();
			description.setContentType("text/html");
			description.setText(example.getDescription());
			description.setEditable(false);
			
			description.addHyperlinkListener(new HyperlinkListener() {

				@Override
				public void hyperlinkUpdate(HyperlinkEvent hle) {
					if (HyperlinkEvent.EventType.ACTIVATED.equals(
							hle.getEventType())) {
                        Desktop desktop = Desktop.getDesktop();
                        
                        try {
                            desktop.browse(hle.getURL().toURI());
                        } catch (Exception ex) {
                            // unable to launch browser
                        }
                    }
				}
				
			});
			
			tabbedPane.addTab("Description", description);
			
			for (String resource : example.getResources()) {
				try {
					RSyntaxTextArea textArea = new RSyntaxTextArea(20, 60);
					
					if (resource.endsWith(".java")) {
						textArea.setSyntaxEditingStyle(
								SyntaxConstants.SYNTAX_STYLE_JAVA);
					} else {
						textArea.setSyntaxEditingStyle(
								SyntaxConstants.SYNTAX_STYLE_NONE);
					}
					
					textArea.setCodeFoldingEnabled(true);
					textArea.setText(load(resource));
					textArea.setSelectionStart(0);
					textArea.setSelectionEnd(0);
					textArea.setEditable(false);
					RTextScrollPane sp = new RTextScrollPane(textArea);
					
					tabbedPane.addTab(new File(resource).getName(), sp);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			
			if (example.getMainClass() == null) {
				runButton.setEnabled(false);
			} else {
				runButton.setEnabled(true);
			}
		}
	}

}
