package org.bukkit.craftbukkit.entity;

import java.util.Set;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.ComplexEntityPart;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

import net.minecraft.server.EntityComplexPart;
import net.minecraft.server.EntityEnderDragon;

public class CraftEnderDragon extends CraftComplexLivingEntity implements EnderDragon {
	public CraftEnderDragon(CraftServer server, EntityEnderDragon entity) {
		super(server, entity);
	}

	@Override
	public Set<ComplexEntityPart> getParts() {
		Builder<ComplexEntityPart> builder = ImmutableSet.builder();

		for (EntityComplexPart part : getHandle().children) {
			builder.add((ComplexEntityPart) part.getBukkitEntity());
		}

		return builder.build();
	}

	@Override
	public EntityEnderDragon getHandle() {
		return (EntityEnderDragon) entity;
	}

	@Override
	public String toString() {
		return "CraftEnderDragon";
	}

	@Override
	public EntityType getType() {
		return EntityType.ENDER_DRAGON;
	}
}
