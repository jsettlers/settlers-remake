/*******************************************************************************
 * Copyright (c) 2015 - 2017
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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.BitSet;

import go.graphics.GLDrawContext;
import go.graphics.GLDrawContext.GLBuffer;
import go.graphics.GeometryHandle;
import go.graphics.IllegalBufferException;
import go.graphics.TextureHandle;

import jsettlers.common.CommonConstants;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.IGraphicsBackgroundListener;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.position.FloatRectangle;
import jsettlers.graphics.map.MapDrawContext;
import jsettlers.graphics.reader.AdvancedDatFileReader;
import jsettlers.graphics.reader.DatBitmapReader;
import jsettlers.graphics.reader.ImageArrayProvider;
import jsettlers.graphics.reader.ImageMetadata;

/**
 * The map background.
 * <p>
 * This class draws the map background (landscape) layer. It has support for smooth FOW transitions and buffers the background to make it faster.
 * 
 * @author Michael Zangl
 */
public class Background implements IGraphicsBackgroundListener {

	private static final int LAND_FILE = 0;

	/**
	 * The base texture size.
	 */
	private static final int TEXTURE_SIZE = 1024;

	/**
	 * Our base texture is divided into multiple squares that all hold a single texture. Continuous textures occupy 5*5 squares
	 */
	private static final int TEXTURE_GRID = 32;

	/**
	 * Where are the textures on the map?
	 * <p>
	 * x and y coordinates are in Grid units.
	 * <p>
	 * The third entry is the size of the texture. It must be 1 for border tiles and 2..5 for continuous images. Always 1 more than they are wide.
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
	 * |41|42|43|44|45|46|47|48|49|50|51|52|53|54|55|56|57|58|59|60|61|62|63|64|65|66|67|68|69|70|71|72|
	 * +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+ --- ↓ +100
	 * |73|74|75|76|77|78|79|80|81|82|83|84|85|86|87|88|89|90|91|92|93|94|95|96|97|00|01|02|03|04|05|06|
	 * +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
	 * |07|08|09|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31|32|33|34|35|36|37|38|
	 * +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
	 * |39|40|41|42|43|44|45|46|47|48|49|50|51|52|53|54|55|56|57|58|59|60|61|62|63|64|65|66|67|68|69|70|
	 * +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+ --- ↓ +200
	 * |71|72|73|74|75|  |77|78|79|80|81|82|83|84|85|86|87|88|89|90|91|92|93|94|95|96|97|98|99|00|01|02|
	 * +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
	 * |     |     |     |     |     |     |     |              |              |
	 * | 011 | 012 | 013 | 014 | 015 | 016 | 017 |              |              |
	 * |     |     |     |     |     |     |     |              |     230      |
	 * +--+--+--+--+--+--+--+--+--+--+--+--+--+--+  217         |              |--+--+--+--+--+--+--+--+
	 * +03|04|05|06|07|08|09|10|11|12|13|14|15|16|              |              |--+--+--+--+--+--+--+--+
	 * +--+--+--+--+--+--+--+--+--+--+--+--+--+--+              |              |--+--+--+--+--+--+--+--+
	 * +18|19|20|21|22|23|24|25|26|27|28|29|31|32|              |              |--+--+--+--+--+--+--+--+
	 * +--+--+--+--+--+--+--+--+--+--+--+--+--+--+              |              |--+--+--+--+--+--+--+--+
	 * +33|34|                                   |              |              |--+--+--+--+--+--+--+--+
	 * +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
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
			/* 11: small, continuous */{
					0, 20, 2
			},
			/* 12: small, continuous */{
					2, 20, 2
			},
			/* 13: small, continuous */{
					4, 20, 2
			},
			/* 14: small, continuous */{
					6, 20, 2
			},
			/* 15: small, continuous */{
					8, 20, 2
			},
			/* 16: small, continuous */{
					10, 20, 2
			},
			/* 17: small, continuous */{
					12, 20, 2
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

			/* 203: small */{
					0, 22, 1
			},
			/* 204: small */{
					1, 22, 1
			},
			/* 205: small */{
					2, 22, 1
			},
			/* 206: small */{
					3, 22, 1
			},
			/* 207: small */{
					4, 22, 1
			},
			/* 208: small */{
					5, 22, 1
			},
			/* 209: small */{
					6, 22, 1
			},
			/* 210: small */{
					7, 22, 1
			},
			/* 211: small */{
					8, 22, 1
			},
			/* 212: small */{
					9, 22, 1
			},

			/* 213: small */{
					10, 22, 1
			},
			/* 214: small */{
					11, 22, 1
			},
			/* 215: small */{
					12, 22, 1
			},
			/* 216: small */{
					13, 22, 1
			},
			/* 217: big */{
					14, 20, 5
			},
			/* 218: small */{
					1, 23, 1
			},
			/* 219: small */{
					2, 23, 1
			},
			/* 220: small */{
					3, 23, 1
			},
			/* 221: small */{
					4, 23, 1
			},
			/* 222: small */{
					5, 23, 1
			},

			/* 223: small */{
					6, 23, 1
			},
			/* 224: small */{
					7, 23, 1
			},
			/* 225: small */{
					8, 23, 1
			},
			/* 226: small */{
					9, 23, 1
			},
			/* 227: small */{
					10, 23, 1
			},
			/* 228: small */{
					11, 23, 1
			},
			/* 229: small */{
					12, 23, 1
			},
			/* 230: big */{
					19, 20, 5
			},
			/* 231: small */{
					13, 23, 1
			},
			/* 232: small */{
					0, 24, 1
			},
			/* 233: small */{
					1, 24, 1
			},
			/* 234: small */{
					2, 24, 1
			},

			// ...
	};

	private final BitSet fowDimmed = new BitSet();

	private static final short FLOAT_SIZE = 4;
	/**
	 * How many bytes are needed per vertex
	 */
	private static final short VERTEX_SIZE = 6 * FLOAT_SIZE;

	private static final byte DIM_MAX = 20;

	private static final byte[] BLACK = new byte[] {
			0, 0, 0, (byte) 255
	};
	private static final Object preloadMutex = new Object();

	/**
	 * Offset of color definition in bytes, relative to vertex start
	 */
	// private static final int COLOR_OFFSET = 5 * FLOAT_SIZE;

	private byte[] fogOfWarStatus = new byte[1];
	private MapRectangle oldBufferPosition = new MapRectangle(0, 0, 0, 0);
	private int bufferWidth = 1; // in map points.
	private int bufferHeight = 1; // in map points.

	private static TextureHandle texture = null;

	private GeometryHandle geometryhandle = null;

	private int geometrytirs;

	private BitSet geometryInvalid = new BitSet();

	private boolean mapViewResized;

	private static short[] preloadedTexture = null;

	private static short[] getTexture() {
		short[] data = new short[TEXTURE_SIZE * TEXTURE_SIZE];
		try {
			addTextures(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	public static void preloadTexture() {
		synchronized (preloadMutex) {
			if (preloadedTexture == null) {
				preloadedTexture = getTexture();
				ImageProvider.getInstance().addPreloadTask(Background::getTexture);
			}
		}
	}

	private static TextureHandle getTexture(GLDrawContext context) {
		if (texture == null || !texture.isValid()) {
			long startTime = System.currentTimeMillis();
			short[] data;
			synchronized (preloadMutex) {
				if (preloadedTexture != null) {
					data = preloadedTexture;
					// free the array
					preloadedTexture = null;
				} else {
					data = getTexture();
				}
			}
			ByteBuffer buffer = ByteBuffer.allocateDirect(data.length * 2).order(ByteOrder.nativeOrder());
			buffer.asShortBuffer().put(data);
			texture = context.generateTexture(TEXTURE_SIZE, TEXTURE_SIZE, buffer.asShortBuffer());

			System.out.println("Background texture generated in " + (System.currentTimeMillis() - startTime) + "ms");
		}
		return texture;
	}

	private static class ImageWriter implements ImageArrayProvider {
		int arrayOffset;
		int cellSize;
		int maxOffset;
		short[] data;

		// nothing to do. We assume images are a rectangle and have the right size.
		@Override
		public void startImage(int width, int height) throws IOException {
		}

		@Override
		public void writeLine(short[] data, int length) throws IOException {
			if (arrayOffset < maxOffset) {
				for (int i = 0; i < cellSize; i++) {
					this.data[arrayOffset + i] = data[i % length];
				}
				arrayOffset += TEXTURE_SIZE;
			}
		}
	}

	/**
	 * Generates the texture data.
	 * 
	 * @param data
	 *            The texture data buffer.
	 * @throws IOException
	 */
	private static void addTextures(short[] data) throws IOException {
		AdvancedDatFileReader reader = ImageProvider.getInstance().getFileReader(LAND_FILE);
		if (reader == null) {
			throw new IOException("Could not get a file reader for the file.");
		}
		ImageWriter imageWriter = new ImageWriter();
		imageWriter.data = data;

		ImageMetadata meta = new ImageMetadata();

		for (int index = 0; index < TEXTURE_POSITIONS.length; index++) {
			int[] position = TEXTURE_POSITIONS[index];
			int x = position[0] * TEXTURE_GRID;
			int y = position[1] * TEXTURE_GRID;
			int start = y * TEXTURE_SIZE + x;
			int cellSize = position[2] * TEXTURE_GRID;
			int end = (y + cellSize) * TEXTURE_SIZE + x;
			imageWriter.arrayOffset = start;
			imageWriter.cellSize = cellSize;
			imageWriter.maxOffset = end;

			DatBitmapReader.uncompressImage(reader.getReaderForLandscape(index), reader.getLandscapeTranslator(), meta, imageWriter);

			// freaky stuff
			int arrayOffset = imageWriter.arrayOffset;
			int l = arrayOffset - start;
			while (arrayOffset < end) {
				System.arraycopy(data, arrayOffset - l, data, arrayOffset, cellSize);
				arrayOffset += TEXTURE_SIZE;
			}
		}
	}

	/**
	 * Gets the image number of the border
	 * 
	 * @param outer
	 *            The outer landscape (that has two triangle edges).
	 * @param inner
	 *            The inner landscape.
	 * @param useSecond
	 *            If it is true, the secondary texture is used.
	 * @return The texture.
	 */
	private static int getBorder(ELandscapeType outer, ELandscapeType inner, boolean useSecond) {
		int index;

		// water <=> water
		if (outer == ELandscapeType.WATER1 && inner == ELandscapeType.WATER2) {
			index = 84;
		} else if (outer == ELandscapeType.WATER2 && inner == ELandscapeType.WATER1) {
			index = 86;

		} else if (outer == ELandscapeType.WATER2 && inner == ELandscapeType.WATER3) {
			index = 88;
		} else if (outer == ELandscapeType.WATER3 && inner == ELandscapeType.WATER2) {
			index = 90;

		} else if (outer == ELandscapeType.WATER3 && inner == ELandscapeType.WATER4) {
			index = 92;
		} else if (outer == ELandscapeType.WATER4 && inner == ELandscapeType.WATER3) {
			index = 94;

		} else if (outer == ELandscapeType.WATER4 && inner == ELandscapeType.WATER5) {
			index = 96;
		} else if (outer == ELandscapeType.WATER5 && inner == ELandscapeType.WATER4) {
			index = 98;

		} else if (outer == ELandscapeType.WATER5 && inner == ELandscapeType.WATER6) {
			index = 100;
		} else if (outer == ELandscapeType.WATER6 && inner == ELandscapeType.WATER5) {
			index = 102;

		} else if (outer == ELandscapeType.WATER6 && inner == ELandscapeType.WATER7) {
			index = 104;
		} else if (outer == ELandscapeType.WATER7 && inner == ELandscapeType.WATER6) {
			index = 106;

		} else if (outer == ELandscapeType.WATER7 && inner == ELandscapeType.WATER8) {
			index = 108;
		} else if (outer == ELandscapeType.WATER8 && inner == ELandscapeType.WATER7) {
			index = 110;

			// grass <=> dessert
		} else if (outer == ELandscapeType.GRASS && inner == ELandscapeType.DESERT) {
			index = 181;
		} else if (outer == ELandscapeType.DESERT && inner == ELandscapeType.GRASS) {
			index = 183;

			// water <=> sand
		} else if (outer == ELandscapeType.WATER1 && inner == ELandscapeType.SAND) {
			index = 39;
		} else if (outer == ELandscapeType.SAND && inner == ELandscapeType.WATER1) {
			index = 37;

			// grass <=> mountain
		} else if (outer == ELandscapeType.GRASS && inner == ELandscapeType.MOUNTAINBORDEROUTER) {
			index = 116;
		} else if (outer == ELandscapeType.MOUNTAINBORDEROUTER && inner == ELandscapeType.GRASS) {
			index = 118;

		} else if (outer == ELandscapeType.MOUNTAINBORDEROUTER && inner == ELandscapeType.MOUNTAINBORDER) {
			index = 120;
		} else if (outer == ELandscapeType.MOUNTAINBORDER && inner == ELandscapeType.MOUNTAINBORDEROUTER) {
			index = 122;

		} else if (outer == ELandscapeType.MOUNTAINBORDER && inner == ELandscapeType.MOUNTAIN) {
			index = 124;
		} else if (outer == ELandscapeType.MOUNTAIN && inner == ELandscapeType.MOUNTAINBORDER) {
			index = 126;

			// mountain <=> snow
		} else if (outer == ELandscapeType.MOUNTAIN && inner == ELandscapeType.SNOW) {
			index = 156; // OLD!
		} else if (outer == ELandscapeType.SNOW && inner == ELandscapeType.MOUNTAIN) {
			index = 158; // OLD!
		} else if (outer == ELandscapeType.MOUNTAIN && inner == ELandscapeType.SNOWBORDER) {
			index = 156;
		} else if (outer == ELandscapeType.SNOWBORDER && inner == ELandscapeType.MOUNTAIN) {
			index = 158;
		} else if (outer == ELandscapeType.SNOWBORDER && inner == ELandscapeType.SNOW) {
			index = 160;
		} else if (outer == ELandscapeType.SNOW && inner == ELandscapeType.SNOWBORDER) {
			index = 162;

			// earth <=> grass
		} else if (outer == ELandscapeType.EARTH && inner == ELandscapeType.GRASS) {
			index = 170;
		} else if (outer == ELandscapeType.GRASS && inner == ELandscapeType.EARTH) {
			index = 168;

			// grass <=> dry grass
		} else if (outer == ELandscapeType.GRASS && inner == ELandscapeType.DRY_GRASS) {
			index = 116;
		} else if (outer == ELandscapeType.DRY_GRASS && inner == ELandscapeType.GRASS) {
			index = 118;

			// dry grass <=> desert
		} else if (outer == ELandscapeType.DRY_GRASS && inner == ELandscapeType.DESERT) {
			index = 136;
		} else if (outer == ELandscapeType.DESERT && inner == ELandscapeType.DRY_GRASS) {
			index = 138;

			// river <=> grass
		} else if (outer == ELandscapeType.GRASS && inner == ELandscapeType.RIVER1) {
			index = 52;
		} else if (outer == ELandscapeType.RIVER1 && inner == ELandscapeType.GRASS) {
			index = 54;
		} else if (outer == ELandscapeType.GRASS && inner == ELandscapeType.RIVER2) {
			index = 56;
		} else if (outer == ELandscapeType.RIVER2 && inner == ELandscapeType.GRASS) {
			index = 58;
		} else if (outer == ELandscapeType.GRASS && inner == ELandscapeType.RIVER3) {
			index = 60;
		} else if (outer == ELandscapeType.RIVER3 && inner == ELandscapeType.GRASS) {
			index = 62;
		} else if (outer == ELandscapeType.GRASS && inner == ELandscapeType.RIVER4) {
			index = 64;
		} else if (outer == ELandscapeType.RIVER4 && inner == ELandscapeType.GRASS) {
			index = 66;

			// sand <=> river
		} else if (outer == ELandscapeType.SAND && inner == ELandscapeType.RIVER1) {
			index = 68;
		} else if (outer == ELandscapeType.RIVER1 && inner == ELandscapeType.SAND) {
			index = 70;
		} else if (outer == ELandscapeType.SAND && inner == ELandscapeType.RIVER2) {
			index = 72;
		} else if (outer == ELandscapeType.RIVER2 && inner == ELandscapeType.SAND) {
			index = 74;
		} else if (outer == ELandscapeType.SAND && inner == ELandscapeType.RIVER3) {
			index = 76;
		} else if (outer == ELandscapeType.RIVER3 && inner == ELandscapeType.SAND) {
			index = 78;
		} else if (outer == ELandscapeType.SAND && inner == ELandscapeType.RIVER4) {
			index = 80;
		} else if (outer == ELandscapeType.RIVER4 && inner == ELandscapeType.SAND) {
			index = 82;

			// grass <=> sand
		} else if (outer == ELandscapeType.GRASS && inner == ELandscapeType.SAND) {
			index = 114;
		} else if (outer == ELandscapeType.SAND && inner == ELandscapeType.GRASS) {
			index = 112;

			// grass <=> flattened
		} else if (outer == ELandscapeType.GRASS && inner == ELandscapeType.FLATTENED) {
			index = 172;
		} else if (outer == ELandscapeType.FLATTENED && inner == ELandscapeType.GRASS) {
			index = 174;

			// moor <=> grass
		} else if (outer == ELandscapeType.GRASS && inner == ELandscapeType.MOORBORDER) {
			index = 201;
		} else if (outer == ELandscapeType.MOORBORDER && inner == ELandscapeType.GRASS) {
			index = 203;

		} else if (outer == ELandscapeType.MOORBORDER && inner == ELandscapeType.MOORINNER) {
			index = 205;
		} else if (outer == ELandscapeType.MOORINNER && inner == ELandscapeType.MOORBORDER) {
			index = 207;

		} else if (outer == ELandscapeType.MOORINNER && inner == ELandscapeType.MOOR) {
			index = 209;
		} else if (outer == ELandscapeType.MOOR && inner == ELandscapeType.MOORINNER) {
			index = 211;

			// flattened desert <=> desert

		} else if (outer == ELandscapeType.DESERT && inner == ELandscapeType.SHARP_FLATTENED_DESERT) {
			index = 218;
		} else if (outer == ELandscapeType.SHARP_FLATTENED_DESERT && inner == ELandscapeType.DESERT) {
			index = 220;
		} else if (outer == ELandscapeType.DESERT && inner == ELandscapeType.FLATTENED_DESERT) {
			index = 222;
		} else if (outer == ELandscapeType.FLATTENED_DESERT && inner == ELandscapeType.DESERT) {
			index = 224;

		} else if (outer == ELandscapeType.GRAVEL && inner == ELandscapeType.MOUNTAINBORDER) {
			index = 231;
		} else if (outer == ELandscapeType.MOUNTAINBORDER && inner == ELandscapeType.GRAVEL) {
			index = 233;

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
	 * @param screen
	 */
	public void drawMapContent(MapDrawContext context, FloatRectangle screen) {
		try {
			GLDrawContext gl = context.getGl();
			MapRectangle screenArea = context.getConverter().getMapForScreen(screen);
			mapViewResized = geometryhandle == null || !geometryhandle.isValid() || screenArea.getLineLength() + 1 != bufferWidth || screenArea.getLines() != bufferHeight;
			if (mapViewResized) {
				regenerateGeometry(gl, screenArea);
			}

			GLBuffer boundBuffer = gl.startWriteGeometry(geometryhandle);
			reloadGeometry(boundBuffer, screenArea, context);
			gl.endWriteGeometry(geometryhandle);
			gl.glPushMatrix();
			try {
				gl.glTranslatef(0, 0, -.1f);
				gl.glScalef(1, 1, 0);
				gl.glMultMatrixf(context.getConverter().getMatrixWithHeight(), 0);
				gl.color(1, 1, 1, 1);
				gl.drawTrianglesWithTextureColored(getTexture(context.getGl()), geometryhandle, geometrytirs);
			} finally {
				gl.glPopMatrix();
			}

			resetFOWDimStatus();
		} catch (IllegalBufferException e) {
			// TODO: Create crash report.
			e.printStackTrace();
		}
	}

	private void resetFOWDimStatus() {
		fowDimmed.clear();
	}

	private void regenerateGeometry(GLDrawContext gl, MapRectangle screenArea) {
		if (geometryhandle != null && geometryhandle.isValid()) {
			geometryhandle.delete();
		}
		bufferWidth = niceRoundUp(screenArea.getLineLength() + 1);
		bufferHeight = niceRoundUp(screenArea.getLines());
		int count = bufferHeight * bufferWidth;
		fogOfWarStatus = new byte[count * 4];
		geometryInvalid = new BitSet(count);
		geometrytirs = count * 2;

		geometryhandle = gl.generateGeometry(geometrytirs * 3 * VERTEX_SIZE);
	}

	private static int niceRoundUp(int i) {
		return i;
	}

	/**
	 * Gets the geometry of the background as array.
	 * 
	 * @param boundBuffer
	 *            The buffer of opengl.
	 * @param context
	 *            The context to use.
	 * @return The geometry as float array.
	 */
	private void reloadGeometry(GLBuffer boundBuffer, MapRectangle area, MapDrawContext context) {
		boolean hasInvalidFields = hasInvalidFields();

		int width = context.getMap().getWidth();
		int height = context.getMap().getHeight();
		int oldBufferTop = oldBufferPosition.getLineY(0);
		int oldBufferBottom = oldBufferTop + bufferHeight; // excluding

		for (int line = 0; line < bufferHeight; line++) {

			int y = area.getLineY(line);
			int minx = area.getLineStartX(line);
			int maxx = minx + bufferWidth;
			int oldMinX = 0;
			int oldMaxX = 0; // excluding
			if (y >= oldBufferTop && y < oldBufferBottom) {
				oldMinX = oldBufferPosition.getLineStartX(y - oldBufferTop);
				oldMaxX = oldMinX + bufferWidth;
			}
			boolean lineIsInMap = y >= 0 && y < height;

			for (int x = minx; x < maxx; x++) {
				int bufferPosition = getBufferPosition(y, x);
				if (mapViewResized || oldMinX > x || oldMaxX <= x) {
					redrawPoint(boundBuffer, context, x, y, false, bufferPosition);
				} else if (lineIsInMap && x >= 0 && x < width) {
					if (hasInvalidFields && getAndResetInvalid(bufferPosition)) {
						redrawPoint(boundBuffer, context, x, y, true, bufferPosition);
					} else if (context.getVisibleStatus(x, y) != fogOfWarStatus[bufferPosition * 4]) {
						redrawPoint(boundBuffer, context, x, y, true, bufferPosition);
						invalidatePoint(x - 1, y); // only for next pass
						invalidatePoint(x - 1, y - 1);
						invalidatePoint(x - 1, y - 1);
					}
				}
			}
		}

		oldBufferPosition = area;
	}

	private synchronized boolean getAndResetInvalid(int bufferPosition) {
		boolean invalid = geometryInvalid.get(bufferPosition);
		geometryInvalid.clear(bufferPosition);
		return invalid;
	}

	private synchronized boolean hasInvalidFields() {
		return !geometryInvalid.isEmpty();
	}

	/**
	 * Redraws a point on the map to the buffer.
	 * 
	 * @param boundBuffer
	 *            The buffer to use
	 * @param context
	 *            The context
	 * @param x
	 *            The x coordinate of the point
	 * @param y
	 *            The y coordinate of the point
	 * @param wasVisible
	 *            true if and only if the point was already in the buffer.
	 */
	private void redrawPoint(GLBuffer boundBuffer, MapDrawContext context, int x, int y, boolean wasVisible, int pointOffset) {
		boundBuffer.position(pointOffset * 2 * 3 * VERTEX_SIZE);

		if (x >= 0 && y >= 0 && x < context.getMap().getWidth() - 1 && y < context.getMap().getHeight() - 1) {
			if (wasVisible) {
				dimFogOfWarBuffer(context, (pointOffset * 4), x, y);
				dimFogOfWarBuffer(context, (pointOffset * 4) + 1, x + 1, y);
				dimFogOfWarBuffer(context, (pointOffset * 4) + 2, x, y + 1);
				dimFogOfWarBuffer(context, (pointOffset * 4) + 3, x + 1, y + 1);
			} else {
				addFogOfWarBuffer(context, (pointOffset * 4), x, y);
				addFogOfWarBuffer(context, (pointOffset * 4) + 1, x + 1, y);
				addFogOfWarBuffer(context, (pointOffset * 4) + 2, x, y + 1);
				addFogOfWarBuffer(context, (pointOffset * 4) + 3, x + 1, y + 1);
			}
			addTrianglesToGeometry(context, boundBuffer, x, y, pointOffset * 4);
		} else {
			addPseudoTrianglesToGeometry(context, boundBuffer, x, y);
		}
	}

	private synchronized void invalidatePoint(int x, int y) {
		geometryInvalid.set(getBufferPosition(y, x));
	}

	private void addFogOfWarBuffer(MapDrawContext context, int offset, int x, int y) {
		fogOfWarStatus[offset] = context.getVisibleStatus(x, y);
	}

	/**
	 * Dims the fog of war buffer
	 * 
	 * @param context
	 *            The context
	 * @param offset
	 *            The fog of war buffer offset
	 * @param x
	 *            The x coordinate of the tile
	 * @param y
	 *            The y coordinate of the tile.
	 * @return true if and only if the dim has finished.
	 */
	private void dimFogOfWarBuffer(MapDrawContext context, int offset, int x, int y) {
		if (!fowDimmed.get(offset)) {
			byte newFog = context.getVisibleStatus(x, y);
			fogOfWarStatus[offset] = dim(fogOfWarStatus[offset], newFog);
			fowDimmed.set(offset);
		}
	}

	private static byte dim(byte value, byte dimTo) {
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

	private final int getBufferPosition(int y, int x) {
		int linePos = y % bufferHeight;
		int colPos = x % bufferWidth;
		while (linePos < 0) {
			linePos += bufferHeight;
		}
		while (colPos < 0) {
			colPos += bufferWidth;
		}

		return (linePos * bufferWidth + colPos);
	}

	/**
	 * Adds the two triangles for a point to the list of verteces
	 * 
	 * @param context
	 * @param buffer
	 * @param x
	 * @param y
	 * @param fogBase
	 */
	private void addTrianglesToGeometry(MapDrawContext context, GLBuffer buffer, int x, int y, int fogBase) {
		addTriangle1ToGeometry(context, buffer, x, y, fogBase);
		addTriangle2ToGeometry(context, buffer, x, y, fogBase);
	}

	private static void addPseudoTrianglesToGeometry(MapDrawContext context, GLBuffer buffer, int x, int y) { // manually do everything...
		addBlackPointToGeometry(context, buffer, x, y);
		addBlackPointToGeometry(context, buffer, x, y + 1);
		addBlackPointToGeometry(context, buffer, x + 1, y + 1);
		addBlackPointToGeometry(context, buffer, x, y);
		addBlackPointToGeometry(context, buffer, x + 1, y + 1);
		addBlackPointToGeometry(context, buffer, x + 1, y);
	}

	/**
	 * Draws the triangle that is facing up
	 * 
	 * @param context
	 * @param buffer
	 * @param x
	 * @param y
	 * @param fogBase
	 */
	private void addTriangle1ToGeometry(MapDrawContext context, GLBuffer buffer, int x, int y, int fogBase) {
		ELandscapeType topLandscape = context.getLandscape(x, y);
		ELandscapeType leftLandscape = context.getLandscape(x, y + 1);
		ELandscapeType rightLandscape = context.getLandscape(x + 1, y + 1);

		boolean useSecond = ((x * 37 + y * 17) & 0x1) == 0;
		ETextureOrientation texturePos;
		int textureIndex;
		if (topLandscape == leftLandscape && topLandscape == rightLandscape) {
			textureIndex = topLandscape.getImageNumber();
			texturePos = ETextureOrientation.CONTINUOUS_UP;
		} else if (leftLandscape == rightLandscape) {
			texturePos = ETextureOrientation.BOTTOM;
			textureIndex = getBorder(leftLandscape, topLandscape, useSecond);
		} else if (leftLandscape == topLandscape) {
			texturePos = ETextureOrientation.TOPLEFT;
			textureIndex = getBorder(leftLandscape, rightLandscape, useSecond);
		} else {
			texturePos = ETextureOrientation.TOPRIGHT;
			textureIndex = getBorder(topLandscape, leftLandscape, useSecond);
		}

		int[] positions = TEXTURE_POSITIONS[textureIndex];
		// texture position
		int adddx = 0;
		int adddy = 0;
		if (positions[2] >= 2) {
			adddx = x * DrawConstants.DISTANCE_X - y * DrawConstants.DISTANCE_X / 2;
			adddy = y * DrawConstants.DISTANCE_Y;
			adddx = realModulo(adddx, (positions[2] - 1) * TEXTURE_GRID);
			adddy = realModulo(adddy, (positions[2] - 1) * TEXTURE_GRID);
		}
		adddx += positions[0] * TEXTURE_GRID;
		adddy += positions[1] * TEXTURE_GRID;

		float[] relativeTexCoordinates = texturePos.getRelativecoords();

		{
			// top
			float u = (relativeTexCoordinates[0] + adddx) / TEXTURE_SIZE;
			float v = (relativeTexCoordinates[1] + adddy) / TEXTURE_SIZE;
			addPointToGeometry(context, buffer, x, y, u, v, fogBase);
		}
		{
			// left
			float u = (relativeTexCoordinates[2] + adddx) / TEXTURE_SIZE;
			float v = (relativeTexCoordinates[3] + adddy) / TEXTURE_SIZE;
			addPointToGeometry(context, buffer, x, y + 1, u, v, fogBase + 2);
		}
		{
			// right
			float u = (relativeTexCoordinates[4] + adddx) / TEXTURE_SIZE;
			float v = (relativeTexCoordinates[5] + adddy) / TEXTURE_SIZE;
			addPointToGeometry(context, buffer, x + 1, y + 1, u, v, fogBase + 3);
		}
	}

	private void addPointToGeometry(MapDrawContext context, GLBuffer buffer, int x, int y, float u, float v, int fogOffset) {
		buffer.putFloat(x);
		buffer.putFloat(y);
		buffer.putFloat(context.getHeight(x, y));

		buffer.putFloat(u);
		buffer.putFloat(v);

		addVertexColor(context, buffer, x, y, fogOffset);
	}

	private static void addBlackPointToGeometry(MapDrawContext context, GLBuffer buffer, int x, int y) {
		buffer.putFloat(x);
		buffer.putFloat(y);
		buffer.putFloat(context.getHeight(x, y));
		buffer.putFloat(0);
		buffer.putFloat(0);
		buffer.putByte(BLACK[0]);
		buffer.putByte(BLACK[1]);
		buffer.putByte(BLACK[2]);
		buffer.putByte(BLACK[3]);
	}

	private void addTriangle2ToGeometry(MapDrawContext context, GLBuffer buffer, int x, int y, int fogBase) {
		ELandscapeType leftLandscape = context.getLandscape(x, y);
		ELandscapeType bottomLandscape = context.getLandscape(x + 1, y + 1);
		ELandscapeType rightLandscape = context.getLandscape(x + 1, y);

		boolean useSecond = (x & 0x1) == 0;
		ETextureOrientation texturePos;
		int textureIndex;
		if (bottomLandscape == leftLandscape && bottomLandscape == rightLandscape) {
			texturePos = ETextureOrientation.CONTINUOUS_DOWN;
			textureIndex = bottomLandscape.getImageNumber();
		} else if (leftLandscape == rightLandscape) {
			texturePos = ETextureOrientation.TOP;
			textureIndex = getBorder(leftLandscape, bottomLandscape, useSecond);
		} else if (leftLandscape == bottomLandscape) {
			texturePos = ETextureOrientation.BOTTOMLEFT;
			textureIndex = getBorder(leftLandscape, rightLandscape, useSecond);
		} else {
			texturePos = ETextureOrientation.BOTTOMRIGHT;
			textureIndex = getBorder(rightLandscape, leftLandscape, useSecond);
		}

		int[] positions = TEXTURE_POSITIONS[textureIndex];
		// texture position
		int addDx = 0;
		int addDy = 0;
		if (positions[2] >= 2) {
			addDx = x * DrawConstants.DISTANCE_X - y * DrawConstants.DISTANCE_X / 2;
			addDy = y * DrawConstants.DISTANCE_Y;
			addDx = realModulo(addDx, (positions[2] - 1) * TEXTURE_GRID);
			addDy = realModulo(addDy, (positions[2] - 1) * TEXTURE_GRID);
		}
		addDx += positions[0] * TEXTURE_GRID;
		addDy += positions[1] * TEXTURE_GRID;

		float[] relativeTexCoordinates = texturePos.getRelativecoords();

		{
			// left
			float u = (relativeTexCoordinates[0] + addDx) / TEXTURE_SIZE;
			float v = (relativeTexCoordinates[1] + addDy) / TEXTURE_SIZE;
			addPointToGeometry(context, buffer, x, y, u, v, fogBase);
		}
		{
			// bottom
			float u = (relativeTexCoordinates[2] + addDx) / TEXTURE_SIZE;
			float v = (relativeTexCoordinates[3] + addDy) / TEXTURE_SIZE;
			addPointToGeometry(context, buffer, x + 1, y + 1, u, v, fogBase + 3);
		}
		{
			// right
			float u = (relativeTexCoordinates[4] + addDx) / TEXTURE_SIZE;
			float v = (relativeTexCoordinates[5] + addDy) / TEXTURE_SIZE;
			addPointToGeometry(context, buffer, x + 1, y, u, v, fogBase + 1);
		}

	}

	private static int realModulo(int number, int modulo) {
		if (number >= 0) {
			return number % modulo;
		} else {
			return number % modulo + modulo;
		}
	}

	private void addVertexColor(MapDrawContext context, GLBuffer buffer, int x, int y, int fogOffset) {
		byte color;

		if (x <= 0 || x >= context.getMap().getWidth() - 2 || y <= 0 || y >= context.getMap().getHeight() - 2 || context.getVisibleStatus(x, y) <= 0) {
			color = 0;
		} else {
			int height1 = context.getHeight(x, y - 1);
			int height2 = context.getHeight(x, y);
			float fColor = 0.85f + (height1 - height2) * .15f;
			if (fColor > 1.0f) {
				fColor = 1.0f;
			} else if (fColor < 0.4f) {
				fColor = 0.4f;
			}
			fColor *= (float) fogOfWarStatus[fogOffset] / CommonConstants.FOG_OF_WAR_VISIBLE;
			fColor *= 255f;
			color = (byte) (int) fColor;
		}

		buffer.putByte(color);
		buffer.putByte(color);
		buffer.putByte(color);
		buffer.putByte((byte) 255);
	}

	@Override
	public void backgroundChangedAt(int x, int y) {
		if (oldBufferPosition != null) {
			if (oldBufferPosition.contains(x, y)) {
				invalidatePoint(x, y);
			}
			if (oldBufferPosition.contains(x - 1, y)) {
				invalidatePoint(x - 1, y);
			}
			if (oldBufferPosition.contains(x - 1, y - 1)) {
				invalidatePoint(x - 1, y - 1);
			}
			if (oldBufferPosition.contains(x, y - 1)) {
				invalidatePoint(x, y - 1);
			}
		}
	}

	/**
	 * Invalidates the background texture.
	 */
	public static void invalidateTexture() {
		texture = null;
	}
}
