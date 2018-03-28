/*******************************************************************************
 * Copyright (c) 2018
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
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.awt.Graphics;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.nio.IntBuffer;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import go.graphics.DrawmodeListener;
import go.graphics.swing.AreaContainer;
import go.graphics.swing.event.swingInterpreter.GOSwingEventConverter;

public abstract class AsyncContextCreator extends ContextCreator implements Runnable,DrawmodeListener {

	private boolean offscreen = true;
	private boolean clear_offscreen = true;
    private boolean continue_run = true;

	protected boolean ignore_resize = false;
    protected BufferedImage bi = null;
    protected IntBuffer pixels;

    private Thread render_thread;

    public AsyncContextCreator(AreaContainer ac)  {
        super(ac);
    }

    @Override
    public void stop() {
        continue_run = false;
    }

    @Override
    public void initSpecific() {
        JPanel panel = new JPanel() {
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
            }
        };

        canvas = panel;
        render_thread = new Thread(this);
        render_thread.start();


    }

    @Override
    public void repaint() {
        canvas.repaint();
    }

    @Override
    public void requestFocus() {
        canvas.requestFocus();
    }

    public abstract void async_init();

    public abstract void async_set_size(int width, int height);

    public abstract void async_refresh();

    public abstract void async_swapbuffers();

    public abstract void async_stop();

    @Override
    public void run() {
        async_init();

        parent.init();

        while(continue_run) {
			if (change_res) {
				if(!ignore_resize) {
					width = new_width;
					height = new_height;
					async_set_size(width, height);

					parent.resize_gl(width, height);

					bi = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
					pixels = BufferUtils.createIntBuffer(width * height);
				}
				ignore_resize = false;
				change_res = false;
			}

			async_refresh();

			parent.draw();

			if (offscreen) {
				synchronized (wnd_lock) {
					GL11.glReadPixels(0, 0, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixels);
					for (int x = 0; x != width; x++) {
						for (int y = 0; y != height; y++) {
							bi.setRGB(x, height - y - 1, pixels.get(y * width + x));
						}
					}
				}
			}

			if(!offscreen || clear_offscreen ){
				if(clear_offscreen) {
					GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
					clear_offscreen = false;
				}
				async_swapbuffers();
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
