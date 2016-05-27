/*******************************************************************************
 * Copyright (c) 2016
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
package jsettlers.common.utils.collections.map;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Andreas Eberle on 06.05.2016.
 */
public class ArrayListMap<K, V> implements Serializable {
	public static class Entry<K, V> implements Serializable {
		final K key;
		final V value;

		Entry(K key, V value) {
			this.key = key;
			this.value = value;
		}

		public K getKey() {
			return key;
		}

		public V getValue() {
			return value;
		}
	}

	private final List<Entry<K, V>> entries = new ArrayList<>();

	public int size() {
		return entries.size();
	}

	public boolean isEmpty() {
		return entries.isEmpty();
	}

	public V get(K key) {
		for (Entry<K, V> entry : entries){
			if (entry.key.equals(key)) {
				return entry.value;
			}
		}
		return null;
	}

	public V put(K key, V value) {
		V old = remove(key);
		entries.add(new Entry<>(key, value));
		return old;
	}

	public V remove(K key) {
		for (Iterator<Entry<K, V>> iterator = entries.iterator(); iterator.hasNext();) {
			Entry<K, V> entry = iterator.next();
			if (entry.key.equals(key)) {
				iterator.remove();
				return entry.value;
			}
		}
		return null;
	}

	public void clear() {
		entries.clear();
	}

	public Set<Entry<K, V>> entrySet() {
		return new HashSet<>(entries);
	}
}
