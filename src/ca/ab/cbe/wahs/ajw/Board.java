package ca.ab.cbe.wahs.ajw;

public class Board {

	public boolean[][] cells;

	public int width = 40, height = 40;

	public Board() {
		cells = new boolean[height][width];

		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
				cells[y][x] = false;
	}

	public int getRow(int y) {
		if (y <= 0 || y > 631) return -1;
		else return y / Main.tileSize;
	}

	public int getColumn(int x) {
		if (x <= 0 || x > 631) return -1;
		else return x / Main.tileSize;
	}

}
