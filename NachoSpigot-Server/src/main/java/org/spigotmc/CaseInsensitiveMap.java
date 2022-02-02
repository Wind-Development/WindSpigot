package org.spigotmc;

import java.util.Map;

import gnu.trove.map.hash.TCustomHashMap;

public class CaseInsensitiveMap<V> extends TCustomHashMap<String, V> {

	public CaseInsensitiveMap() {
		super(CaseInsensitiveHashingStrategy.INSTANCE);
	}

	public CaseInsensitiveMap(Map<? extends String, ? extends V> map) {
		super(CaseInsensitiveHashingStrategy.INSTANCE, map);
	}
}
