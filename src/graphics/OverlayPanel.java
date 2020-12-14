package graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;

public abstract class OverlayPanel {
	
	private static Color OUTLINE = Color.black;
	
	protected String title;
	protected Dimension dimension;
	protected Panel panel;
	protected int x;
	protected int y;
	protected int borderBuffer;
	
	public OverlayPanel(Panel p, Dimension d) {
		this.dimension = d;
		this.panel = p;
		this.borderBuffer = 5;
		this.x = 0;
		this.y = 0;
		this.title = "";
	}
	
	protected void drawOutline(Graphics g, Color color) {
		g.setColor(color);
		g.drawRect(x, y, dimension.width, dimension.height);
	}
	
	protected boolean isPointInPanel(int x, int y) {
		if(x >= this.x && x <= (this.x + dimension.width) && y >= this.y && y <= (this.y + dimension.height)) {
			return true;
		} else {
			return false;
		}
	}
	
	protected int partition(int size, int numAreas) {
		return size / numAreas;
	}
	
	protected void renderTitle(Graphics g) {
		int location = partition(this.dimension.width, 2);
		int width = g.getFontMetrics().stringWidth(this.title);
		int height = g.getFontMetrics().getHeight();
		g.drawString(this.title, this.x + location - (width/2) , this.y + height);
	}
	
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void render(Graphics g) {
		this.drawOutline(g, getOUTLINE());
		this.renderTitle(g);
	}
	
	public void fillBackgroud(Graphics g, Color color) {
		g.setColor(color);
		g.fillRect(x, y, dimension.width, dimension.height);
	}

	public abstract void clicked(int x, int y);

	public Dimension getDimension() {
		return this.dimension;
	}

	public static Color getOUTLINE() {
		return OUTLINE;
	}

	public static void setOUTLINE(Color oUTLINE) {
		OUTLINE = oUTLINE;
	}
}
