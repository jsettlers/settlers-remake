package jsettlers.graphics.test;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.IStack;

public class TestStack implements IStack {

	private final EMaterialType material;
	private final int count;

	public TestStack(EMaterialType material, int count) {
		this.material = material;
		this.count = count;
    }

	@Override
    public EMaterialType getMaterial() {
		return this.material;
    }

	@Override
    public IStack getNextStack() {
	    return null;
    }

	@Override
    public byte getNumberOfElements() {
	    return (byte) this.count;
    }
	
}
