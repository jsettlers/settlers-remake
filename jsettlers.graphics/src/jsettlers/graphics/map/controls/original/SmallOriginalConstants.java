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

import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.OriginalImageLink;

public class SmallOriginalConstants extends IOriginalConstants {

	public SmallOriginalConstants() {
		UI_RATIO = (float) 480 / 209;

		UI_CENTERY = (float) 338 / 480;

		UI_CENTERX = (float) 136 / 209;

		UI_DECORATIONRIGHT = (float) 8 / 209 + UI_CENTERX;

		UI_BG_SEQ_MAIN =
				new OriginalImageLink(EImageLinkType.SETTLER, UI_BG_FILE,
						UI_BG_SEQINDEX_MAIN, 0);
		UI_BG_SEQ_MINIMAPR =
				new OriginalImageLink(EImageLinkType.SETTLER, UI_BG_FILE,
						UI_BG_SEQINDEX_MINIMAPR, 0);
		UI_BG_SEQ_MINIMAPL =
				new OriginalImageLink(EImageLinkType.SETTLER, UI_BG_FILE,
						UI_BG_SEQINDEX_MINIMAPL, 0);
		UI_BG_SEQ_RIGHT =
				new OriginalImageLink(EImageLinkType.SETTLER, UI_BG_FILE,
						UI_BG_SEQINDEX_RIGHT, 0);

		UI_TABS1_TOP = 1 - (float) 13 / 338;
		UI_TABS1_BOTTOM = 1 - (float) 46 / 338;
		UI_TABS1_SIDEMARGIN = (float) 8 / 136;
		UI_TABS1_WIDTH = (float) 33 / 136;
		UI_TABS1_SPACING = (float) 9 / 136;

		UI_TABS2_TOP = UI_TABS1_BOTTOM;
		UI_TABS2_BOTTOM = UI_TABS2_TOP - (float) 24 / 338;
		UI_TABS2_SIDEMARGIN = (float) 8 / 136;
		UI_TABS2_WIDTH = (float) 27 / 136;
		UI_TABS2_SPACING = (float) 3 / 136;

		CONTENT_LEFT = (float) 8 / 136;
		CONTENT_BOTTOM = 1 - (float) 294 / 338;
		CONTENT_RIGHT = (float) 128 / 136;
		CONTENT_TOP = 1 - (float) 76 / 338;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof SmallOriginalConstants;
	}
}
