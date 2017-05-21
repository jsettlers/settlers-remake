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

package jsettlers;

import java.io.IOException;

import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.menu.FakeMapGame;
import jsettlers.common.menu.IMapInterfaceConnector;
import jsettlers.common.menu.IStartedGame;
import jsettlers.common.menu.action.EActionType;
import jsettlers.common.utils.OptionableProperties;
import jsettlers.graphics.action.PointAction;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.main.swing.SwingManagedJSettlers;
import jsettlers.main.swing.lookandfeel.JSettlersLookAndFeelExecption;
import jsettlers.main.swing.resources.SwingResourceLoader;
import jsettlers.testutils.TestUtils;

public class TestToolUtils extends TestUtils {
	public static IMapInterfaceConnector openTestWindow(final IGraphicsGrid map) throws JSettlersLookAndFeelExecption, IOException, SwingResourceLoader.ResourceSetupException {
		IStartedGame game = new FakeMapGame(map);
		return openTestWindow(game);
	}

	public static IMapInterfaceConnector openTestWindow(IStartedGame startedGame) throws JSettlersLookAndFeelExecption, IOException, SwingResourceLoader.ResourceSetupException {
		SwingManagedJSettlers.setupResourceManagers(new OptionableProperties());
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
