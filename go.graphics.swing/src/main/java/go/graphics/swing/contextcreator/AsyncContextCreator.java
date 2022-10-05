/*******************************************************************************
 * Copyright (c) 2018 - 2019
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
package go.graphics.swing.contextcreator;

import org.lwjgl.BufferUtils;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.nio.IntBuffer;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import go.graphics.DrawmodeListener;
import go.graphics.FramerateComputer;
import go.graphics.swing.ContextContainer;
import go.graphics.swing.event.swingInterpreter.GOSwingEventConverter;

public abstract class AsyncContextCreator extends ContextCreator implements Runnable,DrawmodeListener {

	protected boolean offscreen = true;
	protected boolean async_resized = false;
	protected boolean clear_offscreen = true;
	private boolean continue_run = true;

	private BufferedImage bi = null;
	private IntBuffer pixels;

	private Thread render_thread = new Thread(this, "AsyncRenderer");

	public AsyncContextCreator(ContextContainer container, boolean debug)  {
		super(container, debug);
	}

	@Override
	public void stop() {
		continue_run = false;
	}

	@Override
	public void initSpecific() {
		canvas = new JPanel() {
			public void paintComponent(Graphics graphics) {
				super.paintComponent(graphics);

				if(first_draw) {
					SwingUtilities.windowForComponent(this).addKeyListener(new GOSwingEventConverter(parent, parent));
					first_draw = false;
				}

				if(offscreen) {
					synchronized (wnd_lock) {
						graphics.drawImage(bi, 0, 0, null);
						graphics.dispose();
					}
				} else {
					graphics.drawString("Press m to enable offscreen transfer", width/3, height/2);
				}

				if(fpsLimit == 0) repaint();
			}
		};

		render_thread.start();
	}

	public abstract void async_init();

	public abstract void async_set_size(int width, int height);

	public abstract void async_refresh();

	public abstract void async_swapbuffers();

	public abstract void async_stop();

	@Override
	public void run() {
		synchronized (wnd_lock) {
			width = new_width;
			height = new_height;
		}
		async_init();

		FramerateComputer fpsComputer = new FramerateComputer();

		while(continue_run) {
			try {
				if (change_res) {
					synchronized (wnd_lock) {
						width = new_width;
						height = new_height;

						if (async_resized) {
							async_resized = false;
						} else {
							async_set_size(width, height);
						}

						Thread.sleep(20); // we must wait a bit because X is async and our window must not be resized in time otherwise
						parent.resizeContext(width, height);

						bi = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
						pixels = BufferUtils.createIntBuffer(width * height);
					}
					change_res = false;
				}
				async_refresh();

				parent.draw();
				parent.finishFrame();

				if (offscreen) {
					synchronized (wnd_lock) {
						parent.readFramebuffer(pixels, width, height);
						for (int x = 0; x != width; x++) {
							for (int y = 0; y != height; y++) {
								bi.setRGB(x, height - y - 1, pixels.get(y * width + x));
							}
						}
					}
				}

				if (!offscreen || clear_offscreen) {
					if (clear_offscreen && !offscreen) {
						parent.clearFramebuffer();
						clear_offscreen = false;
					}
					async_swapbuffers();
					if (fpsLimit != 0) fpsComputer.nextFrame(fpsLimit);
				}
			} catch(ContextException ignored) {
			} catch(Throwable thrown) {
				thrown.printStackTrace();
			}
		}

		async_stop();
	}

	@Override
	public void changeDrawMode() {
		offscreen = !offscreen;
		clear_offscreen = true;
	}
}
