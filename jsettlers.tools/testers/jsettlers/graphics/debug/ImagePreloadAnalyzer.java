package jsettlers.graphics.debug;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import jsettlers.common.utils.MainUtils;
import jsettlers.graphics.image.MultiImageImage;
import jsettlers.graphics.image.MultiImageMap;
import jsettlers.graphics.image.MultiImageMap.MultiImageMapSpecification;
import jsettlers.graphics.map.draw.ImagePreloadTask;
import jsettlers.graphics.reader.ImageMetadata;
import jsettlers.main.swing.SwingManagedJSettlers;

/**
 * This class analyzes how well the {@link ImagePreloadTask} packs the images.
 * 
 * @author Michael Zangl
 */
public class ImagePreloadAnalyzer extends ImagePreloadTask {

	private static class DebugData {
		private int x, y, width, height, offsetX, offsetY;
		private String name;

		public DebugData(int x, int y, ImageMetadata settlermeta, String name) {
			super();
			this.x = x;
			this.y = y;
			this.name = name;
			this.width = settlermeta.width;
			this.height = settlermeta.height;
			this.offsetX = settlermeta.offsetX;
			this.offsetY = settlermeta.offsetY;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("DebugData [x=");
			builder.append(x);
			builder.append(", y=");
			builder.append(y);
			builder.append(", width=");
			builder.append(width);
			builder.append(", height=");
			builder.append(height);
			builder.append(", offsetX=");
			builder.append(offsetX);
			builder.append(", offsetY=");
			builder.append(offsetY);
			builder.append("]");
			return builder.toString();
		}
	}

	@Override
	protected void preload(MultiImageMapSpecification spec) {
		final ArrayList<DebugData> rects = new ArrayList<>();
		MultiImageMap map = new MultiImageMap(spec) {
			private String name;

			@Override
			protected void loadSequenceFromDatFile(int index, CacheFileWriter cacheWriter) throws IOException {
				name = specification.getFile(index) + "," + specification.getSequence(index);
				super.loadSequenceFromDatFile(index, cacheWriter);
			}

			@Override
			protected MultiImageImage generateImage(ImageMetadata settlermeta, ImageMetadata torsometa, int settlerx,
					int settlery, int torsox, int torsoy) {
				rects.add(new DebugData(settlerx, settlery, settlermeta, name));
				if (torsometa != null) {
					rects.add(new DebugData(torsox, torsoy, torsometa, name));
				}
				return super.generateImage(settlermeta, torsometa, settlerx, settlery, torsox, torsoy);
			}

			@Override
			public synchronized boolean hasCache() {
				return false;
			}
		};
		map.load();

		openDebugWindow(spec + "", rects, map.getWidth(), map.getHeight());
	}

	private void openDebugWindow(String title, final ArrayList<DebugData> rects, int width, int height) {
		JPanel panel = new JPanel() {
			/**
				 * 
				 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(Color.RED);
				for (DebugData r : rects) {
					g.drawRect(r.x, r.y, r.width, r.height);
					g.drawString(r.name, r.x, r.y + r.height - 2);
				}
			}
		};
		panel.setMinimumSize(new Dimension(width, height));
		panel.setPreferredSize(new Dimension(width, height));

		JFrame frame = new JFrame(title);
		frame.add(new JScrollPane(panel));
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		SwingManagedJSettlers.setupResourceManagers(MainUtils.loadOptions(args), "../jsettlers.main.swing/config.prp");
		new ImagePreloadAnalyzer().run();
	}
}
