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

import org.lwjgl.system.Platform;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.stream.Stream;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import go.graphics.swing.GLContainer;

public class BackendSelector extends JComboBox<EBackendType> {

	private EBackendType current_item = null;

	public static final EBackendType FALLBACK_BACKEND = EBackendType.GLFW;

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		super.actionPerformed(actionEvent);

		if(actionEvent.getActionCommand().equals("comboBoxChanged")) {
			if(getSelectedItem() instanceof String) {
				setSelectedItem(current_item);
				return;
			}
			EBackendType bi = (EBackendType) getSelectedItem();
			if (bi.platform != null && bi.platform != Platform.get()) {
				setSelectedItem(current_item);
				BackendSelector.this.hidePopup();
				JOptionPane.showMessageDialog(BackendSelector.this.getParent(), bi.cc_name + " is only available on " + bi.platform);
			} else {
				current_item = bi;
			}

		}
	}

	public BackendSelector() {
		setEditable(false);

		addActionListener(this);

		availableBackends().forEach(backend -> addItem(backend));
	}

	private static Stream<EBackendType> availableBackends() {
		return Arrays.stream(EBackendType.values()).filter(backend -> backend.available(Platform.get()));
	}

	public static EBackendType getBackendByName(String name) {
		// matching and matching and suitable backends
		return availableBackends().filter(backend -> backend.cc_name.equalsIgnoreCase(name)).findFirst().orElse(EBackendType.DEFAULT);
	}

	public static ContextCreator createBackend(GLContainer container, EBackendType backend, boolean debug) throws Exception {
		EBackendType real_backend = backend;

		if(backend == null || backend.cc_class == null) {
			// first of all usable and suitable backends sorted for being default
			real_backend = availableBackends().filter(current_backend -> current_backend.default_for == Platform.get()).sorted().findFirst().orElse(FALLBACK_BACKEND);
		}

		return real_backend.cc_class.getConstructor(GLContainer.class, Boolean.TYPE).newInstance(container, debug);
	}

}
