package jsettlers.mapcreator.main.window.sidebar;

import javax.swing.JTabbedPane;

import jsettlers.mapcreator.localization.EditorLabels;
import jsettlers.mapcreator.mapvalidator.IScrollToAble;
import jsettlers.mapcreator.mapvalidator.result.fix.FixData;

/**
 * The sidebar with all tabs
 * 
 * @author Andreas Butti
 */
public class Sidebar extends JTabbedPane {
	private static final long serialVersionUID = 1L;

	/**
	 * Sidebar with the tools
	 */
	private final ToolSidebar toolSidebar;

	/**
	 * Sidebar with the erros
	 */
	private final ErrorSidebar errorSidebar;

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
		this.errorSidebar = new ErrorSidebar(scrollTo);

		addTab(EditorLabels.getLabel("sidebar.tools"), toolSidebar);
		addTab(EditorLabels.getLabel("sidebar.errors"), errorSidebar);
	}

	/**
	 * @param fixData
	 *            Fix data helper
	 */
	public void setFixData(FixData fixData) {
		errorSidebar.setFixData(fixData);
	}

	/**
	 * @return Sidebar with the erros
	 */
	public ErrorSidebar getErrorSidebar() {
		return errorSidebar;
	}

	/**
	 * Select the error tab
	 */
	public void selectError() {
		setSelectedComponent(errorSidebar);
	}

	/**
	 * Select the tools tab
	 */
	public void showTools() {
		setSelectedComponent(toolSidebar);
	}
}
