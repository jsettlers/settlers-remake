package jsettlers.graphics;

import go.graphics.RedrawListener;
import go.graphics.region.RegionContent;


public interface SettlersContent extends RegionContent {

	public void addRedrawListener(RedrawListener l);
	
	public void removeRedrawListener(RedrawListener l);
}
