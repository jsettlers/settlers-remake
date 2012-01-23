package jsettlers.network.test;

import jsettlers.common.network.IMatchSettings;

public final class TestMatchSettings implements IMatchSettings {

	private final String matchName;

	TestMatchSettings(String matchName) {
		this.matchName = matchName;
	}

	@Override
	public String getMatchName() {
		return matchName;
	}

	@Override
	public int getMaxPlayers() {
		return 5;
	}

	@Override
	public long getRandomSeed() {
		return 0;
	}

}
