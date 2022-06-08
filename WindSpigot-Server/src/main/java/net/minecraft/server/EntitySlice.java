package net.minecraft.server;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class EntitySlice<T> extends AbstractSet<T> {

	private static final Set<Class<?>> a = Sets.newConcurrentHashSet(); // CraftBukkit
	private final Map<Class<?>, List<T>> b = Maps.newHashMap();
	private final Set<Class<?>> c = Sets.newIdentityHashSet();
	private final Class<T> d;
	private final List<T> e = Lists.newArrayList();

	public EntitySlice(Class<T> oclass) {
		this.d = oclass;
		this.c.add(oclass);
		this.b.put(oclass, this.e);
		Iterator iterator = EntitySlice.a.iterator();

		while (iterator.hasNext()) {
			Class oclass1 = (Class) iterator.next();

			this.a(oclass1);
		}

	}

	protected void a(Class<?> oclass) {
		EntitySlice.a.add(oclass);
		Iterator iterator = this.e.iterator();

		while (iterator.hasNext()) {
			Object object = iterator.next();

			if (oclass.isAssignableFrom(object.getClass())) {
				this.a((T) object, oclass);
			}
		}

		this.c.add(oclass);
	}

	protected Class<?> b(Class<?> oclass) {
		if (this.d.isAssignableFrom(oclass)) {
			if (!this.c.contains(oclass)) {
				this.a(oclass);
			}

			return oclass;
		} else {
			throw new IllegalArgumentException("Don\'t know how to search for " + oclass);
		}
	}

	@Override
	public boolean add(T t0) {
		Iterator iterator = this.c.iterator();

		while (iterator.hasNext()) {
			Class oclass = (Class) iterator.next();

			if (oclass.isAssignableFrom(t0.getClass())) {
				this.a(t0, oclass);
			}
		}

		return true;
	}

	private void a(T t0, Class<?> oclass) {
		List list = this.b.get(oclass);

		if (list == null) {
			this.b.put(oclass, Lists.newArrayList(t0));
		} else {
			list.add(t0);
		}

	}

	@Override
	public boolean remove(Object object) {
		Object object1 = object;
		boolean flag = false;
		Iterator iterator = this.c.iterator();

		while (iterator.hasNext()) {
			Class oclass = (Class) iterator.next();

			if (oclass.isAssignableFrom(object1.getClass())) {
				List list = this.b.get(oclass);

				if (list != null && list.remove(object1)) {
					flag = true;
				}
			}
		}

		return flag;
	}

	@Override
	public boolean contains(Object object) {
		return Iterators.contains(this.c(object.getClass()).iterator(), object);
	}

	public <S> Iterable<S> c(final Class<S> oclass) {
		return (Iterable) () -> {
			List list = EntitySlice.this.b.get(EntitySlice.this.b(oclass));

			if (list == null) {
				return Iterators.emptyIterator();
			} else {
				Iterator iterator = list.iterator();

				return Iterators.filter(iterator, oclass);
			}
		};
	}

	@Override
	public Iterator<T> iterator() {
		return this.e.isEmpty() ? Iterators.<T>emptyIterator() : Iterators.unmodifiableIterator(this.e.iterator());
	}

	@Override
	public int size() {
		return this.e.size();
	}
}
