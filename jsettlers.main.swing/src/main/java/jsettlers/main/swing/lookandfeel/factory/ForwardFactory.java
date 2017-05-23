/*******************************************************************************
 * Copyright (c) 2016 - 2017
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
package jsettlers.main.swing.lookandfeel.factory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;

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
	 * Load the current factory type from UIManager
	 * 
	 * @param key
	 *            Key of factory
	 */
	public void loadFromType(String key) {
		Object factoryClassName = UIManager.get(key);
		try {
			factoryClass = Class.forName(String.valueOf(factoryClassName));
			method = factoryClass.getMethod("createUI", JComponent.class);
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
