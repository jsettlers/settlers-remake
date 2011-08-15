package jsettlers.graphics;

import go.RedrawListener;
import go.region.RegionContent;

public interface SettlersContent extends RegionContent {

	public void addRedrawListener(RedrawListener l);
	
	public void removeRedrawListener(RedrawListener l);
}
