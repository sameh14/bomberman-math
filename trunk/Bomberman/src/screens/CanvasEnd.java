package screens;

import java.util.Random;

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

public class CanvasEnd extends GameCanvas implements Runnable {

	public static final int OPR_AND = 10, OPR_OR = 11, OPR_XOR = 12, OPR_EQ = 13, OPR_UNK = 19;

	protected Thread runner = new Thread(this);
	protected boolean isRunning = false;
	protected ResMgr resources;
	protected Bomberman midlet;

	protected int counter;
	protected TiledLayer score, back;
	protected String message;

	protected CanvasEnd(boolean suppressKeyEvents, Bomberman _midlet, ResMgr _resources, int _score, String _message) {
		super(suppressKeyEvents);
		setFullScreenMode(true);
		resources = _resources;
		midlet = _midlet;
		message = _message;
		init(_score);
	}

	protected void init(int _score) {
		counter = 40;
		// score
		resources.loadImage("sprites/powerups-16x16.png");
		Image imChars = resources.getImage("sprites/powerups-16x16.png");
		score = new TiledLayer(4, 1, imChars, 16, 16);
		score.setVisible(true);
		int s = _score;
		for (int i = 0; i < 4; i++) {
			score.setCell(3 - i, 0, 20 + s % 10 + 1);
			s /= 10;
		}
		// back
		resources.loadImage("sprites/end-176x128.png");
		Image imBack = resources.getImage("sprites/end-176x128.png");
		back = new TiledLayer(1, 1, imBack, 176, 128);
		back.setVisible(true);
		int r = Math.abs(new Random().nextInt()) % 3;
		if (message.charAt(5) < ('A' + 'Z') / 2)
			back.setCell(0, 0, r + 4);
		else
			back.setCell(0, 0, r + 1);
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
				Thread.sleep(Bomberman.TIME_BETWEEN_FRAMES);
			} catch (Exception ex) {}
		}
	}

	protected void paintAll() {
		// bg
		Graphics g = getGraphics();
		g.setColor(0x222222);
		g.fillRect(0, 0, getWidth(), getHeight());
		// back
		int osc = (int) (8 * Math.sin(0.1 * counter));
		g.translate(getWidth() / 2 - back.getCellWidth() / 2, getHeight() / 2 - back.getCellHeight() / 2 + osc);
		back.paint(g);
		g.translate(-g.getTranslateX(), -g.getTranslateY());
		// title
		g.setColor(0xFFFFFF);
		g.drawString("your final score is", getWidth() / 2, 0, Graphics.HCENTER | Graphics.TOP);
		// score
		g.translate(getWidth() / 2 - score.getColumns() * score.getCellWidth() / 2, 32);
		score.paint(g);
		// title
		if (Math.abs(counter) % 20 < 10) {
			g.translate(-g.getTranslateX(), -g.getTranslateY());
			g.setColor(0xFFFFFF);
			g.drawString(message, getWidth() / 2, getHeight(), Graphics.HCENTER | Graphics.BOTTOM);
		}
	}

	protected void updateAll() {
		--counter;
		if (counter < 0 && getKeyStates() != 0)
			midlet.gotoCharSelect();
	}
}
