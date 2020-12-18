package input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import graphics.Panel;

//handles key input
public class KeyInput implements KeyListener {
	private boolean[] keys;
	
	private Panel panel;
	
	private boolean pausePressed = false;
	private boolean resetPressed = false;
	
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
		if (this.keys[KeyEvent.VK_I]) {
            this.panel.keyAction(KeyEvent.VK_I);
        }
		if (this.keys[KeyEvent.VK_F]) {
			this.panel.keyAction(KeyEvent.VK_F);
		}
		if (this.keys[KeyEvent.VK_T]) {
			this.panel.keyAction(KeyEvent.VK_T);
		}
		if (this.keys[KeyEvent.VK_P]) {
            this.panel.keyAction(KeyEvent.VK_P);
        }
		// pause
		if (this.keys[KeyEvent.VK_SPACE]) {
			if (!this.pausePressed) {
				this.panel.keyAction(KeyEvent.VK_SPACE);
				this.pausePressed = true;
			}
		} else {
			this.pausePressed = false;
		}
		// reset
		if (this.keys[KeyEvent.VK_R]) {
			if (!this.resetPressed) {
				this.panel.keyAction(KeyEvent.VK_R);
				this.resetPressed = true;
			}
		} else {
			this.resetPressed = false;
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		
		if (code == KeyEvent.VK_ESCAPE) {
			System.exit(0);
		}
		
		if (0 <= code && code < 256) {
			keys[code] = true;		
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		
		if (0 <= code && code < 256) {
			keys[code] = false;		
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		
	}
}
