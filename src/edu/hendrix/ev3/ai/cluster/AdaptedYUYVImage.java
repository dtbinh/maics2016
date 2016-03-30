package edu.hendrix.ev3.ai.cluster;

import java.util.function.IntBinaryOperator;

import edu.hendrix.ev3.util.DeepCopyable;
import edu.hendrix.ev3.util.Util;
import javafx.scene.paint.Color;
import edu.hendrix.ev3.YUYVImage;

public class AdaptedYUYVImage extends YUYVImage implements DeepCopyable<AdaptedYUYVImage>, Clusterable<AdaptedYUYVImage> {
	private byte[] pix;
	
	public static byte[] pixelCopy(byte[] pixels) {
		byte[] newPix = new byte[pixels.length];
		for (int i = 0; i < pixels.length; i++) {
			newPix[i] = pixels[i];
		}
		return newPix;
	}
	
	public AdaptedYUYVImage(int width, int height) {
		this(new byte[width*height*2], width, height);
	}
	
	public AdaptedYUYVImage(byte[] pix, int width, int height) {
		super(pix, width, height);
		this.pix = pix;
	}
	
	public AdaptedYUYVImage(AdaptedYUYVImage other) {
		this(pixelCopy(other.getBytes()), other.getWidth(), other.getHeight());
	}
	
	public AdaptedYUYVImage(YUYVPixelMaker creator, int width, int height) {
		this(width, height);
		for (int i = 0; i < pix.length; i++) {
			int x = (i % (width*2)) / 2;
			int y = i / (width*2);
			YUV what = i % 2 == 0 ? YUV.Y : i % 4 == 1 ? YUV.U : YUV.V;
			pix[i] = creator.apply(x, y, what);
		}
	}
	
	public static AdaptedYUYVImage fromString(String src) {
		String[] nums = src.split(" ");
		int width = Integer.parseInt(nums[0]);
		int height = Integer.parseInt(nums[1]);
		int numPix = nums.length - 2;
		if (width * height * 2 != numPix) {
			throw new IllegalArgumentException(String.format("Badly formatted AdaptedYUYVImage: w:%d h:%d p:%d", width, height, numPix));
		}
		
		byte[] pix = new byte[numPix];
		for (int i = 0; i < numPix; i++) {
			pix[i] = Byte.parseByte(nums[i+2]);
		}
		return new AdaptedYUYVImage(pix, width, height);
	}
	
	public byte[] getBytes() {
		byte[] result = new byte[pix.length];
		for (int i = 0; i < pix.length; i++) {
			result[i] = pix[i];
		}
		return result;
	}
	
	int getColumn(int pixel) {
		return pixel % (getWidth() * 2);
	}
	
	int getRow(int pixel) {
		return pixel / (getWidth() * 2);
	}
	
	public AdaptedYUYVImage shrunken(int shrinkFactor) {
		Util.assertArgument(getWidth() % shrinkFactor == 0 && getHeight() % shrinkFactor == 0, "Uneven shrinkage: " + shrinkFactor);
		int totalShrink = shrinkFactor*shrinkFactor;
		byte[] shrunken = new byte[pix.length / totalShrink];
		int p = 0;
		for (int i = 0; i < pix.length; i++) {
			if (getRow(i) % shrinkFactor == 0 && getColumn(i) % (shrinkFactor * 2) <= 1) {
				shrunken[p++] = pix[i];
			}
		}
		return new AdaptedYUYVImage(shrunken, getWidth() / shrinkFactor, getHeight() / shrinkFactor);
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(getWidth());
		result.append(" ");
		result.append(getHeight());
		for (int i = 0; i < pix.length; i++) {
			result.append(' ');
			result.append(pix[i]);
		}
		return result.toString();
	}
	
	@Override
	public int getY(int x, int y) {
		return super.getY(x, y) & 0xFF;
	}
	
	@Override
	public int getU(int x, int y) {
		return super.getU(x, y) & 0xFF;
	}
	
	@Override
	public int getV(int x, int y) {
		return super.getV(x, y) & 0xFF;
	}
	
	public static int clamp(int value) {
		return Math.min(255, Math.max(0, value));
	}
	
	public Color getRGBColor(int x, int y) {
		int c = getY(x, y) - 16;
		int d = getU(x, y) - 128;
		int e = getV(x, y) - 128;
		int r = clamp((298*c + 409*e + 128) >> 8);
		int g = clamp((298*c - 100*d - 208*e + 128) >> 8);
		int b = clamp((298*c + 516*d + 128) >> 8);
		return new Color(r / 255.0, g / 255.0, b / 255.0, 1.0);
	}

	@Override
	public AdaptedYUYVImage weightedCentroidWith(AdaptedYUYVImage other, int thisCount, int otherCount) {
		return combine(this, other, (p1, p2) -> (p1 * thisCount + p2 * otherCount) / (thisCount + otherCount));
	}
	
	public static void checkCompatibleImages(AdaptedYUYVImage img1, AdaptedYUYVImage img2) {
		if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
			throw new IllegalArgumentException("Images of unequal dimensions");
		}
	}
	
	// Helper functions
	public static AdaptedYUYVImage combine(AdaptedYUYVImage img1, AdaptedYUYVImage img2, IntBinaryOperator combiner) {
		checkCompatibleImages(img1, img2);
		byte[] newPix = new byte[img2.getBytes().length];
		for (int i = 0; i < newPix.length; i++) {
			int combo = combiner.applyAsInt(Util.byteBits2Int(img1.pix[i]), Util.byteBits2Int(img2.pix[i]));
			newPix[i] = (byte)combo;
		}
		return new AdaptedYUYVImage(newPix, img1.getWidth(), img1.getHeight());
	}

	@Override
	public AdaptedYUYVImage deepCopy() {
		return new AdaptedYUYVImage(this);
	}
}
