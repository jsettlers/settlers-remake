package jsettlers;

import go.graphics.swing.sound.SwingSoundPlayer;

import java.util.Collections;

import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.resources.ResourceManager;
import jsettlers.common.statistics.IStatisticable;
import jsettlers.graphics.JSettlersScreen;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.action.PointAction;
import jsettlers.graphics.map.IMapInterfaceListener;
import jsettlers.graphics.map.MapContent;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.startscreen.interfaces.IStartedGame;
import jsettlers.graphics.swing.SwingResourceLoader;
import jsettlers.graphics.swing.SwingResourceProvider;
import jsettlers.main.swing.SwingManagedJSettlers;

public class TestWindow {
	static { // sets the native library path for the system dependent jogl libs
		SwingResourceLoader.setupSwingPaths();
		ResourceManager.setProvider(new SwingResourceProvider());
	}

	private TestWindow() {
	}

	public static MapInterfaceConnector openTestWindow(final IGraphicsGrid map) {
		IStartedGame game = new IStartedGame() {
			@Override
			public IStatisticable getPlayerStatistics() {
				return new IStatisticable() {
					@Override
					public int getNumberOf(EMovableType movableType) {
						return 0;
					}

					@Override
					public int getNumberOf(EMaterialType materialType) {
						return 0;
					}

					@Override
					public int getJoblessBearers() {
						return 0;
					}

					@Override
					public int getGameTime() {
						return 0;
					}
				};
			}

			@Override
			public int getPlayer() {
				return 0;
			}

			@Override
			public IGraphicsGrid getMap() {
				return map;
			}
		};
		return openTestWindow(game);
	}

	public static MapInterfaceConnector openTestWindow(IStartedGame game) {
		ImageProvider.getInstance().startPreloading();
		JSettlersScreen content = SwingManagedJSettlers.startGui(Collections
				.<String> emptyList());
		MapContent mapContent = new MapContent(game, new SwingSoundPlayer());
		content.setContent(mapContent);
		// TODO: Add a better redraw method.

		mapContent.getInterfaceConnector().addListener(
				new IMapInterfaceListener() {

					@Override
					public void action(Action action) {
						if (action.getActionType() == EActionType.SELECT_POINT) {
							PointAction selectAction = (PointAction) action;

							System.out.println("Action preformed: "
									+ action.getActionType() + " at: "
									+ selectAction.getPosition());
						} else {
							System.out.println("Action preformed: "
									+ action.getActionType());
						}
					}
				});

		return mapContent.getInterfaceConnector();
	}
}
