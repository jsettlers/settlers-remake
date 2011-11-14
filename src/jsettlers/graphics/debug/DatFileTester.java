package jsettlers.graphics.debug;

import go.graphics.Color;
import go.graphics.GLDrawContext;
import go.graphics.area.Area;
import go.graphics.event.GOEvent;
import go.graphics.event.GOKeyEvent;
import go.graphics.region.Region;
import go.graphics.region.RegionContent;
import go.graphics.swing.AreaContainer;
import go.graphics.swing.text.JOGLTextDrawer;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ShortBuffer;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import jsettlers.graphics.JoglLibraryPathInitializer;
import jsettlers.graphics.image.GuiImage;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.image.LandscapeImage;
import jsettlers.graphics.image.SettlerImage;
import jsettlers.graphics.reader.AdvancedDatFileReader;
import jsettlers.graphics.reader.SequenceList;
import jsettlers.graphics.sequence.Sequence;

public class DatFileTester {

	static { // sets the native library path for the system dependent jogl libs
		JoglLibraryPathInitializer.initLibraryPath();
	}

	private static final String FILE =
	        "/home/michael/.wine/drive_c/BlueByte/S3AmazonenDemo/GFX/siedler3_%.7c003e01f.dat";

	private static final Color[] colors = new Color[] {
		new Color(0xffffff)
	};
	// private static final String FILE =
	// "D:/Games/Siedler3/GFX/siedler3_%.7c003e01f.dat";

	private static int datFileIndex = 13;

	private AdvancedDatFileReader reader;

	private Region region;

	private DatFileTester() throws IOException {
		reloadDatFile();

		region = new Region(Region.POSITION_CENTER);
		region.setContent(new Content());

		Area area = new Area();
		area.add(region);
		AreaContainer glcanvas = new AreaContainer(area);

		JFrame frame = new JFrame("Opengl image test");
		frame.getContentPane().add(glcanvas);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(new Dimension(400, 400));
		frame.setVisible(true);

	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		new DatFileTester();
		//
		// long intime = System.nanoTime();
		// int images = 0;
		//
		// int y = offsetY;
		// int seqIndex = 0;
		// for (Sequence<SettlerImage> seq : reader.getSettlers()) {
		// int x = offsetX;
		// System.out.println("next sequence: " + seqIndex);
		//
		// for (SettlerImage image : seq) {
		// gl.glEnable(GL.GL_TEXTURE_2D);
		// gl.glColor3f(1, 1, 1);
		// image.drawAt(gl, x - image.getOffsetX(),
		// y + image.getHeight() + image.getOffsetY(),
		// colors[images % colors.length]);
		//
		// gl.glDisable(GL.GL_TEXTURE_2D);
		// gl.glColor3f(1, 0, 0);
		// gl.glBegin(GL.GL_LINE_STRIP);
		// gl.glVertex2i(x, y);
		// gl.glVertex2i(x,
		// y + image.getHeight() + image.getOffsetY());
		// gl.glVertex2i(x - image.getOffsetX(),
		// y + image.getHeight() + image.getOffsetY());
		// gl.glEnd();
		// drawPoint(gl, x, y);
		// drawPoint(gl, x + image.getWidth(), y);
		// drawPoint(gl, x + image.getWidth(),
		// y + image.getHeight());
		// drawPoint(gl, x, y + image.getHeight());
		//
		// System.out.println("image sizes: " + image.getWidth()
		// + "x" + image.getHeight());
		//
		// x += 100;
		// images++;
		// }
		//
		// gl.glColor3f(0, 0, 0);
		// gl.glRasterPos2i(20, y + 20);
		// glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, seqIndex
		// + ":");
		//
		// seqIndex++;
		// y -= 200;
		// }
		//
		// y = offsetY;
		// int landImageIndex = 0;
		// for (Sequence<LandscapeImage> seq : reader.getLandscapes()) {
		// LandscapeImage image = seq.getImage(0);
		// gl.glEnable(GL.GL_TEXTURE_2D);
		// image.bind(gl);
		// gl.glColor3f(1, 1, 1);
		// gl.glBegin(GL2.GL_QUADS);
		// gl.glTexCoord2f(0, 0);
		// gl.glVertex2f(50, y);
		// gl.glTexCoord2f(0, 1);
		// gl.glVertex2f(50, y + 32);
		// gl.glTexCoord2f(1, 1);
		// gl.glVertex2f(82, y + 32);
		// gl.glTexCoord2f(1, 0);
		// gl.glVertex2f(82, y);
		// gl.glEnd();
		// gl.glDisable(GL.GL_TEXTURE_2D);
		//
		// gl.glColor3f(0, 0, 0);
		// gl.glRasterPos2i(20, y + 20);
		// glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12,
		// landImageIndex + ":");
		//
		// if (image.isContinuous()) {
		// gl.glRasterPos2i(100, y + 20);
		// glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "big");
		// }
		// images++;
		// landImageIndex++;
		// y -= 40;
		// }
		//
		// y = offsetY;
		// int guiImageIndex = 0;
		// for (Sequence<GuiImage> seq : reader.getGuis()) {
		// GuiImage image = seq.getImage(0);
		// System.out.println("next gui image: " + guiImageIndex + " "
		// + image.getWidth() + "x" + image.getHeight()
		// + " px");
		// gl.glEnable(GL.GL_TEXTURE_2D);
		// image.drawAt(gl, 100, y);
		// gl.glDisable(GL.GL_TEXTURE_2D);
		//
		// gl.glColor3f(0, 0, 0);
		// gl.glRasterPos2i(20, y + 20);
		// glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12,
		// guiImageIndex + ":");
		//
		// images++;
		// guiImageIndex++;
		// y -= image.getHeight() + 20;
		// }
		//
		// System.out.println("Nanotime: " + (System.nanoTime() - intime)
		// + " for " + images + " images");
		// gl.glFlush();
		//
		// }
		//
		// private void drawPoint(GL2 gl, int x, int y) {
		// gl.glColor3f(0, 0, 1);
		// gl.glBegin(GL2.GL_QUADS);
		// gl.glVertex2i(x + 2, y + 2);
		// gl.glVertex2i(x + 2, y - 2);
		// gl.glVertex2i(x - 2, y - 2);
		// gl.glVertex2i(x - 2, y + 2);
		// gl.glEnd();
		// gl.glColor3f(1, 1, 1);
		// }
		// });
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
				}
				region.requestRedraw();
			}
		}

		@Override
		public void drawContent(GLDrawContext gl2, int width, int height) {
			if (mode == SETTLERS) {
				SequenceList<SettlerImage> sequences = reader.getSettlers();
				drawSequences(gl2, width, height, sequences);
			} else if (mode == GUI) {
				Sequence<GuiImage> sequences = reader.getGuis();
				drawSequence(gl2, width, height, 0, sequences);
			} else {
				Sequence<LandscapeImage> sequences = reader.getLandscapes();
				drawSequence(gl2, width, height, 0, sequences);
			}

		}

		private <T extends Image> void drawSequences(GLDrawContext gl2,
		        int width, int height, SequenceList<T> sequences) {
			gl2.glTranslatef(offsetX, offsetY, 0);

			int y = 0;
			int seqIndex = 0;
			TextDrawer drawer = JOGLTextDrawer.getTextDrawer(EFontSize.NORMAL);
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

		private <T extends Image> int drawSequence(GLDrawContext gl2,
		        int width, int height, int y, Sequence<T> seq) {
			int maxheight = 0;
			int x = 0;
			for (int index = 0; index < seq.length(); index++) {
				T image = seq.getImage(index);
				maxheight = Math.max(maxheight, image.getHeight());

				if (x > -offsetX - 100 && x < -offsetX + width + 100
				        && y > -offsetY - 100 && y < -offsetY + height + 100) {
					drawImage(gl2, y, index, x, image);
				}
				x += 100;
			}
			return maxheight;
		}

		private void drawImage(GLDrawContext gl2, int y, int index, int x,
		        Image image) {
			image.drawAt(gl2, x - image.getOffsetX(), y + image.getHeight()
			        + image.getOffsetY(), colors[index % colors.length]);

			gl2.color(1, 0, 0, 1);
			float[] line =
			        new float[] {
			                x,
			                y,
			                0,
			                x,
			                y + image.getHeight() + image.getOffsetY(),
			                0,
			                x - image.getOffsetX(),
			                y + image.getHeight() + image.getOffsetY(),
			                0
			        };
			gl2.drawLine(line, false);
			drawPoint(gl2, x, y);
			drawPoint(gl2, x + image.getWidth(), y);
			drawPoint(gl2, x + image.getWidth(), y + image.getHeight());
			drawPoint(gl2, x, y + image.getHeight());
		}

		private void drawPoint(GLDrawContext gl2, int x, int y) {
			// TODO Auto-generated method stub
		}
		
		private void printHelp() {
			System.out
			        .println("HELP:\nUse arrow keys to navigate.\nS shows settlers. \nG shows gui images. \nB shows Background. \nE exports as png");
		}
	}

	private void reloadDatFile() throws IOException {
		File file =
		        new File(FILE.replace("%", String.format("%02d", datFileIndex)));

		reader = new AdvancedDatFileReader(file);
	}

	protected void export() {
		final JFileChooser fc = new JFileChooser();

		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File dir = fc.getSelectedFile();
			exportTo(dir);
		}
	}

	private void exportTo(File dir) {
		export(reader.getSettlers(), new File(dir, "settlers"));
		exportSequence(new File(dir, "gui"), 0, reader.getGuis());
		exportSequence(new File(dir, "landscape"), 1, reader.getLandscapes());
	}

	private <T extends Image> void export(SequenceList<T> sequences, File dir) {
		for (int index = 0; index < sequences.size(); index++) {
			Sequence<T> seq = sequences.get(index);
			exportSequence(dir, index, seq);
		}
	}

	private <T extends Image> void exportSequence(File dir, int index, Sequence<T> seq) {
	    File seqdir = new File(dir, index + "");
	    seqdir.mkdirs();
	    for(int j = 0; j < seq.length(); j++) {
	    	T image = seq.getImage(j);
	    	export(image, new File(seqdir, j + ".png"));
	    	if (image instanceof SettlerImage
	    	        && ((SettlerImage) image).getTorso() != null) {
	    		export(((SettlerImage) image).getTorso(), new File(seqdir,
	    		        j + "_torso.png"));
	    	}
	    }
    }

	private void export(Image image, File file) {
		// does not work if gpu does not support non-power-of-two
		BufferedImage rendered =
		        new BufferedImage(image.getWidth(), image.getHeight(),
		                BufferedImage.TYPE_INT_ARGB);
		ShortBuffer data = image.getData().duplicate();
		data.rewind();
		int[] rgbArray = new int[data.remaining()];
		for (int i = 0; i < rgbArray.length; i++) {
			short myColor = data.get();
			float red = (float) ((myColor >> 11) & 0x1f) / 0x1f;
			float green = (float) ((myColor >> 6) & 0x1f) / 0x1f;
			float blue = (float) ((myColor >> 1) & 0x1f) / 0x1f;
			float alpha = myColor & 0x1;
			rgbArray[i] = new Color(red, green, blue, alpha).getRGB();
		}

		rendered.setRGB(0, 0, image.getWidth(), image.getHeight(), rgbArray,
		        -image.getWidth() + image.getHeight() * image.getWidth(),
		        -image.getWidth());

		try {
			ImageIO.write(rendered, "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
