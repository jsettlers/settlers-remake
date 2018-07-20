package go.graphics.swing.opengl;

import org.lwjgl.opengl.ARBDebugOutput;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLDebugMessageARBCallback;
import org.lwjgl.opengl.GLDebugMessageARBCallbackI;
import org.lwjgl.opengl.GLDebugMessageCallbackI;
import org.lwjgl.opengl.KHRDebug;

import java.util.HashMap;

public class LWJGLDebugOutput {

	LWJGLDebugOutput(LWJGLDrawContext dc) {
		if(dc.glcaps.GL_KHR_debug) {
			GL11.glEnable(KHRDebug.GL_DEBUG_OUTPUT_SYNCHRONOUS);
			KHRDebug.glDebugMessageCallback(debugCallback, 0);
		} else if(dc.glcaps.GL_ARB_debug_output) {
			GL11.glEnable(ARBDebugOutput.GL_DEBUG_OUTPUT_SYNCHRONOUS_ARB);
			ARBDebugOutput.glDebugMessageCallbackARB(debugCallbackARB, 0);
		}
	}


	private static int lastId = -1;
	private static int lastType = -1;
	private static int lastSource = -1;
	private static int lastSeverity = -1;
	private static long lastMessageCount = -1;
	private static String lastHeader = null;
	private static final int MAX_PRINT_MESSAGES = 10;
	private static final int MAX_REPEAT_MESSEAGES = (int)10e5;

	private static void flushMessages() {
		if(lastMessageCount >= MAX_PRINT_MESSAGES) { // not all messages have already been printed
			System.err.print("last message repeated " + (lastMessageCount - 1) + " times\n\n");
		}
	}

	private static void writeMessage(String msg) {
		System.err.print(lastHeader);
		System.err.println(msg);
	}

	private static void debugMessage(int source, int type, int id, int severity, int length, long message) {
		String msg = GLDebugMessageARBCallback.getMessage(length, message);
		if(lastId == id && lastType == type && lastSource == source && lastSeverity == severity && lastMessageCount != Long.MAX_VALUE) {
			if(lastMessageCount < MAX_PRINT_MESSAGES) writeMessage(msg);
			lastMessageCount++;
		} else {
			if(lastId != -1) flushMessages();
			lastId = id;
			lastType = type;
			lastSource = source;
			lastSeverity = severity;
			lastHeader = "T:" + S(type) + " S:" + S(source) + " SE:" + S(severity) + " I:" + id + " MSG: ";
			lastMessageCount = 1;
			writeMessage(msg);
		}

		// print a report every MAX_REPEAT_MESSAGES
		if(lastMessageCount%MAX_REPEAT_MESSEAGES == 0) flushMessages();
	}

	private static final HashMap<Integer, String> debugEnum = new HashMap<>();
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

	private static String S(int type) {
		return debugEnum.getOrDefault(type, String.valueOf(type));
	}


	private static final GLDebugMessageCallbackI debugCallback = (int source, int type, int id, int severity, int length, long message, long userParam) -> debugMessage(source, type, id, severity, length, message);
	private static final GLDebugMessageARBCallbackI debugCallbackARB = (int source, int type, int id, int severity, int length, long message, long userParam) -> debugMessage(source, type, id, severity, length, message);
}
