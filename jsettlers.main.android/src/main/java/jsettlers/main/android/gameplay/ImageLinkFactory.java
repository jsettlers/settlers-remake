package jsettlers.main.android.gameplay;

import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.common.movable.EMovableType;

/**
 * Created by tompr on 13/01/2017.
 */

public class ImageLinkFactory {

	public static ImageLink get(EMovableType movableType) {

		switch (movableType) {
		// Soldiers
		case SWORDSMAN_L1:
			return new OriginalImageLink(EImageLinkType.GUI, 14, 207, 0);
		case SWORDSMAN_L2:
			return new OriginalImageLink(EImageLinkType.GUI, 14, 216, 0);
		case SWORDSMAN_L3:
			return new OriginalImageLink(EImageLinkType.GUI, 14, 225, 0);
		case PIKEMAN_L1:
			return new OriginalImageLink(EImageLinkType.GUI, 14, 210, 0);
		case PIKEMAN_L2:
			return new OriginalImageLink(EImageLinkType.GUI, 14, 219, 0);
		case PIKEMAN_L3:
			return new OriginalImageLink(EImageLinkType.GUI, 14, 228, 0);
		case BOWMAN_L1:
			return new OriginalImageLink(EImageLinkType.GUI, 14, 213, 0);
		case BOWMAN_L2:
			return new OriginalImageLink(EImageLinkType.GUI, 14, 222, 0);
		case BOWMAN_L3:
			return new OriginalImageLink(EImageLinkType.GUI, 14, 231, 0);

		// Specialists
		case PIONEER:
			return new OriginalImageLink(EImageLinkType.GUI, 14, 204, 0);
		case GEOLOGIST:
			return new OriginalImageLink(EImageLinkType.GUI, 14, 186, 0);
		case THIEF:
			return new OriginalImageLink(EImageLinkType.GUI, 14, 183, 0);

		default:
			throw new RuntimeException("No image found for movable type: " + movableType.name());
		}
	}
}