package org.bukkit.craftbukkit.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.EntityType;

import net.minecraft.server.TileEntityMobSpawner;

public class CraftCreatureSpawner extends CraftBlockState implements CreatureSpawner {
	private final TileEntityMobSpawner spawner;

	public CraftCreatureSpawner(final Block block) {
		super(block);

		spawner = (TileEntityMobSpawner) ((CraftWorld) block.getWorld()).getTileEntityAt(getX(), getY(), getZ());
	}

	public CraftCreatureSpawner(final Material material, TileEntityMobSpawner te) {
		super(material);
		spawner = te;
	}

	@Override
	@Deprecated
	public CreatureType getCreatureType() {
		return CreatureType.fromName(spawner.getSpawner().getMobName());
	}

	@Override
	public EntityType getSpawnedType() {
		return EntityType.fromName(spawner.getSpawner().getMobName());
	}

	@Override
	@Deprecated
	public void setCreatureType(CreatureType creatureType) {
		spawner.getSpawner().setMobName(creatureType.getName());
	}

	@Override
	public void setSpawnedType(EntityType entityType) {
		if (entityType == null || entityType.getName() == null) {
			throw new IllegalArgumentException("Can't spawn EntityType " + entityType + " from mobspawners!");
		}

		spawner.getSpawner().setMobName(entityType.getName());
	}

	@Override
	@Deprecated
	public String getCreatureTypeId() {
		return spawner.getSpawner().getMobName();
	}

	@Override
	@Deprecated
	public void setCreatureTypeId(String creatureName) {
		setCreatureTypeByName(creatureName);
	}

	@Override
	public String getCreatureTypeName() {
		return spawner.getSpawner().getMobName();
	}

	@Override
	public void setCreatureTypeByName(String creatureType) {
		// Verify input
		EntityType type = EntityType.fromName(creatureType);
		if (type == null) {
			return;
		}
		setSpawnedType(type);
	}

	@Override
	public int getDelay() {
		return spawner.getSpawner().spawnDelay;
	}

	@Override
	public void setDelay(int delay) {
		spawner.getSpawner().spawnDelay = delay;
	}

	@Override
	public TileEntityMobSpawner getTileEntity() {
		return spawner;
	}
}
