package jsettlers.graphics.map.controls.original;

import jsettlers.common.images.ImageLink;

public abstract class IOriginalConstants {
	float UI_RATIO;

	float UI_CENTERY;

	float UI_CENTERX;

	float UI_DECORATIONRIGHT;
	
	// relative to top minimap area. This is the whole area including the right half.
	float MINIMAP_WIDTH = 127f / 201f;
	float MINIMAP_TOPLEFT_X = 68f / 201f;
	float MINIMAP_TOPRIGHT_X = MINIMAP_WIDTH + MINIMAP_TOPLEFT_X;
	float MINIMAP_TOP_Y = (142f - 39f) / 142f;
	float MINIMAP_HEIGHT = 128f / 142f;
	float MINIMAP_BOTTOMLEFT_X = 5f / 201f;
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

	ImageLink UI_BG_SEQ_MAIN;
	ImageLink UI_BG_SEQ_MINIMAPR;
	ImageLink UI_BG_SEQ_MINIMAPL;
	ImageLink UI_BG_SEQ_RIGHT;
}
