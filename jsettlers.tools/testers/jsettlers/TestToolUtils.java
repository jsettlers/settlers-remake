package jsettlers;

import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.menu.FakeMapGame;
import jsettlers.common.menu.IMapInterfaceConnector;
import jsettlers.common.menu.IMapInterfaceListener;
import jsettlers.common.menu.IStartedGame;
import jsettlers.common.menu.action.EActionType;
import jsettlers.common.menu.action.IAction;
import jsettlers.graphics.action.PointAction;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.main.swing.SwingManagedJSettlers;
import jsettlers.main.swing.lookandfeel.JSettlersLookAndFeelExecption;

public class TestToolUtils extends TestUtils {
	public static IMapInterfaceConnector openTestWindow(final IGraphicsGrid map) throws JSettlersLookAndFeelExecption {
		IStartedGame game = new FakeMapGame(map);
		return openTestWindow(game);
	}

	public static IMapInterfaceConnector openTestWindow(IStartedGame startedGame) throws JSettlersLookAndFeelExecption {
		setupSwingResources();
		ImageProvider.getInstance().startPreloading();

		IMapInterfaceConnector mapInterfaceConnector = SwingManagedJSettlers.showJSettlers(startedGame);

		mapInterfaceConnector.addListener(
				new IMapInterfaceListener() {
					@Override
					public void action(IAction action) {
						if (action.getActionType() == EActionType.SELECT_POINT) {
							PointAction selectAction = (PointAction) action;
							System.out.println("Action preformed: " + action.getActionType() + " at: " + selectAction.getPosition());
						} else {
							System.out.println("Action preformed: " + action.getActionType());
						}
					}
				});

		return mapInterfaceConnector;
	}
}
