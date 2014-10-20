package jsettlers.logic.map.random.instructions;

import java.util.Hashtable;

public class MetaInstruction extends GenerationInstruction {
	private static Hashtable<String, String> defaults = new Hashtable<String, String>();

	static {
		defaults.put("width", "100");
		defaults.put("height", "100");
		defaults.put("name", "unnamed");
	}
	
	@Override
    protected Hashtable<String, String> getDefaultValues() {
		return defaults;
    }
	
}
