package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderDragonPart;
import org.bukkit.entity.Entity;
import org.bukkit.util.NumberConversions;

import net.minecraft.server.EntityComplexPart;

public class CraftEnderDragonPart extends CraftComplexPart implements EnderDragonPart {
	public CraftEnderDragonPart(CraftServer server, EntityComplexPart entity) {
		super(server, entity);
	}

	@Override
	public EnderDragon getParent() {
		return (EnderDragon) super.getParent();
	}

	@Override
	public EntityComplexPart getHandle() {
		return (EntityComplexPart) entity;
	}

	@Override
	public String toString() {
		return "CraftEnderDragonPart";
	}

	@Override
	public void damage(double amount) {
		getParent().damage(amount);
	}

	@Override
	public void damage(double amount, Entity source) {
		getParent().damage(amount, source);
	}

	@Override
	public double getHealth() {
		return getParent().getHealth();
	}

	@Override
	public void setHealth(double health) {
		getParent().setHealth(health);
	}

	@Override
	public double getMaxHealth() {
		return getParent().getMaxHealth();
	}

	@Override
	public void setMaxHealth(double health) {
		getParent().setMaxHealth(health);
	}

	@Override
	public void resetMaxHealth() {
		getParent().resetMaxHealth();
	}

	@Override
	@Deprecated
	public void _INVALID_damage(int amount) {
		damage(amount);
	}

	@Override
	@Deprecated
	public void _INVALID_damage(int amount, Entity source) {
		damage(amount, source);
	}

	@Override
	@Deprecated
	public int _INVALID_getHealth() {
		return NumberConversions.ceil(getHealth());
	}

	@Override
	@Deprecated
	public void _INVALID_setHealth(int health) {
		setHealth(health);
	}

	@Override
	@Deprecated
	public int _INVALID_getMaxHealth() {
		return NumberConversions.ceil(getMaxHealth());
	}

	@Override
	@Deprecated
	public void _INVALID_setMaxHealth(int health) {
		setMaxHealth(health);
	}
}
