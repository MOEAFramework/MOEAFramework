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
package org.moeaframework.util.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Writer;
import java.security.DigestInputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Settings;
import org.moeaframework.util.CommandLineUtility;

/**
 * Detects corrupted files using the MD5 message digest in a format compatible
 * with the Unix command line utility {@code md5sum}.
 * 
 * <ol>
 *   <li>Strict mode - All files must be validated, otherwise exceptions are 
 *       thrown
 *   <li>Safe mode - Files with associated digest files are validated, but only
 *       warnings are printed if no digest file exists
 * </ol>
 */
public class FileProtection extends CommandLineUtility {
	
	/**
	 * The property value for strict mode.
	 */
	public static final String STRICT_MODE = "STRICT";
	
	/**
	 * The property value for safe mode.
	 */
	public static final String SAFE_MODE = "SAFE";
	
	/**
	 * Private constructor to prevent instantiation.
	 */
	private FileProtection() {
		super();
	}
	
	/**
	 * Creates and returns a new instance of {@link MessageDigest}.
	 * 
	 * @return a new instance of {@code MessageDigest}
	 * @throws FrameworkException if the message digest could not be created
	 */
	private static MessageDigest createMessageDigest() {
		try {
			return MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new FrameworkException(e);
		}
	}

	/**
	 * Returns the digest file for the specified file.
	 * 
	 * @param file the file to be validated
	 * @return the digest file for the specified file
	 */
	public static File getDigestFile(File file) {
		return new File(file.getParentFile(), MessageFormat.format(
				Settings.getFileProtectionFormat(), file.getName()));
	}
	
	/**
	 * Saves the message digest file in a format compatible with {@code md5sum}.
	 * 
	 * @param file the file to be validated
	 * @param digest the message digest
	 * @throws IOException if an I/O error occurred
	 */
	private static void saveDigest(File file, byte[] digest) 
	throws IOException {
		PrintStream ps = null;
		
		try {
			ps = new PrintStream(getDigestFile(file));
			
			ps.print(Hex.encodeHex(digest));
			ps.print("  ");
			ps.print(file.getPath());
		} finally {
			if (ps != null) {
				ps.close();
			}
		}
	}
	
	/**
	 * Returns the digest value stored in the message digest file.
	 * 
	 * @param file the file to be validated
	 * @return the message digest
	 * @throws IOException if an I/O error occurred
	 */
	private static byte[] loadDigest(File file) throws IOException {
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(getDigestFile(file)));
			
			String line = reader.readLine();
			
			if (line == null) {
				throw new ValidationException(file, "invalid digest file");
			}
			
			int split = line.indexOf(' ');
			String digestHex = line.substring(0, split);
			String fileName = line.substring(split+2);

			if (!file.getPath().equals(fileName)) {
				throw new ValidationException(file, "invalid digest file");
			}

			return Hex.decodeHex(digestHex.toCharArray());
		} catch (Exception e) {
			throw new ValidationException(file, "invalid digest file");
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
	
	/**
	 * Computes and returns the message digest for the specified file.
	 * 
	 * @param file the file to be validated
	 * @return the message digest for the specified file
	 * @throws IOException if an I/O error occurred
	 */
	private static byte[] computeDigest(File file) throws IOException {
		InputStream is = null;
		
		try {
			is = new FileInputStream(file);
			
			return DigestUtils.md5(is);
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}
	
	/**
	 * Returns a {@link Reader} wrapping the result from calling
	 * {@code openInputStream} with the specified file.
	 * 
	 * @param file the file to be opened
	 * @return a {@code Reader} wrapping the result from calling
	 *         {@code openInputStream} with the specified file
	 * @throws FileNotFoundException if the specified file does not exist
	 */
	public static Reader openReader(final File file)
			throws FileNotFoundException {
		return new InputStreamReader(openInputStream(file));
	}

	/**
	 * Returns an {@link InputStream} for reading from the specified file and
	 * performs validation on the file when the {@code close} method is invoked
	 * on the stream.
	 * 
	 * @param file the file to the opened and validated
	 * @return an {@code InputStream} for reading from the specified file
	 * @throws FileNotFoundException if the specified file does not exist
	 */
	public static InputStream openInputStream(final File file)
			throws FileNotFoundException {
		return new DigestInputStream(new FileInputStream(file),
				createMessageDigest()) {

			@Override
			public void close() throws IOException {
				//finish reading the file contents
				byte[] buffer = new byte[Settings.BUFFER_SIZE];
				
				while (read(buffer) != -1) {
					//reading the data calculates the checksum, nothing else to
					//do here
				}
				
				super.close();
				
				//check digest
				validate(file, getMessageDigest().digest());
			}

		};
	}

	/**
	 * Returns a {@link Writer} wrapping the result from calling
	 * {@code openOutputStream} with the specified file.
	 * 
	 * @param file the file to be opened
	 * @return a {@code Writer} wrapping the result from calling
	 *         {@code openOutputStream} with the specified file
	 * @throws FileNotFoundException if the specified file does not exist
	 */
	public static Writer openWriter(final File file)
			throws FileNotFoundException {
		return new OutputStreamWriter(openOutputStream(file));
	}
	
	/**
	 * Validates the file.
	 * 
	 * @param file the file being validated
	 * @param actual the actual message digest of the file
	 * @throws IOException if an I/O error occurred
	 */
	private static void validate(File file, byte[] actual) throws IOException {
		String mode = Settings.getFileProtectionMode();
		File digestFile = getDigestFile(file);
		
		if (digestFile.exists()) {
			byte[] expected = loadDigest(file);
			
			if (!MessageDigest.isEqual(actual, expected)) {
				throw new ValidationException(file, 
						"digest does not match");
			}
		} else {
			if (mode.equalsIgnoreCase(STRICT_MODE)) {
				throw new ValidationException(file, "no digest file");
			} else {
				System.err.println("no digest file exists to validate "
						+ file);
			}
		}
	}
	
	/**
	 * Validates the file.
	 * 
	 * @param file the file to be validated
	 * @throws IOException if an I/O error occurred
	 */
	public static void validate(File file) throws IOException {
		validate(file, computeDigest(file));
	}

	/**
	 * Returns an {@link OutputStream} for writing to the specified file and
	 * saves a digest file for validating its contents when the {@code close}
	 * method is invoked.
	 * 
	 * @param file the file to be opened
	 * @return an {@code OutputStream} for writing to the specified file
	 * @throws FileNotFoundException if the specified file does not exist
	 */
	public static OutputStream openOutputStream(final File file)
			throws FileNotFoundException {
		return new DigestOutputStream(new FileOutputStream(file),
				createMessageDigest()) {

			@Override
			public void close() throws IOException {
				super.close();
				
				saveDigest(file, getMessageDigest().digest());
			}

		};
	}

	@SuppressWarnings("static-access")
	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		
		options.addOption(OptionBuilder
				.withLongOpt("check")
				.withDescription("Validates the listed files")
				.create('c'));
		
		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		if (commandLine.hasOption("check")) {
			int validCount = 0;
			int invalidCount = 0;
			String mode = Settings.getFileProtectionMode();
			
			for (String filename : commandLine.getArgs()) {
				File file = new File(filename);
				
				System.out.print(file);
				System.out.print(": ");
				
				if (getDigestFile(file).exists()) {
					byte[] actual = computeDigest(file);
					byte[] expected = loadDigest(file);
					boolean valid = MessageDigest.isEqual(actual, expected);

					if (valid) {
						validCount++;
						System.out.println("OK");
					} else {
						invalidCount++;
						System.out.println("FAILED");
					}
				} else {
					if (mode.equalsIgnoreCase(STRICT_MODE)) {
						invalidCount++;
						System.out.println("FAILED");
					} else {
						validCount++;
						System.out.println("OK (NO DIGEST FILE)");
					}
				}
			}
			
			
			if (invalidCount > 0) {
				System.err.print("WARNING: ");
				System.err.print(invalidCount);
				System.err.print(" of ");
				System.err.print(validCount + invalidCount);
				System.err.println(" computed checksums did NOT match");
			}
		} else {
			for (String filename : commandLine.getArgs()) {
				File file = new File(filename);
				byte[] digest = computeDigest(file);
				saveDigest(file, digest);
			}
		}
	}
	
	/**
	 * Starts the command line utility for validating files using message 
	 * digests.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new FileProtection().start(args);
	}

}
