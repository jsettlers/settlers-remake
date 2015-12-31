package jsettlers.mapcreator.main.window;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import jsettlers.mapcreator.localization.EditorLabels;

/**
 * Base class for all OK / Cancel dialogs
 * 
 * @author Andreas Butti
 *
 */
public abstract class AbstractOkCancelDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	/**
	 * If the user pressed OK
	 */
	private boolean confirmed = false;

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            Parent to center on
	 */
	public AbstractOkCancelDialog(JFrame parent) {
		super(parent);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		initButton();
	}

	/**
	 * @return If the user pressed OK
	 */
	public boolean isConfirmed() {
		return confirmed;
	}

	/**
	 * Initialize buttons
	 */
	private void initButton() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JButton btOk = new JButton(EditorLabels.getLabel("OK"));
		btOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!doOkAction()) {
					return;
				}
				confirmed = true;
				dispose();
			}
		});

		JButton btCancel = new JButton(EditorLabels.getLabel("Cancel"));
		btCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!doCancelAction()) {
					return;
				}
				dispose();
			}
		});

		buttonPanel.add(btCancel);
		buttonPanel.add(btOk);

		Dimension size = btOk.getPreferredSize();
		if (btCancel.getPreferredSize().width > size.width) {
			size.width = btCancel.getPreferredSize().width;
		}
		btOk.setPreferredSize(size);
		btCancel.setPreferredSize(size);

		add(buttonPanel, BorderLayout.SOUTH);
	}

	/**
	 * Action performed before cancel
	 * 
	 * @return true to close the dialog
	 */
	protected boolean doCancelAction() {
		return true;
	}

	/**
	 * Action performed before OK
	 * 
	 * @return true to close the dialog
	 */
	protected boolean doOkAction() {
		return true;
	}
}
