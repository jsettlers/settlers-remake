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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Locale;

import javax.swing.JPanel;

/**
 * Debug class that displays a file as an image.
 * 
 * @author michael
 *
 */
public class DataImage extends JPanel implements MouseListener,
		MouseMotionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6066076752384349596L;

	private final byte[] data;

	private int offset = 0;
	private int pixelLength = 1; // in bytes

	private long redMask = 0xff;
	private long greenMask = 0x0000;
	private long blueMask = 0x0000;

	private int width = 128; // in pixel
	private int height = 200;

	private int currentMarked = 0;

	private boolean littleEndian = true;

	private int pixelSize = 3;

	public DataImage(byte[] data) {
		this.data = data;
		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		for (int i = 64; i < 100000; i += 128) {
			System.out.println(getData(i, 1));
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		for (int index = 0; index < getImageHeight() * getImageWidth(); index++) {
			int pos = pixelIndexToDataPos(index);
			long current = getData(pos, this.pixelLength);

			Color c = new Color(getMasked(current, this.redMask), getMasked(current,
					this.greenMask), getMasked(current, this.blueMask));
			g2d.setColor(c);
			Point screen = dataPosToScreen(pos);
			g2d.fillRect(screen.x, screen.y, this.pixelSize, this.pixelSize);
		}

		paintMarkedPoint(g2d);
	}

	/**
	 * Uses a mask and maps the result to 0..1
	 * 
	 * @param pos
	 * @param greenMask2
	 * @return
	 */
	private float getMasked(long toMask, long mask) {
		long maxValue = 1; // one bigger than max value
		long resultValue = 0;
		for (int bitIndex = 63; bitIndex >= 0; bitIndex--) {
			long currentBit = toMask & (1 << bitIndex);
			long maskBit = mask & (1 << bitIndex);

			if (maskBit != 0) {
				resultValue *= 2;
				if (currentBit != 0) {
					resultValue += 1;
				}
				maxValue *= 2;
			}
		}

		if (maxValue <= 1) {
			return .0f;
		} else {
			return ((float) resultValue) / (maxValue - 1);
		}
	}

	/**
	 * Gets a data block in the current endianness.
	 * 
	 * @param pos
	 * @param length
	 * @return
	 */
	private long getData(int pos, int length) {
		if (pos < 0 || pos + length >= this.data.length) {
			return 0;
		}

		long result = 0;
		for (int i = 0; i < length && i < 8; i++) {
			int resultByte;
			if (this.littleEndian) {
				resultByte = i;
			} else {
				resultByte = length - 1 - i;
			}
			result |= (0xffl & this.data[pos + i]) << (resultByte * 8);
		}

		// if ((pos & 0x04) != 0) {
		result += pos;
		// } else {
		// result -= pos;
		// }
		result &= 0xff;

		if (pos % 128 < 64) {
			result = 0xff - result;
		}

		// result |= (pos & 0xffff) << 8;

		if (result < 100 || result > 155) {
			return 0;
		} else {
			return result;
		}
		// return result;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(this.width, getImageHeight());
	}

	public void setOffset(int offset) {
		this.offset = offset;
		this.repaint();
	}

	public int getOffset() {
		return this.offset;
	}

	public void setPixelLength(int pixelLength) {
		this.pixelLength = pixelLength;
		this.repaint();
	}

	public int getPixelLength() {
		return this.pixelLength;
	}

	public void setImageWidth(int width) {
		this.width = width;
		this.repaint();
	}

	public int getImageWidth() {
		return this.width;
	}

	public void setImageHeight(int height) {
		this.height = height;
		this.repaint();
	}

	public int getImageHeight() {
		return this.height;
	}

	private int screenToDataPos(Point p) {
		int x = p.x / this.pixelSize;
		int y = p.y / this.pixelSize;
		return this.offset + (y * getImageWidth() + x) * getPixelLength();
	}

	private Point dataPosToScreen(int pos) {
		int y = (pos - this.offset) / getPixelLength() / getImageWidth();
		int x = (pos - this.offset) / getPixelLength() - y * getImageWidth();
		return new Point(x * this.pixelSize, y * this.pixelSize);
	}

	private int pixelIndexToDataPos(int index) {
		return index * getPixelLength() + this.offset;
	}

	private void paintMarkedPoint(Graphics2D g) {
		Point pos = dataPosToScreen(this.currentMarked);
		int realCurrentMarked = screenToDataPos(pos);

		g.setColor(Color.RED);
		g.drawRect(pos.x - 1, pos.y - 1, this.pixelSize + 2, this.pixelSize + 2);

		String str = "" + realCurrentMarked + ":";

		/*
		 * for (int i = 0; i < getPixelLength(); i++) { str += " " + Integer.toHexString(0xff & data[i]); }
		 */
		long pixel = getData(realCurrentMarked, getPixelLength());
		str += " 0x" + String.format(Locale.ENGLISH, "%0" + (getPixelLength() * 2) + "x", pixel);

		int fontHeight = g.getFontMetrics().getHeight() + 4;
		g.setColor(Color.WHITE);
		g.fillRect(0, getHeight() - fontHeight, getWidth(), fontHeight);
		g.setColor(Color.BLACK);
		g.drawString(str, 2, getHeight() - 2);
	}

	private void mouseDownOver(Point point) {
		this.currentMarked = screenToDataPos(point);
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mouseDownOver(e.getPoint());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseDownOver(e.getPoint());
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	public void setLittleEndian(boolean littleEndian) {
		this.littleEndian = littleEndian;
		this.repaint();
	}

	public boolean isLittleEndian() {
		return this.littleEndian;
	}

	public void setRedMask(long redMask) {
		this.redMask = redMask;
		this.repaint();
	}

	public long getRedMask() {
		return this.redMask;
	}

	public void setGreenMask(long greenMask) {
		this.greenMask = greenMask;
		this.repaint();
	}

	public long getGreenMask() {
		return this.greenMask;
	}

	public void setBlueMask(long blueMask) {
		this.blueMask = blueMask;
		this.repaint();
	}

	public long getBlueMask() {
		return this.blueMask;
	}

	/**
	 * moves the marker
	 * 
	 * @param x
	 * @param y
	 */
	public void moveMarked(int x, int y) {
		this.currentMarked += (x + y * getImageWidth()) * getPixelLength();
		repaint();
	}

}
