package jsettlers.graphics.generation;

import java.awt.image.BufferedImage;
import java.nio.ShortBuffer;

import jsettlers.common.Color;
import jsettlers.graphics.image.ImageDataPrivider;

public class ProvidedImage implements ImageDataPrivider {

	private final BufferedImage image;
	private final int[] offsets;

	public ProvidedImage(BufferedImage image, int[] offsets) {
		this.image = image;
		this.offsets = offsets;
    }

	@Override
	public ShortBuffer getData() {
		ShortBuffer data = ShortBuffer.allocate(image.getWidth() * image.getHeight());
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				Color color = new Color(image.getRGB(x, y));
				data.put(color.toShortColor(1));
			}
		}
		data.rewind();
		return data;
	}

	@Override
	public int getWidth() {
		return image.getWidth();
	}

	@Override
	public int getHeight() {
		return image.getHeight();
	}

	@Override
	public int getOffsetX() {
		return offsets[0];
	}

	@Override
	public int getOffsetY() {
		return offsets[1];
	}

}
