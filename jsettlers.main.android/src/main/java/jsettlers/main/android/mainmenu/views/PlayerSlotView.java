/*
 * Copyright (c) 2017
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package jsettlers.main.android.mainmenu.views;

import jsettlers.main.android.mainmenu.presenters.setup.playeritem.Civilisation;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.PlayerType;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.StartPosition;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.Team;

/**
 * Created by tompr on 18/02/2017.
 */
public interface PlayerSlotView {
	void setName(String name);

	void setReady(boolean ready);

	void setPossibleCivilisations(Civilisation[] possibleCivilisations);

	void setCivilisation(Civilisation civilisation);

	void setPossiblePlayerTypes(PlayerType[] ePlayerTypes);

	void setPlayerType(PlayerType playerType);

	void setPossibleStartPositions(StartPosition[] possibleSlots);

	void setStartPosition(StartPosition slot);

	void setPossibleTeams(Team[] possibleTeams);

	void setTeam(Team team);

	void showReadyControl();

	void hideReadyControl();

	void setControlsEnabled();

	void setControlsDisabled();
}
