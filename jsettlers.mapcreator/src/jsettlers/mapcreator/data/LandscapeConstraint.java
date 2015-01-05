package jsettlers.mapcreator.data;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.mapcreator.data.objects.ObjectContainer;

/**
 * Placeholder object with harder constraints on the landscape.
 * 
 * @author michael
 *
 */
public interface LandscapeConstraint extends ObjectContainer {

	public ELandscapeType[] getAllowedLandscapes();

	public boolean needsFlatGround();
}
