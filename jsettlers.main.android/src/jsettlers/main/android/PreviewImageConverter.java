package jsettlers.main.android;

import jsettlers.common.Color;
import jsettlers.logic.map.save.MapFileHeader;
import android.graphics.Bitmap;

public class PreviewImageConverter {
	public static Bitmap toBitmap(short[] data) {
		if (data == null) {
			Bitmap b =
			        Bitmap.createBitmap(1, 1,  Bitmap.Config.ARGB_8888);
			b.setPixel(0, 0, 0xffffffff);
			return b;
		}
		
		if (data.length != MapFileHeader.PREVIEW_IMAGE_SIZE
		        * MapFileHeader.PREVIEW_IMAGE_SIZE) {
			throw new IllegalArgumentException();
		}

		Bitmap b =
		        Bitmap.createBitmap(MapFileHeader.PREVIEW_IMAGE_SIZE * 3 / 2,
		                MapFileHeader.PREVIEW_IMAGE_SIZE,
		                Bitmap.Config.ARGB_8888);

		for (int y = 0; y < MapFileHeader.PREVIEW_IMAGE_SIZE; y++) {
			int offset = (MapFileHeader.PREVIEW_IMAGE_SIZE - y) / 2;
			for (int x = 0; x < MapFileHeader.PREVIEW_IMAGE_SIZE; x++) {
				b.setPixel(
				        offset + x,
				        y,
				        Color.fromShort(
				                data[x + y * MapFileHeader.PREVIEW_IMAGE_SIZE])
				                .getARGB());
			}
		}
		return b;
	}
}
