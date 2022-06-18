package me.rastrian.dev.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public final class IndexedLinkedHashSet<E> implements Set<E> {

	private final List<E> list = Lists.newCopyOnWriteArrayList();
	private final Set<E> set = Sets.newConcurrentHashSet();

	@Override
	public boolean add(E e) {
		if (set.add(e)) {
			return list.add(e);
		}
		return false;
	}

	@Override
	public boolean remove(Object o) {
		if (set.remove(o)) {
			return list.remove(o);
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return set.containsAll(c);
	}

	@Override
	public void clear() {
		set.clear();
		list.clear();
	}

	public E get(int index) {
		return list.get(index);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		if (set.removeAll(c)) {
			return list.removeAll(c);
		}
		return true;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		if (set.retainAll(c)) {
			return list.retainAll(c);
		}
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean modified = false;
		for (E e : c) {
			if (add(e)) {
				modified = true;
			}
		}
		return modified;
	}

	@Override
	public int size() {
		return set.size();
	}

	@Override
	public boolean isEmpty() {
		return set.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return set.contains(o);
	}

	@Override
	public Iterator<E> iterator() {
		return list.iterator();
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}

	public int indexOf(Object o) {
		return list.indexOf(o);
	}
}