package go.graphics.swing.opengl;
;
import org.lwjgl.opengl.ARBDebugOutput;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLDebugMessageARBCallback;
import org.lwjgl.opengl.GLDebugMessageARBCallbackI;
import org.lwjgl.opengl.GLDebugMessageCallbackI;
import org.lwjgl.opengl.KHRDebug;

import java.util.ArrayList;
import java.util.HashMap;

public class LWJGLDebugOutput {

	private boolean khr;
	private boolean arb;

	LWJGLDebugOutput(LWJGLDrawContext dc) {
		if(dc.glcaps.GL_KHR_debug) {
			GL11.glEnable(KHRDebug.GL_DEBUG_OUTPUT_SYNCHRONOUS);
			KHRDebug.glDebugMessageCallback(debugCallback, 0);
		} else if(dc.glcaps.GL_ARB_debug_output) {
			GL11.glEnable(ARBDebugOutput.GL_DEBUG_OUTPUT_SYNCHRONOUS_ARB);
			ARBDebugOutput.glDebugMessageCallbackARB(debugCallbackARB, 0);
		}
		khr = dc.glcaps.GL_KHR_debug;
		arb = dc.glcaps.GL_ARB_debug_output;
	}


	private int lastId = -1;
	private int lastType = -1;
	private int lastSource = -1;
	private int lastSeverity = -1;
	private int lastMessageCount = -1;
	private String lastHeader = null;
	private static final int MAX_PRINT_MESSAGES = 10;
	private static final int MAX_REPEAT_MESSEAGES = Integer.MAX_VALUE;
	private ArrayList<String> lastMessages = new ArrayList<>(MAX_PRINT_MESSAGES);

	private void flushMessages() {
		if(lastMessageCount <= MAX_PRINT_MESSAGES) {
			for(int i = 0;i != lastMessageCount;i++) {
				System.err.print(lastHeader);
				System.err.println(lastMessages.get(lastMessageCount-1).split("\n")[0]);
			}
		} else {
			System.err.print("last message repeated " + (lastMessageCount-1) + " times\n\n");
			lastMessageCount = 1;
		}
	}

	private void debugMessage(int source, int type, int id, int severity, int length, long message) {
		String msg = GLDebugMessageARBCallback.getMessage(length, message);
		if(lastId == id && lastType == type && lastSource == source && lastSeverity == severity) {
			if(lastMessageCount < MAX_PRINT_MESSAGES) lastMessages.add(lastMessageCount, msg);
			lastMessageCount++;
		} else {
			if(lastId != -1) flushMessages();
			lastId = id;
			lastType = type;
			lastSource = source;
			lastSeverity = severity;
			lastHeader = "T:" + S(type) + " S:" + S(source) + " SE:" + S(severity) + " I:" + id + " MSG: ";
			lastMessages.add(0, msg);
			lastMessageCount = 1;
			flushMessages();
		}

		if(lastMessageCount == MAX_REPEAT_MESSEAGES) flushMessages();
	}

	private static HashMap<Integer, String> debugEnum = new HashMap<>();
	static {
		debugEnum.put(KHRDebug.GL_DEBUG_TYPE_ERROR, "ERROR");
		debugEnum.put(KHRDebug.GL_DEBUG_TYPE_OTHER, "OTHER");
		debugEnum.put(KHRDebug.GL_DEBUG_TYPE_PERFORMANCE, "PERF ");
		debugEnum.put(KHRDebug.GL_DEBUG_TYPE_PORTABILITY, "PORT ");
		debugEnum.put(KHRDebug.GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR, "NDEF ");
		debugEnum.put(KHRDebug.GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR, "DEPRE");

		debugEnum.put(KHRDebug.GL_DEBUG_SOURCE_API, "API");
		debugEnum.put(KHRDebug.GL_DEBUG_SOURCE_SHADER_COMPILER, "SHADER_COMPILER");
		debugEnum.put(KHRDebug.GL_DEBUG_SOURCE_WINDOW_SYSTEM, "WINDOW_SYSTEM");
		debugEnum.put(KHRDebug.GL_DEBUG_SEVERITY_HIGH, "HIGH");
		debugEnum.put(KHRDebug.GL_DEBUG_SEVERITY_MEDIUM, "MEDIUM");
		debugEnum.put(KHRDebug.GL_DEBUG_SEVERITY_LOW, "LOW");
		debugEnum.put(KHRDebug.GL_DEBUG_SEVERITY_NOTIFICATION, "NOTIFICATION");
	}

	private String S(int type) {
		return debugEnum.getOrDefault(type, String.valueOf(type));
	}


	private final GLDebugMessageCallbackI debugCallback = (int source, int type, int id, int severity, int length, long message, long userParam) -> debugMessage(source, type, id, severity, length, message);
	private final GLDebugMessageARBCallbackI debugCallbackARB = (int source, int type, int id, int severity, int length, long message, long userParam) -> debugMessage(source, type, id, severity, length, message);

	public void flush() {
		if(khr) KHRDebug.glDebugMessageInsert(				KHRDebug.GL_DEBUG_SOURCE_APPLICATION, KHRDebug.GL_DEBUG_TYPE_OTHER, 42, KHRDebug.GL_DEBUG_SEVERITY_NOTIFICATION, "printing all remaining messages... and bye");
		else if(arb) ARBDebugOutput.glDebugMessageInsertARB(KHRDebug.GL_DEBUG_SOURCE_APPLICATION, KHRDebug.GL_DEBUG_TYPE_OTHER, 42, KHRDebug.GL_DEBUG_SEVERITY_NOTIFICATION, "printing all remaining messages... and bye");
	}
}
