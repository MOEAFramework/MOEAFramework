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
package org.moeaframework.algorithm.continuation;

import java.util.EventListener;

import org.moeaframework.algorithm.Algorithm;

/**
 * The listener interface for receiving time continuation (restart) events.  The {@link #restarted} method is called
 * immediately after the restart has occurred, prior to the {@link Algorithm#step()} method returning.
 */
public interface RestartListener extends EventListener {

	/**
	 * Invoked when a restart event has occurred.
	 * 
	 * @param event the object storing details about the restart event
	 */
	public void restarted(RestartEvent event);

}
