/*
 * Copyright (c) 2017 - 2018
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
package jsettlers;

import jsettlers.common.action.EActionType;
import jsettlers.common.action.PointAction;
import jsettlers.common.ai.EPlayerType;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.menu.FakeMapGame;
import jsettlers.common.menu.IMapInterfaceConnector;
import jsettlers.common.menu.IStartedGame;
import jsettlers.common.player.ECivilisation;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.logic.player.Player;
import jsettlers.logic.player.Team;
import jsettlers.main.swing.SwingManagedJSettlers;
import jsettlers.main.swing.lookandfeel.JSettlersLookAndFeelExecption;
import jsettlers.testutils.TestUtils;

import java.io.IOException;

public class TestToolUtils extends TestUtils {
	public static IMapInterfaceConnector openTestWindow(final IGraphicsGrid map) throws JSettlersLookAndFeelExecption, IOException {
		Player player = new Player((byte) 0, new Team((byte) 0), (byte) 42, EPlayerType.HUMAN, ECivilisation.ROMAN);
		IStartedGame game = new FakeMapGame(map, player);
		return openTestWindow(game);
	}

	public static IMapInterfaceConnector openTestWindow(IStartedGame startedGame) throws JSettlersLookAndFeelExecption, IOException {
		SwingManagedJSettlers.setupResources(false);
		ImageProvider.getInstance().startPreloading();

		IMapInterfaceConnector mapInterfaceConnector = SwingManagedJSettlers.showJSettlers(startedGame);

		mapInterfaceConnector.addListener(action -> {
			if (action.getActionType() == EActionType.SELECT_POINT) {
				PointAction selectAction = (PointAction) action;
				System.out.println("Action preformed: " + action.getActionType() + " at: " + selectAction.getPosition());
			} else {
				System.out.println("Action preformed: " + action.getActionType());
			}
		});

		return mapInterfaceConnector;
	}
}
