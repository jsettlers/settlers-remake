package jsettlers.graphics.image;

public enum EImageType {
	PRIMARY(false),
	PRIMARY_WITH_TORSO(false),
	TORSO(true);
	
	private final boolean useColor;

	private EImageType(boolean useColor) {
		this.useColor = useColor;
	}

	public boolean useColor() {
		return useColor;
	}
}
