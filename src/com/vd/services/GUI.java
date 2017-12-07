/**
 *
 */
package com.vd.services;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import com.vd.constants.VideoConstant;
import com.vd.gui.listeners.ButtonPlayActionListener;
import com.vd.gui.listeners.ButtonStopActionListener;
import com.vd.gui.listeners.TapestryMouseClickListener;
import com.vd.gui.listeners.VideoSliderChangeListener;
import com.vd.player.AVPlayer;
import com.vd.runnable.VideoRunnable;

/**
 * @author Vis
 *
 */
public class GUI {

	private JFrame frame = new JFrame();
	private JPanel videoPanel;
	private JPanel sliderPanel;
	private JPanel bottomPanel;

	private JLabel lblVideoDisplay;
	private JLabel lblTapestryDisplay;

	ImageIcon tapestryIcon;

	private AVPlayer avPlayer;

	private VideoRunnable videoRunnable;
	private JProgressBar progressBar;

	private JButton btnPlay;

	private boolean play;

	public boolean isPlay() {
		return play;
	}

	public void setPlay(boolean play) {
		this.play = play;
	}

	public GUI(String title, String[] args) {
		frame.setTitle(title);
		avPlayer = new AVPlayer(args);
		videoRunnable = new VideoRunnable(this);

		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

		videoPanel = new JPanel();
		videoPanel.setPreferredSize(new Dimension(500, 330));
		frame.getContentPane().add(videoPanel);

		lblVideoDisplay = new JLabel();
		videoPanel.add(lblVideoDisplay);
		videoPanel.setVisible(true);
		videoPanel.setBackground(Color.black);

		sliderPanel = new JPanel();
		frame.getContentPane().add(sliderPanel);

		btnPlay = new JButton();
		ImageIcon playBtnIcon = new ImageIcon("R:\\Java_Workspace\\Multimedia\\BriefVid\\playButton.png");
		sliderPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		btnPlay.setIcon(null);
		btnPlay.setText("Play");
		btnPlay.addActionListener(new ButtonPlayActionListener(this));
		sliderPanel.setBackground(Color.gray);
		sliderPanel.add(btnPlay);

		JButton btnStop = new JButton();
		btnStop.setIcon(null);
		btnStop.setText("Stop");
		btnStop.addActionListener(new ButtonStopActionListener(this));
		sliderPanel.add(btnStop);

		progressBar = new JProgressBar(0, 100);
		progressBar.setStringPainted(true);
		sliderPanel.add(progressBar);
		progressBar.addMouseListener(new VideoSliderChangeListener(this));

		bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.setPreferredSize(new Dimension(1365, 210));
		bottomPanel.setBackground(Color.gray);
		frame.getContentPane().add(bottomPanel);

		lblTapestryDisplay = new JLabel();
		bottomPanel.add(lblTapestryDisplay, BorderLayout.CENTER);

		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// wd 105 ht 90
		lblTapestryDisplay.addMouseListener(new TapestryMouseClickListener(this));
	}

	public GUI() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public void displayTapestry(BufferedImage img) {
		tapestryIcon = new ImageIcon(img);
		lblTapestryDisplay.setIcon(tapestryIcon);

	}

	public void displayImage(BufferedImage img) {
		lblVideoDisplay.setIcon(new ImageIcon(img));
	}

	public AVPlayer getAvPlayer() {
		return avPlayer;
	}

	public void setAvPlayer(AVPlayer avPlayer) {
		this.avPlayer = avPlayer;
	}

	public void startPlay() {
		videoRunnable = new VideoRunnable(this);
		videoRunnable.start();
	}

	public void pausePlay() {
		videoRunnable.toggleStop();
		try {
			videoRunnable.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		avPlayer.stopBufferThread();
	}

	public void stopPlay() {
		// stopAllThreads
		pausePlay();
		avPlayer.getVideo().setCurrentFramePtr(0);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				resetGUI();
			}
		});

	}

	private void resetGUI() {
		progressBar.setValue(0);
		progressBar.setString(0 + "%");
		btnPlay.setText(VideoConstant.BUTTON_START_TEXT);
		lblVideoDisplay.setIcon(null);
	}

	public void updateSlider(int n) {
		progressBar.setValue(n);
		progressBar.setString(n + "%");
	}

	public void togglePlay() {
		play = !play;
	}

}
