package game;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import screens.CanvasGame;
import utils.Point;
import utils.Values;

public class EntityBomb extends Entity {

	public static int[] SEQ = { 0, 1, 2, 2 };

	protected double counter = 60;
	protected Point spd = new Point(0, 0);
	// attr
	public int range = 1;
	public boolean pierce, roll;
	public EntityBomberman bomber;

	public EntityBomb(CanvasGame game, double x, double y, EntityBomberman _bomber) {
		super(game, x * game.GRID, y * game.GRID);
		spriteSpeed = 0.05;
		game.resources.loadImage("sprites/bomb-16x16.png");
		Image im = game.resources.getImage("sprites/bomb-16x16.png");
		sprite = new Sprite(im, 16, 16);
		sprite.setFrameSequence(SEQ);
		// bomber
		bomber = _bomber;
		range = bomber.bomb_range;
		pierce = bomber.bomb_pierce;
		roll = bomber.bomb_roll;
		if (roll)
			spriteSpeed = 0;
		spd = Map.mv[bomber.dir];
	}

	public void update(CanvasGame game, double dt) {
		// UPDATE
		super.update(game, dt);
		// ROLL
		if (roll) {
			pos.x += spd.x * 4 * dt;
			pos.y += spd.y * 4 * dt;
			if (!game.posHas(this, Map.GRID_CLEAR)) {
				while (game.posHas(this, Map.GRID_WALL)) {
					pos.x -= spd.x * 4;
					pos.y -= spd.y * 4;
				}
				explode(game);
			} else if (game.collision(this, game.enemies) != null)
				explode(game);
		}
		// CHAIN REACTION
		if (game.posHas(this, Map.GRID_EXPLOSION))
			explode(game);
		if (!roll) {
			// BLOCK
			if (sprite.getFrame() > 0)
				game.map.grid[(int) (pos.y / game.GRID)][(int) (pos.x / game.GRID)] = Map.GRID_BOMB;
			// EXPLODE
			counter -= dt;
			if (counter <= 0)
				explode(game);
		}
	}

	public void explode(CanvasGame game) {
		game.resources.playWav("audio/explosion.wav");
		placeExplosion(game, pos.x / game.GRID, pos.y / game.GRID, 6);
		for (int i = 0; i < Map.mv.length; i++) {
			boolean tip = true;
			int j;
			for (j = 1; j < range && tip; j++) {
				int x = (int) (pos.x / game.GRID + Map.mv[i].x * j);
				int y = (int) (pos.y / game.GRID + Map.mv[i].y * j);
				if (game.map.grid[y][x] == Map.GRID_WALL || (!pierce && game.map.grid[y][x] == Map.GRID_BOX))
					tip = false;
				placeExplosion(game, x, y, 4 + (i % 2));
			}
			if (tip)
				placeExplosion(game, pos.x / game.GRID + Map.mv[i].x * j, pos.y / game.GRID + Map.mv[i].y * j, i);
		}
		game.bombs.removeElement(this);
		game.view.shake(game, 0.4 / 9 * range);
	}

	protected boolean placeExplosion(CanvasGame game, double xd, double yd, int type) {
		int x = (int) xd;
		int y = (int) yd;
		if (game.map.grid[y][x] == Map.GRID_WALL)
			return false;
		if (game.map.grid[y][x] == Map.GRID_BOX) {
			game.placeItem(x, y);
			bomber.score += game.SCORE_BOX;
		}
		EntityExplosion myExplosion = new EntityExplosion(game, x, y, type);
		myExplosion.bomber = bomber;
		game.explosions.addElement(myExplosion);
		return true;
	}

	public void paint(CanvasGame game, Graphics g) {
		super.paint(game, g);
	}
}
