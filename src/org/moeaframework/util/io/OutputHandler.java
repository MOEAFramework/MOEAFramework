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
package org.moeaframework.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import org.apache.commons.io.IOUtils;
import org.moeaframework.core.Settings;

/**
 * Java Logging {@link Handler} with some more sensible defaults (in my opinion), including (1) sending logs to
 * {@link System.out} instead of {@link System.err}, and (2) using a shorter, single line format.
 */
public class OutputHandler extends StreamHandler {

	public static void updateConfiguration(boolean force) {
		String format = "[%1$tc] %4$s: %5$s%n";
		
		if (Settings.PROPERTIES.getBoolean(Settings.KEY_HIDE_TIMESTAMP, false)) {
			format = "%4$s: %5$s%n";
		}
		
		final String loggingConfiguration = String.join("\n",
				"handlers=" + OutputHandler.class.getName(),
				"java.util.logging.SimpleFormatter.format=" + format);

		try (InputStream in = IOUtils.toInputStream(loggingConfiguration, StandardCharsets.UTF_8)) {
			LogManager.getLogManager().updateConfiguration(in, (k) -> (o, n) -> o == null || force ? n : o);
		} catch (IOException e) {
			// fall back to defaults
		}
	}

	public static Logger getLogger(String name) {
		updateConfiguration(false);

		Logger logger = Logger.getLogger(name);
		logger.setUseParentHandlers(false);

		for (Handler handler : logger.getHandlers()) {
			logger.removeHandler(handler);
		}

		logger.addHandler(new OutputHandler());
		return logger;
	}

    public OutputHandler() {
    	super(System.out, new SimpleFormatter());
    }

    @Override
    public void publish(LogRecord record) {
        super.publish(record);
        flush();
    }

    @Override
    public void close() {
        flush();
    }

}
