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
package org.moeaframework.algorithm;

import java.util.EventObject;

/**
 * An event emitted by {@link AdaptiveTimeContinuation} indicating a time
 * continuation (restart) event has occurred.
 */
public class RestartEvent extends EventObject {

	private static final long serialVersionUID = -1876259076446997596L;

	/**
	 * The type of restart event.
	 */
	private final RestartType type;

	/**
	 * Constructs a restart event originating from the specified source.
	 * 
	 * @param source the source of this restart event
	 * @param type the type of this restart event
	 */
	public RestartEvent(AdaptiveTimeContinuation source, RestartType type) {
		super(source);
		this.type = type;
	}

	@Override
	public AdaptiveTimeContinuation getSource() {
		return (AdaptiveTimeContinuation)super.getSource();
	}

	/**
	 * Returns the type of this restart event.
	 * 
	 * @return the type of this restart event
	 */
	public RestartType getType() {
		return type;
	}

}
