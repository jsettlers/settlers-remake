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
	protected boolean confirmed = false;

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
		initButtons();
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
	private void initButtons() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JButton btOk = new JButton(EditorLabels.getLabel("general.OK"));
		btOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (beforeOkAction()) {
					confirmed = true;
					dispose();
				}
			}
		});

		JButton btCancel = new JButton(EditorLabels.getLabel("general.Cancel"));
		btCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!beforeCancelAction()) {
					dispose();
				}
			}
		});

		buttonPanel.add(btCancel);
		buttonPanel.add(btOk);

		int width = Math.max(btOk.getPreferredSize().width, btCancel.getPreferredSize().width);
		Dimension size = new Dimension(width, btOk.getPreferredSize().height);
		btOk.setPreferredSize(size);
		btCancel.setPreferredSize(size);

		add(buttonPanel, BorderLayout.SOUTH);
	}

	/**
	 * Action performed before cancel
	 * 
	 * @return true to close the dialog
	 */
	protected boolean beforeCancelAction() {
		return true;
	}

	/**
	 * Action performed before OK
	 * 
	 * @return true to close the dialog
	 */
	protected boolean beforeOkAction() {
		return true;
	}
}
