package input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import graphics.Panel;

//handles key input
public class KeyInput implements KeyListener {
	private boolean[] keys;

	private Panel panel;
	
	public KeyInput(Panel panel) {
		keys = new boolean[256];
		for (int i = 0; i < 256; ++i) {
			keys[i] = false;
		}
		
		this.panel = panel;
	}
	
	// send info to the panel
	public void tick() {
		if (this.keys[KeyEvent.VK_UP] || this.keys[KeyEvent.VK_W]) {
			this.panel.keyAction(KeyEvent.VK_UP);
		}
		if (this.keys[KeyEvent.VK_DOWN] || this.keys[KeyEvent.VK_S]) {
			this.panel.keyAction(KeyEvent.VK_DOWN);
		}
		if (this.keys[KeyEvent.VK_LEFT] || this.keys[KeyEvent.VK_A]) {
			this.panel.keyAction(KeyEvent.VK_LEFT);
		}
		if (this.keys[KeyEvent.VK_RIGHT] || this.keys[KeyEvent.VK_D]) {
			this.panel.keyAction(KeyEvent.VK_RIGHT);
		}
		if (this.keys[KeyEvent.VK_N]) {
			this.panel.keyAction(KeyEvent.VK_N);
		}
		if (this.keys[KeyEvent.VK_M]) {
			this.panel.keyAction(KeyEvent.VK_M);
		}
		if (this.keys[KeyEvent.VK_F]) {
			this.panel.keyAction(KeyEvent.VK_F);
		}
		if (this.keys[KeyEvent.VK_T]) {
			this.panel.keyAction(KeyEvent.VK_T);
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		
		if (code == KeyEvent.VK_ESCAPE) {
			System.exit(0);
		}
		
		keys[code] = true;		
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		
	}
}
