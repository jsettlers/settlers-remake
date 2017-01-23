package jsettlers.graphics.androidui.utils;

import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

import jsettlers.common.Color;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.material.EMaterialType;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.image.SingleImage;
import jsettlers.graphics.map.draw.ImageProvider;
import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.ImageView;

public class OriginalImageProvider {
	private static final OriginalImageProvider INSTANCE = new OriginalImageProvider();

	private final HashMap<String, ImageReference> cachedImages = new HashMap<String, ImageReference>();
	private ImageLoaderThread imageLoadThread;
	private final LinkedBlockingQueue<ImageReference> toLoad = new LinkedBlockingQueue<ImageReference>();

	public static ImageReference get(EBuildingType building) {
		return INSTANCE.create(building.getGuiImage());
	}

	public static ImageReference get(EMaterialType material) {
		return INSTANCE.create(material.getIcon());
	}

	public static ImageReference get(String imageName) {
		return INSTANCE.create(ImageLink.fromName(imageName, 0));
	}

	public static ImageReference get(ImageLink imageLink) {
		return INSTANCE.create(imageLink);
	}

	/**
	 * Needs to be called on the Android UI Thread.
	 * 
	 * @param image
	 *            The image to get
	 * @return The reference to the image that will be loaded.
	 */
	private ImageReference create(ImageLink image) {
		String name = image.getName();
		ImageReference cached = cachedImages.get(name);
		if (cached != null) {
			return cached;
		}
		ImageReference reference = new ImageReference(image);
		toLoad.add(reference);
		ensureImageLoaderStarted();
		cachedImages.put(name, reference);
		return reference;
	}

	private void ensureImageLoaderStarted() {
		if (imageLoadThread == null) {
			imageLoadThread = new ImageLoaderThread();
			imageLoadThread.start();
		}
	}

	/**
	 * A reference to an image that can be loaded at a later moment.
	 */
	public static class ImageReference {
		private ImageLink image;
		private ArrayList<ImageView> viewsToUpdate = new ArrayList<ImageView>();
		private final Handler handler = new Handler();
		private Bitmap bm = null;

		public ImageReference(ImageLink image) {
			this.image = image;
		}

		public void load() {
			Image loaded = ImageProvider.getInstance().getImage(image);
			int[] colors = new int[loaded.getWidth() * loaded.getHeight()];
			if (loaded instanceof SingleImage) {
				ShortBuffer data = ((SingleImage) loaded).getData().duplicate();
				data.rewind();
				for (int i = 0; i < colors.length; i++) {
					colors[i] = Color.convertTo32Bit(data.get());
				}
			} else {
				// TODO: Handle error.
			}
			bm = Bitmap.createBitmap(colors, 0, loaded.getWidth(), loaded.getWidth(), loaded.getHeight(), Bitmap.Config.ARGB_8888);

			handler.post(new Runnable() {
				@Override
				public void run() {
					for (ImageView view : viewsToUpdate) {
						realSetAsImage(view);
					}
					viewsToUpdate = null;
				}
			});
		}

		/**
		 * To be called in the UI Thread.
		 * 
		 * @param view
		 */
		public void setAsImage(ImageView view) {
			if (viewsToUpdate != null) {
				viewsToUpdate.add(view);
			} else {
				realSetAsImage(view);
			}
		}

		public void setAsButton(ImageView button) {
			float density = button.getContext().getResources().getDisplayMetrics().density;
			int w = (int) (180 / density);
			button.getLayoutParams().width = w;
			button.getLayoutParams().height = w * 3 / 4;
			button.setScaleType(ImageView.ScaleType.FIT_CENTER);
			setAsImage(button);
		}

		private void realSetAsImage(ImageView view) {
			view.setImageBitmap(bm);
		}
	}

	private class ImageLoaderThread extends Thread {

		public ImageLoaderThread() {
			super("Image loader");
		}

		@Override
		public void run() {
			try {
				while (true) {
					ImageReference image = toLoad.take();
					image.load();
				}
			} catch (InterruptedException e) {
				// TODO: Error reporting.
			}
		}
	}
}
