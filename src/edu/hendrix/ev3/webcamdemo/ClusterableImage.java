package edu.hendrix.ev3.webcamdemo;

import java.awt.image.BufferedImage;
import java.util.EnumMap;

import edu.hendrix.ev3.ai.cluster.Clusterable;
import edu.hendrix.ev3.util.DeepCopyable;
import edu.hendrix.ev3.util.Util;

public class ClusterableImage implements Clusterable<ClusterableImage>, DeepCopyable<ClusterableImage> {
	private BufferedImage img;
	
	public ClusterableImage(BufferedImage img) {
		this.img = img;
	}

	@Override
	public ClusterableImage weightedCentroidWith(ClusterableImage that, int thisCount, int thatCount) {
		Util.assertArgument(this.img.getWidth() == that.img.getWidth() && this.img.getHeight() == that.img.getHeight(), "Images are different sizes");
		BufferedImage target = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < target.getWidth(); x++) {
			for (int y = 0; y < target.getHeight(); y++) {
				int thisPixel = this.img.getRGB(x, y);
				int thatPixel = that.img.getRGB(x, y);
				EnumMap<ColorChannel,Integer> colorCombos = new EnumMap<>(ColorChannel.class);
				for (ColorChannel c: ColorChannel.values()) {
					int den = thisCount + thatCount;
					int combo = thisCount * c.extractFrom(thisPixel) + thatCount * c.extractFrom(thatPixel);
					colorCombos.put(c, combo / den);
				}
				target.setRGB(x, y, ColorChannel.buildPixelFrom(colorCombos));
			}
		}
		return new ClusterableImage(target);
	}
	
	public javafx.scene.paint.Color getColor(int x, int y) {
		return ColorChannel.buildColorFrom(getRGB(x, y));
	}

	public int getRGB(int x, int y) {return img.getRGB(x, y);}
	public int getWidth() {return img.getWidth();}
	public int getHeight() {return img.getHeight();}
	
	public static long distance(ClusterableImage img1, ClusterableImage img2) {
		Util.assertArgument(img1.getWidth() == img2.getWidth() && img1.getHeight() == img2.getHeight(), "Images must be the same size");
		long total = 0;
		for (int x = 0; x < img1.getWidth(); x++) {
			for (int y = 0; y < img2.getHeight(); y++) {
				EnumMap<ColorChannel,Integer> colors1 = ColorChannel.buildChannelsFrom(img1.getRGB(x, y));
				EnumMap<ColorChannel,Integer> colors2 = ColorChannel.buildChannelsFrom(img2.getRGB(x, y));
				for (ColorChannel c: ColorChannel.values()) {
					int diff = colors1.get(c) - colors2.get(c);
					total += diff * diff;
				}
			}
		}
		return total;
	}

	@Override
	// Note that this class has immutable semantics, making this possible.
	public ClusterableImage deepCopy() {
		return new ClusterableImage(img);
	}
}
