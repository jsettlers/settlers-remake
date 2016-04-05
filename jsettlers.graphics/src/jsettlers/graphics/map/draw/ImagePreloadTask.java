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

import jsettlers.graphics.map.draw.settlerimages.SettlerImageMap;

public class ImagePreloadTask implements Runnable {
	@Override
	public void run() {
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
			ip.getFileReader(1).generateImageMap(1024, 2048, new int[] {
					// trees
					1,// grown
					2,// grown
					3,
					4,// grown
					6,
					7,// grown
					8,// grown
					9,
					16,// grown
					17,// grown
					18,
					// water
					26,
					// stones
					31,
					// goods
					33,
					34,
					35,
					36,
					37,
					38,
					39,
					40,
					41,
					42,
					43,
					44,
					45,
					46,
					47,
					48,
					49,
					50,
					51,
					52,
					53,
					54,
					55,
					56,
					// signs
					93,
					94,
					95,
					96,
					97,
					98,
					99,
					// arrows
					100,
					101,
					102,
					103,
					104,
					105,
			}, "1");
		} catch (Throwable e) {
			ImageProvider.traceImageLoad("Error pre-loading: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void loadRomanSettlers(ImageProvider ip) {
		try {
			ip.getFileReader(10).generateImageMap(2048, 2048, new int[] {
					// settlers
					0,
					1,
					2,
					3,
					4,
					5,
					6,
					7,
					8,
					9,
					10,
					11,
					12,
					13,
					14,
					15,
					16,
					17,
					18,
					19,
					20,
					21,
					22,
					23,
					24,
					25,
					26,
					27,
					28,
					29,
					30,
					31,
					32,
					33,
					34,
					45
			}, "10");
		} catch (Throwable e) {
			ImageProvider.traceImageLoad("Error pre-loading: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void loadRomanWorkers(ImageProvider ip) {
		try {
			ip.getFileReader(11).generateImageMap(2048, 2048, new int[] {
					// workers
					13,
					14,
					15,
					16,
					17,
					18,
					19,
					20,
					21,
					22,
					23,
					24,
					25,
					26,
					27,
					28,
					29,
					30,
					31,
					32,
					33,
					34,
					35,
					36,

					// pioneer
					37,
					38,
					39,

					// default workers
					40,
					55,
					65,

					// priest
					188,

					// pioneer
					204,
					205,
					206,

					// building workers
					206,
					207,
					208,
					209,
					210,
					211,
					212,
					213,
					214,
					215,
					216,
					217,
					218,
					219,
					220,
					221,
					222,
					223,

					231,
					232,
			}, "11");
		} catch (Throwable e) {
			ImageProvider.traceImageLoad("Error pre-loading: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void loadRomanSoldiers(ImageProvider ip) {
		try {
			ip.getFileReader(12).generateImageMap(2048, 2048, new int[] {
					// soldiers

					// swordsman
					9,
					10,
					11,
					12,
					13,
					14,

					// pikeman
					15,
					// 16,
					17,
					18,
					// 19,
					20,

					// bowman
					21,
					// 22,
					23,
					24,
					// 25,
					26,

					// ghost
					27,

					// inside tower
					28
			}, "12");
		} catch (Throwable e) {
			ImageProvider.traceImageLoad("Error pre-loading: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void loadRomanBuildings(ImageProvider ip) {
		try {
			ip.getFileReader(13).generateImageMap(2048, 2048, new int[] {
					0, 1, 3, 17, 63, 64, 65
			}, "13");
		} catch (Throwable e) {
			ImageProvider.traceImageLoad("Error pre-loading: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
