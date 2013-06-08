package jsettlers.graphics.startscreen;

import go.graphics.region.RegionContent;
import jsettlers.graphics.utils.UIPanel;

public interface IContentSetable {
	void setContent(UIPanel panel);
	void setContent(RegionContent panel);
}
