package jsettlers.graphics.map.draw;

import jsettlers.graphics.map.draw.settlerimages.SettlerImageMap;

public class ImagePreloadTask implements Runnable {
	@Override
	public void run() {
		SettlerImageMap.getInstance();

		Background.preloadTexture();

		ImageProvider ip = ImageProvider.getInstance();
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
			});
		} catch (Throwable e) {
		}

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
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}

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
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}

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
			        //16,
			        17,
			        18,
			        //19,
			        20,

			        // bowman
			        21,
			        //22,
			        23,
			        24,
			        //25,
			        26,

			        // ghost
			        27,

			        // inside tower
			        28
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
