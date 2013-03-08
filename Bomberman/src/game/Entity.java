package game;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import screens.CanvasGame;
import utils.Point;

public abstract class Entity {
	public Point pos;
	protected Sprite sprite;
	protected double spriteIndex = 0, spriteSpeed = 1;

	public Entity(CanvasGame game, double x, double y) {
		pos = new Point(x, y);
	}

	public void update(CanvasGame game, double dt) {
		spriteIndex += dt * spriteSpeed;
		while (spriteIndex >= 1) {
			sprite.nextFrame();
			spriteIndex--;
		}
	}

	public void paint(CanvasGame game, Graphics g) {
		sprite.setPosition((int) pos.x, (int) pos.y);
		sprite.paint(g);
	}
}