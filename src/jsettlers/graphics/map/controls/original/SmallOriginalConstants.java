package jsettlers.graphics.map.controls.original;

import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;

public class SmallOriginalConstants extends IOriginalConstants {

	public SmallOriginalConstants() {
		UI_RATIO = (float) 480 / 209;

		UI_CENTERY = (float) 338 / 480;

		UI_CENTERX = (float) 136 / 209;

		UI_DECORATIONRIGHT = (float) 8 / 209 + UI_CENTERX;

		UI_BG_SEQ_MAIN =
		        new ImageLink(EImageLinkType.SETTLER, UI_BG_FILE,
		                UI_BG_SEQINDEX_MAIN, 0);
		UI_BG_SEQ_MINIMAPR =
		        new ImageLink(EImageLinkType.SETTLER, UI_BG_FILE,
		                UI_BG_SEQINDEX_MINIMAPR, 0);
		UI_BG_SEQ_MINIMAPL =
		        new ImageLink(EImageLinkType.SETTLER, UI_BG_FILE,
		                UI_BG_SEQINDEX_MINIMAPL, 0);
		UI_BG_SEQ_RIGHT =
		        new ImageLink(EImageLinkType.SETTLER, UI_BG_FILE,
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
