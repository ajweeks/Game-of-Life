package ca.ab.cbe.wahs.ajw;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Mouse implements MouseListener, MouseMotionListener {

	public boolean down = false;

	public int x = 0, y = 0;
	
	public void mousePressed(MouseEvent e) {
		down = true;
	}

	public void mouseMoved(MouseEvent e) {
		x = e.getX();
		y = e.getY();
	}

	public void mouseClicked(MouseEvent e) {}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mouseReleased(MouseEvent e) {}

	public void mouseDragged(MouseEvent e) {}


}
