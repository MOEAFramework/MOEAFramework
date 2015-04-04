package org.moeaframework.algorithm;

import java.io.IOException;
import org.junit.Test;

/**
 * Tests the {@link PAES} class.
 */
public class PAESTest extends AlgorithmTest {
	
	@Test
	public void testDTLZ1() throws IOException {
		test("DTLZ1_2", "PAES", "PAES-JMetal");
	}
	
	@Test
	public void testDTLZ2() throws IOException {
		test("DTLZ2_2", "PAES", "PAES-JMetal");
	}
	
	@Test
	public void testDTLZ7() throws IOException {
		test("DTLZ7_2", "PAES", "PAES-JMetal");
	}
	
	@Test
	public void testUF1() throws IOException {
		test("UF1", "PAES", "PAES-JMetal");
	}

}
