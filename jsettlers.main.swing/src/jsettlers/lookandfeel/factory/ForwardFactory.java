package jsettlers.lookandfeel.factory;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Forward the unhandled calls to the default look and feel factory
 * 
 * @author Andreas Butti
 *
 */
public class ForwardFactory {

	/**
	 * Factory class
	 */
	private Class<?> factoryClass = null;

	/**
	 * Factory method to call
	 */
	private Method method;

	/**
	 * Constructor
	 */
	public ForwardFactory() {
	}

	/**
	 * Load the current factory type from UIManager
	 * 
	 * @param key
	 *            Key of factory
	 */
	public void loadFromType(String key) {
		Object factoryClassName = UIManager.get(key);
		try {
			factoryClass = Class.forName(String.valueOf(factoryClassName));
			method = factoryClass.getMethod("createUI", new Class[] { JComponent.class });
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
			throw new IllegalArgumentException(key, e);
		}
	}

	/**
	 * @param c
	 *            Component to create the UI for
	 * @return New instance of UI
	 */
	public ComponentUI create(JComponent c) {
		try {
			return (ComponentUI) method.invoke(null, c);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			System.err.println("Couldn't create ui for component " + c);
			e.printStackTrace();
			return null;
		}
	}

}
