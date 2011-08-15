package jsettlers.graphics.debug;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.swing.JFrame;

import jsettlers.graphics.image.GuiImage;
import jsettlers.graphics.image.LandscapeImage;
import jsettlers.graphics.image.SettlerImage;
import jsettlers.graphics.reader.DatFileReader;
import jsettlers.graphics.sequence.Sequence;

import com.jogamp.opengl.util.gl2.GLUT;

public class DatFileTester {

	private static final String FILE =
	        "/home/michael/.wine/drive_c/BlueByte/S3AmazonenDemo/GFX/siedler3_%.7c003e01f.dat";

	private static int datFileIndex = 14;

	private static int offsetY = 400;
	private static int offsetX = 200;

	static DatFileReader reader;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		reloadDatFile();

		GLProfile profile = GLProfile.get(GLProfile.GL2);
		GLCapabilities capabilities = new GLCapabilities(profile);
		final GLCanvas glcanvas = new GLCanvas(capabilities);

		JFrame frame = new JFrame("Opengl image test");
		frame.getContentPane().add(glcanvas);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(new Dimension(400, 400));
		frame.setVisible(true);

		glcanvas.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_UP) {
					offsetY -= 400;
				} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					offsetY += 400;
				} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					offsetX += 100;
				} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					offsetX -= 100;
				}
				glcanvas.display();
			}
		});
		glcanvas.addGLEventListener(new GLEventListener() {
			@Override
			public void reshape(GLAutoDrawable arg0, int x, int y, int width,
			        int height) {
				final GL2 gl = arg0.getGL().getGL2();

				gl.glViewport(0, 0, width, height);
				gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
				gl.glLoadIdentity();

				gl.glOrtho(0, width, 0, height, 0, 1);
				gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
			}

			@Override
			public void init(GLAutoDrawable arg0) {
				GL2 gl = arg0.getGL().getGL2();
				gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
				gl.glShadeModel(GLLightingFunc.GL_FLAT);
				gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
			}

			@Override
			public void dispose(GLAutoDrawable arg0) {
			}

			@Override
			public void display(GLAutoDrawable arg0) {
				GL2 gl = arg0.getGL().getGL2();

				// gl.glBlendFunc(GL2.GL_SRC_ALPHA,GL2.GL_ONE_MINUS_SRC_ALPHA);
				// gl.glEnable(GL2.GL_BLEND);
				gl.glAlphaFunc(GL.GL_GREATER, 0.1f);
				gl.glEnable(GL2ES1.GL_ALPHA_TEST);

				gl.glDisable(GL.GL_TEXTURE_2D);

				gl.glClearColor(0, 0, 0, 0);
				gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
				gl.glLoadIdentity();

				gl.glColor3d(1, 1, 1);
				gl.glBegin(GL2.GL_QUADS);
				gl.glVertex3i(0, 0, 0);
				gl.glVertex3i(2000, 0, 0);
				gl.glVertex3i(2000, 2000, 0);
				gl.glVertex3i(0, 2000, 0);
				gl.glEnd();

				Color[] colors =
				        new Color[] {
				                Color.BLACK,
				                Color.DARK_GRAY,
				                Color.GRAY,
				                Color.WHITE,
				                Color.BLUE,
				                Color.RED,
				                Color.GREEN,
				                Color.ORANGE,
				                Color.YELLOW
				        };

				GLUT glut = new GLUT();
				long intime = System.nanoTime();
				int images = 0;

				int y = offsetY;
				int seqIndex = 0;
				for (Sequence<SettlerImage> seq : reader.getSettlers()) {
					int x = offsetX;
					System.out.println("next sequence: "+ seqIndex);

					for (SettlerImage image : seq) {
						gl.glEnable(GL.GL_TEXTURE_2D);
						gl.glColor3f(1, 1, 1);
						image.drawAt(gl, x - image.getOffsetX(),
						        y + image.getHeight() + image.getOffsetY(),
						        colors[images % colors.length]);

						gl.glDisable(GL.GL_TEXTURE_2D);
						gl.glColor3f(1, 0, 0);
						gl.glBegin(GL.GL_LINE_STRIP);
						gl.glVertex2i(x, y);
						gl.glVertex2i(x,
						        y + image.getHeight() + image.getOffsetY());
						gl.glVertex2i(x - image.getOffsetX(),
						        y + image.getHeight() + image.getOffsetY());
						gl.glEnd();
						drawPoint(gl, x, y);
						drawPoint(gl, x + image.getWidth(), y);
						drawPoint(gl, x + image.getWidth(),
						        y + image.getHeight());
						drawPoint(gl, x, y + image.getHeight());

						System.out.println("image sizes: " + image.getWidth()
						        + "x" + image.getHeight());

						x += 100;
						images++;
					}

					gl.glColor3f(0, 0, 0);
					gl.glRasterPos2i(20, y + 20);
					glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, seqIndex
					        + ":");

					seqIndex++;
					y -= 200;
				}

				y = offsetY;
				int landImageIndex = 0;
				for (Sequence<LandscapeImage> seq : reader.getLandscapes()) {
					LandscapeImage image = seq.getImage(0);
					gl.glEnable(GL.GL_TEXTURE_2D);
					image.bind(gl);
					gl.glColor3f(1, 1, 1);
					gl.glBegin(GL2.GL_QUADS);
					gl.glTexCoord2f(0, 0);
					gl.glVertex2f(50, y);
					gl.glTexCoord2f(0, 1);
					gl.glVertex2f(50, y + 32);
					gl.glTexCoord2f(1, 1);
					gl.glVertex2f(82, y + 32);
					gl.glTexCoord2f(1, 0);
					gl.glVertex2f(82, y);
					gl.glEnd();
					gl.glDisable(GL.GL_TEXTURE_2D);

					gl.glColor3f(0, 0, 0);
					gl.glRasterPos2i(20, y + 20);
					glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12,
					        landImageIndex + ":");

					if (image.isContinuous()) {
						gl.glRasterPos2i(100, y + 20);
						glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "big");
					}
					images++;
					landImageIndex++;
					y -= 40;
				}

				y = offsetY;
				int guiImageIndex = 0;
				for (Sequence<GuiImage> seq : reader.getGuis()) {
					GuiImage image = seq.getImage(0);
					System.out.println("next gui image: "+ guiImageIndex + " " + image.getWidth() + "x" + image.getHeight() + " px");
					gl.glEnable(GL.GL_TEXTURE_2D);
					image.drawAt(gl, 100, y);
					gl.glDisable(GL.GL_TEXTURE_2D);

					gl.glColor3f(0, 0, 0);
					gl.glRasterPos2i(20, y + 20);
					glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12,
					        guiImageIndex + ":");

					images++;
					guiImageIndex++;
					y -= image.getHeight() + 20;
				}

				System.out.println("Nanotime: " + (System.nanoTime() - intime)
				        + " for " + images + " images");
				gl.glFlush();

			}

			private void drawPoint(GL2 gl, int x, int y) {
				gl.glColor3f(0, 0, 1);
				gl.glBegin(GL2.GL_QUADS);
				gl.glVertex2i(x + 2, y + 2);
				gl.glVertex2i(x + 2, y - 2);
				gl.glVertex2i(x - 2, y - 2);
				gl.glVertex2i(x - 2, y + 2);
				gl.glEnd();
				gl.glColor3f(1, 1, 1);
			}
		});
	}

	private static void reloadDatFile() throws IOException {
		File file =
		        new File(FILE.replace("%", String.format("%02d", datFileIndex)));

		reader = new DatFileReader(file);
	}

}
