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
package jsettlers.graphics.ui.generate;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import jsettlers.graphics.ui.generate.AbstractArgument;
import jsettlers.graphics.ui.generate.LayoutPanel;

public class LayoutSourceGenerator {

	private final LayoutPanel root;
	private final String name;
	private String packageName;

	private int idCounter = 1;

	public LayoutSourceGenerator(String name, LayoutPanel root) {
		this.name = name;
		this.root = root;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getSource() {
		try (StringWriter writer = new StringWriter()) {
			writeSourceTo(writer);
			return writer.toString();
		} catch (IOException e) {
			return "";
		}
	}

	public void writeSourceTo(Writer writer) {
		try (PrintWriter printWriter = new PrintWriter(writer)) {
			writeSourceTo(printWriter);
		}
	}

	private void writeSourceTo(PrintWriter writer) {
		if (packageName != null) {
			writer.println("package " + packageName + ";");
		}
		writer.println();
		writer.println("public class " + name + " {");
		writer.println(getField("_root", root));
		writeIdsOfTo(writer, root);

		writer.println();
		writer.println("public " + name + "() {");
		String rootVar = writeSourceOfTo(writer, root);
		writer.println("this._root = " + rootVar + ";");
		writer.println("}");
		writer.println("}");
	}

	private void writeIdsOfTo(PrintWriter writer, LayoutPanel elm) {
		if (elm.getId() != null) {
			writer.println(getField(elm));
		}
		for (LayoutPanel c : elm.getChildren()) {
			writeIdsOfTo(writer, c);
		}
	}

	private String writeSourceOfTo(PrintWriter writer, LayoutPanel elm) {
		String varName;
		if (elm.getId() != null) {
			varName = "this." + elm.getId();
		} else {
			varName = getNextId();
			writer.println(getDeclaration(varName, elm));
		}

		writer.print(varName + " = ");
		writeConstructorCall(writer, elm);
		writer.println(";");

		for (LayoutPanel c : elm.getChildren()) {
			float x = (float) (c.x - elm.x) / elm.width;
			float y = (float) (c.y - elm.y) / elm.height;
			String childVar = writeSourceOfTo(writer, c);
			writer.print(varName);
			writer.print(".addChild(");
			writer.print(childVar);
			writer.print(", ");
			writer.print(x);
			writer.print("f, ");
			writer.print(1 - y - (float) c.height / elm.height);
			writer.print("f, ");
			writer.print(x + (float) c.width / elm.width);
			writer.print("f, ");
			writer.print(1 - y);
			writer.println("f);");
		}
		return varName;
	}

	private void writeConstructorCall(PrintWriter writer, LayoutPanel elm) {
		writer.print("new ");
		writer.print(elm.getClassName());
		writer.print("(");
		boolean addComma = false;
		for (AbstractArgument a : elm.getArguments()) {
			if (addComma) {
				writer.print(", ");
			}
			writer.print(a.getArgumentSource());
			addComma = true;
		}
		writer.print(")");
	}

	private String getField(LayoutPanel elm) {
		return getField(elm.getId(), elm);
	}

	private String getField(String name, LayoutPanel elm) {
		return "public final " + getDeclaration(name, elm);
	}

	private String getDeclaration(String name, LayoutPanel elm) {
		return elm.getClassName() + " " + name + ";";
	}

	private String getNextId() {
		return "__" + idCounter++;
	}

}
