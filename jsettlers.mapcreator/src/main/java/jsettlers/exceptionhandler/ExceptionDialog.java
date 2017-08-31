/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
package jsettlers.exceptionhandler;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

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
	private static final String ERROR_URL = "https://github.com/jsettlers/settlers-remake/issues/new";

	/**
	 * Error to copy to github
	 */
	private String errorString;

	/**
	 * Constructor
	 * 
	 * @param throwable
	 *            Exception
	 * @param description
	 *            Description
	 * @param t
	 *            Thread
	 */
	public ExceptionDialog(Throwable throwable, String description, Thread t) {
		setTitle(ExceptionLabels.getLabel("dialog.header"));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setLayout(new BorderLayout());

		((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JPanel pHeader = new JPanel();
		pHeader.setLayout(new BorderLayout());
		pHeader.add(new JLabel(new ErrorIcon()), BorderLayout.WEST);
		pHeader.add(new JLabel(ExceptionLabels.getLabel("dialog.errordesc")), BorderLayout.CENTER);
		add(pHeader, BorderLayout.NORTH);

		JPanel pContents = prepareContents(throwable, description, t);
		add(pContents, BorderLayout.CENTER);

		JPanel pFooter = new JPanel();
		pFooter.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JButton btCopyToClipboard = new JButton(ExceptionLabels.getLabel("dialog.copy"));
		btCopyToClipboard.addActionListener(e -> {
			StringSelection selection = new StringSelection(errorString);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(selection, selection);
		});

		JButton btOpenGithub = new JButton(ExceptionLabels.getLabel("dialog.open-github"));
		btOpenGithub.addActionListener(e -> {
			try {
				Desktop.getDesktop().browse(new URI(ERROR_URL));
			} catch (IOException | URISyntaxException e1) {
				JOptionPane.showMessageDialog(ExceptionDialog.this, String.format(Locale.ENGLISH, ExceptionLabels.getLabel("dialog.open-failed"), ERROR_URL));
			}
		});
		JButton btCloseDialog = new JButton(ExceptionLabels.getLabel("action.close"));
		btCloseDialog.addActionListener(e -> {
			ExceptionHandler.resetErrorCounter();
			dispose();
		});
		JButton btExit = new JButton(ExceptionLabels.getLabel("action.exit"));
		btExit.addActionListener(e -> System.exit(5));

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
}
