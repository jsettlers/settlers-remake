package jsettlers.network.test;

import jsettlers.network.client.INetworkableObject;

public class NetworkableTestObject implements INetworkableObject {
	private final int t1;
	private final String inputted;

	/**
	 * private default constructor is needed for serialisation with flexjson
	 */
	@SuppressWarnings("unused")
	private NetworkableTestObject() {
		this.inputted = null;
		this.t1 = -1;
	}

	public NetworkableTestObject(String string) {
		this.inputted = string;
		this.t1 = 123;
	}

	public int getT1() {
		return t1;
	}

	public String getInputted() {
		return inputted;
	}

	@Override
	public String toString() {
		return "NetworkableTestObject: (" + t1 + ",  \"" + inputted + "\") ";
	}
}
