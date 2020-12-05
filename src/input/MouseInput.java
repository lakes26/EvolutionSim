package input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import graphics.Panel;

public class MouseInput implements MouseListener {
	
	private Panel panel;
	
	public MouseInput(Panel p) {
		this.panel = p;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		this.panel.mouseClicked(e.getX(), e.getY());		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

}
