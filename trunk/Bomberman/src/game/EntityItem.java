package game;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import screens.CanvasGame;
import utils.Values;

public class EntityItem extends Entity {

	public static final int FIRE_UP = 10, FIRE_DOWN = 11, BOMB_UP = 12, BOMB_DOWN = 13, SPEED_UP = 14, SPEED_DOWN = 15;
	public static final int POWER_BOMB = 16, ROLL_BOMB = 17, NO_ROLL_BOMB = 18, PIERCE_BOMB = 19;

	public int type, counter;

	public EntityItem(CanvasGame game, int x, int y, int _type) {
		super(game, x * game.GRID, y * game.GRID);
		type = _type;
		game.resources.loadImage("sprites/powerups-16x16.png");
		Image im = game.resources.getImage("sprites/powerups-16x16.png");
		sprite = new Sprite(im, 16, 16);
		sprite.setFrameSequence(new int[] { type });
		counter = 400;
	}

	public void update(CanvasGame game, double dt) {}

	public void paint(CanvasGame game, Graphics g) {
		if (type < 10)
			super.paint(game, g);
		else {
			--counter;
			if (counter > 100)
				super.paint(game, g);
			else if (Math.abs(counter) % 10 < 5)
				super.paint(game, g);
			if (counter < 0)
				game.items.removeElement(this);
		}
	}

	public String toString() {
		return type + "";
	}

	public void givePower(CanvasGame game, EntityBomberman e) {
		switch (type) {
		case FIRE_UP:
			e.bomb_range++;
			e.bomb_range = (int) Values.bound(e.bomb_range, 1, 9);
			break;
		case FIRE_DOWN:
			e.bomb_range--;
			e.bomb_range = (int) Values.bound(e.bomb_range, 1, 9);
			break;
		case BOMB_UP:
			e.bomb_max++;
			e.bomb_max = (int) Values.bound(e.bomb_max, 1, 9);
			break;
		case BOMB_DOWN:
			e.bomb_max--;
			e.bomb_max = (int) Values.bound(e.bomb_max, 1, 9);
			break;
		case SPEED_UP:
			e.self_speed++;
			e.self_speed = (int) Values.bound(e.self_speed, 1, 6);
			break;
		case SPEED_DOWN:
			e.self_speed--;
			e.self_speed = (int) Values.bound(e.self_speed, 1, 6);
		case POWER_BOMB:
			e.bomb_range = 9;
			break;
		case ROLL_BOMB:
			e.bomb_roll = true;
			break;
		case NO_ROLL_BOMB:
			e.bomb_roll = false;
			break;
		case PIERCE_BOMB:
			e.bomb_pierce = true;
			break;
		default:
			break;
		}
		game.items.removeElement(this);
		e.score += game.SCORE_ITEM;
		game.resources.playWav("audio/collect.wav");
	}
}
