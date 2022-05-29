package ga.windpvp.windspigot.commons;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

public class InternalPlayerMap<V> extends Long2ObjectOpenHashMap<V> {

	private static final long serialVersionUID = -4787568198041966835L;

	private boolean synchronize() {
		return !Thread.holdsLock(this);
	}

	@Override
	public V put(final long k, final V v) {
		if (synchronize()) {
			synchronized (this) {
				return super.put(k, v);
			}
		} else {
			return super.put(k, v);
		}
	}

	@Override
	public V remove(final long k) {
		if (synchronize()) {
			synchronized (this) {

				return super.remove(k);
			}
		} else {
			return super.remove(k);
		}
	}

	@Override
	public V get(final long k) {
		if (synchronize()) {
			synchronized (this) {

				return super.get(k);
			}
		} else {
			return super.get(k);
		}
	}
}
