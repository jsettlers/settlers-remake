package jsettlers.logic.player;

import jsettlers.common.ai.WhatToDoAiType;

/**
 * @author codingberlin
 */
public class PlayerSetting {

	final boolean isAi;

	final WhatToDoAiType aiType;

	boolean isAvailable;

	public PlayerSetting(boolean isAvailable, boolean isAi, WhatToDoAiType aiType) {
		if (isAi && aiType == null) {
			throw new IllegalArgumentException("isAi = true specified but aiType is null.");
		}
		this.isAvailable = isAvailable;
		this.isAi = isAi;
		this.aiType = aiType;
	}

	public boolean isAvailable() {
		return isAvailable;
	}

	public boolean isAi() {
		return isAi;
	}

	public WhatToDoAiType getAiType() {
		return aiType;
	}

	@Override
	public String toString() {
		return "PlayerSetting(isAvailable: " + isAvailable + ", isAi: " + isAi + ", aiType)" + aiType;
	}
}
