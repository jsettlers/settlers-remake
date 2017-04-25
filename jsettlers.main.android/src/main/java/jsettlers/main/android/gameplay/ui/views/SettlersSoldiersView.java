package jsettlers.main.android.gameplay.ui.views;

import jsettlers.common.images.ImageLink;

/**
 * Created by tompr on 10/03/2017.
 */

public interface SettlersSoldiersView {
	void setStrengthText(String strengthText);

	void setPromotionText(String promotionText);

	void setSwordsmenPromotionEnabled(boolean enabled);

	void setBowmenPromotionEnabled(boolean enabled);

	void setPikemenPromotionEnabled(boolean enabled);

	void setSwordsmenImage(ImageLink imageLink);

	void setBowmenImage(ImageLink imageLink);

	void setPikemenImage(ImageLink imageLink);
}
