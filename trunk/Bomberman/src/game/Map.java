package game;

import java.util.Random;
import java.util.Vector;

import utils.Arrays;
import utils.Point;
import utils.Values;

public class Map {

	// d r u l
	public final static Point[] mv = new Point[] { new Point(0, 1), new Point(1, 0), new Point(0, -1), new Point(-1, 0) };

	public final static int GRID_CLEAR = 0, GRID_BOX = 1, GRID_WALL = 2, GRID_EXPLOSION = 3, GRID_BOMB = 4;
	public int[][] grid;

	public Map(int _bomber_type) {
		switch (_bomber_type) {
		case 0:
			classic(24, 12, 0.4f);
			break;
		case 1:
			random(24, 12, 14 + 12, 0);
			break;
		case 2:
			classic(28, 12, 0.5f);
			break;
		case 3:
			random(28, 12, 14 + 64, 48);
		case 4:
			classic(32, 16, 0.6f);
			break;
		}
	}

	protected void classic(int w, int h, float _factor_boxes) {
		if (w % 2 == 0)
			++w;
		if (h % 2 == 0)
			++h;
		// init
		grid = new int[h][w];
		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid[i].length; j++)
				grid[i][j] = GRID_BOX;
		// walls row
		for (int i = 0; i < w; i++)
			grid[0][i] = grid[h - 1][i] = GRID_WALL;
		// walls col
		for (int i = 0; i < h; i++)
			grid[i][0] = grid[i][w - 1] = GRID_WALL;
		// walls cross
		for (int i = 2; i < h; i += 2)
			for (int j = 2; j < w; j += 2)
				grid[i][j] = GRID_WALL;
		// holes
		Vector boxes = getAll(GRID_BOX, 1, 1, w - 1, h - 1);
		int nBoxes = boxes.size();
		while (boxes.size() > nBoxes * _factor_boxes) {
			int r = Math.abs(new Random().nextInt()) % boxes.size();
			Point p = (Point) boxes.elementAt(r);
			boxes.removeElementAt(r);
			grid[(int) p.y][(int) p.x] = GRID_CLEAR;
		}
	}

	protected void random(int w, int h, int nBoxes, int nHoles) {
		if (w % 2 == 0)
			++w;
		if (h % 2 == 0)
			++h;
		// init
		grid = new int[h][w];
		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid[i].length; j++)
				grid[i][j] = GRID_WALL;
		// random bfs
		Vector q = new Vector();
		q.addElement(new Point(1, 1));
		while (q.size() > 0) {
			int r = Math.abs(new Random().nextInt()) % q.size();
			Point p = (Point) q.elementAt(r);
			q.removeElementAt(r);
			grid[(int) p.y][(int) p.x] = GRID_CLEAR;
			pushNeighabours(q, p, grid);
		}
		// boxes & holes
		Vector walls = getAll(GRID_WALL, 1, 1, grid[0].length - 1, grid.length - 1);
		while ((nHoles > 0 || nBoxes > 0) && walls.size() > 0) {
			int r = Math.abs(new Random().nextInt()) % walls.size();
			Point p = (Point) walls.elementAt(r);
			// box
			if ((new Random().nextInt() & 1) == 1 && nBoxes > 0) {
				walls.removeElementAt(r);
				grid[(int) p.y][(int) p.x] = GRID_BOX;
				--nBoxes;
			}
			// hole
			else if (nHoles > 0) {
				walls.removeElementAt(r);
				grid[(int) p.y][(int) p.x] = GRID_CLEAR;
				--nHoles;
			}
		}
	}
	
	public int getArea(int x, int y) {
		int ans = getArea(x, y, new boolean[height()][width()]);
		return ans;
	}

	private int getArea(int x, int y, boolean[][] vis) {
		vis[y][x] = true;
		int ans = 1;
		for (int i = 0; i < mv.length; i++) {
			int newX = (int) (x + mv[i].x);
			int newY = (int) (y + mv[i].y);
			if (newY >= 0 && newY < grid.length && newX >= 0 && newX < grid[0].length)
				if (!vis[newY][newX] && grid[newY][newX] == grid[y][x])
					ans += getArea(newX, newY, vis);
		}
		return ans;
	}

	public Vector getAll(int g, int x1, int y1, int x2, int y2) {
		Vector v = new Vector();
		for (int i = y1; i < y2; i++)
			for (int j = x1; j < x2; j++)
				if (grid[i][j] == g)
					v.addElement(new Point(j, i));
		return v;
	}

	private static void pushNeighabours(Vector q, Point u, int[][] out) {
		for (int i = 0; i < mv.length; i++) {
			Point v = new Point(u.x + mv[i].x * 2, u.y + mv[i].y * 2);
			Point m = new Point((u.x + v.x) / 2, (u.y + v.y) / 2);
			if (Values.isBounded(v.y, 0, out.length - 1) && Values.isBounded(v.x, 0, out[0].length - 1))
				if (out[(int) v.y][(int) v.x] == GRID_WALL) {
					out[(int) v.y][(int) v.x] = GRID_CLEAR;
					out[(int) m.y][(int) m.x] = GRID_CLEAR;
					q.addElement(v);
				}
		}
	}

	public int width() {
		return grid[0].length;
	}

	public int height() {
		return grid.length;
	}
}
