package jsettlers.mapcreator.main.window.sidebar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jsettlers.mapcreator.localization.EditorLabels;
import jsettlers.mapcreator.mapvalidator.IScrollToAble;
import jsettlers.mapcreator.mapvalidator.ValidationResultListener;
import jsettlers.mapcreator.mapvalidator.result.AbstarctErrorEntry;
import jsettlers.mapcreator.mapvalidator.result.ErrorEntry;
import jsettlers.mapcreator.mapvalidator.result.ErrorHeader;
import jsettlers.mapcreator.mapvalidator.result.ValidationList;

/**
 * The sidebar with all tabs
 * 
 * @author Andreas Butti
 */
public class Sidebar extends JTabbedPane implements ValidationResultListener {
	private static final long serialVersionUID = 1L;

	/**
	 * Interface to scroll to position
	 */
	private JScrollPane scrolErrorList = null;

	/**
	 * Sidebar with the tools
	 */
	private ToolSidebar toolSidebar;

	/**
	 * List with the error entries
	 */
	private JList<AbstarctErrorEntry> errorList;

	/**
	 * Interface to scroll to position
	 */
	private final IScrollToAble scrollTo;

	/**
	 * Constructor
	 * 
	 * @param toolSidebar
	 *            Sidebar with the tools
	 * @param scrollTo
	 *            Interface to scroll to position
	 */
	public Sidebar(ToolSidebar toolSidebar, IScrollToAble scrollTo) {
		this.toolSidebar = toolSidebar;
		this.scrollTo = scrollTo;
		addTab(EditorLabels.getLabel("sidebar.tools"), toolSidebar);

		initErrorList();
	}

	/**
	 * Initialize the error view
	 */
	private void initErrorList() {
		this.errorList = new JList<>();
		this.errorList.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			/**
			 * Default font
			 */
			private Font defaultFont = getFont();

			/**
			 * Header font
			 */
			private Font headerFont = getFont().deriveFont(Font.BOLD);

			/**
			 * Header background
			 */
			private final Color BACKGROUND_HEADER = new Color(0xE0E0E0);

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

				AbstarctErrorEntry entry = (AbstarctErrorEntry) value;
				setText(entry.getText());

				if (entry instanceof ErrorHeader) {
					setFont(headerFont);
					if (!isSelected) {
						setBackground(BACKGROUND_HEADER);
					}
				} else {
					setFont(defaultFont);
				}

				return this;
			}
		});
		errorList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				AbstarctErrorEntry value = errorList.getSelectedValue();
				if (value == null) {
					return;
				}
				if (!(value instanceof ErrorEntry)) {
					return;
				}

				ErrorEntry entry = (ErrorEntry) value;
				scrollTo.scrollTo(entry.getPos());
			}
		});

		this.scrolErrorList = new JScrollPane(errorList);
		addTab(EditorLabels.getLabel("sidebar.errors"), scrolErrorList);
	}

	@Override
	public void validationFinished(ValidationList list) {
		errorList.setModel(list);
	}

	/**
	 * Select the error tab
	 */
	public void selectError() {
		setSelectedComponent(scrolErrorList);
	}

	/**
	 * Select the tools tab
	 */
	public void showTools() {
		setSelectedComponent(toolSidebar);
	}
}
