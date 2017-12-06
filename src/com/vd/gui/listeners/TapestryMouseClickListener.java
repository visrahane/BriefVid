package com.vd.gui.listeners;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.List;

import com.vd.services.GUI;
import com.vd.services.KeyFrameService;
import com.vd.services.TapestryService;
import com.vd.util.VideoIOUtil;

public class TapestryMouseClickListener implements MouseListener {

	private GUI gui;
	private int zoomLevel;
	int contextFrame;
	List<Integer> contextFrames;

	public TapestryMouseClickListener(GUI gui) {
		this.gui = gui;
		zoomLevel = 1;
		contextFrame = -1;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			System.out.println("Double click!");
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		System.out.println("X : " + e.getX() + " Y : " + e.getY());

		int wd = 105, ht = 90;
		int x = e.getX(), y = e.getY();

		int frameX = x / wd;
		int frameY = y / ht;

		int imageNumber = (frameX * 2) + frameY;
		TapestryService tapestryService = new TapestryService(gui);
		BufferedImage tapestryImage;
		List<Integer> framesToDisplay = null;

		// Zoom in
		if ((e.getModifiers() & InputEvent.SHIFT_MASK) != 0) {
			System.out.println("shift + click detected!");
			List<Integer> levelFrames = null, prevLevelFrames = null;

			if (zoomLevel < 3) {
				zoomLevel++;
			}
			if (zoomLevel == 2) {
				contextFrame = KeyFrameService.level1.get(imageNumber);
				contextFrames = getContextFrames(contextFrame, zoomLevel);
				framesToDisplay = contextFrames;
			} else if (zoomLevel == 3) {
				contextFrame = contextFrames.get(imageNumber);
				framesToDisplay = getContextFrames(contextFrame, zoomLevel);

			}
			tapestryImage = tapestryService.prepareTapestry(framesToDisplay);
			tapestryService.displayTapestry(tapestryImage);
		}
		// Zoom out
		else if ((e.getModifiers() & InputEvent.CTRL_MASK) != 0) {
			System.out.println("ctrl + click detected!");
			List<Integer> zoomOutframesToDisplay = null;

			if (zoomLevel > 1) {
				zoomLevel--;
			}
			if (zoomLevel == 1) {
				zoomOutframesToDisplay = KeyFrameService.level1;
			} else if (zoomLevel == 2) {
				zoomOutframesToDisplay = contextFrames;
			}

			tapestryImage = tapestryService.prepareTapestry(zoomOutframesToDisplay);
			tapestryService.displayTapestry(tapestryImage);
		}
		// Seek
		else {
			System.out.println("Frame : " + imageNumber);

			gui.pausePlay();
			gui.getAvPlayer().getVideo().setCurrentFramePtr(KeyFrameService.level1.get(imageNumber));
			if (gui.isPlay()) {
				gui.startPlay();
			} else {
				gui.displayImage(VideoIOUtil.getFrame(gui.getAvPlayer().getVideo().getFile(),
						KeyFrameService.level1.get(imageNumber)));
			}
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

	private List<Integer> getFramesFromRegion(int r, int level) {
		List<Integer> levelFrames = null;
		if (level == 2) {
			levelFrames = KeyFrameService.level2;
		} else if (level == 3) {
			levelFrames = KeyFrameService.level3;
		}

		int size = levelFrames.size();
		int elementsInDiv = size / 5;

		if (elementsInDiv * (r + 1) > size) {
			return levelFrames.subList(elementsInDiv * r, size);
		} else {
			return levelFrames.subList(elementsInDiv * r, elementsInDiv * (r + 1));
		}
	}

	private List<Integer> getContextFrames(int frameNumber, int level) {
		List<Integer> levelFrames = null;
		if (level == 2) {
			levelFrames = KeyFrameService.level2;
		} else if (level == 3) {
			levelFrames = KeyFrameService.level3;
		}

		int size = levelFrames.size();
		int index = levelFrames.indexOf(new Integer(frameNumber));

		if (index - 10 < 0) {
			return levelFrames.subList(0, index + 10);
		} else if (index + 10 > size) {
			return levelFrames.subList(index - 10, size);
		} else {
			return levelFrames.subList(index - 10, index + 10);
		}
	}
}
