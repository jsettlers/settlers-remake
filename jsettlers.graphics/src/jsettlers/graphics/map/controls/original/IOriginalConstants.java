/*******************************************************************************
 * Copyright (c) 2015
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
package jsettlers.graphics.map.controls.original;

import jsettlers.common.images.OriginalImageLink;

public abstract class IOriginalConstants {
	float UI_RATIO;

	float UI_CENTERY;

	float UI_CENTERX;

	float UI_DECORATIONRIGHT;

	// relative to top minimap area. This is the whole area including the right half.
	float MINIMAP_WIDTH = 126f / 201f;
	float MINIMAP_TOPLEFT_X = 68f / 201f;
	float MINIMAP_TOPRIGHT_X = MINIMAP_WIDTH + MINIMAP_TOPLEFT_X;
	float MINIMAP_TOP_Y = (142f - 39f) / 142f;
	float MINIMAP_HEIGHT = 128f / 142f;
	float MINIMAP_BOTTOMLEFT_X = 3f / 201f;
	float MINIMAP_BOTTOMRIGHT_X = MINIMAP_WIDTH + MINIMAP_BOTTOMLEFT_X;
	float MINIMAP_BOTTOM_Y = 4f / 142f;

	// relative to main content
	public float UI_TABS1_TOP = 1 - (float) 13 / 338;
	public float UI_TABS1_BOTTOM = 1 - (float) 46 / 338;
	public float UI_TABS1_SIDEMARGIN = (float) 8 / 136;
	public float UI_TABS1_WIDTH = (float) 33 / 136;
	public float UI_TABS1_SPACING = (float) 9 / 136;

	public float UI_TABS2_TOP = UI_TABS1_BOTTOM;
	public float UI_TABS2_BOTTOM = UI_TABS2_TOP - (float) 24 / 338;
	public float UI_TABS2_SIDEMARGIN = (float) 8 / 136;
	public float UI_TABS2_WIDTH = (float) 27 / 136;
	public float UI_TABS2_SPACING = (float) 3 / 136;

	public float CONTENT_LEFT = (float) 8 / 136;
	public float CONTENT_BOTTOM = 1 - (float) 294 / 338;
	public float CONTENT_RIGHT = (float) 128 / 136;
	public float CONTENT_TOP = 1 - (float) 76 / 338;

	// file
	protected final int UI_BG_FILE = 4;

	// sequences
	protected final int UI_BG_SEQINDEX_MAIN = 2;

	protected final int UI_BG_SEQINDEX_MINIMAPR = 1;

	protected final int UI_BG_SEQINDEX_MINIMAPL = 0;

	protected final int UI_BG_SEQINDEX_RIGHT = 3;

	OriginalImageLink UI_BG_SEQ_MAIN;
	OriginalImageLink UI_BG_SEQ_MINIMAPR;
	OriginalImageLink UI_BG_SEQ_MINIMAPL;
	OriginalImageLink UI_BG_SEQ_RIGHT;
}
