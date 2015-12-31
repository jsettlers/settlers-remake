package jsettlers.mapcreator.main.window.sidebar;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jsettlers.common.position.ILocatable;
import jsettlers.mapcreator.localization.EditorLabels;
import jsettlers.mapcreator.main.error.ErrorList;
import jsettlers.mapcreator.main.error.IScrollToAble;

/**
 * The sidebar with all tabs
 * 
 * @author Andreas Butti
 */
public class Sidebar extends JTabbedPane {
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
	 * Constructor
	 * 
	 * @param toolSidebar
	 *            Sidebar with the tools
	 */
	public Sidebar(ToolSidebar toolSidebar) {
		this.toolSidebar = toolSidebar;
		addTab(EditorLabels.getLabel("sidebar.tools"), toolSidebar);
	}

	/**
	 * Initialize the error tab
	 * 
	 * @param errorList
	 *            List model with the errors
	 * @param scrollTo
	 *            Interface to scroll to position
	 */
	public void initErrorTab(final ErrorList errorList, final IScrollToAble scrollTo) {
		final JList<ILocatable> list = new JList<>(errorList);
		list.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				int index = list.getSelectedIndex();
				if (index > 0) {
					scrollTo.scrollTo(errorList.getElementAt(index).getPos());
				}
			}
		});

		this.scrolErrorList = new JScrollPane(list);
		addTab(EditorLabels.getLabel("sidebar.errors"), scrolErrorList);
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
