package org.bukkit.craftbukkit.entity;

import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ocelot;

import net.minecraft.server.EntityOcelot;

public class CraftOcelot extends CraftTameableAnimal implements Ocelot {
	public CraftOcelot(CraftServer server, EntityOcelot wolf) {
		super(server, wolf);
	}

	@Override
	public EntityOcelot getHandle() {
		return (EntityOcelot) entity;
	}

	@Override
	public Type getCatType() {
		return Type.getType(getHandle().getCatType());
	}

	@Override
	public void setCatType(Type type) {
		Validate.notNull(type, "Cat type cannot be null");
		getHandle().setCatType(type.getId());
	}

	@Override
	public EntityType getType() {
		return EntityType.OCELOT;
	}
}
