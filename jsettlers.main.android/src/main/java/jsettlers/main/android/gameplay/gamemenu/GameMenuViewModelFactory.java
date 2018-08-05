/*
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
 */

package jsettlers.main.android.gameplay.gamemenu;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import jsettlers.main.android.core.controls.ControlsResolver;

public class GameMenuViewModelFactory {

	private final Application application;
	private final ControlsResolver controlsResolver;

	public GameMenuViewModelFactory(Application application) {
		this.application = application;
		controlsResolver = new ControlsResolver(application);
	}

	public GameMenuViewModel get(@NonNull Fragment fragment) {
		Factory factory = new Factory();
		return ViewModelProviders.of(fragment, factory).get(GameMenuViewModel.class);
	}

	public GameMenuViewModel get(@NonNull FragmentActivity fragmentActivity) {
		Factory factory = new Factory();
		return ViewModelProviders.of(fragmentActivity, factory).get(GameMenuViewModel.class);
	}

	class Factory implements ViewModelProvider.Factory {
		Factory() {
		}

		@Override
		public <T extends ViewModel> T create(Class<T> modelClass) {
			if (modelClass == GameMenuViewModel.class) {
				return (T) new GameMenuViewModel(application, controlsResolver.getGameMenu());
			}
			throw new RuntimeException("GameMenuViewModelFactory doesn't know how to create: "
					+ modelClass.toString());
		}
	}
}
