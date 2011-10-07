package go.graphics.opengl.test;

import go.graphics.GLDrawContext;
import go.graphics.area.Area;
import go.graphics.event.GOEvent;
import go.graphics.event.GOModalEventHandler;
import go.graphics.event.command.GOCommandEvent;
import go.graphics.event.mouse.GODrawEvent;
import go.graphics.event.mouse.GOHoverEvent;
import go.graphics.event.mouse.GOPanEvent;
import go.graphics.opengl.AreaContainer;
import go.graphics.region.Region;
import go.graphics.region.RegionContent;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map.Entry;

import javax.swing.JFrame;

/**
 * A jogl test class.
 * 
 * @author michael
 */
public class JoglTest {

	private Area area;

	public static Area generateArea() {
		Region region1 = new Region(Region.POSITION_TOP);
		Region region2 = new Region(Region.POSITION_LEFT);
		Region region3 = new Region(Region.POSITION_CENTER);

		Area area = new Area();
		area.add(region1);
		area.add(region2);
		region2.setSize(200);
		area.add(region3);
		RegionContent content = new TestContent(region3);
		region3.setContent(content);
		region3.addEventHandler(content);
		return area;
	}

	private static class TestContent implements RegionContent {

		private Hashtable<Object, ArrayList<Point>> draw =
		        new Hashtable<Object, ArrayList<Point>>();
		private final Region region;

		private TestContent(Region region) {
			this.region = region;
		}

		private GOModalEventHandler handler = new GOModalEventHandler() {

			@Override
			public void phaseChanged(GOEvent event) {
			}

			@Override
			public void finished(GOEvent event) {
				eventDataChanged(event);
			}

			@Override
			public void aborted(GOEvent event) {
				draw.remove(event);
			}

			@Override
			public void eventDataChanged(GOEvent event) {
				if (event instanceof GOHoverEvent) {
					draw.get(event).add(
					        ((GOHoverEvent) event).getHoverPosition());
				} else if (event instanceof GODrawEvent) {
					draw.get(event)
					        .add(((GODrawEvent) event).getDrawPosition());
				} else if (event instanceof GOPanEvent) {
					draw.get(event).add(((GOPanEvent) event).getPanDistance());
				} else if (event instanceof GOCommandEvent) {
					draw.get(event).add(
					        ((GOCommandEvent) event).getCommandPosition());
				}
				region.requestRedraw();
			}
		};

		@Override
		public void handleEvent(GOEvent event) {
			event.setHandler(handler);
			draw.put(event, new ArrayList<Point>());
			handler.eventDataChanged(event);
		}

		@Override
		public void drawContent(GLDrawContext gl2, int width, int height) {
			for (Entry<Object, ArrayList<Point>> e : draw.entrySet()) {
				if (e.getKey() instanceof GOHoverEvent) {
					gl2.color(.7f, .0f, .0f, .5f);
				} else if (e.getKey() instanceof GODrawEvent) {
					gl2.color(.7f, .7f, .0f, .5f);
				} else if (e.getKey() instanceof GOPanEvent) {
					gl2.color(.0f, .7f, .0f, .5f);
				} else if (e.getKey() instanceof GOCommandEvent) {
					gl2.color(.0f, .0f, .7f, .5f);
				} else {
					gl2.color(1, 1, 1, 1);
				}

				int pointn = e.getValue().size();

				float[] points = new float[pointn * 3];
				for (int i = 0; i < pointn; i++) {
					Point point = e.getValue().get(i);
					points[i * 3] = point.x;
					points[i * 3 + 1] = point.y;
					points[i * 3 + 2] = 0;
				}

				gl2.drawLine(points, false);
			}
		}

	}

	/**
	 * creates a test window.
	 */
	protected JoglTest() {
		JFrame window = new JFrame("Test");
		area = generateArea();

		AreaContainer content = new AreaContainer(area);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		window.add(content);
		window.setSize(500, 500);
		window.setVisible(true);
	}

	/**
	 * Starts the test program.
	 * 
	 * @param args
	 *            The arguments
	 */
	public static void main(String[] args) {
		new JoglTest();
	}
}
