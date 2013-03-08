package utils;

import javax.microedition.lcdui.Image;

public class Images {
	private static Image original;
	private static int[] rawInput, rawOutput;
	private static int newWidth, newHeight;

	public static void initScaleImage(Image _original, int _newWidth, int _newHeight) {
		original = _original;
		newWidth = _newWidth;
		newHeight = _newHeight;		
		rawInput = new int[original.getHeight() * original.getWidth()];
		rawOutput = new int[newWidth * newHeight];
	}

	public static Image scaleImage() {
		original.getRGB(rawInput, 0, original.getWidth(), 0, 0, original.getWidth(), original.getHeight());

		// YD compensates for the x loop by subtracting the width back out
		int YD = (original.getHeight() / newHeight) * original.getWidth() - original.getWidth();
		int YR = original.getHeight() % newHeight;
		int XD = original.getWidth() / newWidth;
		int XR = original.getWidth() % newWidth;
		int outOffset = 0;
		int inOffset = 0;

		for (int y = newHeight, YE = 0; y > 0; y--) {
			for (int x = newWidth, XE = 0; x > 0; x--) {
				rawOutput[outOffset++] = rawInput[inOffset];
				inOffset += XD;
				XE += XR;
				if (XE >= newWidth) {
					XE -= newWidth;
					inOffset++;
				}
			}
			inOffset += YD;
			YE += YR;
			if (YE >= newHeight) {
				YE -= newHeight;
				inOffset += original.getWidth();
			}
		}
		return Image.createRGBImage(rawOutput, newWidth, newHeight, false);
	}
}
