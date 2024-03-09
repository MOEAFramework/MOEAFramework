package org.moeaframework.core.indicator;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class NativeHypervolumeTest {
	
	// TODO: Add more test coverage
	
	@Test
	public void testParseCommand() throws IOException {
		String command = "java -jar \"C:\\Program Files\\Test\\test.jar\" \"\"\"";
		String[] expected = new String[] { "java", "-jar", "C:\\Program Files\\Test\\test.jar", "\"" };
		String[] actual = NativeHypervolume.parseCommand(command);
		
		Assert.assertArrayEquals(expected, actual);
	}

}
