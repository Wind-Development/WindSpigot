package com.windpvp.windspigot.commons;

import net.minecraft.server.IntHashMap;

// WindSpigot - zero-allocation synchronized methods
public class ConcurrentIntHashMap<V> extends IntHashMap<V> {
	
	@Override
	public synchronized V get(int var1) {
		return super.get(var1);
	}

	@Override
	public synchronized boolean b(int var1) {
		return super.b(var1);
	}

	@Override
	public synchronized void a(int var1, V var2) {
		super.a(var1, var2);
	}

	@Override
	public synchronized V d(int var1) {
		return super.d(var1);
	}

	@Override
	public synchronized void c() {
		super.c();
	}
}
