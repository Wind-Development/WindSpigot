package net.minecraft.server;

public class EntityDamageSourceIndirect extends EntityDamageSource {

	private Entity owner;

	public EntityDamageSourceIndirect(String s, Entity entity, Entity entity1) {
		super(s, entity);
		this.owner = entity1;
	}

	@Override
	public Entity i() {
		return this.q;
	}

	@Override
	public Entity getEntity() {
		return this.owner;
	}

	@Override
	public IChatBaseComponent getLocalizedDeathMessage(EntityLiving entityliving) {
		IChatBaseComponent ichatbasecomponent = this.owner == null ? this.q.getScoreboardDisplayName()
				: this.owner.getScoreboardDisplayName();
		ItemStack itemstack = this.owner instanceof EntityLiving ? ((EntityLiving) this.owner).bA() : null;
		String s = "death.attack." + this.translationIndex;
		String s1 = s + ".item";

		return itemstack != null && itemstack.hasName() && LocaleI18n.c(s1)
				? new ChatMessage(s1,
				entityliving.getScoreboardDisplayName(), ichatbasecomponent, itemstack.C())
				: new ChatMessage(s, entityliving.getScoreboardDisplayName(), ichatbasecomponent);
	}

	// CraftBukkit start
	public Entity getProximateDamageSource() {
		return super.getEntity();
	}
	// CraftBukkit end
}
