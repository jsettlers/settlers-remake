/*******************************************************************************
 * Copyright (c) 2019
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
package go.graphics.swing.opengl;

import static org.lwjgl.opengl.GL20C.*;
import static org.lwjgl.opengl.ARBDebugOutput.*;
import static org.lwjgl.opengl.KHRDebug.*;
import org.lwjgl.opengl.GLDebugMessageARBCallback;
import org.lwjgl.opengl.GLDebugMessageARBCallbackI;
import org.lwjgl.opengl.GLDebugMessageCallbackI;

import java.util.HashMap;

public class LWJGLDebugOutput {

	LWJGLDebugOutput(LWJGLDrawContext dc) {
		glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS);
		if(dc.glcaps.GL_KHR_debug) {
			glDebugMessageCallback(debugCallback, 0);
		} else if(dc.glcaps.GL_ARB_debug_output) {
			glDebugMessageCallbackARB(debugCallbackARB, 0);
		}
	}


	private static int lastId = -1;
	private static int lastType = -1;
	private static int lastSource = -1;
	private static int lastSeverity = -1;
	private static long lastMessageCount = -1;
	private static String lastHeader = null;
	private static final int MAX_PRINT_MESSAGES = 10;
	private static final int MAX_REPEAT_MESSEAGES = 1000000; //10^6

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
		if(lastId == id && lastType == type && lastSource == source && lastSeverity == severity && lastMessageCount != Long.MAX_VALUE && lastHeader!=null) {
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
		debugEnum.put(GL_DEBUG_TYPE_ERROR, "ERROR");
		debugEnum.put(GL_DEBUG_TYPE_OTHER, "OTHER");
		debugEnum.put(GL_DEBUG_TYPE_PERFORMANCE, "PERF ");
		debugEnum.put(GL_DEBUG_TYPE_PORTABILITY, "PORT ");
		debugEnum.put(GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR, "NDEF ");
		debugEnum.put(GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR, "DEPRE");

		debugEnum.put(GL_DEBUG_SOURCE_API, "API");
		debugEnum.put(GL_DEBUG_SOURCE_SHADER_COMPILER, "SHADER_COMPILER");
		debugEnum.put(GL_DEBUG_SOURCE_WINDOW_SYSTEM, "WINDOW_SYSTEM");
		debugEnum.put(GL_DEBUG_SEVERITY_HIGH, "HIGH");
		debugEnum.put(GL_DEBUG_SEVERITY_MEDIUM, "MEDIUM");
		debugEnum.put(GL_DEBUG_SEVERITY_LOW, "LOW");
		debugEnum.put(GL_DEBUG_SEVERITY_NOTIFICATION, "NOTIFICATION");
	}

	private static String S(int type) {
		return debugEnum.getOrDefault(type, String.valueOf(type));
	}


	private static final GLDebugMessageCallbackI debugCallback = (int source, int type, int id, int severity, int length, long message, long userParam) -> debugMessage(source, type, id, severity, length, message);
	private static final GLDebugMessageARBCallbackI debugCallbackARB = (int source, int type, int id, int severity, int length, long message, long userParam) -> debugMessage(source, type, id, severity, length, message);
}
