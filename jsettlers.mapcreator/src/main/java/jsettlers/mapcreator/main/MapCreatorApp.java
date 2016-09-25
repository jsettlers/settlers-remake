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
package jsettlers.mapcreator.main;

import javax.swing.JFrame;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import jsettlers.common.map.MapLoadException;
import jsettlers.common.utils.MainUtils;
import jsettlers.common.utils.OptionableProperties;
import jsettlers.exceptionhandler.ExceptionHandler;
import jsettlers.main.swing.SwingManagedJSettlers;
import jsettlers.mapcreator.control.EditorControl;
import jsettlers.mapcreator.main.window.EditorFrame;
import jsettlers.mapcreator.main.window.NewFilePanel;
import jsettlers.mapcreator.main.window.NewOrOpenDialog;
import jsettlers.mapcreator.main.window.OpenPanel;

/**
 * Entry point for Map Editor application
 */
public class MapCreatorApp {

	/**
	 * Sets the look and feel to "Nimbus", looks the same for all platforms, and should be available on all plattforms
	 */
	private static void loadLookAndFeel() {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}

			ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
		} catch (Exception e) {
			// could not be loaded, ignore error
		}
	}

	/**
	 * Display the New or open selection dialog
	 */
	private static void startWithSelectionDialog() {
		// dummy frame for icon, TODO to test on windows, ubuntu does not display the icon...
		JFrame dummyFrame = new JFrame();
		dummyFrame.setIconImage(EditorFrame.APP_ICON);
		dummyFrame.setLocationRelativeTo(null);

		NewOrOpenDialog dlg = new NewOrOpenDialog(dummyFrame);
		dlg.setVisible(true);
		dummyFrame.dispose();

		if (!dlg.isConfirmed()) {
			return;
		}

		if (dlg.isLastUsed()) {
			OpenPanel openFile = dlg.getLastUsed();
			try {
				EditorControl control = new EditorControl();
				control.loadMap(openFile.getSelectedMap());
			} catch (MapLoadException e) {
				ExceptionHandler.displayError(e, "Could not open map!");
			}
		} else if (dlg.isOpenAction()) {
			OpenPanel openFile = dlg.getOpenPanel();
			try {
				EditorControl control = new EditorControl();
				control.loadMap(openFile.getSelectedMap());
			} catch (MapLoadException e) {
				ExceptionHandler.displayError(e, "Could not open map!");
			}
		} else {
			NewFilePanel newFile = dlg.getNewFilePanel();
			EditorControl control = new EditorControl();
			control.createNewMap(newFile.getHeader(), newFile.getGroundTypes());
		}
	}

	/**
	 * Main
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ExceptionHandler.setupDefaultExceptionHandler();

			OptionableProperties options = MainUtils.loadOptions(args);
			SwingManagedJSettlers.setupResourceManagers(options);
			SwingManagedJSettlers.loadOptionalSettings(options);
			loadLookAndFeel();

			startWithSelectionDialog();
		} catch (Exception e) {
			ExceptionHandler.displayError(e, "Error launching application");
		}
	}

}
