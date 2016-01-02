package jsettlers.mapcreator.main;

import javax.swing.JOptionPane;

/**
 * Error Display
 * 
 * @author Andreas Butti
 *
 */
public class ErrorDisplay {

	/**
	 * Constructor
	 */
	public ErrorDisplay() {
	}

	/**
	 * Display an error message if an unexpected exception occures
	 * 
	 * @param e
	 *            Exception
	 * @param description
	 *            Description to display
	 */
	public static void displayError(Throwable e, String description) {
		System.err.println(description);
		e.printStackTrace();

		StringBuilder b = new StringBuilder();
		b.append(e.getClass().getName());
		b.append(": ");
		b.append(e.getMessage());

		JOptionPane.showMessageDialog(null, b.toString(), "Error occured", JOptionPane.ERROR_MESSAGE);
	}

}
