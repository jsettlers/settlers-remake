package jsettlers;

import jsettlers.common.map.IGraphicsGrid;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.action.PointAction;
import jsettlers.graphics.map.IMapInterfaceConnector;
import jsettlers.graphics.map.IMapInterfaceListener;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.startscreen.interfaces.FakeMapGame;
import jsettlers.graphics.startscreen.interfaces.IStartedGame;
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
					public void action(Action action) {
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
