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
 * NativeGlWrapper.c
 *
 *  Created on: 20.10.2012
 *      Author: michael
 */

#include "NativeGLWrapper.h"

#include <string.h>
#include <strings.h>
#include <GL/glew.h>


#define GL_ERROR(s) printf(s "\n")

static void bindTexture(int id) {
	glBindTexture(GL_TEXTURE_2D, id);
}
void *activeBuffer = NULL;
void *activeBufferStart = NULL;
static jint supportedTextureSize(jint size) {
	const GLubyte* glVersion = glGetString(GL_VERSION);
	int hasNonPowerOfTow = 0;
	if (glVersion == NULL || glVersion[0] == '1') {

		const GLubyte* glExts = glGetString(GL_EXTENSIONS);
		if (strstr((const char*) glExts, "ARB_texture_non_power_of_two")) {
			hasNonPowerOfTow = 1;
		}
	} else {
		hasNonPowerOfTow = 1;
	}

	if (hasNonPowerOfTow) {
		return size;
	} else {
		int real = 1;
		while (real && real < size) {
			real *= 2;
		}
		return real;
	}
}

static inline void glBeginPlain() {
	bindTexture(0);
	glDisableClientState(GL_TEXTURE_COORD_ARRAY);
}
static inline void glEndPlain() {
	glEnableClientState(GL_TEXTURE_COORD_ARRAY);
}

JNIEXPORT void JNICALL Java_go_graphics_nativegl_NativeGLWrapper_fillQuad(
		JNIEnv *env, jobject obj, jfloat x1, jfloat y1, jfloat x2, jfloat y2) {
	glBeginPlain();

	GLfloat rect[8] = { x1, y1, x1, y2, x2, y2, x2, y1 };
	glVertexPointer(2, GL_FLOAT, 0, rect);
	glDrawArrays(GL_QUADS, 0, 4);
	glEndPlain();
}

JNIEXPORT void JNICALL Java_go_graphics_nativegl_NativeGLWrapper_drawLine(
		JNIEnv *env, jobject obj, jfloatArray points, jboolean loop) {
	glBeginPlain();

	jsize pointsLength = (*env)->GetArrayLength(env, points);
	jfloat *floatBuff = (*env)->GetFloatArrayElements(env, points, NULL );
	if (floatBuff == 0) {
		GL_ERROR("drawLine: Could not get array content");
		return;
	}

	glVertexPointer(3, GL_FLOAT, 0, floatBuff);
	glDrawArrays(loop ? GL_LINE_LOOP : GL_LINE_STRIP, 0, pointsLength / 3);

	(*env)->ReleaseFloatArrayElements(env, points, floatBuff, 0);

	glEndPlain();
}

JNIEXPORT void JNICALL Java_go_graphics_nativegl_NativeGLWrapper_glPushMatrix(
		JNIEnv *env, jobject obj) {
	glPushMatrix();
}

JNIEXPORT void JNICALL Java_go_graphics_nativegl_NativeGLWrapper_glTranslatef(
		JNIEnv *env, jobject obj, jfloat x, jfloat y, jfloat z) {
	glTranslatef((GLfloat) x, (GLfloat) y, (GLfloat) z);
}

JNIEXPORT void JNICALL Java_go_graphics_nativegl_NativeGLWrapper_glScalef(
		JNIEnv *env, jobject obj, jfloat x, jfloat y, jfloat z) {
	glScalef((GLfloat) x, (GLfloat) y, (GLfloat) z);
}

JNIEXPORT void JNICALL Java_go_graphics_nativegl_NativeGLWrapper_glPopMatrix(
		JNIEnv *env, jobject obj) {
	glPopMatrix();
}

JNIEXPORT void JNICALL Java_go_graphics_nativegl_NativeGLWrapper_color(
		JNIEnv *env, jobject obj, jfloat r, jfloat g, jfloat b, jfloat a) {
	glColor4f(r, g, b, a);
}

JNIEXPORT jint JNICALL Java_go_graphics_nativegl_NativeGLWrapper_generateTexture(
		JNIEnv *env, jobject obj, jint width, jint height, jobject databuffer) {
	void* data = (*env)->GetDirectBufferAddress(env, databuffer);
	if (data == NULL ) {
		GL_ERROR("generateTexture: Could not get native buffer");
		return 0;
	}

	GLuint textureIndex;
	glGenTextures(1, &textureIndex);
	if (textureIndex == 0) {
		return 0;
	}

	bindTexture(textureIndex);
	glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB5_A1, width, height, 0, GL_RGBA,
			GL_UNSIGNED_SHORT_5_5_5_1, data);

	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

	return (jint) textureIndex;
}

JNIEXPORT void JNICALL Java_go_graphics_nativegl_NativeGLWrapper_updateTexture(
		JNIEnv *env, jobject obj, jint textureIndex, jint left, jint bottom,
		jint width, jint height, jobject databuffer) {

	void* data = (*env)->GetDirectBufferAddress(env, databuffer);
	if (data == NULL ) {
		GL_ERROR("updateTexture: Could not get native buffer");
		return;
	}

	bindTexture(textureIndex);
	glTexSubImage2D(GL_TEXTURE_2D, 0, left, bottom, width, height, GL_RGBA,
			GL_UNSIGNED_SHORT_5_5_5_1, data);
}
JNIEXPORT void JNICALL Java_go_graphics_nativegl_NativeGLWrapper_deleteTexture(
		JNIEnv *env, jobject obj, jint textureIndex) {
	glDeleteTextures(1, (GLuint*) &textureIndex);
}

JNIEXPORT void JNICALL Java_go_graphics_nativegl_NativeGLWrapper_drawQuadWithTexture(
		JNIEnv *env, jobject obj, jint textureid, jfloatArray databuffer) {
	jsize buffLength = (*env)->GetArrayLength(env, databuffer);
	jfloat *floatBuff = (*env)->GetFloatArrayElements(env, databuffer, NULL );
	if (floatBuff == 0) {
		GL_ERROR("drawQuadWithTexture: Could not get array content");
		return;
	}

	bindTexture(textureid);

	glVertexPointer(3, GL_FLOAT, 5 * 4, floatBuff);
	glTexCoordPointer(2, GL_FLOAT, 5 * 4, floatBuff + 3);
	glDrawArrays(GL_QUADS, 0, buffLength / 5);

	(*env)->ReleaseFloatArrayElements(env, databuffer, floatBuff, 0);
}

JNIEXPORT void JNICALL Java_go_graphics_nativegl_NativeGLWrapper_drawTrianglesWithTexture__I_3F(
		JNIEnv *env, jobject obj, jint textureid, jfloatArray databuffer) {
	jsize buffLength = (*env)->GetArrayLength(env, databuffer);
	jfloat *floatBuff = (*env)->GetFloatArrayElements(env, databuffer, NULL );
	if (floatBuff == 0) {
		GL_ERROR("drawTrianglesWithTexture: Could not get array content");
		return;
	}

	bindTexture(textureid);

	glVertexPointer(3, GL_FLOAT, 5 * 4, floatBuff);
	glTexCoordPointer(2, GL_FLOAT, 5 * 4, floatBuff + 3);
	glDrawArrays(GL_TRIANGLES, 0, buffLength / 5);

	(*env)->ReleaseFloatArrayElements(env, databuffer, floatBuff, 0);
}

JNIEXPORT void JNICALL Java_go_graphics_nativegl_NativeGLWrapper_drawTrianglesWithTextureColored__ILjava_nio_ByteBuffer_2I(
		JNIEnv *env, jobject obj, jint textureid, jobject databuffer,
		jint triangles) {
	GLfloat* data = (GLfloat*) (*env)->GetDirectBufferAddress(env, databuffer);
	if (data == NULL ) {
		GL_ERROR("drawTrianglesWithTextureColored: Could not get native buffer");
		return;
	}

	bindTexture(textureid);

	glVertexPointer(3, GL_FLOAT, 6 * 4, data);
	glTexCoordPointer(2, GL_FLOAT, 6 * 4, data + 3);
	glColorPointer(4, GL_UNSIGNED_BYTE, 6 * 4, data + 5);
	glEnableClientState(GL_COLOR_ARRAY);
	glDrawArrays(GL_TRIANGLES, 0, triangles * 3);
	glDisableClientState(GL_COLOR_ARRAY);
}

JNIEXPORT void JNICALL Java_go_graphics_nativegl_NativeGLWrapper_drawTrianglesWithTexture__III(
		JNIEnv *env, jobject obj, int textureid, int geometryindex,
		int triangleCount) {
	bindTexture(textureid);

	glBindBuffer(GL_ARRAY_BUFFER, geometryindex);
	glVertexPointer(3, GL_FLOAT, 5 * 4, 0);
	glTexCoordPointer(2, GL_FLOAT, 5 * 4, (void*) (3 * 4));

	glDrawArrays(GL_TRIANGLES, 0, triangleCount * 3);

	glBindBuffer(GL_ARRAY_BUFFER, 0);
}

JNIEXPORT void JNICALL Java_go_graphics_nativegl_NativeGLWrapper_drawTrianglesWithTextureColored__III(
		JNIEnv *env, jobject obj, int textureid, int geometryindex,
		int triangleCount) {
	bindTexture(textureid);

	glBindBuffer(GL_ARRAY_BUFFER, geometryindex);
	glVertexPointer(3, GL_FLOAT, 6 * 4, 0);
	glTexCoordPointer(2, GL_FLOAT, 6 * 4, (void*) (3 * 4));
	glColorPointer(4, GL_UNSIGNED_BYTE, 6 * 4, (void*) (5 * 4));

	glEnableClientState(GL_COLOR_ARRAY);
	glDrawArrays(GL_TRIANGLES, 0, triangleCount * 3);
	glDisableClientState(GL_COLOR_ARRAY);

	glBindBuffer(GL_ARRAY_BUFFER, 0);
}

JNIEXPORT jint JNICALL Java_go_graphics_nativegl_NativeGLWrapper_makeWidthValid(
		JNIEnv *env, jobject obj, jint width) {
	return supportedTextureSize(width);
}

JNIEXPORT jint JNICALL Java_go_graphics_nativegl_NativeGLWrapper_makeHeightValid(
		JNIEnv *env, jobject obj, jint height) {
	return supportedTextureSize(height);
}

JNIEXPORT void JNICALL Java_go_graphics_nativegl_NativeGLWrapper_glMultMatrixf(
		JNIEnv *env, jobject obj, jfloatArray matrix, jint offset) {
	if ((*env)->GetArrayLength(env, matrix) - offset < 16) {
		GL_ERROR("glMultMatrixf: Matrix array not long enough");
		return;
	}

	jfloat floatBuff[16];
	(*env)->GetFloatArrayRegion(env, matrix, offset, offset + 16, floatBuff);
	glMultMatrixf(floatBuff);
}

static jint generateGeometry(jint bytes) {
	GLuint vertexBufferId;
	glGenBuffers(1, &vertexBufferId);

	if (vertexBufferId == 0) {
		GL_ERROR("generateGeometry: Could not allocate buffer");
		return -1;
	}

	glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);
	glBufferData(GL_ARRAY_BUFFER, bytes, NULL, GL_DYNAMIC_DRAW);
	glBindBuffer(GL_ARRAY_BUFFER, 0);
	return (jint) vertexBufferId;
}

JNIEXPORT jboolean JNICALL Java_go_graphics_nativegl_NativeGLWrapper_isGeometryValid(
		JNIEnv *env, jobject obj, jint geometryindex) {
	return geometryindex > 0;
}

JNIEXPORT void JNICALL Java_go_graphics_nativegl_NativeGLWrapper_removeGeometry(
		JNIEnv *env, jobject obj, jint geometryindex) {
	glDeleteBuffers(1, (GLuint *) &geometryindex);
}

JNIEXPORT jobject JNICALL Java_go_graphics_nativegl_NativeGLWrapper_startWriteGeometry(
		JNIEnv *env, jobject obj, jint geometryindex) {
	glBindBuffer(GL_ARRAY_BUFFER, geometryindex);
	activeBufferStart = glMapBuffer(GL_ARRAY_BUFFER, GL_WRITE_ONLY);
	activeBuffer = activeBufferStart;
	return obj;
}

JNIEXPORT void JNICALL Java_go_graphics_nativegl_NativeGLWrapper_endWriteGeometry(
		JNIEnv *env, jobject obj, jint geometryindex) {
	glUnmapBuffer(GL_ARRAY_BUFFER);
	glBindBuffer(GL_ARRAY_BUFFER, 0);
}

JNIEXPORT jint JNICALL Java_go_graphics_nativegl_NativeGLWrapper_generateGeometry(
		JNIEnv *env, jobject obj, jint bytes) {
	return generateGeometry(bytes);
}

JNIEXPORT void JNICALL Java_go_graphics_nativegl_NativeGLWrapper_putFloat(
		JNIEnv *env, jobject obj, jfloat f) {
	GLfloat *fb = (GLfloat*) activeBuffer;
	*fb = f;
	activeBuffer = &fb[1];
}
JNIEXPORT void JNICALL Java_go_graphics_nativegl_NativeGLWrapper_putByte(
		JNIEnv *env, jobject obj, jbyte b) {
	GLbyte *bb = (GLbyte*) activeBuffer;
	*bb = b;
	activeBuffer = &bb[1];
}

JNIEXPORT void JNICALL Java_go_graphics_nativegl_NativeGLWrapper_position(
		JNIEnv *env, jobject obj, jint i) {
	activeBuffer = (GLbyte*) activeBufferStart + i;
}

