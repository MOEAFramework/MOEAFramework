/* Copyright 2009-2025 David Hadka
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
package org.moeaframework.util.mvc;

import java.awt.Window;

import javax.swing.JFrame;

import org.moeaframework.algorithm.Algorithm;
import org.moeaframework.core.termination.CancellationSignal;

/**
 * Abstract class for building example GUIs.  This abstract class handles running the algorithm in a background thread,
 * invoking {@link #update(Algorithm, int)} after each iteration, and stopping when the window is closed.
 * 
 * @param <T> the type of algorithm
 */
public abstract class ExampleUI<T extends Algorithm> extends JFrame implements ControllerListener {
	
	private static final long serialVersionUID = 5771940090079596225L;
	
	private final ExampleController<T> controller;
	
	/**
	 * Constructs a new example GUI.
	 * 
	 * @param title the title of this window
	 * @param algorithm the algorithm
	 */
	public ExampleUI(String title, T algorithm) {
		super(title);
		
		controller = new ExampleController<>(this, algorithm);
		controller.addControllerListener(this);
	}
	
	/**
	 * Triggers the start of this example.
	 */
	public void start() {
		controller.start();
	}

	/**
	 * Invoked on the event dispatch thread to update the GUI after each iteration.
	 * 
	 * @param algorithm the algorithm
	 * @param iteration the current iteration
	 */
	public abstract void update(T algorithm, int iteration);
	
	@Override
	public void controllerStateChanged(ControllerEvent event) {
		update(controller.getAlgorithm(), controller.getIteration());
	}
	
	private static class ExampleController<T extends Algorithm> extends Controller {
		
		private final CancellationSignal cancellationSignal;
		
		private final T algorithm;
		
		private final Thread thread;
		
		private int iteration;
		
		public ExampleController(Window window, T algorithm) {
			super(window);
			this.algorithm = algorithm;
			
			cancellationSignal = new CancellationSignal();
			addShutdownHook(this::stop);
			
			thread = new Thread() {
				
				{
					setDaemon(true);
				}
				
				@Override
				public void run() {
					cancellationSignal.initialize(algorithm);
					
					while (!cancellationSignal.isCancelled()) {
						algorithm.step();
						iteration += 1;
						
						fireEvent("update");
					}
					
					algorithm.terminate();
				}
				
			};
		}
		
		public T getAlgorithm() {
			return algorithm;
		}
		
		public int getIteration() {
			return iteration;
		}
		
		public void start() {
			thread.start();
		}
		
		public void stop() {
			cancellationSignal.cancel();
		}
		
	}

}
