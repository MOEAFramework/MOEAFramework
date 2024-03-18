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
package org.moeaframework.examples.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.moeaframework.core.Settings;

/**
 * A GUI that mimics a terminal window for displaying the output from a
 * {@link TerminalExample}.
 */
public class TerminalGUI extends JFrame {
	
	private static final long serialVersionUID = 2927412401050615141L;

	/**
	 * The example to run.
	 */
	private final TerminalExample example;
	
	/**
	 * The text area containing the standard output from the example.
	 */
	private JTextArea output;
	
	/**
	 * The status message indicating if the example is running or finished.
	 */
	private JLabel status;
	
	/**
	 * The button for closing this window.
	 */
	private JButton closeButton;
	
	/**
	 * Constructs and displays a new terminal GUI.  The example starts running
	 * immediately after the GUI is displayed.
	 * 
	 * @param example the example to run
	 */
	public TerminalGUI(TerminalExample example) {
		super(example.getName() + " Output");
		this.example = example;
		
		layoutComponents();
		setupActions();
		
		setVisible(true);
		runExample();
	}
	
	/**
	 * Layout the components on the GUI.
	 */
	private void layoutComponents() {
		output = new JTextArea();
		output.setEditable(false);
		output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		
		status = new JLabel("Status: Setting up...");
		closeButton = new JButton("Close");
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		buttonPane.add(status);
		buttonPane.add(Box.createHorizontalGlue());
		buttonPane.add(closeButton);
		
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(new JScrollPane(output), BorderLayout.CENTER);
		contentPane.add(buttonPane, BorderLayout.SOUTH);
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(contentPane, BorderLayout.CENTER);
		setSize(600, 400);
		setLocationRelativeTo(null);
		setIconImages(Settings.getIcon().getResolutionVariants());
	}
	
	/**
	 * Register any event listeners.
	 */
	private void setupActions() {
		closeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
			
		});
	}
	
	/**
	 * Runs the example and pipes its output and error streams to the text
	 * area.
	 */
	private void runExample() {
		try {
			String[] command = new String[] {
					"java",
					"-classpath", 
					System.getProperty("java.class.path"),
					example.getMainClass().getName()};
			
			// echo command to output, quoting the classpath
			output.append("> java -classpath \"");
			output.append(System.getProperty("java.class.path"));
			output.append("\" ");
			output.append(example.getMainClass().getName());
			output.append(System.lineSeparator());
			
			status.setText("Status: Running...");
			
			Process process = Runtime.getRuntime().exec(command);
			
			new RedirectThread(process.getErrorStream()).start();
			new RedirectThread(process.getInputStream()).start();
		} catch (Exception e) {
			output.append("Error: " + e.getMessage());
			status.setText("Status: Error");
		}
	}
	
	/**
	 * Thread that pipes the output stream to the text area and updates the status
	 * when complete or on error.
	 */
	private class RedirectThread extends Thread {
		
		private final InputStream inputStream;
		
		public RedirectThread(InputStream inputStream) {
			super();
			this.inputStream = inputStream;
		}
		
		@Override
		public void run() {
			try (InputStreamReader reader = new InputStreamReader(inputStream)) {
				char[] buffer = new char[Settings.BUFFER_SIZE];
				int len;

				while ((len = reader.read(buffer, 0, buffer.length)) != -1) {
					final String content = String.valueOf(buffer, 0, len);
					SwingUtilities.invokeLater(() -> output.append(content));
				}
				
				SwingUtilities.invokeLater(() -> status.setText("Status: Finished"));
			} catch (IOException e) {
				SwingUtilities.invokeLater(() -> {
					output.append("Error: " + e.getMessage());
					status.setText("Status: Error");
				});
			}
		}
		
	}

}
