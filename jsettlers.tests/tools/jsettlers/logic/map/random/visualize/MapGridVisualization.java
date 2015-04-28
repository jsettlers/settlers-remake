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
package jsettlers.logic.map.random.visualize;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.IMapData;
import jsettlers.common.map.object.MapObject;
import jsettlers.common.map.object.MapStoneObject;
import jsettlers.common.map.object.MapTreeObject;
import jsettlers.logic.map.random.RandomMapEvaluator;
import jsettlers.logic.map.random.RandomMapFile;

public class MapGridVisualization extends JPanel {
	/**
     * 
     */
	private static final long serialVersionUID = -6644377132896522388L;
	private final IMapData grid;

	public MapGridVisualization(IMapData grid) {
		this.grid = grid;

	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(grid.getWidth(), grid.getHeight());
	}

	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		for (int x = 0; x < grid.getWidth(); x++) {
			for (int y = 0; y < grid.getHeight(); y++) {
				ELandscapeType landscape = grid.getLandscape(x, y);
				MapObject object = grid.getMapObject(x, y);
				Color color;
				if (object != null) {
					color = objectColor(object);
				} else {
					color = landscapeColor(landscape);
				}
				g.setColor(color);
				g.drawRect(x, y, 1, 1);
			}
		}
	}

	private Color objectColor(MapObject object) {
		if (object instanceof MapTreeObject) {
			return new Color(102, 59, 68);
		} else if (object instanceof MapStoneObject) {
			return new Color(200, 200, 200);
		} else {
			return new Color(70, 70, 70);
		}
	}

	private Color landscapeColor(ELandscapeType landscape) {
		if (landscape == null) {
			return Color.black;
		}
		switch (landscape) {
		case GRASS:
			return Color.GREEN;

		case MOUNTAIN:
			return Color.DARK_GRAY;

		case MOUNTAINBORDER:
			return Color.GRAY;

		case WATER1:
			return Color.BLUE;

		case SAND:
			return Color.YELLOW;

		case RIVER1:
		case RIVER2:
		case RIVER3:
		case RIVER4:
			return Color.CYAN;

		default:
			return Color.BLACK;
		}
	}

	@SuppressWarnings("null")
	public static void main(String[] args) {
		RandomMapFile file = null;// RandomMapFile.getByName("test");
		RandomMapEvaluator evaluator = new RandomMapEvaluator(file.getInstructions(), 3);
		evaluator.createMap(new Random());

		JFrame frame2 = new JFrame("grid");
		frame2.getContentPane().add(new MapGridVisualization(evaluator.getGrid()));
		frame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame2.pack();
		frame2.setVisible(true);
	}
}
