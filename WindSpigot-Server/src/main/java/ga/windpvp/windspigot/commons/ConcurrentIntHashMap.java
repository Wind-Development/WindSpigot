package ga.windpvp.windspigot.commons;

import net.minecraft.server.IntHashMap;

public class ConcurrentIntHashMap<V> extends IntHashMap<V> {
	
	private boolean synchronize() {
		return !Thread.holdsLock(this);
	}
	
	public V get(int var1) {
		if (synchronize()) {
			synchronized(this) {
				return super.get(var1);
			}
		} else {
			return super.get(var1);
		}
	}

	public boolean b(int var1) {
		if (synchronize()) {
			synchronized(this) {
				return super.b(var1);
			}
		} else {
			return super.b(var1);
		}
	}

	public void a(int var1, V var2) {
		if (synchronize()) {
			synchronized(this) {
				super.a(var1, var2);
			}
		} else {
			super.a(var1, var2);
		}
	}

	public V d(int var1) {
		if (synchronize()) {
			synchronized(this) {
				return super.d(var1);
			}
		} else {
			return super.d(var1);
		}
	}

	public void c() {
		if (synchronize()) {
			synchronized(this) {
				super.c();
			}
		} else {
			super.c();
		}
	}
}
