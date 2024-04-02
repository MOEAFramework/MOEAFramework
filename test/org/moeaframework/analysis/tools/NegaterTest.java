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
package org.moeaframework.analysis.tools;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TempFiles;
import org.moeaframework.analysis.io.MatrixReader;

public class NegaterTest {
    
    @Test
    public void testSingleNegation() throws Exception {
        File file = TempFiles.createFileWithContent("0.0 0.1 -0.1 -0.1\n#foo bar\n5.12e-10 -5.12e10 0.5 0.000001");
        
        Negater.main(new String[] {
               "-d", "1,1,0,1",
               file.getPath()
        });
        
        double[][] expected = { { 0.0, -0.1, -0.1, 0.1}, {-5.12e-10, 5.12e10, 0.5, -0.000001} };
        double[][] actual = MatrixReader.load(file);
        
        Assert.assertEquals(expected, actual);
    }
    
    @Test
    public void testDoubleNegation() throws Exception {
        File file = TempFiles.createFileWithContent("0.0 0.1 -0.1 -0.1\n#foo bar\n5.12e-10 -5.12e10 0.5 0.000001");
        File copy = TempFiles.createFile();
        
        Files.copy(file.toPath(), copy.toPath(), StandardCopyOption.REPLACE_EXISTING);
        
        Negater.main(new String[] {
               "-d", "1,1,0,1",
               file.getPath()
        });
        
        Negater.main(new String[] {
                "-d", "1,1,0,1",
                file.getPath()
        });
        
        Assert.assertEquals(MatrixReader.load(copy), MatrixReader.load(file));
    }
    
    @Test
    public void testNoOverwriteOnError1() throws Exception {
        File file = TempFiles.createFileWithContent("0.0 0.1 -0.1 -0.1\n#foo bar\n5.12e-10 -5.12e10 0.5");
        File copy = TempFiles.createFile();
        
        Files.copy(file.toPath(), copy.toPath(), StandardCopyOption.REPLACE_EXISTING);

        Negater.main(new String[] {
               "-d", "1,1,0,1",
               file.getPath()
        });
        
        Assert.assertEquals(Files.readString(copy.toPath(), StandardCharsets.UTF_8),
        		Files.readString(file.toPath(), StandardCharsets.UTF_8));
    }

    @Test
    public void testNoOverwriteOnError2() throws Exception {
        File file = TempFiles.createFileWithContent("0.0 0.1 -0.1 -0.1\n#foo bar\n5.12e-10 0,1,2 0.5 0.000001");
        File copy = TempFiles.createFile();
        
        Files.copy(file.toPath(), copy.toPath(), StandardCopyOption.REPLACE_EXISTING);

        Negater.main(new String[] {
               "-d", "1,1,0,1",
               file.getPath()
        });
        
        Assert.assertEquals(Files.readString(copy.toPath(), StandardCharsets.UTF_8),
        		Files.readString(file.toPath(), StandardCharsets.UTF_8));
    }

}
