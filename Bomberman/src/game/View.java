package game;

import java.util.Random;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.GameCanvas;

import screens.CanvasGame;
import utils.Values;

public class View extends Entity {

	public Entity target;
	protected double shake_value = 0, counter = 0;

	public View(CanvasGame game, int x, int y, Entity _target) {
		super(game, x, y);
		target = _target;
	}

	public void update(CanvasGame game, double dt) {
		// x
		double x = target.pos.x - game.getWidth() / 2;
		if (x > (game.map.width() + 1) * game.GRID - game.getWidth())
			x = (game.map.width() + 1) * game.GRID - game.getWidth();
		if (x < 0)
			x = 0;
		// y
		double y = target.pos.y - game.getHeight() / 2;
		if (y > (game.map.height() + 1) * game.GRID - game.getHeight())
			y = (game.map.height() + 1) * game.GRID - game.getHeight();
		if (y < 0)
			y = 0;
		// update
		counter += 4;
		shake_value *= 0.9;
		int r = (int) (shake_value * Math.sin(counter) * game.getHeight() / 4);
		pos.x = x;
		pos.y = (int) (y + r);
	}

	public void paint(CanvasGame game, Graphics g) {
		g.translate((int) -pos.x, (int) -pos.y);
	}

	public void shake(CanvasGame game, double _shake) {
		Display.getDisplay(game.midlet).vibrate((int) (2000 * _shake));
		shake_value = _shake;
	}

}