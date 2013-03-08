package game;

import java.util.Vector;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.game.TiledLayer;

import screens.CanvasGame;
import utils.Point;
import utils.Values;

public class EntityBomberman extends Entity {

	public static final int STATE_STOP = 0, STATE_WALK = 1, STATE_BLOW = 2, STATE_FAINT = 3, STATE_POWER = 4, STATE_WIN = 5, STATE_LOSE = 6;
	// d r u l
	public static final int[][][] SEQ = {
			// STOP
			{ { 0 },
			{ 3 },
			{ 6 },
			{ 9 } },
			// WALK
			{ { 1, 2 },
			{ 4, 5 },
			{ 7, 8 },
			{ 10, 11 } },
			// BLOW
			{ { 12, 13, 14, 14, 14 } },
			// FAINT
			{ { 15, 16, 17, 17, 17 } },
			// POWER
			{ { 18 } },
			// WIN
			{ { 19 } },
			// LOSE
			{ { 20 } } };

	// powerups
	public int bomb_max = 1, bomb_range = 1, self_speed = 2, self_character;
	public boolean bomb_pierce = false, bomb_roll = false;
	// state
	protected int dir = -1, state = STATE_STOP, score;

	public EntityBomberman(CanvasGame game, double x, double y, int character_num, int _score) {
		super(game, x * game.GRID + game.GRID / 2, y * game.GRID + game.GRID / 2);
		self_character = character_num;
		score = _score;
		game.resources.loadImage("sprites/char" + self_character + "-22x24.png");
		Image im = game.resources.getImage("sprites/char" + self_character + "-22x24.png");
		sprite = new Sprite(im, 22, 24);
		sprite.defineReferencePixel(11, 16);
		setState(STATE_STOP, 0);
	}

	public void update(CanvasGame game, double dt) {
		// SPRITE
		spriteSpeed = 0.1 * self_speed;
		switch (state) {
		case STATE_BLOW:
			if (sprite.getFrame() >= 4)
				spriteSpeed = 0;
			break;
		case STATE_FAINT:
			if (sprite.getFrame() >= 4)
				spriteSpeed = 0;
			break;
		}
		// ANY KEY
		if (game.getKeyStates() != 0)
			switch (state) {
			case STATE_BLOW:
				game.midlet.lose(score);
				break;
			case STATE_FAINT:
				game.midlet.lose(score);
				break;
			case STATE_WIN:
				game.midlet.win(score);
				break;
			}
		// FIRE
		if ((game.getKeyStates() & game.FIRE_PRESSED) != 0)
			fire(game);
		// PICK NUMBER
		if ((game.getKeyStates() & game.GAME_A_PRESSED) != 0) {
			EntityItem item = (EntityItem) game.collision(this, game.items);
			if (item != null && item.type <= 9) {
				if (item.type == game.req_num) {
					score += game.SCORE_WIN;
					setState(STATE_WIN, 0);
				} else
					faint();
				return;
			}
		}
		// MOVE
		int currState = game.getKeyStates() & ~game.FIRE_PRESSED & ~game.GAME_A_PRESSED;
		if ((currState & game.DOWN_PRESSED) != 0)
			walk(0);
		else if ((currState & game.RIGHT_PRESSED) != 0)
			walk(1);
		else if ((currState & game.UP_PRESSED) != 0)
			walk(2);
		else if ((currState & game.LEFT_PRESSED) != 0)
			walk(3);
		else if (currState == 0)
			stop(dir);
		// PICKUP
		EntityItem item = (EntityItem) game.collision(this, game.items);
		if (item != null)
			if (item.type > 9)
				item.givePower(game, this);
		// ENEMY
		EntityEnemy enemy = (EntityEnemy) game.collision(this, game.enemies);
		if (enemy != null)
			if (enemy.state != enemy.STATE_BLOW)
				faint();
		// EXPLOSION
		EntityExplosion explosion = (EntityExplosion) game.collision(this, game.explosions);
		if (explosion != null)
			blow();
		// UPDATE
		super.update(game, dt);
		if (state == STATE_WALK) {
			// if can move
			if (empty(Map.mv[dir].x, Map.mv[dir].y, game)) {
				// move
				pos.x += Map.mv[dir].x * self_speed * dt;
				pos.y += Map.mv[dir].y * self_speed * dt;
				// smooth
				if (Map.mv[dir].x == 0)
					pos.x += Values.sign((int) (Values.floor(pos.x, game.GRID) + game.GRID / 2) - (int) pos.x);
				if (Map.mv[dir].y == 0)
					pos.y += Values.sign((int) (Values.floor(pos.y, game.GRID) + game.GRID / 2) - (int) pos.y);
			}
			// can't
			else
				stop(dir);
		}
	}

	public void faint() {
		if (state == STATE_STOP || state == STATE_WALK || state == STATE_POWER)
			setState(STATE_FAINT, 0);
	}

	public void blow() {
		if (state == STATE_STOP || state == STATE_WALK || state == STATE_POWER)
			setState(STATE_BLOW, 0);
	}

	public void stop(int i) {
		if (state == STATE_STOP || state == STATE_WALK || state == STATE_POWER)
			setState(STATE_STOP, i);
	}

	public void walk(int i) {
		if (state == STATE_STOP || state == STATE_WALK || state == STATE_POWER)
			setState(STATE_WALK, i);
	}

	protected boolean empty(double x, double y, CanvasGame game) {
		int xx = Values.floor(pos.x + x * game.GRID / 2, game.GRID) / game.GRID;
		int yy = Values.floor(pos.y + y * game.GRID / 2, game.GRID) / game.GRID;
		return game.map.grid[yy][xx] == Map.GRID_CLEAR || game.map.grid[yy][xx] == Map.GRID_EXPLOSION;
	}

	public void fire(CanvasGame game) {
		if (!(state == STATE_STOP || state == STATE_WALK || state == STATE_POWER))
			return;
		// THROW BOMB
		int nBomb = 0;
		for (int i = 0; i < game.bombs.size(); i++) {
			EntityBomb b = (EntityBomb) game.bombs.elementAt(i);
			if (b.pos.sqrDist(pos) < game.GRID * game.GRID)
				return;
			if (b.bomber == this)
				nBomb++;
		}
		if (nBomb < bomb_max && game.collision(this, game.bombs) == null)
			game.bombs.addElement(new EntityBomb(game, (int) (pos.x / game.GRID), (int) (pos.y / game.GRID), this));
	}

	protected void setState(int _state, int _dir) {
		if (state != _state || dir != _dir % 4)
			sprite.setFrameSequence(SEQ[_state][_dir % 4]);
		state = _state;
		dir = _dir % 4;
	}

	public void paint(CanvasGame game, Graphics g) {
		// g.setColor(0xFF0000);
		// g.drawRect(pos.x / game.GRID * game.GRID, pos.y / game.GRID *
		// game.GRID, game.GRID, game.GRID);
		sprite.setPosition((int) (pos.x - 11), (int) (pos.y - 16));
		sprite.paint(g);
	}

	public void paintHUD(CanvasGame game, TiledLayer hud) {
		// hud
		hud.setCell(6, 0, (bomb_roll ? 38 : 0));
		hud.setCell(7, 0, 36);
		hud.setCell(8, 0, 20 + bomb_max + 1);
		hud.setCell(9, 0, 35);
		hud.setCell(10, 0, 20 + bomb_range + 1);
		hud.setCell(11, 0, 37);
		hud.setCell(12, 0, 20 + self_speed + 1);
		hud.setCell(13, 0, (bomb_pierce ? 39 : 0));
		// score
		int s = score;
		for (int i = 0; i < 4; i++) {
			hud.setCell(17 - i, 0, 20 + s % 10 + 1);
			s /= 10;
		}
	}
}
