/**
 *
 */
package com.vd.services;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * @author Vis
 *
 */
public class ImageDisplayService {

	private JFrame frame;

	public ImageDisplayService(String title) {
		frame = new JFrame(title);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public void displayTapestry(BufferedImage img) {
		JPanel mergePanel = new JPanel();
		JScrollPane scrollableJScrollPane = new JScrollPane(new JLabel(new ImageIcon(img)));
		scrollableJScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		mergePanel.add(scrollableJScrollPane);
		frame.getContentPane().add(mergePanel, BorderLayout.SOUTH);
	}

	public void displayImage(BufferedImage img) {
		JPanel panel = new JPanel();
		panel.add(new JLabel(new ImageIcon(img)));
		JButton b1 = new JButton("Play");
		b1.setVisible(true);
		JButton b2 = new JButton("Stop");
		JButton b3 = new JButton("Pause");
		frame.add(b1, BorderLayout.WEST);
		frame.add(b2, BorderLayout.WEST);
		frame.add(b3, BorderLayout.WEST);


		frame.getContentPane().add(panel, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
