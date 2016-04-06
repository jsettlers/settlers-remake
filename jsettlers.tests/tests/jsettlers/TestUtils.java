//<<<<<<< HEAD:jsettlers.tests/tools/jsettlers/TestUtils.java
///*******************************************************************************
// * Copyright (c) 2015
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
// * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
// * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
// * DEALINGS IN THE SOFTWARE.
// *******************************************************************************/
//package jsettlers;
//
//import go.graphics.swing.sound.SwingSoundPlayer;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//
//import jsettlers.common.map.IGraphicsGrid;
//import jsettlers.graphics.JSettlersScreen;
//import jsettlers.graphics.action.Action;
//import jsettlers.graphics.action.EActionType;
//import jsettlers.graphics.action.PointAction;
//import jsettlers.graphics.map.IMapInterfaceListener;
//import jsettlers.graphics.map.MapContent;
//import jsettlers.graphics.map.MapInterfaceConnector;
//import jsettlers.graphics.map.draw.ImageProvider;
//import jsettlers.graphics.startscreen.interfaces.FakeMapGame;
//import jsettlers.graphics.startscreen.interfaces.IStartedGame;
//import jsettlers.graphics.swing.resources.ConfigurationPropertiesFile;
//import jsettlers.graphics.swing.resources.SwingResourceLoader;
//import jsettlers.main.swing.SwingManagedJSettlers;
//
///**
// * Utility class holding methods needed by serveral test classes.
// * 
// * @author Andreas Eberle
// * 
// */
//public final class TestUtils {
//	private TestUtils() {
//	}
//
//	public static <T> T serializeAndDeserialize(T object) throws IOException,
//			ClassNotFoundException {
//		ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
//		ObjectOutputStream oos = new ObjectOutputStream(byteOutStream);
//
//		oos.writeObject(object);
//		oos.close();
//
//		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(byteOutStream.toByteArray()));
//
//		@SuppressWarnings("unchecked")
//		T readList = (T) ois.readObject();
//		ois.close();
//
//		return readList;
//	}
//
//	public static synchronized void setupSwingResources() {
//		try {
//			setupResourcesManager();
//			SwingResourceLoader.setupGraphicsAndSoundResources(getDefaultConfigFile());
//		} catch (IOException e) {
//			throw new RuntimeException("Config file not found!", e);
//		}
//	}
//
//	public static synchronized void setupResourcesManager() {
//		try {
//			SwingResourceLoader.setupResourcesManager(getDefaultConfigFile());
//		} catch (IOException e) {
//			throw new RuntimeException("Config file not found!", e);
//		}
//	}
//
//	private static ConfigurationPropertiesFile getDefaultConfigFile() throws IOException {
//		File directory = new File("../jsettlers.main.swing");
//		ConfigurationPropertiesFile configFile = SwingManagedJSettlers.createDefaultConfigFile(directory);
//		if (!configFile.isLoadedFromFile()) {
//			throw new IOException("Default config file not found at " + directory);
//		}
//		return configFile;
//	}
//
//	public static MapInterfaceConnector openTestWindow(final IGraphicsGrid map) {
//		IStartedGame game = new FakeMapGame(map);
//		return openTestWindow(game);
//	}
//
//	public static MapInterfaceConnector openTestWindow(IStartedGame game) {
//		setupSwingResources();
//
//		ImageProvider.getInstance().startPreloading();
//		JSettlersScreen content = SwingManagedJSettlers.startGui();
//		MapContent mapContent = new MapContent(game, new SwingSoundPlayer());
//		content.setContent(mapContent);
//
//		mapContent.getInterfaceConnector().addListener(
//				new IMapInterfaceListener() {
//					@Override
//					public void action(Action action) {
//						if (action.getActionType() == EActionType.SELECT_POINT) {
//							PointAction selectAction = (PointAction) action;
//							System.out.println("Action preformed: " + action.getActionType() + " at: " + selectAction.getPosition());
//						} else {
//							System.out.println("Action preformed: " + action.getActionType());
//						}
//					}
//				});
//
//		return mapContent.getInterfaceConnector();
//	}
//
//}
//=======
/*******************************************************************************
 * Copyright (c) 2015
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
 *******************************************************************************/
package jsettlers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import jsettlers.graphics.swing.resources.ConfigurationPropertiesFile;
import jsettlers.graphics.swing.resources.SwingResourceLoader;
import jsettlers.main.swing.SwingManagedJSettlers;

/**
 * Utility class holding methods needed by serveral test classes.
 * 
 * @author Andreas Eberle
 * 
 */
public class TestUtils {

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

	public static synchronized void setupSwingResources() {
		try {
			SwingResourceLoader.setupResourcesByConfigFile(getDefaultConfigFile());
		} catch (IOException e) {
			throw new RuntimeException("Config file not found!", e);
		}
	}

	private static ConfigurationPropertiesFile getDefaultConfigFile() throws IOException {
		File directory = new File("../jsettlers.main.swing");
		ConfigurationPropertiesFile configFile = SwingManagedJSettlers.createDefaultConfigFile(directory);
		if (!configFile.isLoadedFromFile()) {
			throw new IOException("Default config file not found at " + directory);
		}
		return configFile;
	}
}
// >>>>>>> master:jsettlers.tests/tests/jsettlers/TestUtils.java
