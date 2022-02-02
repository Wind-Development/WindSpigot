package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.entity.Minecart;
import org.bukkit.material.MaterialData;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import net.minecraft.server.Blocks;
import net.minecraft.server.EntityMinecartAbstract;
import net.minecraft.server.IBlockData;

public abstract class CraftMinecart extends CraftVehicle implements Minecart {
	public CraftMinecart(CraftServer server, EntityMinecartAbstract entity) {
		super(server, entity);
	}

	@Override
	public void setDamage(double damage) {
		getHandle().setDamage((float) damage);
	}

	@Override
	public double getDamage() {
		return getHandle().getDamage();
	}

	@Override
	public double getMaxSpeed() {
		return getHandle().maxSpeed;
	}

	@Override
	public void setMaxSpeed(double speed) {
		if (speed >= 0D) {
			getHandle().maxSpeed = speed;
		}
	}

	@Override
	public boolean isSlowWhenEmpty() {
		return getHandle().slowWhenEmpty;
	}

	@Override
	public void setSlowWhenEmpty(boolean slow) {
		getHandle().slowWhenEmpty = slow;
	}

	@Override
	public Vector getFlyingVelocityMod() {
		return getHandle().getFlyingVelocityMod();
	}

	@Override
	public void setFlyingVelocityMod(Vector flying) {
		getHandle().setFlyingVelocityMod(flying);
	}

	@Override
	public Vector getDerailedVelocityMod() {
		return getHandle().getDerailedVelocityMod();
	}

	@Override
	public void setDerailedVelocityMod(Vector derailed) {
		getHandle().setDerailedVelocityMod(derailed);
	}

	@Override
	public EntityMinecartAbstract getHandle() {
		return (EntityMinecartAbstract) entity;
	}

	@Override
	@Deprecated
	public void _INVALID_setDamage(int damage) {
		setDamage(damage);
	}

	@Override
	@Deprecated
	public int _INVALID_getDamage() {
		return NumberConversions.ceil(getDamage());
	}

	@Override
	public void setDisplayBlock(MaterialData material) {
		if (material != null) {
			IBlockData block = CraftMagicNumbers.getBlock(material.getItemTypeId()).fromLegacyData(material.getData());
			this.getHandle().setDisplayBlock(block);
		} else {
			// Set block to air (default) and set the flag to not have a display block.
			this.getHandle().setDisplayBlock(Blocks.AIR.getBlockData());
			this.getHandle().a(false);
		}
	}

	@Override
	public MaterialData getDisplayBlock() {
		IBlockData blockData = getHandle().getDisplayBlock();
		return CraftMagicNumbers.getMaterial(blockData.getBlock())
				.getNewData((byte) blockData.getBlock().toLegacyData(blockData));
	}

	@Override
	public void setDisplayBlockOffset(int offset) {
		getHandle().SetDisplayBlockOffset(offset);
	}

	@Override
	public int getDisplayBlockOffset() {
		return getHandle().getDisplayBlockOffset();
	}
}
