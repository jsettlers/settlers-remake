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
package jsettlers.graphics.debug;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import go.graphics.GLDrawContext;
import go.graphics.area.Area;
import go.graphics.event.GOEvent;
import go.graphics.event.GOKeyEvent;
import go.graphics.region.Region;
import go.graphics.region.RegionContent;
import go.graphics.swing.AreaContainer;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;

import jsettlers.common.Color;
import jsettlers.common.resources.SettlersFolderChecker;
import jsettlers.common.utils.FileUtils;
import jsettlers.common.utils.OptionableProperties;
import jsettlers.common.utils.mutables.Mutable;
import jsettlers.graphics.image.GuiImage;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.image.LandscapeImage;
import jsettlers.graphics.image.SettlerImage;
import jsettlers.graphics.image.SingleImage;
import jsettlers.graphics.reader.AdvancedDatFileReader;
import jsettlers.graphics.reader.DatFileType;
import jsettlers.graphics.reader.SequenceList;
import jsettlers.graphics.sequence.Sequence;
import jsettlers.graphics.swing.utils.ImageUtils;
import jsettlers.main.swing.resources.ConfigurationPropertiesFile;

public class DatFileTester {
	private static final int DAT_FILE_INDEX = 14;
	private static final DatFileType TYPE = DatFileType.RGB565;

	private static final String FILE_NAME_PATTERN = "siedler3_%02d" + TYPE.getFileSuffix();
	private static final String FILE_NAME = String.format(Locale.ENGLISH, FILE_NAME_PATTERN, DAT_FILE_INDEX);

	private static final Color[] colors = new Color[] { Color.WHITE };

	private final AdvancedDatFileReader reader;
	private final Region region;

	private DatFileTester() throws IOException {
		String settlersFolderPath = new ConfigurationPropertiesFile(new OptionableProperties()).getSettlersFolderValue();
		SettlersFolderChecker.SettlersFolderInfo settlersFolderInfo = SettlersFolderChecker.checkSettlersFolder(settlersFolderPath);
		File settlersGfxFolder = settlersFolderInfo.gfxFolder;

		File file = findFileIgnoringCase(settlersGfxFolder, FILE_NAME);
		reader = new AdvancedDatFileReader(file, TYPE);

		region = new Region(Region.POSITION_CENTER);
		region.setContent(new Content());
	}

	private File findFileIgnoringCase(File settlersGfxFolder, String fileName) {
		String fileNameLowercase = fileName.toLowerCase();

		Mutable<File> graphicsFile = new Mutable<>();

		FileUtils.iterateChildren(settlersGfxFolder, currentFile -> {
			if(currentFile.isFile() && fileNameLowercase.equalsIgnoreCase(currentFile.getName())){
				graphicsFile.object = currentFile;
			}
		});

		return graphicsFile.object;
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		DatFileTester datFileTester = new DatFileTester();

		Area area = new Area();
		area.add(datFileTester.region);
		AreaContainer glCanvas = new AreaContainer(area);

		JFrame frame = new JFrame("Opengl image test: " + DAT_FILE_INDEX);
		frame.getContentPane().add(glCanvas);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(new Dimension(400, 400));
		frame.setVisible(true);
	}

	private class Content implements RegionContent {

		private static final int SETTLERS = 1;
		private static final int GUI = 2;
		private static final int LANDSCAPE = 2;
		private int offsetY = 400;
		private int offsetX = 200;
		private int mode = SETTLERS;

		public Content() {
			printHelp();
		}

		@Override
		public void handleEvent(GOEvent event) {
			if (event instanceof GOKeyEvent) {
				String keyCode = ((GOKeyEvent) event).getKeyCode();
				if ("UP".equalsIgnoreCase(keyCode)) {
					offsetY -= 400;
				} else if ("DOWN".equalsIgnoreCase(keyCode)) {
					offsetY += 400;
				} else if ("LEFT".equalsIgnoreCase(keyCode)) {
					offsetX += 100;
				} else if ("RIGHT".equalsIgnoreCase(keyCode)) {
					offsetX -= 100;
				} else if ("L".equalsIgnoreCase(keyCode)) {
					mode = LANDSCAPE;
				} else if ("S".equalsIgnoreCase(keyCode)) {
					mode = SETTLERS;
				} else if ("G".equalsIgnoreCase(keyCode)) {
					mode = GUI;
				} else if ("E".equalsIgnoreCase(keyCode)) {
					export();
				} else if ("W".equalsIgnoreCase(keyCode)) {
					exportAll();
				}
				region.requestRedraw();
			}
		}

		@Override
		public void drawContent(GLDrawContext gl2, int width, int height) {
			if (mode == SETTLERS) {
				SequenceList<Image> sequences = reader.getSettlers();
				drawSequences(gl2, width, height, sequences);
			} else if (mode == GUI) {
				Sequence<GuiImage> sequences = reader.getGuis();
				drawSequence(gl2, width, height, 0, sequences);
			} else {
				Sequence<LandscapeImage> sequences = reader.getLandscapes();
				drawSequence(gl2, width, height, 0, sequences);
			}

		}

		private <T extends Image> void drawSequences(GLDrawContext gl2, int width, int height, SequenceList<T> sequences) {
			gl2.glTranslatef(offsetX, offsetY, 0);

			int y = 0;
			int seqIndex = 0;
			TextDrawer drawer = gl2.getTextDrawer(EFontSize.NORMAL);
			for (int i = 0; i < sequences.size(); i++) {
				Sequence<T> seq = sequences.get(i);
				int maxheight;

				maxheight = drawSequence(gl2, width, height, y, seq);

				gl2.color(0, 0, 0, 1);
				drawer.drawString(20, y + 20, seqIndex + ":");

				seqIndex++;
				y -= maxheight + 40;
			}
		}

		private <T extends Image> int drawSequence(GLDrawContext gl2, int width, int height, int y, Sequence<T> seq) {
			int maxheight = 0;
			int x = 0;
			for (int index = 0; index < seq.length(); index++) {
				T image = seq.getImage(index);
				maxheight = Math.max(maxheight, image.getHeight());

				if (x > -offsetX - 100 && x < -offsetX + width + 100 && y > -offsetY - 100 && y < -offsetY + height + 100) {
					drawImage(gl2, y, index, x, (SingleImage) image);
				}
				x += 100;
			}
			return maxheight;
		}

		private void drawImage(GLDrawContext gl2, int y, int index, int x, SingleImage image) {
			image.drawAt(gl2, x - image.getOffsetX(), y + image.getHeight() + image.getOffsetY(), colors[index % colors.length]);

			gl2.color(1, 0, 0, 1);
			float[] line = new float[] { x, y, 0, x, y + image.getHeight() + image.getOffsetY(), 0, x - image.getOffsetX(),
					y + image.getHeight() + image.getOffsetY(), 0 };
			gl2.drawLine(line, false);
			drawPoint(gl2, x, y);
			drawPoint(gl2, x + image.getWidth(), y);
			drawPoint(gl2, x + image.getWidth(), y + image.getHeight());
			drawPoint(gl2, x, y + image.getHeight());
		}

		private void drawPoint(GLDrawContext gl2, int x, int y) {
		}

		private void printHelp() {
			System.out
					.println("HELP:\nUse arrow keys to navigate.\nS shows settlers. \nG shows gui images. \nB shows Background. \nE exports as png");
		}
	}

	protected void export() {
		final JFileChooser fc = new JFileChooser();

		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File dir = fc.getSelectedFile();
			exportTo(dir, reader);
		}
	}

	private static void exportAll() {
		final JFileChooser fc = new JFileChooser();

		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File dir = fc.getSelectedFile();
			for (int i = 0; i <= 99; i++) {
				File file = new File(FILE_NAME_PATTERN.replace("%", String.format(Locale.ENGLISH, "%02d", i)));

				if (file.exists()) {
					AdvancedDatFileReader reader = new AdvancedDatFileReader(file, TYPE);
					exportTo(new File(dir, "" + i), reader);
				}
			}
		}
	}

	private static void exportTo(File dir, AdvancedDatFileReader reader) {
		export(reader.getSettlers(), new File(dir, "settlers"));
		Sequence<GuiImage> guis = reader.getGuis();
		if (guis.length() > 0) {
			exportSequence(new File(dir, "gui"), 0, guis);
		}
		Sequence<LandscapeImage> landscapes = reader.getLandscapes();
		if (landscapes.length() > 0) {
			exportSequence(new File(dir, "landscape"), 1, landscapes);
		}
	}

	private static <T extends Image> void export(SequenceList<T> sequences, File dir) {
		for (int index = 0; index < sequences.size(); index++) {
			Sequence<T> seq = sequences.get(index);
			exportSequence(dir, index, seq);
		}
	}

	private static <T extends Image> void exportSequence(File dir, int index, Sequence<T> seq) {
		File seqdir = new File(dir, index + "");
		seqdir.mkdirs();
		for (int j = 0; j < seq.length(); j++) {
			T image = seq.getImage(j);
			export((SingleImage) image, new File(seqdir, j + ".png"));
			if (image instanceof SettlerImage && ((SettlerImage) image).getTorso() != null) {
				export((SingleImage) ((SettlerImage) image).getTorso(), new File(seqdir, j + "_torso.png"));
			}
		}
	}

	private static void export(SingleImage image, File file) {
		// does not work if gpu does not support non-power-of-two
		BufferedImage rendered = ImageUtils.convertToBufferedImage(image);
		if (rendered == null) {
			return;
		}

		try {
			ImageIO.write(rendered, "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
