package jsettlers.exceptionhandler;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import jsettlers.exceptionhandler.localization.ExceptionLabels;

/**
 * Dialog to display exception
 * 
 * @author Andreas Butti
 *
 */
public class ExceptionDialog extends JFrame {
	private static final long serialVersionUID = 1L;

	/**
	 * Url to report bug
	 */
	private final String ERROR_URL = "https://github.com/jsettlers/settlers-remake/issues/new";

	/**
	 * Error to copy to github
	 */
	private String errorString;

	/**
	 * Constructor
	 * 
	 * @param e
	 *            Exception
	 * @param description
	 *            Description
	 * @param t
	 *            Thread
	 */
	public ExceptionDialog(Throwable e, String description, Thread t) {
		setTitle(ExceptionLabels.getLabel("dialog.header"));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setLayout(new BorderLayout());

		((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JPanel pHeader = new JPanel();
		pHeader.setLayout(new BorderLayout());
		pHeader.add(new JLabel(new ErrorIcon()), BorderLayout.WEST);
		pHeader.add(new JLabel(ExceptionLabels.getLabel("dialog.errordesc")), BorderLayout.CENTER);
		add(pHeader, BorderLayout.NORTH);

		JPanel pContents = prepareContents(e, description, t);
		add(pContents, BorderLayout.CENTER);

		JPanel pFooter = new JPanel();
		pFooter.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JButton btCopyToClipboard = new JButton(ExceptionLabels.getLabel("dialog.copy"));
		btCopyToClipboard.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				StringSelection selection = new StringSelection(errorString);
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(selection, selection);
			}
		});

		JButton btOpenGithub = new JButton(ExceptionLabels.getLabel("dialog.open-github"));
		btOpenGithub.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(new URI(ERROR_URL));
				} catch (IOException | URISyntaxException e1) {
					JOptionPane.showMessageDialog(ExceptionDialog.this, String.format(ExceptionLabels.getLabel("dialog.open-failed"), ERROR_URL));
				}
			}
		});
		JButton btCloseDialog = new JButton(ExceptionLabels.getLabel("action.close"));
		btCloseDialog.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ExceptionHandler.resetErrorCounter();
				dispose();
			}
		});
		JButton btExit = new JButton(ExceptionLabels.getLabel("action.exit"));
		btExit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(5);
			}
		});

		pFooter.add(btCopyToClipboard);
		pFooter.add(btOpenGithub);
		pFooter.add(btCloseDialog);
		pFooter.add(btExit);

		add(pFooter, BorderLayout.SOUTH);

		pack();
		setLocationRelativeTo(null);
	}

	/**
	 * Prepare the contents
	 * 
	 * @param e
	 *            Exception
	 * @param description
	 *            Description
	 * @param t
	 *            Thread
	 * @return Panel with all iformation
	 */
	private JPanel prepareContents(Throwable e, String description, Thread t) {
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		JTextArea txt = new JTextArea(10, 40);

		StringBuilder b = new StringBuilder();

		b.append("```\n");
		b.append(description);
		b.append("\n---\n== Thread ==");
		b.append("\nname: ");
		b.append(t.getName());
		b.append("\nID: ");
		b.append(t.getId());
		b.append("\n---\n== Exception ==");
		b.append("\nclass: ");
		b.append(e.getClass().getName());
		b.append("\nmessage: ");
		b.append(e.getMessage());
		b.append("\nmessage: ");
		b.append(e.getMessage());
		b.append("\nstacktrace:\n");

		StringWriter errors = new StringWriter();
		e.printStackTrace(new PrintWriter(errors));
		b.append(errors.toString());

		b.append("```\n");

		errorString = b.toString();
		txt.setText(b.toString());
		txt.setCaretPosition(0);

		p.add(new JScrollPane(txt), BorderLayout.CENTER);
		return p;
	}

	/**
	 * To generate an exception with a big stacktrace...
	 */
	private static void endlessRecursion() {
		endlessRecursion();
	}

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
			ExceptionHandler.displayError(e, "test erro");
		}
	}

}
