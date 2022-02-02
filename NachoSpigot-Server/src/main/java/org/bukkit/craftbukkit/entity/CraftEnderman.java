package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.EntityType;
import org.bukkit.material.MaterialData;

import net.minecraft.server.EntityEnderman;
import net.minecraft.server.IBlockData;

public class CraftEnderman extends CraftMonster implements Enderman {
	public CraftEnderman(CraftServer server, EntityEnderman entity) {
		super(server, entity);
	}

	@Override
	public MaterialData getCarriedMaterial() {
		IBlockData blockData = getHandle().getCarried();
		return CraftMagicNumbers.getMaterial(blockData.getBlock())
				.getNewData((byte) blockData.getBlock().toLegacyData(blockData));
	}

	@Override
	public void setCarriedMaterial(MaterialData data) {
		getHandle().setCarried(CraftMagicNumbers.getBlock(data.getItemTypeId()).fromLegacyData(data.getData()));
	}

	@Override
	public EntityEnderman getHandle() {
		return (EntityEnderman) entity;
	}

	@Override
	public String toString() {
		return "CraftEnderman";
	}

	@Override
	public EntityType getType() {
		return EntityType.ENDERMAN;
	}
}
