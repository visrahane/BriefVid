/**
 *
 */
package com.vd.services;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Vis
 *
 */
public class ImageDisplayService {

	private JFrame frame;

	public ImageDisplayService(String title) {
		frame = new JFrame(title);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public void displayImage(BufferedImage img) {
		JPanel panel = new JPanel();
		panel.add(new JLabel(new ImageIcon(img)));
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
