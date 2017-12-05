package com.vd.gui.listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.vd.services.GUI;
import com.vd.services.KeyFrameService;
import com.vd.util.VideoIOUtil;

public class TapestryMouseClickListener implements MouseListener {

	private GUI gui;

	public TapestryMouseClickListener(GUI gui) {
		this.gui = gui;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		System.out.println("X : " + e.getX() + " Y : " + e.getY());

		int wd = 105, ht = 90;
		int x = e.getX(), y = e.getY();

		int frameX = x / wd;
		int frameY = y / ht;

		int frameNumber = (frameX * 2) + frameY;

		System.out.println("Frame : " + frameNumber);

		gui.pausePlay();
		gui.getAvPlayer().getVideo().setCurrentFramePtr(KeyFrameService.level1.get(frameNumber));
		if (gui.isPlay()) {
			gui.startPlay();
		} else {
			gui.displayImage(VideoIOUtil.getFrame(gui.getAvPlayer().getVideo().getFile(), KeyFrameService.level1.get(frameNumber)));
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
