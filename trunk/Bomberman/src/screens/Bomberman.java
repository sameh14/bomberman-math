package screens;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import resource.FileMgr;
import resource.ResMgr;

public class Bomberman extends MIDlet {

	public static final long FRAMES_PER_SECOND = 15;
	public static final long TIME_BETWEEN_FRAMES = 1000 / FRAMES_PER_SECOND;

	public ResMgr resources = new ResMgr("/");
	protected CanvasCharSelect canvas_selectChar = null;
	protected CanvasQuiz canvas_quiz = null;
	protected CanvasGame canvas_game = null;
	protected CanvasEnd canvas_end = null;
	protected int level, selectedChar, score, num_chars_available = 1;
	private GameCanvas canvas = null;

	public Bomberman() {}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		notifyDestroyed();
	}

	protected void pauseApp() {
		canvas_selectChar.stop();
	}

	protected void startApp() throws MIDletStateChangeException {
		gotoCharSelect();
		loadGame();
		canvas_selectChar.num_chars_available = num_chars_available;
	}

	public void gotoCharSelect() {
		resources.freeAll();
		resources.playWav("audio/bleep.wav");
		if (canvas_end != null) {
			canvas_end.stop();
			canvas_end = null;
		}
		canvas_selectChar = new CanvasCharSelect(true, this, resources, num_chars_available);
		canvas = canvas_selectChar;
		Display.getDisplay(this).setCurrent(canvas_selectChar);
		canvas_selectChar.start();
	}

	public void gotoQuiz(int _level, int _selectedChar, int _score, int _num_chars_available) {
		resources.freeAll();
		resources.playWav("audio/choose.wav");
		if (canvas_game != null) {
			canvas_game.stop();
			canvas_game = null;
		}
		if (canvas_selectChar != null) {
			canvas_selectChar.stop();
			canvas_selectChar = null;
		}
		level = _level;
		selectedChar = _selectedChar;
		score = _score;
		num_chars_available = _num_chars_available;
		canvas_quiz = new CanvasQuiz(true, this, resources);
		canvas = canvas_quiz;
		Display.getDisplay(this).setCurrent(canvas_quiz);
		canvas_quiz.start();
	}

	public void gotoLevel(int[] quiz_array, int hidden_value) {
		resources.freeAll();
		resources.playWav("audio/bleep.wav");
		if (canvas_selectChar != null) {
			canvas_selectChar.stop();
			canvas_selectChar = null;
		}
		canvas_game = new CanvasGame(true, this, resources);
		canvas = canvas_game;
		Display.getDisplay(this).setCurrent(canvas_game);
		while (!canvas_game.init(level, hidden_value, quiz_array, selectedChar, score, level * 2 + 5, 0.5f))
			;
		canvas_game.start();
	}

	public void gotoEnd(String message) {
		resources.freeAll();
		if (canvas_game != null) {
			canvas_game.stop();
			canvas_game = null;
		}
		canvas_end = new CanvasEnd(true, this, resources, score, message);
		canvas = canvas_end;
		Display.getDisplay(this).setCurrent(canvas_end);
		canvas_end.start();
	}

	public void lose(int _score) {
		resources.freeAll();
		resources.playWav("audio/lose.wav");
		score = _score;
		gotoEnd("YOU LOSE");
	}

	public void win(int _score) {
		resources.freeAll();
		resources.playWav("audio/win.wav");
		score = _score;
		++level;
		if (level < 3)
			gotoQuiz(level, selectedChar, score, num_chars_available);
		else {
			gotoEnd("YOU WIN");
			if (num_chars_available < 5 && selectedChar == num_chars_available - 1)
				++num_chars_available;
			saveGame();
		}
	}

	public void quit() {
		resources.freeAll();
		resources.playWav("audio/choose.wav");
		try {
			destroyApp(true);
		} catch (MIDletStateChangeException e) {}
	}

	private void loadGame() {
		try {
			String save = null;
			try {
				save = FileMgr.read("temp/bomberman.sav");
				// LOAD
				num_chars_available = save.charAt(0);
				Display.getDisplay(this).setCurrent(new Alert("", "Loaded from SD card", null, AlertType.INFO), canvas);
			} catch (Exception e) {
				e.printStackTrace();
				saveGame();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Display.getDisplay(this).setCurrent(new Alert("", "Can't load", null, AlertType.INFO), canvas);
		}
	}

	private void saveGame() {
		try {
			String save = ((char) num_chars_available) + "2";
			// SAVE
			FileMgr.write("temp/bomberman.sav", save);
			Display.getDisplay(this).setCurrent(new Alert("", "Saved to SD card", null, AlertType.INFO), canvas);
		} catch (Exception e) {
			e.printStackTrace();
			Display.getDisplay(this).setCurrent(new Alert("", "Can't save", null, AlertType.INFO), canvas);
		}
	}

}
