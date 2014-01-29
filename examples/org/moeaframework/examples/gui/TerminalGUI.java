/* Copyright 2009-2014 David Hadka
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
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.commons.lang3.StringUtils;
import org.moeaframework.core.Settings;

public class TerminalGUI extends JFrame {
	
	private static final long serialVersionUID = 2927412401050615141L;

	private final TerminalExample example;
	
	private JTextArea output;
	
	private JLabel status;
	
	private JButton closeButton;
	
	public TerminalGUI(TerminalExample example) {
		super(example.getName() + " Output");
		this.example = example;
		
		layoutComponents();
		setupActions();
		
		setVisible(true);
		runExample();
	}
	
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
	}
	
	private void setupActions() {
		closeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
			
		});
	}
	
	private void runExample() {
		try {
			String[] command = new String[] {
					"java",
					"-classpath", 
					System.getProperty("java.class.path"),
					example.getMainClass().getName()};
			
			output.append("> ");
			output.append(StringUtils.join(command, " "));
			output.append(Settings.NEW_LINE);
			
			status.setText("Status: Running...");
			
			Process process = Runtime.getRuntime().exec(command);
			InputStream outputStream = process.getInputStream();
			InputStream errorStream = process.getErrorStream();
			
			new ErrorRedirectThread(errorStream).start();
			new OutputRedirectThread(outputStream).start();
		} catch (Exception e) {
			output.append("Error: " + e.getMessage());
			status.setText("Status: Error");
		}
	}
	
	private class ErrorRedirectThread extends Thread {
		
		private final InputStream errorStream;
		
		public ErrorRedirectThread(InputStream errorStream) {
			super();
			this.errorStream = errorStream;
		}
		
		@Override
		public void run() {
			try {
				byte[] buffer = new byte[Settings.BUFFER_SIZE];
				int len;

				while ((len = errorStream.read(buffer, 0, buffer.length)) != -1) {
					output.append(new String(Arrays.copyOfRange(buffer, 0, len)));
				}
			} catch (IOException e) {
				output.append("Error: " + e.getMessage());
				status.setText("Status: Error");
			}
		}
	}
	
	private class OutputRedirectThread extends Thread {
		
		private final InputStream outputStream;
		
		public OutputRedirectThread(InputStream outputStream) {
			super();
			this.outputStream = outputStream;
		}
		
		@Override
		public void run() {
			try {
				byte[] buffer = new byte[Settings.BUFFER_SIZE];
				int len;

				while ((len = outputStream.read(buffer, 0, buffer.length)) != -1) {
					output.append(new String(Arrays.copyOfRange(buffer, 0, len)));
				}
				
				status.setText("Status: Finished");
			} catch (IOException e) {
				output.append("Error: " + e.getMessage());
				status.setText("Status: Error");
			}
		}
		
	}

}
