package game;

import java.util.Random;
import java.util.Vector;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import screens.CanvasGame;

public class EntityEnemy extends Entity {

	public static final int STATE_WALK = 0, STATE_BLOW = 1;
	protected int[][] SEQ = { { 0, 1, 2 }, { 3 } };

	protected int self_type, self_speed, dir = -1, state = -1, dead_counter = 0;

	public EntityEnemy(CanvasGame game, double x, double y, int type_num) {
		super(game, (int) x * game.GRID + game.GRID / 2, (int) y * game.GRID + game.GRID / 2);
		self_type = type_num;
		self_speed = 1 + self_type / 2;
		game.resources.loadImage("sprites/enemy" + self_type + "-16x20.png");
		Image im = game.resources.getImage("sprites/enemy" + self_type + "-16x20.png");
		sprite = new Sprite(im, 16, 20);
		sprite.defineReferencePixel(8, 12);
		spriteSpeed = 0.1;
		setState(STATE_WALK);
	}

	public void update(CanvasGame game, double dt) {
		super.update(game, dt);
		// MAKE DECISION
		if ((pos.x - game.GRID / 2) % game.GRID <= 2 && (pos.y - game.GRID / 2) % game.GRID <= 2) {
			// find possible directions
			Vector possible = new Vector();
			for (int i = 0; i < Map.mv.length; i++) {
				pos.x += Map.mv[i].x * game.GRID;
				pos.y += Map.mv[i].y * game.GRID;
				if (game.posHas(this, Map.GRID_CLEAR)) {
					possible.addElement(new Integer(i));
					if (i == dir)
						for (int j = 0; j < 8; j++)
							possible.addElement(new Integer(i));
				}
				pos.x -= Map.mv[i].x * game.GRID;
				pos.y -= Map.mv[i].y * game.GRID;
			}
			// pick one
			if (possible.size() > 0) {
				int r = Math.abs(new Random().nextInt()) % possible.size();
				dir = ((Integer) possible.elementAt(r)).intValue();
			} else
				dir = -1;
		}
		// MOVE
		if (dir != -1) {
			pos.x += Map.mv[dir].x * self_speed * dt;
			pos.y += Map.mv[dir].y * self_speed * dt;
		}
		// DEAD
		if (state == STATE_BLOW) {
			dead_counter--;
			if (dead_counter < 0)
				game.enemies.removeElement(this);
		}
		// EXPLOSION
		else {
			EntityExplosion explosion = (EntityExplosion) game.collision(this, game.explosions);
			if (explosion != null)
				blow(game, explosion);
		}
	}

	private void blow(CanvasGame game, EntityExplosion explosion) {
		setState(STATE_BLOW);
		dir = -1;
		dead_counter = 10;
		explosion.bomber.score += game.SCORE_ENEMY * (self_type + 1);
		game.resources.playWav("audio/pop.wav");
	}

	protected void setState(int _state) {
		if (state != _state)
			sprite.setFrameSequence(SEQ[_state]);
		state = _state;
	}

	public void paint(CanvasGame game, Graphics g) {
		// g.setColor(0xFF0000);
		// g.drawRect(pos.x / game.GRID * game.GRID, pos.y / game.GRID *
		// game.GRID, game.GRID, game.GRID);
		sprite.setPosition((int) pos.x - 8, (int) pos.y - 12);
		sprite.paint(g);
	}

}
