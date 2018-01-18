/*******************************************************************************
 * Copyright (c) 2018
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
package go.graphics.swing.contextcreator;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import go.graphics.swing.AreaContainer;

public class GLFWContextCreator extends AsyncContextCreator {

    public GLFWContextCreator(AreaContainer ac) {
        super(ac);
    }

    private long glfw_wnd;

    public void async_init() {
        GLFWErrorCallback ec = GLFWErrorCallback.createPrint(System.err);;
        GLFW.glfwSetErrorCallback(ec);

        GLFW.glfwInit();

        GLFW.glfwWindowHint(GLFW.GLFW_STENCIL_BITS, 1);
        glfw_wnd = GLFW.glfwCreateWindow(width + 1, width + 1, "lwjgl-offscreen", 0, 0);
        GLFW.glfwMakeContextCurrent(glfw_wnd);
        GLFW.glfwSwapInterval(0);
    }

    public void async_set_size(int width, int height) {
        GLFW.glfwSetWindowSize(glfw_wnd, width, height);

    }

    public void async_refresh() {
        GLFW.glfwPollEvents();
    }

    public void async_swapbuffers() {
        GLFW.glfwSwapBuffers(glfw_wnd);
    }

    public void async_stop() {
        GLFW.glfwTerminate();
    }
}
