package jsettlers.mapcreator.tools;

/**
 * This is a tool that only holds some more tools.
 * 
 * @author michael
 */
public class ToolBox implements ToolNode {

	private final String name;
	private final ToolNode[] tools;

	public ToolBox(String name, ToolNode[] tools) {
		this.name = name;
		this.tools = tools;
	}

	@Override
	public String getName() {
		return name;
	}

	public ToolNode[] getTools() {
		return tools;
	}
}
