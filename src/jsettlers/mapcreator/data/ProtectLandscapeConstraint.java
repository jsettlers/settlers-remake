package jsettlers.mapcreator.data;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.mapcreator.data.objects.ProtectContainer;

public class ProtectLandscapeConstraint extends ProtectContainer implements LandscapeConstraint {
	
	private final ELandscapeType[] allowed;

	public ProtectLandscapeConstraint(ELandscapeType[] allowed) {
		this.allowed = allowed;
	}
	
	public ELandscapeType[] getAllowedLandscapes() {
		return allowed;
	}

	@Override
    public boolean allowHeightChange() {
	    return false;
    }

}
