package game;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import screens.CanvasGame;
import utils.Point;

public class EntityExplosion extends Entity {

	protected int[] SEQ = { 2, 0, 1, 2, 3, 3, 3 };

	protected double counter = 15;
	protected int type = 0;
	public EntityBomberman bomber;

	public EntityExplosion(CanvasGame game, int x, int y, int type) {
		super(game, x * game.GRID, y * game.GRID);
		spriteSpeed = 0.3;
		game.resources.loadImage("sprites/explosion-16x16.png");
		Image im = game.resources.getImage("sprites/explosion-16x16.png");
		sprite = new Sprite(im, 16, 16);
		for (int i = 0; i < SEQ.length; i++)
			SEQ[i] += type * 4;
		sprite.setFrameSequence(SEQ);
		game.map.grid[y][x] = Map.GRID_EXPLOSION;
	}

	public void update(CanvasGame game, double dt) {
		// UPDATE
		super.update(game, dt);
		counter -= dt;
		if (counter <= 0)
			fade(game);
	}

	public void fade(CanvasGame game) {
		game.explosions.removeElement(this);
		game.map.grid[(int) (pos.y / game.GRID)][(int) (pos.x / game.GRID)] = Map.GRID_CLEAR;
	}

	public void paint(CanvasGame game, Graphics g) {
		super.paint(game, g);
	}
}
