package jsettlers.common.map;

public interface IVisibilityStateProvider {
	byte[][] getVisibleStatusArray();

	interface IVSPProvider {
		IVisibilityStateProvider getVSP();
	}

}