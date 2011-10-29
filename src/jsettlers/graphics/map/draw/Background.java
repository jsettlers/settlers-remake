package jsettlers.graphics.map.draw;

import go.graphics.GLDrawContext;

import java.nio.ShortBuffer;

import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.position.IntRectangle;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.map.MapDrawContext;

/**
 * The map background
 * 
 * @author michael
 */
public class Background {

	private static final int LAND_FILE = 0;

	/**
	 * The base texture size.
	 */
	private static final int TEXTURE_SIZE = 1024;

	/**
	 * Our base texture is divided into multiple squares that all hold a single
	 * texture. Coninuous textures occupy 5*5 squares
	 */
	private static final int TEXTURE_GRID = 32;

	/**
	 * Where are the textures on the map?
	 * <p>
	 * x and y coordinates are in Grid units.
	 * <p>
	 * The third entry is the size of the texture. It must be 1 for border tiles
	 * and 2..5 for continuous images. Always 1 more than they are wide.
	 * 
	 * <pre>
	 * +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
	 * |0             |1             |3             |4             |5             |7             | 5| 6|
	 * +              +              +              +              +              +              +--+--+
	 * |0             |1             |3             |4             |5             |7             | 8| 9|
	 * +              +              +              +              +              +              +--+--+
	 * |0             |1             |3             |4             |5             |7             |11|12|
	 * +              +              +              +              +              +              +--+--+
	 * |0             |1             |3             |4             |5             |7             |13|14|
	 * +              +              +              +              +              +              +--+--+
	 * |0             |1             |3             |4             |5             |7             |15|16|
	 * +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
	 * |10            |18            |              |              |              |              |17|19|
	 * +              +              +              +              +              +              +--+--+
	 * |10            |18            |              |              |              |              |20|22|
	 * +              +              +              +              +              +              +--+--+
	 * |10            |18            |     21       |     24       |     31       |      35      |23|25|
	 * +              +              +              +              +              +              +--+--+
	 * |10            |18            |              |              |              |              |26|27|
	 * +              +              +              +              +              +              +--+--+
	 * |10            |18            |              |              |              |              |28|29|
	 * +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
	 * |              |              |              |              |              |              |30|32|
	 * +              +              +              +              +              +              +--+--+
	 * |              |              |              |              |              |              |33|34|
	 * +              +              +              +              +              +              +--+--+
	 * |      36      |     176      |              |              |              |              |98|99|
	 * +              +              +              +              +              +              +--+--+
	 * |              |              |              |              |              |              |37|38|
	 * +              +              +              +              +              +              +--+--+
	 * |              |              |              |              |              |              |39|40|
	 * +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
	 * |41|42|43|44|45|46|47|48|49|50|51|52|53|54|55|56|57|58|59|60|61|62|63|64|65|66|67|68|69|70|71|72|
	 * +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+ --- ↓ +100
	 * |73|74|75|76|77|78|79|80|81|82|83|84|85|86|87|88|89|90|91|92|93|94|95|96|97|00|01|02|03|04|05|06|
	 * +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
	 * |07|08|09|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31|32|33|34|35|36|37|38|
	 * +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
	 * |39|40|41|42|43|44|45|46|47|48|49|50|51|52|53|54|55|56|57|58|59|60|61|62|63|64|65|66|67|68|69|70|
	 * +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+ --- ↓ +200
	 * |71|72|73|74|75|  |77|78|79|80|81|82|83|84|85|86|87|88|89|90|91|92|93|94|95|96|97|98|99|00|01|02|
	 */
	private static final int[][] TEXTURE_POSITIONS = {
	        /* 0: big */{
	                0, 0, 5
	        },
	        /* 1: big */{
	                5, 0, 5
	        },
	        /* 2: big */{
	                10, 0, 5
	        },
	        /* 3: big */{
	                15, 0, 5
	        },
	        /* 4: big */{
	                20, 0, 5
	        },
	        /* 5: small */{
	                30, 0, 1
	        },
	        /* 6: small */{
	                31, 0, 1
	        },
	        /* 7: big */{
	                25, 0, 5
	        },
	        /* 8: small */{
	                30, 1, 1
	        },
	        /* 9: small */{
	                31, 1, 1
	        },
	        /* 10: big */{
	                0, 5, 5
	        },
	        /* 11: small */{
	                30, 2, 1
	        },
	        /* 12: small */{
	                31, 2, 1
	        },
	        /* 13: small */{
	                30, 3, 1
	        },
	        /* 14: small */{
	                31, 3, 1
	        },
	        /* 15: small */{
	                30, 4, 1
	        },
	        /* 16: small */{
	                31, 4, 1
	        },
	        /* 17: small */{
	                30, 5, 1
	        },
	        /* 18: big */{
	                5, 5, 5
	        },
	        /* 19: small */{
	                31, 5, 1
	        },
	        /* 20: small */{
	                30, 6, 1
	        },
	        /* 21: big */{
	                10, 5, 5
	        },
	        /* 22: small */{
	                31, 6, 1
	        },
	        /* 23: small */{
	                30, 7, 1
	        },
	        /* 24: big */{
	                15, 5, 5
	        },
	        /* 25: small */{
	                31, 7, 1
	        },
	        /* 26: small */{
	                30, 8, 1
	        },
	        /* 27: small */{
	                31, 8, 1
	        },
	        /* 28: small */{
	                30, 9, 1
	        },
	        /* 29: small */{
	                31, 9, 1
	        },
	        /* 30: small */{
	                30, 10, 1
	        },
	        /* 31: big */{
	                20, 5, 5
	        },
	        /* 32: small */{
	                31, 10, 1
	        },
	        /* 33: small */{
	                30, 11, 1
	        },
	        /* 34: small */{
	                31, 11, 1
	        },
	        /* 35: big */{
	                25, 5, 5
	        },
	        /* 36: big */{
	                0, 10, 5
	        },
	        /* 37: small */{
	                30, 13, 1
	        },
	        /* 38: small */{
	                31, 13, 1
	        },
	        /* 39: small */{
	                30, 14, 1
	        },
	        /* 40: small */{
	                31, 14, 1
	        },
	        /* 41: small */{
	                0, 15, 1
	        },
	        /* 42: small */{
	                1, 15, 1
	        },
	        /* 43: small */{
	                2, 15, 1
	        },
	        /* 44: small */{
	                3, 15, 1
	        },
	        /* 45: small */{
	                4, 15, 1
	        },
	        /* 46: small */{
	                5, 15, 1
	        },
	        /* 47: small */{
	                6, 15, 1
	        },
	        /* 48: small */{
	                7, 15, 1
	        },
	        /* 49: small */{
	                8, 15, 1
	        },
	        /* 50: small */{
	                9, 15, 1
	        },
	        /* 51: small */{
	                10, 15, 1
	        },
	        /* 52: small */{
	                11, 15, 1
	        },
	        /* 53: small */{
	                12, 15, 1
	        },
	        /* 54: small */{
	                13, 15, 1
	        },
	        /* 55: small */{
	                14, 15, 1
	        },
	        /* 56: small */{
	                15, 15, 1
	        },
	        /* 57: small */{
	                16, 15, 1
	        },
	        /* 58: small */{
	                17, 15, 1
	        },
	        /* 59: small */{
	                18, 15, 1
	        },
	        /* 60: small */{
	                19, 15, 1
	        },
	        /* 61: small */{
	                20, 15, 1
	        },
	        /* 62: small */{
	                21, 15, 1
	        },
	        /* 63: small */{
	                22, 15, 1
	        },
	        /* 64: small */{
	                23, 15, 1
	        },
	        /* 65: small */{
	                24, 15, 1
	        },
	        /* 66: small */{
	                25, 15, 1
	        },
	        /* 67: small */{
	                26, 15, 1
	        },
	        /* 68: small */{
	                27, 15, 1
	        },
	        /* 69: small */{
	                28, 15, 1
	        },
	        /* 70: small */{
	                29, 15, 1
	        },
	        /* 71: small */{
	                30, 15, 1
	        },
	        /* 72: small */{
	                31, 15, 1
	        },
	        // ------------------------------------
	        /* 73: small */{
	                0, 16, 1
	        },
	        /* 74: small */{
	                1, 16, 1
	        },
	        /* 75: small */{
	                2, 16, 1
	        },
	        /* 76: small */{
	                3, 16, 1
	        },
	        /* 77: small */{
	                4, 16, 1
	        },
	        /* 78: small */{
	                5, 16, 1
	        },
	        /* 79: small */{
	                6, 16, 1
	        },
	        /* 80: small */{
	                7, 16, 1
	        },
	        /* 81: small */{
	                8, 16, 1
	        },
	        /* 82: small */{
	                9, 16, 1
	        },
	        /* 83: small */{
	                10, 16, 1
	        },
	        /* 84: small */{
	                11, 16, 1
	        },
	        /* 85: small */{
	                12, 16, 1
	        },
	        /* 86: small */{
	                13, 16, 1
	        },
	        /* 87: small */{
	                14, 16, 1
	        },
	        /* 88: small */{
	                15, 16, 1
	        },
	        /* 89: small */{
	                16, 16, 1
	        },
	        /* 90: small */{
	                17, 16, 1
	        },
	        /* 91: small */{
	                18, 16, 1
	        },
	        /* 92: small */{
	                19, 16, 1
	        },
	        /* 93: small */{
	                20, 16, 1
	        },
	        /* 94: small */{
	                21, 16, 1
	        },
	        /* 95: small */{
	                22, 16, 1
	        },
	        /* 96: small */{
	                23, 16, 1
	        },
	        /* 97: small */{
	                24, 16, 1
	        },
	        /* 98: small */{
	                30, 16, 1
	        },
	        /* 99: small */{
	                31, 12, 1
	        },
	        /* 100: small */{
	                25, 12, 1
	        },
	        /* 101: small */{
	                26, 16, 1
	        },
	        /* 102: small */{
	                27, 16, 1
	        },
	        /* 103: small */{
	                28, 16, 1
	        },
	        /* 104: small */{
	                29, 16, 1
	        },
	        /* 105: small */{
	                30, 16, 1
	        },
	        /* 106: small */{
	                31, 16, 1
	        },
	        // ------------------------------------
	        /* 107: small */{
	                0, 17, 1
	        },
	        /* 108: small */{
	                1, 17, 1
	        },
	        /* 109: small */{
	                2, 17, 1
	        },
	        /* 110: small */{
	                3, 17, 1
	        },
	        /* 111: small */{
	                4, 17, 1
	        },
	        /* 112: small */{
	                5, 17, 1
	        },
	        /* 113: small */{
	                6, 17, 1
	        },
	        /* 114: small */{
	                7, 17, 1
	        },
	        /* 115: small */{
	                8, 17, 1
	        },
	        /* 116: small */{
	                9, 17, 1
	        },
	        /* 117: small */{
	                10, 17, 1
	        },
	        /* 118: small */{
	                11, 17, 1
	        },
	        /* 119: small */{
	                12, 17, 1
	        },
	        /* 120: small */{
	                13, 17, 1
	        },
	        /* 121: small */{
	                14, 17, 1
	        },
	        /* 122: small */{
	                15, 17, 1
	        },
	        /* 123: small */{
	                16, 17, 1
	        },
	        /* 124: small */{
	                17, 17, 1
	        },
	        /* 125: small */{
	                18, 17, 1
	        },
	        /* 126: small */{
	                19, 17, 1
	        },
	        /* 127: small */{
	                20, 17, 1
	        },
	        /* 128: small */{
	                21, 17, 1
	        },
	        /* 129: small */{
	                22, 17, 1
	        },
	        /* 130: small */{
	                23, 17, 1
	        },
	        /* 131: small */{
	                24, 17, 1
	        },
	        /* 132: small */{
	                25, 17, 1
	        },
	        /* 133: small */{
	                26, 17, 1
	        },
	        /* 134: small */{
	                27, 17, 1
	        },
	        /* 135: small */{
	                28, 17, 1
	        },
	        /* 136: small */{
	                29, 17, 1
	        },
	        /* 137: small */{
	                30, 17, 1
	        },
	        /* 138: small */{
	                31, 17, 1
	        },
	        // ------------------------------------
	        /* 139: small */{
	                0, 18, 1
	        },
	        /* 140: small */{
	                1, 18, 1
	        },
	        /* 141: small */{
	                2, 18, 1
	        },
	        /* 142: small */{
	                3, 18, 1
	        },
	        /* 143: small */{
	                4, 18, 1
	        },
	        /* 144: small */{
	                5, 18, 1
	        },
	        /* 145: small */{
	                6, 18, 1
	        },
	        /* 146: small */{
	                7, 18, 1
	        },
	        /* 147: small */{
	                8, 18, 1
	        },
	        /* 148: small */{
	                9, 18, 1
	        },
	        /* 149: small */{
	                10, 18, 1
	        },
	        /* 150: small */{
	                11, 18, 1
	        },
	        /* 151: small */{
	                12, 18, 1
	        },
	        /* 152: small */{
	                13, 18, 1
	        },
	        /* 153: small */{
	                14, 18, 1
	        },
	        /* 154: small */{
	                15, 18, 1
	        },
	        /* 155: small */{
	                16, 18, 1
	        },
	        /* 156: small */{
	                17, 18, 1
	        },
	        /* 157: small */{
	                18, 18, 1
	        },
	        /* 158: small */{
	                19, 18, 1
	        },
	        /* 159: small */{
	                20, 18, 1
	        },
	        /* 160: small */{
	                21, 18, 1
	        },
	        /* 161: small */{
	                22, 18, 1
	        },
	        /* 162: small */{
	                23, 18, 1
	        },
	        /* 163: small */{
	                24, 18, 1
	        },
	        /* 164: small */{
	                25, 18, 1
	        },
	        /* 165: small */{
	                26, 18, 1
	        },
	        /* 166: small */{
	                27, 18, 1
	        },
	        /* 167: small */{
	                28, 18, 1
	        },
	        /* 168: small */{
	                29, 18, 1
	        },
	        /* 169: small */{
	                30, 18, 1
	        },
	        /* 170: small */{
	                31, 18, 1
	        },
	        // ------------------------------------
	        /* 171: small */{
	                0, 19, 1
	        },
	        /* 172: small */{
	                1, 19, 1
	        },
	        /* 173: small */{
	                2, 19, 1
	        },
	        /* 174: small */{
	                3, 19, 1
	        },
	        /* 175: small */{
	                4, 19, 1
	        },
	        /* 176: big (odd shape?) */{
	                5, 10, 5
	        },
	        /* 177: small */{
	                6, 19, 1
	        },
	        /* 178: small */{
	                7, 19, 1
	        },
	        /* 179: small */{
	                8, 19, 1
	        },
	        /* 180: small */{
	                9, 19, 1
	        },
	        /* 181: small */{
	                10, 19, 1
	        },
	        /* 182: small */{
	                11, 19, 1
	        },
	        /* 183: small */{
	                12, 19, 1
	        },
	        /* 184: small */{
	                13, 19, 1
	        },
	        /* 185: small */{
	                14, 19, 1
	        },
	        /* 186: small */{
	                15, 19, 1
	        },
	        /* 187: small */{
	                16, 19, 1
	        },
	        /* 188: small */{
	                17, 19, 1
	        },
	        /* 189: small */{
	                18, 19, 1
	        },
	        /* 190: small */{
	                19, 19, 1
	        },
	        /* 191: small */{
	                20, 19, 1
	        },
	        /* 192: small */{
	                21, 19, 1
	        },
	        /* 193: small */{
	                22, 19, 1
	        },
	        /* 194: small */{
	                23, 19, 1
	        },
	        /* 195: small */{
	                24, 19, 1
	        },
	        /* 196: small */{
	                25, 19, 1
	        },
	        /* 197: small */{
	                26, 19, 1
	        },
	        /* 198: small */{
	                27, 19, 1
	        },
	        /* 199: small */{
	                28, 19, 1
	        },
	        /* 200: small */{
	                29, 19, 1
	        },
	        /* 201: small */{
	                30, 19, 1
	        },
	        /* 202: small */{
	                31, 19, 1
	        },
	// ------------------------------------

	        // ...
	        };

	/**
	 * How many floats are needed per vertex
	 */
	private static final short VERTEX_SIZE = 8;

	private static final int EXTRA_PADDING = 50;

	private static final int HASH_INVALID = 0x80000000;

	private static final byte DIM_MAX = 20;

	private ImageProvider imageProvider = ImageProvider.getInstance();

	/**
	 * The geometry buffer. It has a length of bufferwidth * bufferheight * 2 *
	 * 3 verteces.
	 */
	private float[] geometryBuffer = new float[VERTEX_SIZE * 6];
	private int[] geometryHash = new int[1];
	private byte[] fogOfWarStatus = new byte[1];
	private MapRectangle oldBufferPosition = null;
	private int bufferwidth = 1;; // in map points.
	private int bufferheight = 1; // in map points.

	private int texture = -1;

	private int getTexture(GLDrawContext context) {
		if (texture < 0) {
			if (imageProvider.isPreloaded(LAND_FILE)) {
				short[] data = new short[TEXTURE_SIZE * TEXTURE_SIZE];
				for (int i = 0; i < data.length; i++) {
					data[i] = 0x00ff;
				}
				addTextures(data);
				ShortBuffer buffer = ShortBuffer.wrap(data);
				texture =
				        context.generateTexture(TEXTURE_SIZE, TEXTURE_SIZE,
				                buffer);
			} else {
				imageProvider.preload(LAND_FILE);
			}

		}
		return texture;
	}

	/**
	 * Generates the texture data.
	 * 
	 * @param data
	 */
	private void addTextures(short[] data) {
		for (int index = 0; index < TEXTURE_POSITIONS.length; index++) {
			ImageLink link =
			        new ImageLink(EImageLinkType.LANDSCAPE, LAND_FILE, index, 0);
			Image image = imageProvider.getImage(link);

			copyImageAt(data, image, TEXTURE_POSITIONS[index]);
		}
	}

	private void copyImageAt(short[] data, Image image, int[] texturepos) {
		int startx = texturepos[0] * TEXTURE_GRID;
		int starty = texturepos[1] * TEXTURE_GRID;
		int maxx = startx + texturepos[2] * TEXTURE_GRID;
		int maxy = starty + texturepos[2] * TEXTURE_GRID;

		for (int x = startx; x < maxx; x += image.getWidth()) {
			for (int y = starty; y < maxy; y += image.getHeight()) {
				int width = Math.min(image.getWidth(), maxx - x);
				int height = Math.min(image.getHeight(), maxy - y);
				copyImage(data, image, x, y, width, height);
			}
		}
	}

	/**
	 * Copys the left top image corner to the buffer at (x, y), assuming the
	 * buffer is a TEXTURE_SIZE wide image.
	 * 
	 * @param data
	 * @param image
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	private void copyImage(short[] data, Image image, int x, int y, int width,
	        int height) {
		short[] sourceData = image.getData().array();
		for (int dy = 0; dy < height && dy < image.getHeight(); dy++) {
			System.arraycopy(sourceData, image.getWidth() * dy, data,
			        TEXTURE_SIZE * (y + dy) + x, width);
		}

	}

	/**
	 * gets the image number of the border
	 * 
	 * @param useSecond
	 * @param left
	 * @param bottom
	 * @return
	 */
	private int getBorder(ELandscapeType outer, ELandscapeType inner,
	        boolean useSecond) {
		int index;

		// grass <=> dessert
		if (outer == ELandscapeType.GRASS && inner == ELandscapeType.DESERT) {
			index = 181;
		} else if (outer == ELandscapeType.DESERT
		        && inner == ELandscapeType.GRASS) {
			index = 183;

			// water <=> sand
		} else if (outer == ELandscapeType.WATER
		        && inner == ELandscapeType.SAND) {
			index = 39;
		} else if (outer == ELandscapeType.SAND
		        && inner == ELandscapeType.WATER) {
			index = 37;

			// grass <=> mountain
		} else if (outer == ELandscapeType.GRASS
		        && inner == ELandscapeType.MOUNTAINBORDER) {
			index = 116;
		} else if (outer == ELandscapeType.MOUNTAINBORDER
		        && inner == ELandscapeType.GRASS) {
			index = 118;

		} else if (outer == ELandscapeType.MOUNTAINBORDER
		        && inner == ELandscapeType.MOUNTAIN) {
			index = 120;
		} else if (outer == ELandscapeType.MOUNTAIN
		        && inner == ELandscapeType.MOUNTAINBORDER) {
			index = 122;
			// TODO: one more outer circle with 124/126

			// mountain <=> snow
		} else if (outer == ELandscapeType.MOUNTAIN
		        && inner == ELandscapeType.SNOW) {
			index = 156;
		} else if (outer == ELandscapeType.SNOW
		        && inner == ELandscapeType.MOUNTAIN) {
			index = 158;

			// earth <=> grass
		} else if (outer == ELandscapeType.EARTH
		        && inner == ELandscapeType.GRASS) {
			index = 170;
		} else if (outer == ELandscapeType.GRASS
		        && inner == ELandscapeType.EARTH) {
			index = 168;

			// grass <=> dry grass
		} else if (outer == ELandscapeType.GRASS
		        && inner == ELandscapeType.DRY_GRASS) {
			index = 116;
		} else if (outer == ELandscapeType.DRY_GRASS
		        && inner == ELandscapeType.GRASS) {
			index = 118;

			// dry grass <=> desert
		} else if (outer == ELandscapeType.DRY_GRASS
		        && inner == ELandscapeType.DESERT) {
			index = 136;
		} else if (outer == ELandscapeType.DESERT
		        && inner == ELandscapeType.DRY_GRASS) {
			index = 138;

			// river <=> grass
		} else if (outer == ELandscapeType.GRASS
		        && inner == ELandscapeType.RIVER1) {
			index = 52;
		} else if (outer == ELandscapeType.RIVER1
		        && inner == ELandscapeType.GRASS) {
			index = 54;
		} else if (outer == ELandscapeType.GRASS
		        && inner == ELandscapeType.RIVER1) {
			index = 56;
		} else if (outer == ELandscapeType.RIVER2
		        && inner == ELandscapeType.GRASS) {
			index = 58;
		} else if (outer == ELandscapeType.GRASS
		        && inner == ELandscapeType.RIVER3) {
			index = 60;
		} else if (outer == ELandscapeType.RIVER3
		        && inner == ELandscapeType.GRASS) {
			index = 62;
		} else if (outer == ELandscapeType.GRASS
		        && inner == ELandscapeType.RIVER4) {
			index = 64;
		} else if (outer == ELandscapeType.RIVER4
		        && inner == ELandscapeType.GRASS) {
			index = 66;

			// sand <=> river
		} else if (outer == ELandscapeType.SAND
		        && inner == ELandscapeType.RIVER1) {
			index = 68;
		} else if (outer == ELandscapeType.RIVER1
		        && inner == ELandscapeType.SAND) {
			index = 70;
		} else if (outer == ELandscapeType.SAND
		        && inner == ELandscapeType.RIVER1) {
			index = 72;
		} else if (outer == ELandscapeType.RIVER2
		        && inner == ELandscapeType.SAND) {
			index = 74;
		} else if (outer == ELandscapeType.SAND
		        && inner == ELandscapeType.RIVER3) {
			index = 76;
		} else if (outer == ELandscapeType.RIVER3
		        && inner == ELandscapeType.SAND) {
			index = 78;
		} else if (outer == ELandscapeType.SAND
		        && inner == ELandscapeType.RIVER4) {
			index = 80;
		} else if (outer == ELandscapeType.RIVER4
		        && inner == ELandscapeType.SAND) {
			index = 82;

			// grass <=> sand
		} else if (outer == ELandscapeType.GRASS
		        && inner == ELandscapeType.SAND) {
			index = 114;
		} else if (outer == ELandscapeType.SAND
		        && inner == ELandscapeType.GRASS) {
			index = 112;

			// grass <=> flattened
		} else if (outer == ELandscapeType.GRASS
		        && inner == ELandscapeType.FLATTENED) {
			index = 172;
		} else if (outer == ELandscapeType.SAND
		        && inner == ELandscapeType.FLATTENED) {
			index = 174;

		} else {
			index = outer.getImageNumber();
			useSecond = false; // force!
		}

		if (useSecond) {
			index += 1;
		}

		return index;
	}

	/**
	 * Draws a given map content.
	 * 
	 * @param context
	 *            The context to draw at.
	 */
	public void drawMapContent(MapDrawContext context) {
		float[] geometry = getGeometry(context);

		GLDrawContext gl = context.getGl();
		gl.glPushMatrix();
		gl.glScalef(1, 1, 0);
		gl.glMultMatrixf(context.getConverter().getMatrixWithHeight(), 0);

		gl.color(1, 1, 1, 1);
		gl.drawTrianglesWithTextureColored(getTexture(context.getGl()),
		        geometry);

		gl.glPopMatrix();
	}

	private float[] getGeometry(MapDrawContext context) {
		IntRectangle screen =
		        context.getScreen().getPosition().bigger(EXTRA_PADDING);
		MapRectangle area = context.getConverter().getMapForScreen(screen);
		if (area.getLineLength() + 1 != bufferwidth
		        || area.getLines() != bufferheight) {
			bufferwidth = area.getLineLength() + 1;
			bufferheight = area.getLines();
			int count = bufferheight * (bufferwidth);
			geometryBuffer = new float[count * 2 * 3 * VERTEX_SIZE];
			fogOfWarStatus = new byte[count * 4];
			geometryHash = new int[count];
			oldBufferPosition = null;
		}

		for (int line = 0; line < bufferheight; line++) {
			int y = area.getLineY(line);
			int minx = area.getLineStartX(line);
			int maxx = area.getLineEndX(line) + 1;
			for (int x = minx; x < maxx; x++) {
				if (oldBufferPosition == null
				        || !oldBufferPosition.contains(x, y)) {
					redrawPoint(context, x, y, false);
				} else if (geometryHash[getBufferPosition(y, x)] != getContextHash(
				        context, x, y)) {
					redrawPoint(context, x, y, true);
				}
			}
		}

		oldBufferPosition = area;

		return geometryBuffer;
	}

	private void redrawPoint(MapDrawContext context, int x, int y,
	        boolean wasVisible) {
		int pointOffset = getBufferPosition(y, x);
		boolean redrawNextTime = false;
		if (x >= 0 && y >= 0 && x < context.getMap().getWidth() - 1
		        && y < context.getMap().getHeight() - 1) {
			if (wasVisible) {
				boolean finishedDimming = true;
				finishedDimming &=
				                dimFogOfWarBuffer(context, (pointOffset * 4),
				                        x, y);
				finishedDimming &=
				        dimFogOfWarBuffer(context, (pointOffset * 4) + 1,
				                x + 1, y);
				finishedDimming &=
				        dimFogOfWarBuffer(context, (pointOffset * 4) + 2, x,
				                y + 1);
				finishedDimming &=
				        dimFogOfWarBuffer(context, (pointOffset * 4) + 3,
				                x + 1, y + 1);
				redrawNextTime = !finishedDimming;
			} else {
				addFogOfWarBuffer(context, (pointOffset * 4), x, y);
				addFogOfWarBuffer(context, (pointOffset * 4) + 1, x + 1, y);
				addFogOfWarBuffer(context, (pointOffset * 4) + 2, x, y + 1);
				addFogOfWarBuffer(context, (pointOffset * 4) + 3, x + 1, y + 1);
			}
			addTrianglesToGeometry(context, geometryBuffer, pointOffset * 2 * 3
			        * VERTEX_SIZE, x, y, pointOffset * 4);
		} else {
			addPseudoTrianglesToGeometry(context, geometryBuffer, pointOffset
			        * 2 * 3 * VERTEX_SIZE, x, y);
		}
		if (redrawNextTime) {
			geometryHash[pointOffset] = HASH_INVALID;
		} else {
			// TODO: compute hash once.
			geometryHash[pointOffset] = getContextHash(context, x, y);
		}
	}

	private void addFogOfWarBuffer(MapDrawContext context, int offset, int x,
	        int y) {
		fogOfWarStatus[offset] = context.getFogOfWar().getVisibleStatus(x, y);
	}

	/**
	 * Dims the fog of war buffer
	 * 
	 * @param context
	 * @param offset
	 * @param x
	 * @param y
	 * @return true if and only if the dim has finished.
	 */
	private boolean dimFogOfWarBuffer(MapDrawContext context, int offset,
	        int x, int y) {
		byte newFog = context.getFogOfWar().getVisibleStatus(x, y);
		fogOfWarStatus[offset] = dim(fogOfWarStatus[offset], newFog);
		return fogOfWarStatus[offset] == newFog;
	}

	private byte dim(byte value, byte dimTo) {
		if (value < dimTo - DIM_MAX) {
			return (byte) (dimTo - DIM_MAX);
		} else if (value > dimTo + DIM_MAX) {
			return (byte) (dimTo + DIM_MAX);
		} else if (value > dimTo) {
			return (byte) (value - 1);
		} else if (value < dimTo) {
			return (byte) (value + 1);
		} else {
			return value;
		}
	}

	private int getContextHash(MapDrawContext context, int x, int y) {
		return getHash(context, x + 1, y + 1) * 104729
		        + getHash(context, x + 1, y) * 104729
		        + getHash(context, x, y + 1) * 7919 + getHash(context, x, y);
	}

	private int getHash(MapDrawContext context, int x, int y) {
		if (x < 0 || x >= context.getMap().getWidth() || y < 0
		        || y >= context.getMap().getHeight()) {
			return 0;
		} else {
			return context.getMap().getLandscapeTypeAt(x, y).ordinal() * 10000
			        + context.getMap().getHeightAt(x, y) * 100
			        + context.getFogOfWar().getVisibleStatus(x, y);
		}
	}

	private int getBufferPosition(int y, int x) {
		int linepos = y % bufferheight;
		int colpos = x % bufferwidth;
		while (linepos < 0) {
			linepos += bufferheight;
		}
		while (colpos < 0) {
			colpos += bufferwidth;
		}

		return (linepos * bufferwidth + colpos);
	}

	/**
	 * Adds the two triangles for a point to the list of verteces
	 * 
	 * @param context
	 * @param geometry
	 * @param offset
	 * @param x
	 * @param y
	 * @param fogOfWar
	 */
	private void addTrianglesToGeometry(MapDrawContext context,
	        float[] geometry, int offset, int x, int y, int fogBase) {
		addTriangle1ToGeometry(context, geometry, offset, x, y, fogBase);
		addTriangle2ToGeometry(context, geometry, offset + 3 * VERTEX_SIZE, x,
		        y, fogBase);
	}

	private void addPseudoTrianglesToGeometry(MapDrawContext context,
	        float[] geometry, int offset, int x, int y) { // manually do
		                                                  // everything...
		addBlackPointToGeometry(context, geometry, offset, x, y);
		addBlackPointToGeometry(context, geometry, offset + VERTEX_SIZE, x,
		        y + 1);
		addBlackPointToGeometry(context, geometry, offset + 2 * VERTEX_SIZE,
		        x + 1, y + 1);
		addBlackPointToGeometry(context, geometry, offset + 3 * VERTEX_SIZE, x,
		        y);
		addBlackPointToGeometry(context, geometry, offset + 4 * VERTEX_SIZE,
		        x + 1, y + 1);
		addBlackPointToGeometry(context, geometry, offset + 5 * VERTEX_SIZE,
		        x + 1, y);
	}

	// private boolean useRenderbuffer(GL2 gl) {
	// return gl.isExtensionAvailable("GL_EXT_framebuffer_object");
	// }

	/**
	 * Draws the triangle that is facing up
	 * 
	 * @param context
	 * @param geometry
	 * @param offset
	 * @param x
	 * @param y
	 */
	private void addTriangle1ToGeometry(MapDrawContext context,
	        float[] geometry, int offset, int x, int y, int fogBase) {
		ELandscapeType toplandscape = context.getLandscape(x, y);
		ELandscapeType leftlandscape = context.getLandscape(x, y + 1);
		ELandscapeType rightlandscape = context.getLandscape(x + 1, y + 1);

		boolean useSecond = ((x ^ y) & 0x1) == 0;
		ETextureOrientation texturePos;
		int textureindex;
		if (toplandscape == leftlandscape && toplandscape == rightlandscape) {
			texturePos = ETextureOrientation.CONTINUOUS_UP;
			textureindex = toplandscape.getImageNumber();
		} else if (leftlandscape == rightlandscape) {
			texturePos = ETextureOrientation.BOTTOM;
			textureindex = getBorder(leftlandscape, toplandscape, useSecond);
		} else if (leftlandscape == toplandscape) {
			texturePos = ETextureOrientation.TOPLEFT;
			textureindex = getBorder(leftlandscape, rightlandscape, useSecond);
		} else {
			texturePos = ETextureOrientation.TOPRIGHT;
			textureindex = getBorder(toplandscape, leftlandscape, useSecond);
		}

		addPointToGeometry(context, geometry, offset, x, y, fogBase + 0);
		addPointToGeometry(context, geometry, offset + VERTEX_SIZE, x, y + 1,
		        fogBase + 2);
		addPointToGeometry(context, geometry, offset + 2 * VERTEX_SIZE, x + 1,
		        y + 1, fogBase + 3);
		int[] positions = TEXTURE_POSITIONS[textureindex];
		texturePos.addCoordsTo(geometry, offset + 3, VERTEX_SIZE, x, y,
		        positions[0] * TEXTURE_GRID, positions[1] * TEXTURE_GRID,
		        TEXTURE_SIZE);
	}

	private void addPointToGeometry(MapDrawContext context, float[] geometry,
	        int offset, int x, int y, int fogOffset) {
		geometry[offset] = x;
		geometry[offset + 1] = y;
		geometry[offset + 2] = context.getHeight(x, y);
		addVertexcolor(context, geometry, offset + 5, x, y, fogOffset);
	}

	private void addBlackPointToGeometry(MapDrawContext context,
	        float[] geometry, int offset, int x, int y) {
		geometry[offset] = x;
		geometry[offset + 1] = y;
		geometry[offset + 2] = context.getHeight(x, y);
		geometry[offset + 5] = 0;
		geometry[offset + 6] = 0;
		geometry[offset + 7] = 0;
	}

	private void addTriangle2ToGeometry(MapDrawContext context,
	        float[] geometry, int offset, int x, int y, int fogBase) {
		ELandscapeType leftlandscape = context.getLandscape(x, y);
		ELandscapeType bottomlandscape = context.getLandscape(x + 1, y + 1);
		ELandscapeType rightlandscape = context.getLandscape(x + 1, y);

		boolean useSecond = (x & 0x1) == 0;
		ETextureOrientation texturePos;
		int textureindex;
		if (bottomlandscape == leftlandscape
		        && bottomlandscape == rightlandscape) {
			texturePos = ETextureOrientation.CONTINUOUS_DOWN;
			textureindex = bottomlandscape.getImageNumber();
		} else if (leftlandscape == rightlandscape) {
			texturePos = ETextureOrientation.TOP;
			textureindex = getBorder(leftlandscape, bottomlandscape, useSecond);
		} else if (leftlandscape == bottomlandscape) {
			texturePos = ETextureOrientation.BOTTOMLEFT;
			textureindex = getBorder(leftlandscape, rightlandscape, useSecond);
		} else {
			texturePos = ETextureOrientation.BOTTOMRIGHT;
			textureindex = getBorder(rightlandscape, leftlandscape, useSecond);
		}

		addPointToGeometry(context, geometry, offset, x, y, fogBase + 0);
		addPointToGeometry(context, geometry, offset + VERTEX_SIZE, x + 1,
		        y + 1, fogBase + 3);
		addPointToGeometry(context, geometry, offset + 2 * VERTEX_SIZE, x + 1,
		        y, fogBase + 1);
		int[] positions = TEXTURE_POSITIONS[textureindex];
		texturePos.addCoordsTo(geometry, offset + 3, VERTEX_SIZE, x, y,
		        positions[0] * TEXTURE_GRID, positions[1] * TEXTURE_GRID,
		        TEXTURE_SIZE);
	}

	private void addVertexcolor(MapDrawContext context, float[] geometry,
	        int offset, int x, int y, int fogOffset) {
		float color;

		if (x <= 0 || x >= context.getMap().getWidth() - 2 || y <= 0
		        || y >= context.getMap().getHeight() - 2
		        || context.getFogOfWar().getVisibleStatus(x, y) <= 0) {
			color = 0;
		} else {
			int height1 = context.getHeight(x, y);
			int height2 = context.getHeight(x, y + 1);
			color = 0.9f + (height2 - height1) * .1f;
			if (color > 1.0f) {
				color = 1.0f;
			} else if (color < 0.4f) {
				color = 0.4f;
			}
			color *= (float) fogOfWarStatus[fogOffset] / FogOfWar.VISIBLE;
		}
		geometry[offset] = color;
		geometry[offset + 1] = color;
		geometry[offset + 2] = color;
	}

	// private void drawWithRenderbuffer(MapDrawContext context) {
	// IntRectangle screen = context.getScreen().getPosition();
	// GL2 gl = ((JOGLDrawContext)context.getGl()).getGl2();
	// int width = screen.getWidth();
	// int height = screen.getHeight();
	// int newChecksum = computeChecksum(context);
	// if (!screen.equals(bufferScreen) || checksum != newChecksum) {
	// if (bufferScreen == null || width != bufferScreen.getWidth()
	// || height != bufferScreen.getHeight()) {
	// createNewBuffer(gl, width, height);
	// }
	// bufferScreen = screen;
	// checksum = newChecksum;
	// gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, buffer);
	// gl.glPushAttrib(GL2.GL_VIEWPORT_BIT);
	// gl.glViewport(0, 0, textureWidth, textureHeight);
	// gl.glClearColor(0, 0, 0, 1);
	// gl.glClear(GL.GL_COLOR_BUFFER_BIT);
	// draw(context);
	// gl.glPopAttrib();
	// gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
	// }
	//
	// gl.glBindTexture(GL.GL_TEXTURE_2D, texture);
	// gl.glEnable(GL.GL_TEXTURE_2D);
	// gl.glColor3f(1, 1, 1);
	// gl.glBegin(GL2.GL_QUADS);
	// gl.glTexCoord2f(0, 0);
	// gl.glVertex2f(screen.getMinX(), screen.getMinY());
	// gl.glTexCoord2f(0, 1);
	// gl.glVertex2f(screen.getMinX(), screen.getMinY() + textureHeight);
	// gl.glTexCoord2f(1, 1);
	// gl.glVertex2f(screen.getMinX() + textureWidth, screen.getMinY()
	// + textureHeight);
	// gl.glTexCoord2f(1, 0);
	// gl.glVertex2f(screen.getMinX() + textureWidth, screen.getMinY());
	// gl.glEnd();
	// }

	/**
	 * Newly creates the buffer.
	 * 
	 * @param width
	 * @param height
	 */
	// private void createNewBuffer(GL2 gl, int width, int height) {
	// if (buffer != 0) {
	// gl.glDeleteFramebuffers(1, new int[] {
	// buffer
	// }, 0);
	// gl.glDeleteRenderbuffers(1, new int[] {
	// renderbuffer
	// }, 0);
	// gl.glDeleteTextures(0, new int[] {
	// texture
	// }, 0);
	// }
	//
	// textureWidth = TextureCalculator.supportedTextureSize(gl, width);
	// textureHeight = TextureCalculator.supportedTextureSize(gl, height);
	//
	// int[] buffers = new int[1];
	// gl.glGenFramebuffers(1, buffers, 0);
	// buffer = buffers[0];
	// gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, buffer);
	//
	// // renderbuffer
	// gl.glGenRenderbuffers(1, buffers, 0);
	// renderbuffer = buffers[0];
	// gl.glBindRenderbuffer(GL.GL_RENDERBUFFER, renderbuffer);
	// gl.glRenderbufferStorage(GL.GL_RENDERBUFFER, GL.GL_RGBA8, textureWidth,
	// textureHeight);
	// gl.glFramebufferRenderbuffer(GL2.GL_FRAMEBUFFER,
	// GL.GL_COLOR_ATTACHMENT0, GL.GL_RENDERBUFFER, renderbuffer);
	//
	// // texture
	// gl.glGenTextures(1, buffers, 0);
	// texture = buffers[0];
	// gl.glBindTexture(GL.GL_TEXTURE_2D, texture);
	// gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA8, textureWidth,
	// textureHeight, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, null);
	// gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S,
	// GL.GL_CLAMP_TO_EDGE);
	// gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,
	// GL.GL_CLAMP_TO_EDGE);
	// gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
	// GL.GL_LINEAR);
	// gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
	// GL.GL_LINEAR_MIPMAP_LINEAR);
	// gl.glGenerateMipmap(GL.GL_TEXTURE_2D);
	// gl.glFramebufferTexture2D(GL.GL_FRAMEBUFFER, GL.GL_COLOR_ATTACHMENT0,
	// GL.GL_TEXTURE_2D, texture, 0);
	//
	// int status = gl.glCheckFramebufferStatus(GL.GL_FRAMEBUFFER);
	//
	// if (status != GL.GL_FRAMEBUFFER_COMPLETE) {
	// System.err.println("incomplete framebuffer");
	// }
	// }

}
