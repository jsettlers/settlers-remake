package jsettlers;

import go.graphics.swing.sound.SwingSoundPlayer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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
import jsettlers.graphics.swing.resources.SwingResourceLoader;
import jsettlers.main.swing.SwingManagedJSettlers;

/**
 * Utility class holding methods needed by serveral test classes.
 * 
 * @author Andreas Eberle
 * 
 */
public final class TestUtils {
	private TestUtils() {
	}

	public static <T> T serializeAndDeserialize(T object) throws IOException,
			ClassNotFoundException {
		ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(byteOutStream);

		oos.writeObject(object);
		oos.close();

		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(byteOutStream.toByteArray()));

		@SuppressWarnings("unchecked")
		T readList = (T) ois.readObject();
		ois.close();

		return readList;
	}

	private static boolean resourceManagerSetUp = false;

	public static synchronized void setupResourceManagerIfNeeded() {
		if (!resourceManagerSetUp) {
			try {
				File configFile = new File("../jsettlers.main.swing/config.prp");
				System.out.println("configFile: " + configFile.getAbsolutePath());
				SwingResourceLoader.setupResourceManagersByConfigFile(configFile);
			} catch (IOException e) {
				throw new RuntimeException("Config file not found!", e);
			}
			resourceManagerSetUp = true;
		}
	}

	public static MapInterfaceConnector openTestWindow(final IGraphicsGrid map) {
		IStartedGame game = new FakeMapGame(map);
		return openTestWindow(game);
	}

	public static MapInterfaceConnector openTestWindow(IStartedGame game) {
		setupResourceManagerIfNeeded();

		ImageProvider.getInstance().startPreloading();
		JSettlersScreen content = SwingManagedJSettlers.startGui();
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
