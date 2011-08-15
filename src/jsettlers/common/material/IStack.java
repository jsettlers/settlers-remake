package jsettlers.common.material;

public interface IStack {
	public EMaterialType getMaterial();

	public byte getNumberOfElements();

	public IStack getNextStack();
}
