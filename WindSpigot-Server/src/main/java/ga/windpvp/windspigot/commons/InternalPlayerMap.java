package ga.windpvp.windspigot.commons;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import static ga.windpvp.windspigot.async.AsyncUtil.runSynchronized;

public class InternalPlayerMap<V> extends Long2ObjectOpenHashMap<V> {

	private static final long serialVersionUID = -4787568198041966835L;

	@Override
	public V put(final long k, final V v) {
		return runSynchronized(this, () -> super.put(k, v));
	}

	@Override
	public V remove(final long k) {
		return runSynchronized(this, () -> super.remove(k));
	}

	@Override
	public V get(final long k) {
		return runSynchronized(this, () -> super.get(k));
	}
}
