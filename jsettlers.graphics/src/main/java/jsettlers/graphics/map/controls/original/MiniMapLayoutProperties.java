package jsettlers.graphics.map.controls.original;

import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.graphics.map.controls.original.ControlPanelLayoutProperties.Resolution;

public class MiniMapLayoutProperties {
	private static final int DECORATIONS_FOLDER_INDEX = 4;
	private static final EImageLinkType DECORATIONS_SUBFOLDER = EImageLinkType.SETTLER;
	private static final int BUTTONS_FOLDER_INDEX = 3;
	private static final EImageLinkType BUTTONS_SUBFOLDER = EImageLinkType.GUI;
	private static final int LEFT_DECORATION_IMAGE_INDEX = 0;
	private static final int RIGHT_DECORATION_IMAGE_INDEX = 1;

	public final OriginalImageLink RIGHT_DECORATION;
	public final OriginalImageLink LEFT_DECORATION;
	public final OriginalImageLink IMAGELINK_BUTTON_CHAT_ACTIVE;
	public final OriginalImageLink IMAGELINK_BUTTON_CHAT_INACTIVE;

	public final float ASPECT_RATIO;

	public final float MAP_WIDTH;
	public final float MAP_HEIGHT;
	public final float MAP_BOTTOM;
	public final float MAP_LEFT;

	public final float BUTTON_WIDTH;
	public final float BUTTON_HEIGHT;

	public final float BUTTON_CHAT_TOP;
	public final float BUTTON_CHAT_LEFT;

	public final float BUTTON_FEATURES_TOP;
	public final float BUTTON_FEATURES_LEFT;

	public final float BUTTON_SETTLERS_TOP;
	public final float BUTTON_SETTLERS_LEFT;

	public final float BUTTON_BUILDINGS_TOP;
	public final float BUTTON_BUILDINGS_LEFT;

	public final float RIGHT_DECORATION_LEFT;

	// Raw image dimension.

	private final float LEFT_DECORATION_WIDTH_PX;
	private final float LEFT_DECORATION_HEIGHT_PX;

	private final float BUTTON_WIDTH_PX;
	private final float BUTTON_HEIGHT_PX;

	private final float BUTTON_CHAT_TOP_PX;
	private final float BUTTON_CHAT_LEFT_PX;

	private final float BUTTON_FEATURES_TOP_PX;
	private final float BUTTON_FEATURES_LEFT_PX;

	private final float BUTTON_SETTLERS_TOP_PX;
	private final float BUTTON_SETTLERS_LEFT_PX;

	private final float BUTTON_BUILDINGS_TOP_PX;
	private final float BUTTON_BUILDINGS_LEFT_PX;

	private final float RIGHT_DECORATION_WIDTH_PX;
	// private final float RIGHT_DECORATION_HEIGHT_PX;

	private final float miniMap_Top_px;
	private final float miniMap_Bottom_px;

	private final float leftDecoration_MiniMapTopLeftCorner_Left_px;
	private final float leftDecoration_MiniMapBottomLeftCorner_Left_px;
	private final float leftDecoration_MiniMapBottomRightCorner_Right_px;
	private final float rightDecoration_MiniMapTopRightCorner_Right_px;

	public static float getStride(float width) {
		return width / 2f; // Changing to half the width so that the stride (a width-wise distance) scales proportionately to the total width.
	}

	public MiniMapLayoutProperties(Resolution resolution) {
		int imageSequenceNumber;
		switch (resolution) {
		case HEIGHT480:
			LEFT_DECORATION_WIDTH_PX = 136;
			LEFT_DECORATION_HEIGHT_PX = 142;

			BUTTON_WIDTH_PX = 18;
			BUTTON_HEIGHT_PX = 15;
			BUTTON_CHAT_TOP_PX = LEFT_DECORATION_HEIGHT_PX - 5;
			BUTTON_CHAT_LEFT_PX = 5;
			BUTTON_FEATURES_TOP_PX = LEFT_DECORATION_HEIGHT_PX - 8;
			BUTTON_FEATURES_LEFT_PX = 35;
			BUTTON_SETTLERS_TOP_PX = LEFT_DECORATION_HEIGHT_PX - 26;
			BUTTON_SETTLERS_LEFT_PX = 26;
			BUTTON_BUILDINGS_TOP_PX = LEFT_DECORATION_HEIGHT_PX - 45;
			BUTTON_BUILDINGS_LEFT_PX = 16;

			RIGHT_DECORATION_WIDTH_PX = 71;
			// RIGHT_DECORATION_HEIGHT_PX = 142;

			miniMap_Top_px = 10;
			miniMap_Bottom_px = 4;

			leftDecoration_MiniMapTopLeftCorner_Left_px = 68;
			leftDecoration_MiniMapBottomLeftCorner_Left_px = 4;
			leftDecoration_MiniMapBottomRightCorner_Right_px = 4;
			rightDecoration_MiniMapTopRightCorner_Right_px = 11;

			imageSequenceNumber = 0;
			break;
		case HEIGHT600:
			LEFT_DECORATION_WIDTH_PX = 176;
			LEFT_DECORATION_HEIGHT_PX = 170;

			BUTTON_WIDTH_PX = 24;
			BUTTON_HEIGHT_PX = 20;
			BUTTON_CHAT_TOP_PX = LEFT_DECORATION_HEIGHT_PX - 5;
			BUTTON_CHAT_LEFT_PX = 5;
			BUTTON_FEATURES_TOP_PX = LEFT_DECORATION_HEIGHT_PX - 9;
			BUTTON_FEATURES_LEFT_PX = 44;
			BUTTON_SETTLERS_TOP_PX = LEFT_DECORATION_HEIGHT_PX - 32;
			BUTTON_SETTLERS_LEFT_PX = 32;
			BUTTON_BUILDINGS_TOP_PX = LEFT_DECORATION_HEIGHT_PX - 55;
			BUTTON_BUILDINGS_LEFT_PX = 22;

			RIGHT_DECORATION_WIDTH_PX = 84;
			// RIGHT_DECORATION_HEIGHT_PX = 170;

			miniMap_Top_px = 5;
			miniMap_Bottom_px = 5;

			leftDecoration_MiniMapTopLeftCorner_Left_px = 88;
			leftDecoration_MiniMapBottomLeftCorner_Left_px = 8;
			leftDecoration_MiniMapBottomRightCorner_Right_px = 8;
			rightDecoration_MiniMapTopRightCorner_Right_px = 12;

			imageSequenceNumber = 1;
			break;
		default:
		case HEIGHT768:
			LEFT_DECORATION_WIDTH_PX = 216;
			LEFT_DECORATION_HEIGHT_PX = 224;

			BUTTON_WIDTH_PX = 30;
			BUTTON_HEIGHT_PX = 25;
			BUTTON_CHAT_TOP_PX = LEFT_DECORATION_HEIGHT_PX - 8;
			BUTTON_CHAT_LEFT_PX = 8;
			BUTTON_FEATURES_TOP_PX = LEFT_DECORATION_HEIGHT_PX - 13;
			BUTTON_FEATURES_LEFT_PX = 57;
			BUTTON_SETTLERS_TOP_PX = LEFT_DECORATION_HEIGHT_PX - 43;
			BUTTON_SETTLERS_LEFT_PX = 41;
			BUTTON_BUILDINGS_TOP_PX = LEFT_DECORATION_HEIGHT_PX - 72;
			BUTTON_BUILDINGS_LEFT_PX = 25;

			RIGHT_DECORATION_WIDTH_PX = 117;
			// RIGHT_DECORATION_HEIGHT_PX = 224;

			miniMap_Top_px = 8;
			miniMap_Bottom_px = 8;

			leftDecoration_MiniMapTopLeftCorner_Left_px = 108;
			leftDecoration_MiniMapBottomLeftCorner_Left_px = 4;
			leftDecoration_MiniMapBottomRightCorner_Right_px = 2;
			rightDecoration_MiniMapTopRightCorner_Right_px = 15;

			imageSequenceNumber = 2;
		}

		// Adjust the decoration proportions to fit the parallelogram shape of the minimap.
		float stride = getStride(
				LEFT_DECORATION_WIDTH_PX - leftDecoration_MiniMapBottomLeftCorner_Left_px - leftDecoration_MiniMapBottomRightCorner_Right_px);
		float imageStride = leftDecoration_MiniMapTopLeftCorner_Left_px - leftDecoration_MiniMapBottomLeftCorner_Left_px;
		float LEFT_DECORATION_HSTRETCH = stride / imageStride;

		// Adjust the right hand decoration.
		imageStride = (leftDecoration_MiniMapBottomRightCorner_Right_px * LEFT_DECORATION_HSTRETCH)
				+ (RIGHT_DECORATION_WIDTH_PX - rightDecoration_MiniMapTopRightCorner_Right_px);
		float RIGHT_DECORATION_HSTRETCH = stride / imageStride;

		ASPECT_RATIO = ((LEFT_DECORATION_HSTRETCH * LEFT_DECORATION_WIDTH_PX) + (RIGHT_DECORATION_HSTRETCH * RIGHT_DECORATION_WIDTH_PX))
				/ LEFT_DECORATION_HEIGHT_PX;

		MAP_WIDTH = (LEFT_DECORATION_WIDTH_PX - leftDecoration_MiniMapBottomLeftCorner_Left_px - leftDecoration_MiniMapBottomRightCorner_Right_px) /
				(LEFT_DECORATION_WIDTH_PX + (RIGHT_DECORATION_WIDTH_PX * (RIGHT_DECORATION_HSTRETCH / LEFT_DECORATION_HSTRETCH)));
		MAP_HEIGHT = (LEFT_DECORATION_HEIGHT_PX - miniMap_Top_px - miniMap_Bottom_px) / LEFT_DECORATION_HEIGHT_PX;
		MAP_BOTTOM = miniMap_Bottom_px / LEFT_DECORATION_HEIGHT_PX;
		MAP_LEFT = leftDecoration_MiniMapBottomLeftCorner_Left_px
				/ (LEFT_DECORATION_WIDTH_PX + (RIGHT_DECORATION_WIDTH_PX * (RIGHT_DECORATION_HSTRETCH / LEFT_DECORATION_HSTRETCH)));

		RIGHT_DECORATION_LEFT = LEFT_DECORATION_WIDTH_PX
				/ (LEFT_DECORATION_WIDTH_PX + (RIGHT_DECORATION_WIDTH_PX * (RIGHT_DECORATION_HSTRETCH / LEFT_DECORATION_HSTRETCH)));

		BUTTON_HEIGHT = BUTTON_HEIGHT_PX / LEFT_DECORATION_HEIGHT_PX;
		BUTTON_WIDTH = BUTTON_WIDTH_PX / LEFT_DECORATION_WIDTH_PX;

		BUTTON_CHAT_TOP = BUTTON_CHAT_TOP_PX / LEFT_DECORATION_HEIGHT_PX;
		BUTTON_CHAT_LEFT = BUTTON_CHAT_LEFT_PX / LEFT_DECORATION_WIDTH_PX;

		BUTTON_FEATURES_TOP = BUTTON_FEATURES_TOP_PX / LEFT_DECORATION_HEIGHT_PX;
		BUTTON_FEATURES_LEFT = BUTTON_FEATURES_LEFT_PX / LEFT_DECORATION_WIDTH_PX;

		BUTTON_SETTLERS_TOP = BUTTON_SETTLERS_TOP_PX / LEFT_DECORATION_HEIGHT_PX;
		BUTTON_SETTLERS_LEFT = BUTTON_SETTLERS_LEFT_PX / LEFT_DECORATION_WIDTH_PX;

		BUTTON_BUILDINGS_TOP = BUTTON_BUILDINGS_TOP_PX / LEFT_DECORATION_HEIGHT_PX;
		BUTTON_BUILDINGS_LEFT = BUTTON_BUILDINGS_LEFT_PX / LEFT_DECORATION_WIDTH_PX;

		LEFT_DECORATION = new OriginalImageLink(DECORATIONS_SUBFOLDER, DECORATIONS_FOLDER_INDEX, LEFT_DECORATION_IMAGE_INDEX, imageSequenceNumber);
		RIGHT_DECORATION = new OriginalImageLink(DECORATIONS_SUBFOLDER, DECORATIONS_FOLDER_INDEX, RIGHT_DECORATION_IMAGE_INDEX, imageSequenceNumber);

		IMAGELINK_BUTTON_CHAT_ACTIVE = new OriginalImageLink(BUTTONS_SUBFOLDER, BUTTONS_FOLDER_INDEX, 318, imageSequenceNumber);
		IMAGELINK_BUTTON_CHAT_INACTIVE = new OriginalImageLink(BUTTONS_SUBFOLDER, BUTTONS_FOLDER_INDEX, 321, imageSequenceNumber);
	}
}