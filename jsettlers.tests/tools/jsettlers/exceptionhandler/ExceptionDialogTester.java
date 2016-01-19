package jsettlers.exceptionhandler;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class ExceptionDialogTester {
	/**
	 * Main to test the dialog
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			// could not be loaded, ignore error
		}

		try {
			endlessRecursion();
		} catch (Throwable e) {
			ExceptionHandler.displayError(e, "test error");
		}
	}

	/**
	 * To generate an exception with a big stacktrace...
	 */
	private static void endlessRecursion() {
		endlessRecursion();
	}
}
