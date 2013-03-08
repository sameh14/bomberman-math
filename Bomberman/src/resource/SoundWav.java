package resource;

import java.io.InputStream;

import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;

public class SoundWav {
	InputStream is;
	Player player;

	public SoundWav(String file) {
		try {
			is = getClass().getResourceAsStream(file);
			player = Manager.createPlayer(is, "audio/x-wav");
			player.addPlayerListener(new PlayerListener() {
				public void playerUpdate(Player arg0, String arg1, Object arg2) {
					System.out.println(arg0 + arg1);
				}
			});
		} catch (Exception e) {
			System.out.println("CANT INITIALIZE");
			e.printStackTrace();
		}
	}

	public void start() {
		try {
			if (player != null) {
				if (player.getState() == Player.UNREALIZED)
					player.realize();
				if (player.getState() == Player.REALIZED)
					player.prefetch();
//				if (player.getState() == Player.STARTED)
//					player.stop();
				if (player.getState() == Player.PREFETCHED)
					player.start();
			}
		} catch (Exception e) {
			System.out.println("CANT START");
			e.printStackTrace();
		}
	}

	public void destroy() {
		try {
			player.close();
		} catch (Exception e) {
			System.out.println("CANT DESTROY");
			e.printStackTrace();
		}
	}
}