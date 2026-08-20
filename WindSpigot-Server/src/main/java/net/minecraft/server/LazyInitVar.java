package net.minecraft.server;

import java.util.function.Supplier;

// WindSpigot start - thread-safe double-checked locking for LazyInitVar
public class LazyInitVar<T> {
	private volatile T value;
	private final Supplier<T> supplier;

	public LazyInitVar(Supplier<T> supplier) {
		this.supplier = supplier;
	}

	public T get() {
		T result = this.value;
		if (result == null) {
			synchronized (this) {
				result = this.value;
				if (result == null) {
					this.value = result = this.supplier.get();
				}
			}
		}
		return result;
	}

	public boolean isInitialized() {
		return this.value != null;
	}
}
// WindSpigot end 
