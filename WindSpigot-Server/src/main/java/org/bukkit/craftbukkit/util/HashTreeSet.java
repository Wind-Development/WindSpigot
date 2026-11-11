package org.bukkit.craftbukkit.util;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class HashTreeSet<E> {

	private Set<E> hash = new it.unimi.dsi.fastutil.objects.ObjectOpenHashSet<>(); // FalchusSpigot - Replace java.util.HashSet with ObjectOpenHashSet
	private TreeSet<E> tree = new TreeSet<>();

	public int size() {
		return hash.size();
	}

	public boolean isEmpty() {
		return hash.isEmpty();
	}

	public Iterator<E> iterator() {
		return new Iterator<E>() {

			private Iterator<E> it = tree.iterator();
			private E last;

			@Override
			public boolean hasNext() {
				return it.hasNext();
			}

			@Override
			public E next() {
				return last = it.next();
			}

			@Override
			public void remove() {
				if (last == null) {
					throw new IllegalStateException();
				}
				it.remove();
				hash.remove(last);
				last = null;
			}
		};
	}

	public boolean add(E e) {
		if (!hash.add(e)) return false;
		if (!tree.add(e)) {
			hash.remove(e);
			return false;
		}
		return true;
	}

	public boolean remove(Object o) {
		if (!hash.remove(o)) return false;

		if (!tree.remove(o)) {
			Iterator<E> it = tree.iterator();
			while (it.hasNext()) {
				if (it.next().equals(o)) {
					it.remove();
					break;
				}
			}
		}
		return true;
	}

	public E first() {
		return tree.first();
	}

}
