package screens;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.game.TiledLayer;

import resource.ResMgr;

import com.sun.midp.midlet.Selector;

import utils.Values;

public class CanvasCharSelect extends GameCanvas implements Runnable {

	public static final String[] char_name = { "white bomberman", "black bomber", "pretty bomber", "racer", "bagura", "none" };

	protected Thread runner = new Thread(this);
	protected boolean isRunning = false;
	protected ResMgr resources;
	protected MidletGame midlet;

	protected TiledLayer chars;
	protected int cursor = 0, num_chars_available, key_delay = 0, cheat_counter = 0;

	protected CanvasCharSelect(boolean suppressKeyEvents, MidletGame _midlet, ResMgr _resources, int _num_chars_available) {
		super(suppressKeyEvents);
		setFullScreenMode(true);
		resources = _resources;
		midlet = _midlet;
		num_chars_available = _num_chars_available;
		init();
	}

	protected void init() {
		// chars
		resources.loadImage("sprites/chars-48x48.png");
		Image imChars = resources.getImage("sprites/chars-48x48.png");
		chars = new TiledLayer(3, 2, imChars, 48, 48);
		chars.setVisible(true);
	}

	public void start() {
		resume();
		runner.start();
	}

	public void resume() {
		isRunning = true;
	}

	public void stop() {
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

	protected void paintAll() {
		// bg
		Graphics g = getGraphics();
		g.setColor(0x222222);
		g.fillRect(0, 0, getWidth(), getHeight());
		// title
		g.setColor(0xFFFFFF);
		g.drawString("select a character", getWidth() / 2, 0, Graphics.HCENTER | Graphics.TOP);
		// chars
		g.translate(getWidth() / 2 - chars.getCellWidth() * chars.getColumns() / 2, getHeight() / 2 - chars.getCellHeight() * chars.getRows() / 2);
		drawChars(g);
		g.translate(-g.getTranslateX(), -g.getTranslateY());
		// names
		g.setColor(0xFFFFFF);
		g.drawString(char_name[getSelectedChar()], getWidth() / 2, getHeight(), Graphics.HCENTER | Graphics.BOTTOM);
	}

	protected int getSelectedChar() {
		return (int) Values.bound(cursor, 0, num_chars_available - 1);
	}

	protected void drawChars(Graphics g) {
		// BORDER
		int x = cursor % 3;
		int y = cursor / 3;
		g.setColor(Math.cos(Math.abs(key_delay * 0.5)) > 0 ? 0xFFAA00 : 0x22DD00);
		g.fillRect(x * 48 + 2, y * 48 + 2, 48 - 4, 48 - 4);
		// BACK
		for (int i = 0; i < num_chars_available; i++) {
			int xi = i % 3;
			int yi = i / 3;
			g.setColor(cursor == i ? 0x2288FF : 0x224488);
			g.fillRect(xi * 48 + 4, yi * 48 + 4, 48 - 8, 48 - 8);
		}
		for (int i = num_chars_available; i < 6; i++) {
			int xi = i % 3;
			int yi = i / 3;
			g.setColor(0x444444);
			g.fillRect(xi * 48 + 4, yi * 48 + 4, 48 - 8, 48 - 8);
		}
		// PHOTO
		chars.paint(g);
	}

	protected void updateAll() {
		// CHARS
		for (int i = 0; i < num_chars_available; i++)
			chars.setCell(i % 3, i / 3, i * 2 + 1);
		if (cursor < num_chars_available)
			chars.setCell(cursor % 3, cursor / 3, cursor * 2 + 2);
		for (int i = num_chars_available; i < 6; i++)
			chars.setCell(i % 3, i / 3, 0);
		// CHEAT
		if ((getKeyStates() ^ (DOWN_PRESSED | RIGHT_PRESSED)) == 0)
			cheat_counter--;
		else
			cheat_counter = 100;
		if (cheat_counter < 0) {
			cheat_counter = 100;
			num_chars_available = 5;
			resources.playWav("audio/choose.wav");
		}
		// KEYS
		--key_delay;
		if (key_delay < 0) {
			// QUIT
			if ((getKeyStates() & GAME_A_PRESSED) != 0)
				midlet.quit();
			// SELECT
			if ((getKeyStates() & FIRE_PRESSED) != 0)
				midlet.gotoQuiz(0, getSelectedChar(), 0, num_chars_available);
			// MOVE
			int currState = getKeyStates() & ~FIRE_PRESSED;
			if ((currState & DOWN_PRESSED) != 0)
				moveCursor(3);
			else if ((currState & RIGHT_PRESSED) != 0)
				moveCursor(1);
			else if ((currState & UP_PRESSED) != 0)
				moveCursor(-3);
			else if ((currState & LEFT_PRESSED) != 0)
				moveCursor(-1);
		}
	}

	protected void moveCursor(int i) {
		int old_cursor = cursor;
		key_delay = 6;
		cursor += i;
		cursor = (cursor + 6) % 6;
		cursor %= num_chars_available;
		if (old_cursor != cursor)
			resources.playWav("audio/select.wav");
	}
}
