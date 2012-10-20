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

	GLfloat m[16] = {
			2.0f / width, 0, 0, 0,
			0, 2.0f/height, 0, 0,
			0, 0, -.5f, 0,
			-1, -1, .5f, 1
	};

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
	(*windowEnv)->CallVoidMethod(windowEnv, windowObject, resizeMID, (jint) width, (jint) height);
}

GLvoid drawGlWindow() {
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

	jmethodID resizeMID = getWindowMID("drawContent", "()V");
	(*windowEnv)->CallVoidMethod(windowEnv, windowObject, resizeMID);

	glutSwapBuffers();
	glutPostRedisplay();
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

	glewInit();
	glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
	windowEnv = env;
	windowObject = obj;

	glutMainLoop();
}
