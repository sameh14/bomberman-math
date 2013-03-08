package screens;

import java.util.Random;
import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.game.TiledLayer;

import resource.ResMgr;

import com.sun.midp.midlet.Selector;

import utils.Arrays;
import utils.Values;

public class CanvasQuiz extends GameCanvas implements Runnable {

	public static final int OPR_PLUS = 10, OPR_MINUS = 11, OPR_MUL = 12, OPR_EQ = 13, OPR_UNK = 19;

	protected Thread runner = new Thread(this);
	protected boolean isRunning = false;
	protected ResMgr resources;
	protected Bomberman midlet;

	protected TiledLayer quiz;
	protected int[] quiz_array = new int[6];
	protected int hidden_value, counter;

	protected CanvasQuiz(boolean suppressKeyEvents, Bomberman _midlet, ResMgr _resources) {
		super(suppressKeyEvents);
		setFullScreenMode(true);
		resources = _resources;
		midlet = _midlet;
		init();
	}

	protected void init() {
		counter = 40;
		// chars
		resources.loadImage("sprites/powerups-16x16.png");
		Image imChars = resources.getImage("sprites/powerups-16x16.png");
		quiz = new TiledLayer(6, 1, imChars, 16, 16);
		quiz.setVisible(true);
		// shuffle
		int[] v = new int[9];
		for (int i = 0; i < 9; i++)
			v[i] = i + 1;
		Arrays.shuffle(v);
		// pick quiz
		int a = v[0];
		int b = v[1];
		if (a < b)
			a += b - (b = a);
		int o = 10 + v[2] % 3;
		int r = operate(a, b, o);
		int h = v[3] % 2;
		if (r / 10 == 0)
			h = 1;
		if (h == 0)
			hidden_value = r / 10;
		else
			hidden_value = r % 10;
		// array
		quiz_array[0] = 21 + a;
		quiz_array[1] = 21 + o;
		quiz_array[2] = 21 + b;
		quiz_array[3] = 21 + OPR_EQ;
		quiz_array[4] = 21 + (h == 0 ? OPR_UNK : (r / 10));
		quiz_array[5] = 21 + (h == 1 ? OPR_UNK : (r % 10));
		// build tile
		for (int i = 0; i < quiz_array.length; i++)
			quiz.setCell(i, 0, quiz_array[i]);
	}

	protected int operate(int a, int b, int o) {
		switch (o) {
		case OPR_PLUS:
			return a + b;
		case OPR_MINUS:
			return a - b;
		case OPR_MUL:
			return a * b;
		default:
			return a;
		}
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
		// title
		g.setColor(0xFFFFFF);
		g.drawString("find the missing decimal digit", getWidth() / 2, 0, Graphics.HCENTER | Graphics.TOP);
		// chars
		g.translate(getWidth() / 2 - quiz.getColumns() * quiz.getCellWidth() / 2, getHeight() / 2 - quiz.getCellHeight() / 2);
		quiz.paint(g);
		// title
		if (counter < 0 && Math.abs(counter) % 20 < 10) {
			g.translate(-g.getTranslateX(), -g.getTranslateY());
			g.setColor(0xFFFFFF);
			g.drawString("press to continue", getWidth() / 2, getHeight(), Graphics.HCENTER | Graphics.BOTTOM);
		}
	}

	protected void updateAll() {
		--counter;
		if (counter < 0 && getKeyStates() != 0)
			midlet.gotoLevel(quiz_array, hidden_value);
	}
}
