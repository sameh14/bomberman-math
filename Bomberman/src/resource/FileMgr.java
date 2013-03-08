package resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

public class FileMgr {

	public static String read(String path) {
		InputStream is = null;
		FileConnection fconn = null;
		String str = "";
		try {
			String sdcard = System.getProperty("fileconn.dir.memorycard");
			if (sdcard == null)
				return null;
			fconn = (FileConnection) Connector.open(sdcard + path, Connector.READ_WRITE);
			if (fconn.exists()) {
				int size = (int) fconn.fileSize();
				is = fconn.openInputStream();
				byte bytes[] = new byte[size];
				is.read(bytes, 0, size);
				str = new String(bytes, 0, size);
				return str;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != is)
					is.close();
				if (null != fconn)
					fconn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String write(String path, String text) {
		OutputStream os = null;
		FileConnection fconn = null;
		try {
			String sdcard = System.getProperty("fileconn.dir.memorycard");
			if (sdcard == null)
				return null;
			fconn = (FileConnection) Connector.open(sdcard + path, Connector.READ_WRITE);
			if (!fconn.exists())
				fconn.create();
			os = fconn.openDataOutputStream();
			os.write(text.getBytes());
			fconn.setHidden(false);
			return fconn.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != os)
					os.close();
				if (null != fconn)
					fconn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
