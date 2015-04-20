package jsettlers.common.texturegeneration;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import jsettlers.graphics.image.Image;
import jsettlers.graphics.image.ImageDataPrivider;
import jsettlers.graphics.image.SettlerImage;
import jsettlers.graphics.image.SingleImage;
import jsettlers.graphics.reader.AdvancedDatFileReader;

/**
 * This class lets you generate a texture that can be understood by the graphics module. It generates the .texture file.
 * 
 * @author michael
 */
public final class TextureGenerator {

	private static final Pattern ORIGINAL_SETTLER = Pattern.compile("original_\\d+_SETTLER_\\d+_\\d+");

	private static class ImageData {
		ImageDataPrivider data = null;
		ImageDataPrivider torso = null;
		public String name;
	}

	private class LoadImage implements Runnable {
		@Override
		public void run() {
			try {
				while (true) {
					long start = System.currentTimeMillis();
					String toLoad = imagesToLoad.take();
					ImageData data = addIdToTexture(toLoad);
					imagesToStore.put(data);
					System.out.println("Time for loading " + data.name + ": " + (System.currentTimeMillis() - start));
				}
			} catch (InterruptedException e) {
			}
		}
	}

	private class StoreImage implements Runnable {
		@Override
		public void run() {
			try {
				while (true) {
					long start = System.currentTimeMillis();
					ImageData data = imagesToStore.take();
					storeImageData(data);
					System.out.println("Time for storing " + data.name + ": " + (System.currentTimeMillis() - start));
				}
			} catch (InterruptedException e) {
			}
		}
	}

	private static final int QUEUE_LENGTH = 32;
	private static final int THREADS = 8;
	private final File rawDirectory;
	private final File outDirectory;
	private final TextureIndex textureIndex;

	private final ArrayBlockingQueue<ImageData> imagesToStore = new ArrayBlockingQueue<>(
			QUEUE_LENGTH);
	private final ArrayBlockingQueue<String> imagesToLoad = new ArrayBlockingQueue<>(
			QUEUE_LENGTH);
	private final Object pipelineMutex = new Object();
	private int imagesInPipeline;

	private Thread[] started;

	public TextureGenerator(TextureIndex textureIndex, File rawDirectory, File outDirectory) {
		this.textureIndex = textureIndex;
		this.rawDirectory = rawDirectory;
		this.outDirectory = outDirectory;
	}

	/**
	 * Start all threads. FIXME: Leaks threads.
	 */
	public void start() {
		started = new Thread[THREADS * 2];
		for (int i = 0; i < THREADS; i++) {
			started[i] = new Thread(new LoadImage());
			started[i].start();
		}
		for (int i = 0; i < THREADS; i++) {
			started[i + THREADS] = new Thread(new StoreImage());
			started[i + THREADS].start();
		}
	}

	/**
	 * Wait for completion on all threads.
	 * 
	 * @throws InterruptedException
	 */
	public void join() {
		try {
			synchronized (pipelineMutex) {
				while (imagesInPipeline > 0) {
					pipelineMutex.wait();
				}
			}
			for (int i = 0; i < started.length; i++) {
				started[i].interrupt();
			}
		} catch (InterruptedException e) {
		}
	}

	/**
	 * Start compiling a new file. Might block some time.
	 * 
	 * @param list
	 */
	public void addTexturesByName(List<String> list) {
		for (String name : list) {
			try {
				imagesToLoad.put(name);
				synchronized (pipelineMutex) {
					imagesInPipeline++;
				}
			} catch (InterruptedException e) {
			}
		}
	}

	private ImageData addIdToTexture(String name) {
		Matcher matcher = ORIGINAL_SETTLER.matcher(name);
		ImageData imageData = new ImageData();
		imageData.name = name;

		// open original image files
		if (matcher.matches()) {
			File datfile = null; // TODO: Load dat file matcher.group(1)
			AdvancedDatFileReader reader = new AdvancedDatFileReader(datfile);
			Image image = reader.getSettlers()
					.get(Integer.parseInt(matcher.group(2)))
					.getImageSafe(Integer.parseInt(matcher.group(3)));
			if (image instanceof SingleImage) {
				imageData.data = (SingleImage) image;
			}
			if (image instanceof SettlerImage) {
				imageData.torso = (SingleImage) ((SettlerImage) image)
						.getTorso();
			}
		} else {
			imageData.data = getImage(name);
			imageData.torso = getImage(name + ".t");
		}

		if (imageData.data == null) {
			System.err.println("WATNING: loading image " + name
					+ ": No image file found.");
		}
		return imageData;
	}

	private void storeImageData(ImageData imageData) {
		storeImage(imageData.name, imageData.data, imageData.torso != null);
		if (imageData.torso != null) {
			storeImage(imageData.name, imageData.torso, false);
		}

		synchronized (pipelineMutex) {
			imagesInPipeline--;
			pipelineMutex.notifyAll();
		}
	}

	private void storeImage(String name, ImageDataPrivider data,
			boolean hasTorso) {
		try {
			if (data != null) {
				int texture = textureIndex.getNextTextureIndex();
				TexturePosition position = addAsNewImage(data, texture);
				textureIndex.registerTexture(name, texture, data.getOffsetX(),
						data.getOffsetY(), data.getWidth(), data.getHeight(),
						hasTorso, position);
			}
		} catch (Throwable t) {
			System.err.println("WARNING: Problem writing image " + name
					+ ". Problem was: " + t.getMessage());
		}
	}

	// This is slow.
	private TexturePosition addAsNewImage(ImageDataPrivider data, int texture)
			throws IOException {
		int size = getNextPOT(Math.max(data.getWidth(), data.getHeight()));
		TextureFile file = new TextureFile(new File(outDirectory, texture + ""), size, size);
		TexturePosition position = file.addImage(data.getData(),
				data.getWidth());
		file.write();
		return position;
	}

	private static int getNextPOT(int height) {
		int i = 2;
		while (i < height) {
			i *= 2;
		}
		return i;
	}

	private ImageDataPrivider getImage(String id) {
		try {
			File imageFile = new File(rawDirectory, id + ".png");
			int[] offsets = getOffsets(id);
			BufferedImage image = ImageIO.read(imageFile);

			return new ProvidedImage(image, offsets);
		} catch (Throwable t) {
			System.err.println("WARNING: Problem reading image " + id
					+ ". Problem was: " + t.getMessage());
			return null;
		}
	}

	private int[] getOffsets(String id) {
		int[] offsets = new int[2];
		File offset = new File(rawDirectory, id + ".png.offset");
		try (Scanner in = new Scanner(offset)) {
			offsets[0] = in.nextInt();
			in.skip("\\s+");
			offsets[1] = in.nextInt();
			in.close();
			return offsets;

		} catch (Throwable t) {
			System.err.println("WARNING: Problem reading offsets for " + id
					+ ", assuming (0,0). Problem was: " + t.getMessage());
			return new int[] { 0, 0 };
		}
	}

}
