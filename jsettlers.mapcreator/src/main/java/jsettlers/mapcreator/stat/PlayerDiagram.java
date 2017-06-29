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
package jsettlers.mapcreator.stat;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import jsettlers.logic.map.loading.data.objects.MapDataObject;
import jsettlers.logic.map.loading.data.objects.StoneMapDataObject;
import jsettlers.logic.map.loading.data.objects.MapTreeObject;
import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.mapcreator.data.MapData;

/**
 * Display player diagram
 */
public class PlayerDiagram extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final int IMAGE_HEIGHT = 300;
	private static final int WATCHED_DISTANCE = 300;
	private static final int TYPE_COUNT = 2;
	private final int[][] founds;
	private final int[] polylinex = new int[WATCHED_DISTANCE];

	/**
	 * Constructor
	 * 
	 * @param data
	 *            Map data to display
	 * @param player
	 *            Player
	 */
	public PlayerDiagram(MapData data, int player) {
		ShortPoint2D start = data.getStartPoint(player);
		int startx = start.x;
		int starty = start.y;

		founds = new int[TYPE_COUNT][WATCHED_DISTANCE];
		for (int x = 0; x < data.getWidth(); x++) {
			for (int y = 0; y < data.getHeight(); y++) {
				addObjectOnPoint(data, startx, starty, x, y);
			}
		}

		for (int i = 0; i < WATCHED_DISTANCE; i++) {
			polylinex[i] = 2 * i;
		}
	}

	private void addObjectOnPoint(MapData data, int startx, int starty, int x,
			int y) {
		int type = getType(data.getMapObject(x, y));
		if (type >= 0) {
			int distance = (int) MapCircle.getDistance(x, y, startx, starty);
			if (distance < WATCHED_DISTANCE) {
				drawHit(founds[type], distance);
			}
		}
	}

	private static void drawHit(int[] is, int distance) {
		int increase = 5 * WATCHED_DISTANCE / (distance + 5);
		if (distance - 3 >= 0) {
			is[distance - 3] += increase / 4;
		}
		if (distance - 2 >= 0) {
			is[distance - 2] += increase / 2;
		}
		if (distance - 1 >= 0) {
			is[distance - 1] += increase * 2 / 3;
		}
		is[distance] = increase;
		if (distance + 1 < is.length) {
			is[distance + 1] += increase * 2 / 3;
		}
		if (distance + 2 < is.length) {
			is[distance + 2] += increase / 2;
		}
		if (distance + 3 < is.length) {
			is[distance + 3] += increase / 4;
		}
	}

	private static int getType(MapDataObject mapObject) {
		if (mapObject instanceof MapTreeObject) {
			return 0;
		} else if (mapObject instanceof StoneMapDataObject) {
			return 1;
		}
		return -1;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, WATCHED_DISTANCE * 2, IMAGE_HEIGHT);

		g.setColor(Color.DARK_GRAY);
		g.drawString("Stones", 50, 30);
		g.setColor(Color.GREEN);
		g.drawString("Trees", 50, 45);

		((Graphics2D) g).scale(1, -1);
		g.translate(0, -IMAGE_HEIGHT);

		g.setColor(Color.DARK_GRAY);
		g.drawPolyline(polylinex, founds[1], WATCHED_DISTANCE);

		g.setColor(Color.GREEN);
		g.drawPolyline(polylinex, founds[0], WATCHED_DISTANCE);
	}
}
