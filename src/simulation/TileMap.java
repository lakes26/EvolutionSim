package simulation;

import java.util.Random;

public class TileMap {
	private int width, height, tileSize;
	private int tiles[][];
	
	public TileMap(int width, int height, int tileSize) {
		this.width = width;
		this.height = height;
		this.tileSize = tileSize;
		
		// initialize all tiles to zero
		tiles = new int[width][height];
		for (int i = 0; i < width; ++i) {
			for (int j = 0; j < height; ++j) {
				tiles[i][j] = 0;
			}
		}
	}
	
	// randomly create tiles with probability proportion for each tile
	public void randomTiles(double proportion) {
		assert(proportion >= 0 && proportion <= 1);
		
		Random rand = new Random();
		
		int randomTiles = (int) (proportion * width * height);
		
		int counter = 0;
		while (counter < randomTiles) {
			int x = (int) (rand.nextDouble() * width); 
			int y = (int) (rand.nextDouble() * height); 
			
			if (tiles[x][y] == 1) {
				continue;
			}
			
			tiles[x][y] = 1;
			
			++counter;
		}
	}
	
	// make the border
	public void addBorder() {		
		for (int i = 0; i < width; ++i) {
			tiles[i][0] = 1;
			tiles[i][height - 1] = 1;
		}
		
		for (int j = 1; j < height - 1; ++j) {
			tiles[0][j] = 1;
			tiles[width - 1][j] = 1;
		}
	}
	
	// make a vertical line splitting the environment
	public void addSplitWall(int thickness) {	
		for (int i = 0; i < height; ++i) {
			for (int j = (int) (-thickness / 2); j < (int) ((float) thickness / 2 + .5); ++j) {
				tiles[width / 2 + j][i] = 1;
			}
		}
	}
	
	public boolean inWall(float x, float y) {
		// outside of tilemap boundaries is considered in a wall
		if (x < 0 || x > width * tileSize || y < 0 || y > height * tileSize) {
			return true;
		}
		
		int tileX = (int) (x / tileSize);
		int tileY = (int) (y / tileSize);
		
		// on the border should be considered within the outermost tile
		if (tileX == width) {
			--tileX;
		}
		if (tileY == height) {
			--tileY;
		}
		
		return tiles[tileX][tileY] == 1;
	}
	
	public boolean inWall(int x, int y) {
		return inWall((float) x, (float) y);
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int[][] getTiles() {
		return tiles;
	}
	
	public int getTileSize() {
		return tileSize;
	}
}
