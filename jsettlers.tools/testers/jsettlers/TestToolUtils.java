package jsettlers;

import go.graphics.swing.sound.SwingSoundPlayer;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.graphics.JSettlersScreen;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.action.PointAction;
import jsettlers.graphics.map.IMapInterfaceListener;
import jsettlers.graphics.map.MapContent;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.startscreen.interfaces.FakeMapGame;
import jsettlers.graphics.startscreen.interfaces.IStartedGame;
import jsettlers.main.swing.OldSwingManagedJSettlers;

public class TestToolUtils extends TestUtils {
	public static MapInterfaceConnector openTestWindow(final IGraphicsGrid map) {
		IStartedGame game = new FakeMapGame(map);
		return openTestWindow(game);
	}

	public static MapInterfaceConnector openTestWindow(IStartedGame game) {
		setupSwingResources();

		ImageProvider.getInstance().startPreloading();
		JSettlersScreen content = OldSwingManagedJSettlers.startGui();
		MapContent mapContent = new MapContent(game, new SwingSoundPlayer());
		content.setContent(mapContent);

		mapContent.getInterfaceConnector().addListener(
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

		return mapContent.getInterfaceConnector();
	}
}
