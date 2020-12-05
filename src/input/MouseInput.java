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
		this.panel.mouseClicked(e.getX(), e.getY());		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
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
