package resource;

import java.io.*;
import java.lang.ref.WeakReference;
import java.util.*;

import javax.microedition.lcdui.*;
import javax.microedition.media.*;
import javax.microedition.media.control.*;

public class ResMgr {
	protected static String resDir;
	protected Hashtable nameToRes = new Hashtable();

	public ResMgr(String rootDir) {
		resDir = rootDir;
	}

	public boolean loadImage(String name) {
		if (nameToRes.get(name) != null)
			return false;
		try {
			Image img = Image.createImage(resDir + name);
			nameToRes.put(name, img);
			return true;
		} catch (Exception ioex) {}
		return false;
	}

	public Image getImage(String name) {
		return (Image) nameToRes.get(name);
	}

	public boolean playWav(String name) {
		if (nameToRes.get(name) == null)
			try {
				SoundWav s = new SoundWav(resDir + name);
				nameToRes.put(name, s);
			} catch (Exception e) {}
		try {
			SoundWav s = (SoundWav) nameToRes.get(name);
			s.start();
			return true;
		} catch (Exception e) {}
		return false;
	}

	public void freeAll() {
		Enumeration e = nameToRes.keys();
		while (e.hasMoreElements()) {
			String k = (String) e.nextElement();
			Object o = nameToRes.get(k);
			if (o instanceof SoundWav)
				((SoundWav) o).destroy();
			nameToRes.remove(k);
		}
		nameToRes.clear();
		System.gc();
	}
}
