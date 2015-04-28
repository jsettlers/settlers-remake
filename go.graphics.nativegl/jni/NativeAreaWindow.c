/*******************************************************************************
 * Copyright (c) 2015
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
/*
 * NativeAreaWindow.c
 *
 *  Created on: 08.10.2012
 *      Author: michael
 */

#include <stdio.h>
#include <stdlib.h>
#include <GL/glew.h>
#include <GL/glut.h>

#include "NativeAreaWindow.h"

JNIEnv *windowEnv = NULL;
jobject windowObject = NULL;

jmethodID getWindowMID(char* name, char* sig) {
	jclass clazz = (*windowEnv)->GetObjectClass(windowEnv, windowObject);
	jmethodID mid = (*windowEnv)->GetMethodID(windowEnv, clazz, name, sig);
	return mid;
}

GLvoid resizeGlWindow(GLsizei width, GLsizei height) {

	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();

	GLfloat m[16] = { 2.0f / width, 0, 0, 0, 0, 2.0f / height, 0, 0, 0, 0, -.5f,
			0, -1, -1, .5f, 1 };

	glMultMatrixf(m);

	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();

	glViewport(0, 0, width, height);

	glEnableClientState(GL_VERTEX_ARRAY);
	glEnableClientState(GL_TEXTURE_COORD_ARRAY);

	glAlphaFunc(GL_GREATER, 0.1f);
	glEnable(GL_ALPHA_TEST);
	glDepthFunc(GL_LEQUAL);
	glEnable(GL_DEPTH_TEST);

	glEnable(GL_TEXTURE_2D);

	jmethodID resizeMID = getWindowMID("resizeTo", "(II)V");
	(*windowEnv)->CallVoidMethod(windowEnv, windowObject, resizeMID,
			(jint) width, (jint) height);
}

GLvoid drawGlWindow() {
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

	jmethodID resizeMID = getWindowMID("drawContent", "()V");
	(*windowEnv)->CallVoidMethod(windowEnv, windowObject, resizeMID);

	glutSwapBuffers();
	glutPostRedisplay();
}

GLvoid mouseMotionCallback(int x, int y) {
	jmethodID mouseMID = getWindowMID("mousePositionChanged", "(II)V");
	(*windowEnv)->CallVoidMethod(windowEnv, windowObject, mouseMID, x, y);
}

GLvoid mouseCallback(int button, int state, int x, int y) {
	jmethodID mouseMID = getWindowMID("mouseStateChanged", "(IZII)V");
	int jbutton = 0;
	if (button == GLUT_MIDDLE_BUTTON) {
		jbutton = 1;
	} else if (button == GLUT_RIGHT_BUTTON) {
		jbutton = 2;
	}

	(*windowEnv)->CallVoidMethod(windowEnv, windowObject, mouseMID, jbutton,
			(jboolean) (state == GLUT_DOWN), x, y);
}

GLvoid mouseEntryCallback(int state) {
	jmethodID mouseMID = getWindowMID("mouseInsideWindow", "(Z)V");
	(*windowEnv)->CallVoidMethod(windowEnv, windowObject, mouseMID,
			state == GLUT_ENTERED);
}

void pressKey(char* key, jboolean up) {
	jstring jkey = (*windowEnv)->NewStringUTF(windowEnv, key);
	jmethodID mouseMID = getWindowMID("keyPressed", "(Ljava/lang/String;Z)V");
	(*windowEnv)->CallVoidMethod(windowEnv, windowObject, mouseMID, jkey, up);
}

GLvoid keyboardCallback(unsigned char key, int x, int y, jboolean up) {
	if (key == 127) {
		pressKey("DELETE", up);
	} else if (key == 27) {
		pressKey("BACK", up);

	} else if (key >= 32) {
		char keyString[] = { (char) key, 0 };
		pressKey(keyString, up);
	}
}

GLvoid keyboardCallbackDown(unsigned char key, int x, int y) {
	keyboardCallback(key, x, y, JNI_FALSE);
}

GLvoid keyboardCallbackUp(unsigned char key, int x, int y) {
	keyboardCallback(key, x, y, JNI_TRUE);
}

GLvoid keyboardSpecialCallback(int key, int x, int y, jboolean up) {
	char* keyString = NULL;

	switch (key) {
	case GLUT_KEY_F1:
		keyString = "F1";
		break;
	case GLUT_KEY_F2:
		keyString = "F2";
		break;
	case GLUT_KEY_F3:
		keyString = "F3";
		break;
	case GLUT_KEY_F4:
		keyString = "F4";
		break;
	case GLUT_KEY_F5:
		keyString = "F5";
		break;
	case GLUT_KEY_F6:
		keyString = "F6";
		break;
	case GLUT_KEY_F7:
		keyString = "F7";
		break;
	case GLUT_KEY_F8:
		keyString = "F8";
		break;
	case GLUT_KEY_F9:
		keyString = "F9";
		break;
	case GLUT_KEY_F10:
		keyString = "F10";
		break;
	case GLUT_KEY_F11:
		keyString = "F11";
		break;
	case GLUT_KEY_F12:
		keyString = "F12";
		break;
	case GLUT_KEY_LEFT:
		keyString = "LEFT";
		break;
	case GLUT_KEY_RIGHT:
		keyString = "RIGHT";
		break;
	case GLUT_KEY_UP:
		keyString = "UP";
		break;
	case GLUT_KEY_DOWN:
		keyString = "DOWN";
		break;
	}

	if (keyString != NULL ) {
		pressKey(keyString, up);
	}
}

GLvoid keyboardSpecialCallbackDown(int key, int x, int y) {
	keyboardSpecialCallback(key, x, y, JNI_FALSE);
}

GLvoid keyboardSpecialCallbackUp(int key, int x, int y) {
	keyboardSpecialCallback(key, x, y, JNI_TRUE);
}

JNIEXPORT void JNICALL Java_go_graphics_nativegl_NativeAreaWindow_openWindow_1native(
		JNIEnv *env, jobject obj) {
	if (windowEnv != NULL ) {
		return;
	}

	int zero = 0;
	char* title_native = "TODO: title";

	glutInit(&zero, NULL );
	glutInitDisplayMode(GLUT_RGBA | GLUT_DOUBLE | GLUT_DEPTH);
	glutInitWindowSize(640, 480);
	glutCreateWindow(title_native);
	glutReshapeFunc(resizeGlWindow);
	glutDisplayFunc(drawGlWindow);
	glutMouseFunc(mouseCallback);
	glutMotionFunc(mouseMotionCallback);
	glutPassiveMotionFunc(mouseMotionCallback);
	glutEntryFunc(mouseEntryCallback);
	glutKeyboardFunc(keyboardCallbackDown);
	glutSpecialFunc(keyboardSpecialCallbackDown);
	glutKeyboardUpFunc(keyboardCallbackUp);
	glutSpecialUpFunc(keyboardSpecialCallbackUp);

	glewInit();
	glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
	windowEnv = env;
	windowObject = obj;

	glutMainLoop();
}
