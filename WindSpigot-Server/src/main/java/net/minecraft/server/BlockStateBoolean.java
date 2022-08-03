package net.minecraft.server;

import java.util.Collection;

import com.google.common.collect.ImmutableSet;

public class BlockStateBoolean extends BlockState<Boolean> {

	private final ImmutableSet<Boolean> a = ImmutableSet.of(true, false);

	protected BlockStateBoolean(String s) {
		super(s, Boolean.class);
	}

	// TacoSpigot start
	@Override
	public int getValueId(Boolean value) {
		return value ? 1 : 0;
	}

	@Override
	public Boolean getByValueId(int id) {
		switch (id) {
		case 0:
			return false;
		case 1:
			return true;
		default:
			throw new IllegalArgumentException("Invalid id: " + id);
		}
	}
	// TacoSpigot end

	@Override
	public Collection<Boolean> c() {
		return this.a;
	}

	public static BlockStateBoolean of(String s) {
		return new BlockStateBoolean(s);
	}

	@Override
	public String a(Boolean obool) {
		return obool.toString();
	}

	// TacoSpigot start - fix stupid generic thingies
	/*
	 * public String a(Comparable comparable) { return this.a((Boolean) comparable);
	 * }
	 */
	// CraftBukkit end
}
