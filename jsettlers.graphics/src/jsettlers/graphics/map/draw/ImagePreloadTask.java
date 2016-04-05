/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.graphics.map.draw;

import jsettlers.graphics.image.MultiImageMap;
import jsettlers.graphics.image.MultiImageMap.MultiImageMapSpecification;
import jsettlers.graphics.map.draw.settlerimages.SettlerImageMap;

public class ImagePreloadTask implements Runnable {
	private static final int FILE_LANDSCAPE = 1;
	private static final int FILE_ROMAN_BEARER = 10;
	private static final int FILE_ROMAN_WORKER = 11;
	private static final int FILE_ROMAN_SOLDIER = 12;

	private static boolean wasRunning = false;

	@Override
	public void run() {
		if (wasRunning) {
			throw new IllegalStateException("Can only preload once.");
		}
		wasRunning = true;
		ImageProvider.traceImageLoad("Image preload task starting");
		SettlerImageMap.getInstance();

		loadBackground();

		ImageProvider ip = ImageProvider.getInstance();
		loadLandscapeObjects(ip);

		loadRomanSettlers(ip);

		loadRomanWorkers(ip);

		loadRomanSoldiers(ip);

		loadRomanBuildings(ip);
		ImageProvider.traceImageLoad("Image preload task done");
	}

	private void loadBackground() {
		try {
			Background.preloadTexture();
		} catch (Throwable e) {
			ImageProvider.traceImageLoad("Error pre-loading: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void loadLandscapeObjects(ImageProvider ip) {
		try {
			MultiImageMapSpecification spec = new MultiImageMapSpecification(2048, 1024, "landscape");
			// trees
			spec.add(FILE_LANDSCAPE, 1);
			spec.add(FILE_LANDSCAPE, 2);
			spec.add(FILE_LANDSCAPE, 3);
			spec.add(FILE_LANDSCAPE, 4);
			spec.add(FILE_LANDSCAPE, 6);
			spec.add(FILE_LANDSCAPE, 7);
			spec.add(FILE_LANDSCAPE, 8);
			spec.add(FILE_LANDSCAPE, 9);
			spec.add(FILE_LANDSCAPE, 16);
			spec.add(FILE_LANDSCAPE, 17);
			spec.add(FILE_LANDSCAPE, 18);
			// water
			spec.add(FILE_LANDSCAPE, 26);
			// stones
			spec.add(FILE_LANDSCAPE, 31);
			// goods
			spec.add(FILE_LANDSCAPE, 33);
			spec.add(FILE_LANDSCAPE, 34);
			spec.add(FILE_LANDSCAPE, 35);
			spec.add(FILE_LANDSCAPE, 36);
			spec.add(FILE_LANDSCAPE, 37);
			spec.add(FILE_LANDSCAPE, 38);
			spec.add(FILE_LANDSCAPE, 39);
			spec.add(FILE_LANDSCAPE, 40);
			spec.add(FILE_LANDSCAPE, 41);
			spec.add(FILE_LANDSCAPE, 42);
			spec.add(FILE_LANDSCAPE, 43);
			spec.add(FILE_LANDSCAPE, 44);
			spec.add(FILE_LANDSCAPE, 45);
			spec.add(FILE_LANDSCAPE, 46);
			spec.add(FILE_LANDSCAPE, 47);
			spec.add(FILE_LANDSCAPE, 48);
			spec.add(FILE_LANDSCAPE, 49);
			spec.add(FILE_LANDSCAPE, 50);
			spec.add(FILE_LANDSCAPE, 51);
			spec.add(FILE_LANDSCAPE, 52);
			spec.add(FILE_LANDSCAPE, 53);
			spec.add(FILE_LANDSCAPE, 54);
			spec.add(FILE_LANDSCAPE, 55);
			spec.add(FILE_LANDSCAPE, 56);
			// signs
			spec.add(FILE_LANDSCAPE, 93);
			spec.add(FILE_LANDSCAPE, 94);
			spec.add(FILE_LANDSCAPE, 95);
			spec.add(FILE_LANDSCAPE, 96);
			spec.add(FILE_LANDSCAPE, 97);
			spec.add(FILE_LANDSCAPE, 98);
			spec.add(FILE_LANDSCAPE, 99);
			// arrows
			spec.add(FILE_LANDSCAPE, 100);
			spec.add(FILE_LANDSCAPE, 101);
			spec.add(FILE_LANDSCAPE, 102);
			spec.add(FILE_LANDSCAPE, 103);
			spec.add(FILE_LANDSCAPE, 104);
			spec.add(FILE_LANDSCAPE, 105);
			preload(spec);
		} catch (Throwable e) {
			ImageProvider.traceImageLoad("Error pre-loading: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void loadRomanSettlers(ImageProvider ip) {
		try {
			MultiImageMapSpecification spec = new MultiImageMapSpecification(2048, 2048, "rb");
			// settlers
			spec.add(FILE_ROMAN_BEARER, 0);
			spec.add(FILE_ROMAN_BEARER, 1);
			spec.add(FILE_ROMAN_BEARER, 2);
			spec.add(FILE_ROMAN_BEARER, 3);
			spec.add(FILE_ROMAN_BEARER, 4);
			spec.add(FILE_ROMAN_BEARER, 5);
			spec.add(FILE_ROMAN_BEARER, 6);
			spec.add(FILE_ROMAN_BEARER, 7);
			spec.add(FILE_ROMAN_BEARER, 8);
			spec.add(FILE_ROMAN_BEARER, 9);
			spec.add(FILE_ROMAN_BEARER, 10);
			spec.add(FILE_ROMAN_BEARER, 11);
			spec.add(FILE_ROMAN_BEARER, 12);
			spec.add(FILE_ROMAN_BEARER, 13);
			spec.add(FILE_ROMAN_BEARER, 14);
			spec.add(FILE_ROMAN_BEARER, 15);
			spec.add(FILE_ROMAN_BEARER, 16);
			spec.add(FILE_ROMAN_BEARER, 17);
			spec.add(FILE_ROMAN_BEARER, 18);
			spec.add(FILE_ROMAN_BEARER, 19);
			spec.add(FILE_ROMAN_BEARER, 20);
			spec.add(FILE_ROMAN_BEARER, 21);
			spec.add(FILE_ROMAN_BEARER, 22);
			spec.add(FILE_ROMAN_BEARER, 23);
			spec.add(FILE_ROMAN_BEARER, 24);
			spec.add(FILE_ROMAN_BEARER, 25);
			spec.add(FILE_ROMAN_BEARER, 26);
			spec.add(FILE_ROMAN_BEARER, 27);
			spec.add(FILE_ROMAN_BEARER, 28);
			spec.add(FILE_ROMAN_BEARER, 29);
			spec.add(FILE_ROMAN_BEARER, 30);
			spec.add(FILE_ROMAN_BEARER, 31);
			spec.add(FILE_ROMAN_BEARER, 32);
			spec.add(FILE_ROMAN_BEARER, 33);
			spec.add(FILE_ROMAN_BEARER, 34);
			spec.add(FILE_ROMAN_BEARER, 45);
			preload(spec);
		} catch (Throwable e) {
			ImageProvider.traceImageLoad("Error pre-loading: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void loadRomanWorkers(ImageProvider ip) {
		try {
			MultiImageMapSpecification spec = new MultiImageMapSpecification(2048, 2048, "rw");
			// workers
			spec.add(FILE_ROMAN_WORKER, 13);
			spec.add(FILE_ROMAN_WORKER, 14);
			spec.add(FILE_ROMAN_WORKER, 15);
			spec.add(FILE_ROMAN_WORKER, 16);
			spec.add(FILE_ROMAN_WORKER, 17);
			spec.add(FILE_ROMAN_WORKER, 18);
			spec.add(FILE_ROMAN_WORKER, 19);
			spec.add(FILE_ROMAN_WORKER, 20);
			spec.add(FILE_ROMAN_WORKER, 21);
			spec.add(FILE_ROMAN_WORKER, 22);
			spec.add(FILE_ROMAN_WORKER, 23);
			spec.add(FILE_ROMAN_WORKER, 24);
			spec.add(FILE_ROMAN_WORKER, 25);
			spec.add(FILE_ROMAN_WORKER, 26);
			spec.add(FILE_ROMAN_WORKER, 27);
			spec.add(FILE_ROMAN_WORKER, 28);
			spec.add(FILE_ROMAN_WORKER, 29);
			spec.add(FILE_ROMAN_WORKER, 30);
			spec.add(FILE_ROMAN_WORKER, 31);
			spec.add(FILE_ROMAN_WORKER, 32);
			spec.add(FILE_ROMAN_WORKER, 33);
			spec.add(FILE_ROMAN_WORKER, 34);
			spec.add(FILE_ROMAN_WORKER, 35);
			spec.add(FILE_ROMAN_WORKER, 36);

			// pioneer
			spec.add(FILE_ROMAN_WORKER, 37);
			spec.add(FILE_ROMAN_WORKER, 38);
			spec.add(FILE_ROMAN_WORKER, 39);

			// default workers
			spec.add(FILE_ROMAN_WORKER, 40);
			spec.add(FILE_ROMAN_WORKER, 55);
			spec.add(FILE_ROMAN_WORKER, 65);

			// priest
			spec.add(FILE_ROMAN_WORKER, 188);

			// pioneer
			spec.add(FILE_ROMAN_WORKER, 204);
			spec.add(FILE_ROMAN_WORKER, 205);
			spec.add(FILE_ROMAN_WORKER, 206);

			// building workers
			spec.add(FILE_ROMAN_WORKER, 206);
			spec.add(FILE_ROMAN_WORKER, 207);
			spec.add(FILE_ROMAN_WORKER, 208);
			spec.add(FILE_ROMAN_WORKER, 209);
			spec.add(FILE_ROMAN_WORKER, 210);
			spec.add(FILE_ROMAN_WORKER, 211);
			spec.add(FILE_ROMAN_WORKER, 212);
			spec.add(FILE_ROMAN_WORKER, 213);
			spec.add(FILE_ROMAN_WORKER, 214);
			spec.add(FILE_ROMAN_WORKER, 215);
			spec.add(FILE_ROMAN_WORKER, 216);
			spec.add(FILE_ROMAN_WORKER, 217);
			spec.add(FILE_ROMAN_WORKER, 218);
			spec.add(FILE_ROMAN_WORKER, 219);
			spec.add(FILE_ROMAN_WORKER, 220);
			spec.add(FILE_ROMAN_WORKER, 221);
			spec.add(FILE_ROMAN_WORKER, 222);
			spec.add(FILE_ROMAN_WORKER, 223);

			spec.add(FILE_ROMAN_WORKER, 231);
			spec.add(FILE_ROMAN_WORKER, 232);
			preload(spec);
		} catch (Throwable e) {
			ImageProvider.traceImageLoad("Error pre-loading: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void loadRomanSoldiers(ImageProvider ip) {
		try {
			MultiImageMapSpecification spec = new MultiImageMapSpecification(2048, 2048, "rs");
			// soldiers

			// swordsman
			spec.add(FILE_ROMAN_SOLDIER, 9);
			spec.add(FILE_ROMAN_SOLDIER, 10);
			spec.add(FILE_ROMAN_SOLDIER, 11);
			spec.add(FILE_ROMAN_SOLDIER, 12);
			spec.add(FILE_ROMAN_SOLDIER, 13);
			spec.add(FILE_ROMAN_SOLDIER, 14);

			// pikeman
			spec.add(FILE_ROMAN_SOLDIER, 15);
			// 16,
			spec.add(FILE_ROMAN_SOLDIER, 17);
			spec.add(FILE_ROMAN_SOLDIER, 18);
			// 19,
			spec.add(FILE_ROMAN_SOLDIER, 20);

			// bowman
			spec.add(FILE_ROMAN_SOLDIER, 21);
			// 22,
			spec.add(FILE_ROMAN_SOLDIER, 23);
			spec.add(FILE_ROMAN_SOLDIER, 24);
			// 25,
			spec.add(FILE_ROMAN_SOLDIER, 26);

			// ghost
			spec.add(FILE_ROMAN_SOLDIER, 27);

			// inside tower
			spec.add(FILE_ROMAN_SOLDIER, 28);
			preload(spec);
		} catch (Throwable e) {
			ImageProvider.traceImageLoad("Error pre-loading: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void loadRomanBuildings(ImageProvider ip) {
		try {
			// ip.getFileReader(13).generateImageMap(2048, 2048, new int[] {
			// 0, 1, 3, 17, 63, 64, 65
			// }, "13");
		} catch (Throwable e) {
			ImageProvider.traceImageLoad("Error pre-loading: " + e.getMessage());
			e.printStackTrace();
		}
	}

	protected void preload(MultiImageMapSpecification spec) {
		new MultiImageMap(spec).load();
	}
}
