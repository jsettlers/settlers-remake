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
public class OrderedMap<K, V> implements Serializable {
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
