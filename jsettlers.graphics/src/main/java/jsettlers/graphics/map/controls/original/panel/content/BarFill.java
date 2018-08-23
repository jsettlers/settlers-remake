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
package jsettlers.graphics.map.controls.original.panel.content;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import go.graphics.EGeometryFormatType;
import go.graphics.EGeometryType;
import go.graphics.GLDrawContext;
import go.graphics.GeometryHandle;
import go.graphics.IllegalBufferException;
import jsettlers.common.Color;
import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.common.position.FloatRectangle;
import jsettlers.common.action.Action;
import jsettlers.graphics.action.ExecutableAction;
import jsettlers.graphics.ui.UIPanel;

/**
 * This is a bar that is filled
 * 
 * @author michael
 * @author codingberlin
 *
 */
public class BarFill extends UIPanel {

	private static final ImageLink barImageLink = new OriginalImageLink(EImageLinkType.GUI, 3, 336, 0); // checked in the original game

	private static final float EMPTY_X = .07f;
	private static final float FULL_X = .93f;

	private ExecutableAction listener;
	private float barFillPercentage = 0;
	private float descriptionPercentage = 0;

	public BarFill(String name) {
		setBackground(barImageLink);
		this.name = name;
	}

	private GeometryHandle geometry = null;
	private static ByteBuffer geometryBfr = ByteBuffer.allocateDirect(4*4*2).order(ByteOrder.nativeOrder());
	private FloatRectangle writtenPosition = null;
	private float writtenMaxX = -1;
	private String name;

	private static final Color barColor = new Color(0, .78f, .78f, 1);

	@Override
	public void drawAt(GLDrawContext gl) {
		FloatRectangle position = getPosition();
		float fillX = barFillPercentage < .01f ? 0 : barFillPercentage > .99f ? 1 : EMPTY_X * (1 - barFillPercentage) + FULL_X * barFillPercentage;
		float maxX = position.getMinX() * (1 - fillX) + position.getMaxX() * fillX;

		try {
			if (geometry == null || !geometry.isValid()) geometry = gl.generateGeometry(4, EGeometryFormatType.VertexOnly2D, false, name);
			if (!position.equals(writtenPosition) || writtenMaxX != maxX) {
				writtenPosition = position;
				writtenMaxX = maxX;
				geometryBfr.asFloatBuffer().put(new float[]{
						maxX, position.getMinY(), position.getMinX(), position.getMinY(),
						position.getMinX(), position.getMaxY(), maxX, position.getMaxY()});
				gl.updateGeometryAt(geometry, 0, geometryBfr);
			}
			gl.draw2D(geometry, null, EGeometryType.Quad, 0, 4, 0, 0, 0, 1, 1, 1, barColor, 1);
		} catch(IllegalBufferException ex) {
			ex.printStackTrace();
		}

		super.drawBackground(gl);
	}

	@Override
	public Action getAction(final float relativeX, float relativeY) {
		final float relativeFill = getFillForClick(relativeX);
		return new ExecutableAction() {
			@Override
			public void execute() {
				setBarFill(relativeFill, relativeFill);
				if (listener != null) {
					listener.execute();
				}
			}
		};
	}

	protected float getFillForClick(final float relativeX) {
		if (relativeX < EMPTY_X) {
			return 0;
		} else if (relativeX > FULL_X) {
			return 1;
		} else {
			return (relativeX - EMPTY_X) / (FULL_X - EMPTY_X);
		}
	}

	@Override
	public String getDescription(float relativeX, float relativeY) {
		return Math.round(descriptionPercentage * 100) + "%";
	}

	/**
	 *
	 * @return A value from 0 to 1 indicating the proportion of the bar filled.
	 */
	public float getBarFillPercentage() {
		return barFillPercentage;
	}

	public void setAction(ExecutableAction action) {
		this.listener = action;
	}

	/**
	 *
	 * @param descriptionPercentage
	 *            expects a barFillPercentage in the range 0.0 to 1.0. barFillPercentages outside this range will be clamped.
	 */
	public void setBarFill(float barFillPercentage, float descriptionPercentage) {
		if (descriptionPercentage < 0) {
			descriptionPercentage = 0;
		} else if (descriptionPercentage > 1) {
			descriptionPercentage = 1;
		}
		if (barFillPercentage < 0) {
			barFillPercentage = 0;
		} else if (barFillPercentage > 1) {
			barFillPercentage = 1;
		}
		this.descriptionPercentage = descriptionPercentage;
		this.barFillPercentage = barFillPercentage;
	}
}
