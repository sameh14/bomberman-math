package screens;

import game.Entity;
import game.EntityBomberman;
import game.EntityEnemy;
import game.EntityItem;
import game.Map;
import game.View;

import java.util.Random;
import java.util.Vector;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.game.TiledLayer;

import resource.ResMgr;
import utils.Images;
import utils.Point;

public class CanvasGame extends GameCanvas implements Runnable {

	public final int SCORE_BOX = 1, SCORE_ITEM = 2, SCORE_ENEMY = 5, SCORE_WIN = 10;
	public final int GRID = 16;

	public ResMgr resources;

	public Map map;
	public View view;
	public Vector bombermen = new Vector();
	public Vector enemies = new Vector();
	public Vector bombs = new Vector();
	public Vector explosions = new Vector();
	public Vector items = new Vector();

	public Vector availableItems = new Vector();
	protected int level_type;
	public int req_num;

	protected long prevUpdateTime = -1;
	protected boolean isRunning = true;
	protected Thread runner = new Thread(this);
	public MidletGame midlet;

	protected TiledLayer background;
	protected TiledLayer hud;

	protected CanvasGame(boolean suppressKeyEvents, MidletGame _midlet, ResMgr _resources) {
		super(suppressKeyEvents);
		setFullScreenMode(true);
		resources = _resources;
		midlet = _midlet;
	}

	public boolean init(int _level, int _req_num, int[] _quiz_array, int _bomber_type, int _score, int _nEnemy, float _powerup_factor) {
		// map
		map = new Map(_bomber_type);
		level_type = _level;
		req_num = _req_num;
		// background
		resources.loadImage("sprites/back-16x16.png");
		Image imBg = resources.getImage("sprites/back-16x16.png");
		background = new TiledLayer(map.grid[0].length, map.grid.length, imBg, GRID, GRID);
		background.setVisible(true);
		// hud
		resources.loadImage("sprites/powerups-16x16.png");
		Image imHud = resources.getImage("sprites/powerups-16x16.png");
		hud = new TiledLayer(18, 1, imHud, GRID, GRID);
		for (int i = 0; i < _quiz_array.length; i++)
			hud.setCell(i, 0, _quiz_array[i]);
		// place bomberman & enemies
		Vector emptyPos = map.getAll(Map.GRID_CLEAR, 1, 1, map.width() - 1, map.height() - 1);
		if (!placeBomberman(emptyPos, _bomber_type, _score))
			return false;
		if (!placeEnemies(emptyPos, _nEnemy, _level))
			return false;
		// items
		int nBox = map.getAll(Map.GRID_BOX, 0, 0, map.width(), map.height()).size();
		for (int i = 0; i < 10; i++)
			availableItems.addElement(new Integer(i));
		for (int i = 16; i < 20; i++)
			availableItems.addElement(new Integer(i));
		for (int i = 15; i < nBox * _powerup_factor; i++)
			availableItems.addElement(new Integer(10 + i % 6));
		while (availableItems.size() < nBox)
			availableItems.addElement(new Integer(-1));
		// view
		view = new View(this, 0, 0, (EntityBomberman) bombermen.elementAt(0));
		return true;
	}

	protected boolean placeEnemies(Vector emptyPos, int _nEnemy, int _level) {
		EntityBomberman b = (EntityBomberman) bombermen.elementAt(0);
		int bx = (int) (b.pos.x / GRID);
		int by = (int) (b.pos.y / GRID);
		for (int i = 0; i < _nEnemy; i++) {
			if (emptyPos.size() == 0)
				return false;
			int r = Math.abs(new Random().nextInt()) % emptyPos.size();
			Point p = (Point) emptyPos.elementAt(r);
			emptyPos.removeElementAt(r);
			if ((bx - p.x) * (bx - p.x) + (by - p.y) * (by - p.y) > 16)
				enemies.addElement(new EntityEnemy(this, p.x, p.y, _level));
			else
				--i;
		}
		return true;
	}

	protected boolean placeBomberman(Vector emptyPos, int _bomber_type, int _score) {
		Point p = new Point(0, 0);
		while (map.grid[(int) p.y][(int) p.x] != map.GRID_CLEAR || map.getArea((int) p.x, (int) p.y) < 3) {
			if (emptyPos.size() == 0)
				return false;
			int r = Math.abs(new Random().nextInt()) % emptyPos.size();
			p = (Point) emptyPos.elementAt(r);
			emptyPos.removeElementAt(r);
		}
		bombermen.addElement(new EntityBomberman(this, p.x, p.y, _bomber_type, _score));
		return true;
	}

	public void start() {
		resume();
		runner.start();
	}

	public void resume() {
		isRunning = true;
	}

	public void stop() {
		prevUpdateTime = -1;
		isRunning = false;
	}

	public void run() {
		while (isRunning) {
			updateAll();
			paintAll();
			flushGraphics();
			try {
				Thread.sleep(MidletGame.TIME_BETWEEN_FRAMES);
			} catch (Exception ex) {}
		}
	}

	public void paintAll() {
		Graphics g = getGraphics();
		g.setColor(0x222222);
		g.fillRect(0, 0, getWidth(), getHeight());
		// view
		g.translate(0, GRID);
		view.paint(this, g);
		// background
		background.paint(g);
		// vectors
		paintVector(explosions, g);
		paintVector(items, g);
		paintVector(bombs, g);
		paintVector(bombermen, g);
		paintVector(enemies, g);
		// hud
		g.translate(-g.getTranslateX(), -g.getTranslateY());
		g.setColor(0x000000);
		g.fillRect(0, 0, getWidth(), GRID);
		((EntityBomberman) view.target).paintHUD(this, hud);
		g.translate(Math.max(0, getWidth() / 2 - hud.getColumns() * hud.getCellWidth() / 2), 0);
		hud.paint(g);
	}

	public void updateAll() {
		// time delay
		long curUpdateTime = System.currentTimeMillis();
		long actualDelay = MidletGame.TIME_BETWEEN_FRAMES;
		if (prevUpdateTime != -1)
			actualDelay = curUpdateTime - prevUpdateTime;
		double dt = 1.0 * actualDelay / MidletGame.TIME_BETWEEN_FRAMES;
		// background
		for (int i = 0; i < map.grid.length; i++)
			for (int j = 0; j < map.grid[i].length; j++)
				// bombs & explosions
				if (map.grid[i][j] > 2)
					background.setCell(j, i, 1 + 3 * level_type);
				// other
				else
					background.setCell(j, i, map.grid[i][j] + 1 + 3 * level_type);
		// vectors
		view.update(this, dt);
		updateVector(explosions, dt);
		updateVector(items, dt);
		updateVector(bombs, dt);
		updateVector(bombermen, dt);
		updateVector(enemies, dt);
		//
		prevUpdateTime = curUpdateTime;
	}

	private void paintVector(Vector v, Graphics g) {
		for (int i = 0; i < v.size(); i++)
			((Entity) v.elementAt(i)).paint(this, g);
	}

	private void updateVector(Vector v, double dt) {
		for (int i = 0; i < v.size(); i++)
			((Entity) v.elementAt(i)).update(this, dt);
	}

	public Entity collision(Entity e, Vector v) {
		int x = (int) (e.pos.x / GRID);
		int y = (int) (e.pos.y / GRID);
		for (int i = 0; i < v.size(); i++) {
			Entity o = (Entity) v.elementAt(i);
			int xx = (int) (o.pos.x / GRID);
			int yy = (int) (o.pos.y / GRID);
			if (x == xx && y == yy)
				return o;
		}
		return null;
	}

	public boolean posHas(Entity e, int t) {
		int xx = (int) (e.pos.x / GRID);
		int yy = (int) (e.pos.y / GRID);
		return map.grid[yy][xx] == t;
	}

	public void placeItem(int x, int y) {
		if (availableItems.size() == 0)
			return;
		int r = Math.abs(new Random().nextInt()) % availableItems.size();
		int i = ((Integer) availableItems.elementAt(r)).intValue();
		availableItems.removeElementAt(r);
		if (i == -1)
			return;
		items.addElement(new EntityItem(this, x, y, i));
	}

}
