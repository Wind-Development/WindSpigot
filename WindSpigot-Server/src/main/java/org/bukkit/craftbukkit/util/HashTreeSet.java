package org.bukkit.craftbukkit.util;

import java.util.*;

public class HashTreeSet<E> extends AbstractSet<E> { // FalchusSpigot - AbstractSet

	private final Set<E> hash = new it.unimi.dsi.fastutil.objects.ObjectOpenHashSet<>(); // FalchusSpigot - Replace java.util.HashSet with ObjectOpenHashSet
	private final TreeSet<E> tree = new TreeSet<>();

	@Override
	public int size() {
		return hash.size();
	}

	@Override
	public boolean contains(Object o) {
		return hash.contains(o);
	}

	@Override
	public Iterator<E> iterator() {
		Iterator<E> it = tree.iterator();
		return new Iterator<E>() {

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
				it.remove();
				hash.remove(last);
			}
		};
	}

	@Override
	public boolean add(E e) {
		if (!hash.add(e)) return false;
		if (tree.add(e)) return true;

		hash.remove(e);
		return false;
	}

	@Override
	public boolean remove(Object o) {
		if (!hash.contains(o)) return false;

		for (Iterator<E> it = tree.iterator(); it.hasNext();) {
			if (Objects.equals(it.next(), o)) {
				it.remove();
				hash.remove(o);
				return true;
			}
		}
		return false;
	}

	@Override
	public void clear() {
		hash.clear();
		tree.clear();
	}

	public E first() {
		return tree.first();
	}

}
