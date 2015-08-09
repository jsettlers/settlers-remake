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

public class ControlPanelLayoutProperties {
	public enum Resolution {
		HEIGHT480,
		HEIGHT600,
		HEIGHT768
	}

	private static final ControlPanelLayoutProperties height480 = new ControlPanelLayoutProperties(Resolution.HEIGHT480);
	private static final ControlPanelLayoutProperties height600 = new ControlPanelLayoutProperties(Resolution.HEIGHT600);
    private static final ControlPanelLayoutProperties height768 = new ControlPanelLayoutProperties(Resolution.HEIGHT768);
	private static final int UI_BG_FILE = 4;
	private static final int UI_BG_SEQINDEX_MAIN = 2;
	private static final int UI_BG_SEQINDEX_RIGHT = 3;

	public final float ASPECT_RATIO;
	public final float MAIN_PANEL_TOP;

	public final MiniMapLayoutProperties miniMap;

    public final OriginalImageLink IMAGELINK_MAIN;
    public final OriginalImageLink IMAGELINK_DECORATION_RIGHT;

	// relative to main content
	public final float PRIMARY_TABS_TOP;
	public final float PRIMARY_TABS_BOTTOM;
	public final float PRIMARY_TABS_SIDEMARGIN;
	public final float PRIMARY_TABS_WIDTH;
	public final float PRIMARY_TABS_SPACING;

	public final float SECONDARY_TABS_TOP;
	public final float SECONDARY_TABS_BOTTOM;
	public final float SECONDARY_TABS_SIDEMARGIN;
	public final float SECONDARY_TABS_WIDTH;
	public final float SECONDARY_TABS_SPACING;

	public final float CONTENT_TOP;
	public final float CONTENT_BOTTOM;
	public final float CONTENT_LEFT;
	public final float CONTENT_RIGHT;

	public final float SYSTEM_BUTTON_TOP;
	public final float SYSTEM_BUTTON_BOTTOM;
	public final float SYSTEM_BUTTON_LEFT;
	public final float SYSTEM_BUTTON_RIGHT;

	public final float LOWER_TABS_TOP;
	public final float LOWER_TABS_BOTTOM;
	public final float LOWER_TABS_LEFT;
	public final float LOWER_TABS_WIDTH;

    public final float RIGHT_DECORATION_WIDTH;

	// Raw image dimensions.

	private final float PANEL_HEIGHT_PX;
	private final float PANEL_WIDTH_PX;

	private final float RIGHT_DECORATION_WIDTH_PX;

	private final float PRIMARY_TABS_BUTTON_PLACEHOLDER_WIDTH_PX;
	private final float PRIMARY_TABS_BUTTON_PLACEHOLDER_HEIGHT_PX;
	private final float PRIMARY_TABS_INTER_BUTTON_SPACING_PX;
	private final float PRIMARY_TABS_TOP_PX;
	private final float PRIMARY_TABS_LEFT_PX;

	private final float SECONDARY_TABS_BUTTON_WIDTH_PX;
	private final float SECONDARY_TABS_BUTTON_HEIGHT_PX;
	private final float SECONDARY_TABS_INTER_BUTTON_SPACING_PX;
	private final float SECONDARY_TABS_TOP_PX;
	private final float SECONDARY_TABS_LEFT_PX;

	private final float CONTENT_PANE_WIDTH_PX;
	private final float CONTENT_PANE_HEIGHT_PX;
	private final float CONTENT_PANE_TOP_PX;
	private final float CONTENT_PANE_LEFT_PX;

	private final float SYSTEM_BUTTON_PLACEHOLDER_WIDTH_PX;
	private final float SYSTEM_BUTTON_PLACEHOLDER_HEIGHT_PX;
	private final float SYSTEM_BUTTON_TOP_PX;
	private final float SYSTEM_BUTTON_LEFT_PX;

	private final float LOWER_TABS_BUTTON_WIDTH_PX;
	private final float LOWER_TABS_BUTTON_HEIGHT_PX;
	private final float LOWER_TABS_TOP_PX;
	private final float LOWER_TABS_LEFT_PX;

	public static ControlPanelLayoutProperties getLayoutPropertiesFor(float pixelHeight)
	{
		if (pixelHeight < 600) {
			return height480;
		}
		else if (pixelHeight < 768) {
			return height600;
		}
		else {
			return height768;
		}
	}

	public ControlPanelLayoutProperties() {
		this(Resolution.HEIGHT480);
	}

	private ControlPanelLayoutProperties(Resolution resolution) {
		int imageSequenceNumber;

		switch (resolution) {
		case HEIGHT480:
		    miniMap = new MiniMapLayoutProperties(Resolution.HEIGHT480);

			PANEL_WIDTH_PX = 136;
			PANEL_HEIGHT_PX = 338;

			float PRIMARY_BUTTON_IMAGE_SIZE_PX_SMALL = 33;
			PRIMARY_TABS_BUTTON_PLACEHOLDER_WIDTH_PX = PRIMARY_BUTTON_IMAGE_SIZE_PX_SMALL - 1; // Image needs cropping 1 pixel to fit properly.
			PRIMARY_TABS_BUTTON_PLACEHOLDER_HEIGHT_PX = PRIMARY_BUTTON_IMAGE_SIZE_PX_SMALL - 1; // Image needs cropping 1 pixel to fit properly.
			PRIMARY_TABS_INTER_BUTTON_SPACING_PX = 10;
			PRIMARY_TABS_TOP_PX = PANEL_HEIGHT_PX - 14;
			PRIMARY_TABS_LEFT_PX = 9;

			SECONDARY_TABS_BUTTON_WIDTH_PX = 27;
			SECONDARY_TABS_BUTTON_HEIGHT_PX = 24;
			SECONDARY_TABS_INTER_BUTTON_SPACING_PX = 2;
			SECONDARY_TABS_TOP_PX = PANEL_HEIGHT_PX - 46;
			SECONDARY_TABS_LEFT_PX = 10;

			CONTENT_PANE_WIDTH_PX = 118;
			CONTENT_PANE_HEIGHT_PX = 216;
			CONTENT_PANE_TOP_PX = PANEL_HEIGHT_PX - 77;
			CONTENT_PANE_LEFT_PX = 9;

			SYSTEM_BUTTON_PLACEHOLDER_WIDTH_PX = 18;
			SYSTEM_BUTTON_PLACEHOLDER_HEIGHT_PX = 27;
			SYSTEM_BUTTON_TOP_PX = 9 + SYSTEM_BUTTON_PLACEHOLDER_HEIGHT_PX;
			SYSTEM_BUTTON_LEFT_PX = 9;

			LOWER_TABS_BUTTON_WIDTH_PX = 24;
			LOWER_TABS_BUTTON_HEIGHT_PX = 24;
			LOWER_TABS_TOP_PX = 12 + LOWER_TABS_BUTTON_HEIGHT_PX;
			LOWER_TABS_LEFT_PX = 30;

            RIGHT_DECORATION_WIDTH_PX = 8;

			imageSequenceNumber = 0;
			break;
		case HEIGHT600:
            miniMap = new MiniMapLayoutProperties(Resolution.HEIGHT600);

			PANEL_WIDTH_PX = 176;
			PANEL_HEIGHT_PX = 430;

			float PRIMARY_BUTTON_IMAGE_SIZE_PX_MEDIUM = 44;
			PRIMARY_TABS_BUTTON_PLACEHOLDER_WIDTH_PX = PRIMARY_BUTTON_IMAGE_SIZE_PX_MEDIUM - 1;
			PRIMARY_TABS_BUTTON_PLACEHOLDER_HEIGHT_PX = PRIMARY_BUTTON_IMAGE_SIZE_PX_MEDIUM;
			PRIMARY_TABS_INTER_BUTTON_SPACING_PX = 13;
			PRIMARY_TABS_TOP_PX = PANEL_HEIGHT_PX - 5;
			PRIMARY_TABS_LEFT_PX = 9;

			SECONDARY_TABS_BUTTON_WIDTH_PX = 36;
			SECONDARY_TABS_BUTTON_HEIGHT_PX = 32;
			SECONDARY_TABS_INTER_BUTTON_SPACING_PX = 4;
			SECONDARY_TABS_TOP_PX = PRIMARY_TABS_TOP_PX - PRIMARY_TABS_BUTTON_PLACEHOLDER_HEIGHT_PX;
			SECONDARY_TABS_LEFT_PX = 9;

			CONTENT_PANE_WIDTH_PX = 157;
			CONTENT_PANE_HEIGHT_PX = 288;
			CONTENT_PANE_TOP_PX = PANEL_HEIGHT_PX - 91;
			CONTENT_PANE_LEFT_PX = 10;

			float SYSTEM_BUTTON_IMAGE_WIDTH_PX_MEDIUM = 24;
			float SYSTEM_BUTTON_IMAGE_HEIGHT_PX_MEDIUM = 36;
			SYSTEM_BUTTON_PLACEHOLDER_WIDTH_PX = SYSTEM_BUTTON_IMAGE_WIDTH_PX_MEDIUM + 2;
			SYSTEM_BUTTON_PLACEHOLDER_HEIGHT_PX = SYSTEM_BUTTON_IMAGE_HEIGHT_PX_MEDIUM + 3;
			SYSTEM_BUTTON_TOP_PX = 42;
			SYSTEM_BUTTON_LEFT_PX = 8;

			LOWER_TABS_BUTTON_WIDTH_PX = 32;
			LOWER_TABS_BUTTON_HEIGHT_PX = 32;
			LOWER_TABS_TOP_PX = 7 + LOWER_TABS_BUTTON_HEIGHT_PX;
			LOWER_TABS_LEFT_PX = 38;

            RIGHT_DECORATION_WIDTH_PX = 8;

			imageSequenceNumber = 1;
			break;
		default:
		case HEIGHT768:
            miniMap = new MiniMapLayoutProperties(Resolution.HEIGHT768);

			PANEL_WIDTH_PX = 216;
			PANEL_HEIGHT_PX = 544;

			float PRIMARY_BUTTON_IMAGE_SIZE_PX_LARGE = 55;
			PRIMARY_TABS_BUTTON_PLACEHOLDER_WIDTH_PX = PRIMARY_BUTTON_IMAGE_SIZE_PX_LARGE - 2; // Button image needs cropping 2 pixels to fit properly
			PRIMARY_TABS_BUTTON_PLACEHOLDER_HEIGHT_PX = 55;
			PRIMARY_TABS_INTER_BUTTON_SPACING_PX = 17;
			PRIMARY_TABS_TOP_PX = PANEL_HEIGHT_PX - 9;
			PRIMARY_TABS_LEFT_PX = 10;

			SECONDARY_TABS_BUTTON_WIDTH_PX = 45;
			SECONDARY_TABS_BUTTON_HEIGHT_PX = 40;
			SECONDARY_TABS_INTER_BUTTON_SPACING_PX = 4;
			SECONDARY_TABS_TOP_PX = PRIMARY_TABS_TOP_PX - PRIMARY_TABS_BUTTON_PLACEHOLDER_HEIGHT_PX;
			SECONDARY_TABS_LEFT_PX = PRIMARY_TABS_LEFT_PX;

			CONTENT_PANE_WIDTH_PX = 197;
			CONTENT_PANE_HEIGHT_PX = 360;
			CONTENT_PANE_TOP_PX = PANEL_HEIGHT_PX - 116;
			CONTENT_PANE_LEFT_PX = 10;

			SYSTEM_BUTTON_PLACEHOLDER_WIDTH_PX = 30;
			SYSTEM_BUTTON_PLACEHOLDER_HEIGHT_PX = 45;
			SYSTEM_BUTTON_TOP_PX = 8 + SYSTEM_BUTTON_PLACEHOLDER_HEIGHT_PX;
			SYSTEM_BUTTON_LEFT_PX = PRIMARY_TABS_LEFT_PX;

			LOWER_TABS_BUTTON_WIDTH_PX = 40;
			LOWER_TABS_BUTTON_HEIGHT_PX = 40;
			LOWER_TABS_TOP_PX = 13 + LOWER_TABS_BUTTON_HEIGHT_PX;
			LOWER_TABS_LEFT_PX = 45;

            RIGHT_DECORATION_WIDTH_PX = 8;

			imageSequenceNumber = 2;
		}

        float MAIN_PANEL_ASPECT_RATIO = PANEL_WIDTH_PX / PANEL_HEIGHT_PX;
        float miniMapHeightRelativeToControlPanelWidth = 1f / miniMap.ASPECT_RATIO;
        float mainPanelHeightRelativeToControlPanelWidth = miniMap.RIGHT_DECORATION_LEFT / MAIN_PANEL_ASPECT_RATIO;

        ASPECT_RATIO = 1f / (miniMapHeightRelativeToControlPanelWidth + mainPanelHeightRelativeToControlPanelWidth);
        MAIN_PANEL_TOP = 1f - (miniMapHeightRelativeToControlPanelWidth / (miniMapHeightRelativeToControlPanelWidth + mainPanelHeightRelativeToControlPanelWidth));

		RIGHT_DECORATION_WIDTH = (RIGHT_DECORATION_WIDTH_PX / PANEL_WIDTH_PX) * miniMap.RIGHT_DECORATION_LEFT;

		PRIMARY_TABS_TOP = PRIMARY_TABS_TOP_PX / PANEL_HEIGHT_PX;
		PRIMARY_TABS_BOTTOM = (PRIMARY_TABS_TOP_PX - PRIMARY_TABS_BUTTON_PLACEHOLDER_HEIGHT_PX) / PANEL_HEIGHT_PX;
		PRIMARY_TABS_SIDEMARGIN = PRIMARY_TABS_LEFT_PX / PANEL_WIDTH_PX;
		PRIMARY_TABS_WIDTH = PRIMARY_TABS_BUTTON_PLACEHOLDER_WIDTH_PX / PANEL_WIDTH_PX;
		PRIMARY_TABS_SPACING = PRIMARY_TABS_INTER_BUTTON_SPACING_PX / PANEL_WIDTH_PX;

		SECONDARY_TABS_TOP = SECONDARY_TABS_TOP_PX / PANEL_HEIGHT_PX;
		SECONDARY_TABS_BOTTOM = (SECONDARY_TABS_TOP_PX - SECONDARY_TABS_BUTTON_HEIGHT_PX) / PANEL_HEIGHT_PX;
		SECONDARY_TABS_SIDEMARGIN = SECONDARY_TABS_LEFT_PX / PANEL_WIDTH_PX;
		SECONDARY_TABS_WIDTH = SECONDARY_TABS_BUTTON_WIDTH_PX / PANEL_WIDTH_PX;
		SECONDARY_TABS_SPACING = SECONDARY_TABS_INTER_BUTTON_SPACING_PX / PANEL_WIDTH_PX;

		CONTENT_TOP = CONTENT_PANE_TOP_PX / PANEL_HEIGHT_PX;
		CONTENT_BOTTOM = (CONTENT_PANE_TOP_PX - CONTENT_PANE_HEIGHT_PX) / PANEL_HEIGHT_PX;
		CONTENT_LEFT = CONTENT_PANE_LEFT_PX / PANEL_WIDTH_PX;
		CONTENT_RIGHT = (CONTENT_PANE_LEFT_PX + CONTENT_PANE_WIDTH_PX) / PANEL_WIDTH_PX;

		SYSTEM_BUTTON_TOP = SYSTEM_BUTTON_TOP_PX / PANEL_HEIGHT_PX;
		SYSTEM_BUTTON_BOTTOM = (SYSTEM_BUTTON_TOP_PX - SYSTEM_BUTTON_PLACEHOLDER_HEIGHT_PX) / PANEL_HEIGHT_PX;
		SYSTEM_BUTTON_LEFT = SYSTEM_BUTTON_LEFT_PX / PANEL_WIDTH_PX;
		SYSTEM_BUTTON_RIGHT = (SYSTEM_BUTTON_LEFT_PX + SYSTEM_BUTTON_PLACEHOLDER_WIDTH_PX) / PANEL_WIDTH_PX;

		LOWER_TABS_TOP = LOWER_TABS_TOP_PX / PANEL_HEIGHT_PX;
		LOWER_TABS_BOTTOM = (LOWER_TABS_TOP_PX - LOWER_TABS_BUTTON_HEIGHT_PX) / PANEL_HEIGHT_PX;
		LOWER_TABS_LEFT = LOWER_TABS_LEFT_PX / PANEL_WIDTH_PX;
		LOWER_TABS_WIDTH = LOWER_TABS_BUTTON_WIDTH_PX / PANEL_WIDTH_PX;

		IMAGELINK_MAIN = new OriginalImageLink(EImageLinkType.SETTLER, UI_BG_FILE, UI_BG_SEQINDEX_MAIN, imageSequenceNumber);
		IMAGELINK_DECORATION_RIGHT = new OriginalImageLink(EImageLinkType.SETTLER, UI_BG_FILE, UI_BG_SEQINDEX_RIGHT, imageSequenceNumber);
	}
}