/* Copyright 2009-2019 David Hadka
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
package org.moeaframework.analysis.sensitivity;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.util.io.FileUtils;

/**
 * Tests the {@link Negater} class.
 */
public class NegaterTest {
    
    @Test
    public void testSingleNegation() throws Exception {
        File file = TestUtils.createTempFile(
                "0.0 0.1 -0.1 -0.1\n#foo bar\n5.12e-10 -5.12e10 0.5 0.000001");
        
        Negater.main(new String[] {
               "-d", "1,1,0,1",
               file.getPath()
        });
        
        double[][] expected = { { 0.0, -0.1, -0.1, 0.1}, 
                {-5.12e-10, 5.12e10, 0.5, -0.000001} };
        double[][] actual = TestUtils.loadMatrix(file);
        
        TestUtils.assertEquals(expected, actual);
    }
    
    @Test
    public void testDoubleNegation() throws Exception {
        File file = TestUtils.createTempFile(
                "0.0 0.1 -0.1 -0.1\n#foo bar\n5.12e-10 -5.12e10 0.5 0.000001");
        File copy = TestUtils.createTempFile();
        FileUtils.copy(file, copy);
        
        Negater.main(new String[] {
               "-d", "1,1,0,1",
               file.getPath()
        });
        
        Negater.main(new String[] {
                "-d", "1,1,0,1",
                file.getPath()
        });
        
        TestUtils.assertEquals(TestUtils.loadMatrix(copy),
        		TestUtils.loadMatrix(file));
    }
    
    @Test
    public void testNoOverwriteOnError1() throws Exception {
        File file = TestUtils.createTempFile(
                "0.0 0.1 -0.1 -0.1\n#foo bar\n5.12e-10 -5.12e10 0.5");
        File copy = TestUtils.createTempFile();
        FileUtils.copy(file, copy);

        Negater.main(new String[] {
               "-d", "1,1,0,1",
               file.getPath()
        });
        
        Assert.assertArrayEquals(TestUtils.loadFile(copy),
        		TestUtils.loadFile(file));
    }

    @Test
    public void testNoOverwriteOnError2() throws Exception {
        File file = TestUtils.createTempFile(
                "0.0 0.1 -0.1 -0.1\n#foo bar\n5.12e-10 0,1,2 0.5 0.000001");
        File copy = TestUtils.createTempFile();
        FileUtils.copy(file, copy);

        Negater.main(new String[] {
               "-d", "1,1,0,1",
               file.getPath()
        });
        
        Assert.assertArrayEquals(TestUtils.loadFile(copy),
        		TestUtils.loadFile(file));
    }

}
