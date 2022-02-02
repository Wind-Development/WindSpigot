package net.minecraft.server;

import com.google.common.base.Objects;

import net.techcable.tacospigot.BlockStateRegistry;
// TacoSpigot end

public abstract class BlockState<T extends Comparable<T>> implements IBlockState<T> {

	private final Class<T> a;
	private final String b;
	// TacoSpigot start
	private int id = -1;

	@Override
	public int getId() {
		assert id >= 0 : "Id not initialized";
		return id;
	}

	@Override
	public void tryInitId() {
		if (id < 0) {
			this.id = BlockStateRegistry.getId(this);
		}
	}
	// TacoSpigot end

	protected BlockState(String s, Class<T> oclass) {
		this.a = oclass;
		this.b = s;
	}

	@Override
	public String a() {
		return this.b;
	}

	@Override
	public Class<T> b() {
		return this.a;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("name", this.b).add("clazz", this.a).add("values", this.c()).toString();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			BlockState blockstate = (BlockState) object;

			return this.a.equals(blockstate.a) && this.b.equals(blockstate.b);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return 31 * this.a.hashCode() + this.b.hashCode();
	}
}
