package jsettlers.graphics.androidui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ShortBuffer;

import javax.imageio.ImageIO;

import jsettlers.common.Color;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.resources.ResourceManager;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.image.SingleImage;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.swing.SwingResourceProvider;
import jsettlers.graphics.swing.SwingResourceLoader;

/**
 * This is an ant task to copy over the android images.
 * 
 * @author michael
 */
public class AndroidUiImageGenerator {

	private File destinationDirectory = null;

	private int resolution = 0;

	public void setDestinationDirectory(File sourceDirectory) {
		this.destinationDirectory = sourceDirectory;
	}

	public void setResolution(int resolution) {
		this.resolution = resolution;
	}

	public void execute() {
		if (destinationDirectory == null) {
			throw new RuntimeException(
			        "please use destinationDirectory=\"...\"");
		}

		ResourceManager.setProvider(new SwingResourceProvider(new File(new File("").getAbsolutePath()).getParentFile().getParent().replace(
		        '\\', '/')+ "/jsettlers.common"));
		SwingResourceLoader.setupSwingPaths();
		ImageProvider i = ImageProvider.getInstance();

		for (EBuildingType t : EBuildingType.values()) {
			File file =
			        new File(destinationDirectory, t.toString().toLowerCase()
			                + ".png");
			Image guiImage = i.getImage(t.getGuiImage());
			if (guiImage instanceof SingleImage) {
				export((SingleImage) guiImage, file);
			}
		}
	}

	private static void export(SingleImage image, File file) {
		// does not work if gpu does not support non-power-of-two
		int width = image.getWidth();
		int height = image.getHeight();
		if (width <= 0 || height <= 0) {
			return;
		}

		BufferedImage rendered = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		ShortBuffer data = image.getData().duplicate();
		data.rewind();
		int[] rgbArray = new int[data.remaining()];
		for (int i = 0; i < rgbArray.length; i++) {
			short myColor = data.get();
			float red = (float) ((myColor >> 11) & 0x1f) / 0x1f;
			float green = (float) ((myColor >> 6) & 0x1f) / 0x1f;
			float blue = (float) ((myColor >> 1) & 0x1f) / 0x1f;
			float alpha = myColor & 0x1;
			rgbArray[i] = Color.getARGB(red, green, blue, alpha);
		}

		rendered.setRGB(0, 0, width, height, rgbArray, 0, width);

		try {
			ImageIO.write(rendered, "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
