package jsettlers.graphics.map.geometry.test;

import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.map.geometry.MapCoordinateConverter;
import junit.framework.TestCase;

import org.junit.Test;

public class MapCoordinateConverterTest extends TestCase {

	private static final int VIEWHEIGHT = 100;
	private static final int VIEWWIDTH = 100;
	private static final short MAPSIZE = (short) 30;
	private MapCoordinateConverter converter;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.converter =
		        new MapCoordinateConverter(MAPSIZE, MAPSIZE, VIEWWIDTH,
		                VIEWHEIGHT);
		int y = 0;
		for (int x = 0; x < 150; x++) {
			System.out.println("" + x + "," + y + ": "
			        + this.converter.getAreaForPixel(x, y));
		}
	}

	@Test
	public void testGetView() {
		// if (MAPSIZE < VIEWHEIGHT) {
		assertEquals(VIEWWIDTH + .5f * VIEWHEIGHT, this.converter.getViewX(
		        MAPSIZE - 1, 0, 0), 5);
		assertEquals(VIEWHEIGHT, this.converter.getViewY(MAPSIZE - 1, 0, 0), 5);
		assertEquals(0, this.converter.getViewX(0, MAPSIZE - 1, 0), 5);
		assertEquals(0, this.converter.getViewY(0, MAPSIZE - 1, 0), 5);
		// }

		float screenx = converter.getViewX(10, 10, 0);
		float screeny = converter.getViewY(10, 10, 0);
		ShortPoint2D point =
		        new ShortPoint2D(Math
		                .round(converter.getMapX(screenx, screeny)), Math
		                .round(converter.getMapY(screenx, screeny)));
		assertEquals(screenx, converter.getViewX(point), .05);
		assertEquals(screenx, converter.getViewX(point), .05);
	}

	@Test
	public void testGetArea() {
		for (int x = 0; x < VIEWWIDTH + (VIEWHEIGHT + 1) / 2; x++) {
			for (int y = 0; y < VIEWHEIGHT; y++) {
				IMapArea covered = converter.getAreaForPixel(x, y);
				for (ISPosition2D tile : covered) {
					if (tile.getX() >= 0 && tile.getY() >= 0
					        && tile.getX() < MAPSIZE && tile.getY() < MAPSIZE) {
						assertEquals(
						        "Is not really displayed at the pixel (x)", x,
						        converter.getViewX(tile), 1);
						assertEquals(
						        "Is not really displayed at the pixel (x)", y,
						        converter.getViewY(tile), 1);
					}
				}
			}
		}
	}

	@Test
	public void testGetAreaForPixel() {
		boolean[][] hitPoints = new boolean[MAPSIZE][MAPSIZE];

		for (int x = 0; x < VIEWWIDTH + (VIEWHEIGHT + 1) / 2 + 1; x++) {
			for (int y = 0; y < VIEWHEIGHT + 1; y++) {
				IMapArea covered = converter.getAreaForPixel(x, y);
				for (ISPosition2D tile : covered) {
					if (tile.getX() >= 0 && tile.getY() >= 0
					        && tile.getX() < MAPSIZE && tile.getY() < MAPSIZE) {
						if (hitPoints[tile.getX()][tile.getY()]) {
							fail("Point hit twice: " + tile.getX() + ","
							        + tile.getY() + " for view: " + x + "," + y);
						}
						hitPoints[tile.getX()][tile.getY()] = true;
					}
				}
			}
		}

		for (int x = 0; x < MAPSIZE; x++) {
			for (int y = 0; y < MAPSIZE; y++) {
				if (!hitPoints[x][y]) {
					fail("Point not hit: " + x + ", " + y);
				}
			}
		}
	}
}
