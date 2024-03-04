package org.moeaframework.util.io;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class ScreenCapture {
	
	public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		Class<?> mainType = Class.forName(args[0]);
		Method mainMethod = mainType.getMethod("main", String[].class);
		
		mainMethod.invoke(null, (Object)new String[0]);
		
		for (JFrame frame : getJFrames()) {
			Component component = frame.getContentPane();
			
			BufferedImage image = new BufferedImage(component.getWidth(), component.getHeight(), BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = image.createGraphics();
			
			component.paint(graphics);
			
            ImageIO.write(image, "png", new File("plot.png"));
            
            frame.dispose();
		}
	}
	
	public static List<JFrame> getJFrames() {
		List<JFrame> frames = new ArrayList<JFrame>();
		
		for (Frame frame : Frame.getFrames()) {
			if (frame instanceof JFrame) {
				frames.add((JFrame)frame);
			}
		}
		
		return frames;
	}

}
