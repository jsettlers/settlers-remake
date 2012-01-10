package jsettlers.mapcreator.data;

import jsettlers.common.landscape.ELandscapeType;

/**
 * Placeholder object with harder constraints on the landscape.
 * @author michael
 *
 */
public interface LandscapeConstraint extends ObjectContainer {
	
	public ELandscapeType[] getAllowedLandscapes();
	
	public boolean allowHeightChange();
}
