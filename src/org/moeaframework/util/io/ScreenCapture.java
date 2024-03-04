package org.moeaframework.util.io;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FilenameUtils;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.util.CommandLineUtility;

/**
 * Command line utility that runs a Java GUI example and takes a screenshot of the window.
 */
public class ScreenCapture extends CommandLineUtility {

	// <!-- screen:examples/org/moeaframework/plots/PlotApproximationSet.java -->
	// ![image](docs/img/approximationSet.png)

	@Override
	public Options getOptions() {
		Options options = super.getOptions();

		options.addOption(Option.builder("c")
				.longOpt("classname")
				.hasArg()
				.argName("str")
				.required()
				.build());
		options.addOption(Option.builder("t")
				.longOpt("title")
				.hasArg()
				.argName("str")
				.build());
		options.addOption(Option.builder("f")
				.longOpt("format")
				.hasArg()
				.argName("str")
				.build());
		options.addOption(Option.builder("o")
				.longOpt("output")
				.hasArg()
				.argName("file")
				.required()
				.build());

		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		String classname = commandLine.getOptionValue("classname");
		String title = commandLine.getOptionValue("title");
		String output = commandLine.getOptionValue("output");
		String format = commandLine.getOptionValue("format", FilenameUtils.getExtension(output));

		System.out.println("Invoking main method on " + classname);
		Class<?> mainType = Class.forName(classname);
		Method mainMethod = mainType.getMethod("main", String[].class);

		mainMethod.invoke(null, (Object)commandLine.getArgs());

		JFrame frame = findFrame(title);
		System.out.println("Found " + frame.getTitle());
		Component component = frame.getContentPane();

		BufferedImage image = new BufferedImage(component.getWidth(), component.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();

		component.paint(graphics);
		ImageIO.write(image, format, new File(output));
		System.out.println("  > Saved screen capture to " + output);

		frame.dispose();
		System.out.println("  > Disposing frame");
	}

	protected JFrame findFrame(String title) {
		for (Frame frame : Frame.getFrames()) {
			if (frame instanceof JFrame && (title == null || frame.getTitle().equals(title))) {
				return (JFrame)frame;
			}
		}

		throw new FrameworkException("no matching JFrame found in current JVM");
	}

	public static void main(String[] args) throws Exception {
		new ScreenCapture().start(args);
	}

}
