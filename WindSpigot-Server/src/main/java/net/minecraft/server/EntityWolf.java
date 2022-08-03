package net.minecraft.server;

import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
// CraftBukkit end

import com.google.common.base.Predicate;

import ga.windpvp.windspigot.cache.Constants;

public class EntityWolf extends EntityTameableAnimal {

	private float bo;
	private boolean bq;
	private boolean br;
	private float bs;
	private float bt;

	public EntityWolf(World world) {
		super(world);
		this.setSize(0.6F, 0.8F);
		((Navigation) this.getNavigation()).a(true);
		this.goalSelector.a(1, new PathfinderGoalFloat(this));
		this.goalSelector.a(2, this.bm);
		this.goalSelector.a(3, new PathfinderGoalLeapAtTarget(this, 0.4F));
		this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, 1.0D, true));
		this.goalSelector.a(5, new PathfinderGoalFollowOwner(this, 1.0D, 10.0F, 2.0F));
		this.goalSelector.a(6, new PathfinderGoalBreed(this, 1.0D));
		this.goalSelector.a(7, new PathfinderGoalRandomStroll(this, 1.0D));
		this.goalSelector.a(8, new PathfinderGoalBeg(this, 8.0F));
		this.goalSelector.a(9, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
		this.goalSelector.a(9, new PathfinderGoalRandomLookaround(this));
		this.targetSelector.a(1, new PathfinderGoalOwnerHurtByTarget(this));
		this.targetSelector.a(2, new PathfinderGoalOwnerHurtTarget(this));
		this.targetSelector.a(3, new PathfinderGoalHurtByTarget(this, true, new Class[0]));
		this.targetSelector.a(4,
				new PathfinderGoalRandomTargetNonTamed(this, EntityAnimal.class, false, new Predicate() {
					public boolean a(Entity entity) {
						return entity instanceof EntitySheep || entity instanceof EntityRabbit;
					}

					@Override
					public boolean apply(Object object) {
						return this.a((Entity) object);
					}
				}));
		this.targetSelector.a(5, new PathfinderGoalNearestAttackableTarget(this, EntitySkeleton.class, false));
		this.setTamed(false);
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.30000001192092896D);
		if (this.isTamed()) {
			this.getAttributeInstance(GenericAttributes.maxHealth).setValue(20.0D);
		} else {
			this.getAttributeInstance(GenericAttributes.maxHealth).setValue(8.0D);
		}

		this.getAttributeMap().b(GenericAttributes.ATTACK_DAMAGE);
		this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(2.0D);
	}

	@Override
	public void setGoalTarget(EntityLiving entityliving) {
		super.setGoalTarget(entityliving);
		if (entityliving == null) {
			this.setAngry(false);
		} else if (!this.isTamed()) {
			this.setAngry(true);
		}

	}

	// CraftBukkit - add overriden version
	@Override
	public void setGoalTarget(EntityLiving entityliving, org.bukkit.event.entity.EntityTargetEvent.TargetReason reason,
			boolean fire) {
		super.setGoalTarget(entityliving, reason, fire);
		if (entityliving == null) {
			this.setAngry(false);
		} else if (!this.isTamed()) {
			this.setAngry(true);
		}
	}
	// CraftBukkit end

	@Override
	protected void E() {
		this.datawatcher.watch(18, this.getHealth());
	}

	@Override
	protected void h() {
		super.h();
		this.datawatcher.a(18, this.getHealth());
		this.datawatcher.a(19, (byte) 0);
		this.datawatcher.a(20, (byte) EnumColor.RED.getColorIndex());
	}

	@Override
	protected void a(BlockPosition blockposition, Block block) {
		this.makeSound("mob.wolf.step", 0.15F, 1.0F);
	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		nbttagcompound.setBoolean("Angry", this.isAngry());
		nbttagcompound.setByte("CollarColor", (byte) this.getCollarColor().getInvColorIndex());
	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		this.setAngry(nbttagcompound.getBoolean("Angry"));
		if (nbttagcompound.hasKeyOfType("CollarColor", 99)) {
			this.setCollarColor(EnumColor.fromInvColorIndex(nbttagcompound.getByte("CollarColor")));
		}

	}

	@Override
	protected String z() {
		// CraftBukkit - (getFloat(18) < 10) -> (getFloat(18) < this.getMaxHealth() / 2)
		return this.isAngry() ? "mob.wolf.growl"
				: (this.random.nextInt(3) == 0
						? (this.isTamed() && this.datawatcher.getFloat(18) < this.getMaxHealth() / 2 ? "mob.wolf.whine"
								: "mob.wolf.panting")
						: "mob.wolf.bark");
	}

	@Override
	protected String bo() {
		return "mob.wolf.hurt";
	}

	@Override
	protected String bp() {
		return "mob.wolf.death";
	}

	@Override
	protected float bB() {
		return 0.4F;
	}

	@Override
	protected Item getLoot() {
		return Item.getById(-1);
	}

	@Override
	public void m() {
		super.m();
		if (!this.world.isClientSide && this.bq && !this.br && !this.cf() && this.onGround) {
			this.br = true;
			this.bs = 0.0F;
			this.bt = 0.0F;
			this.world.broadcastEntityEffect(this, (byte) 8);
		}

		if (!this.world.isClientSide && this.getGoalTarget() == null && this.isAngry()) {
			this.setAngry(false);
		}

	}

	@Override
	public void t_() {
		super.t_();
		float bp = this.bo;
		if (this.cx()) {
			this.bo += (1.0F - this.bo) * 0.4F;
		} else {
			this.bo += (0.0F - this.bo) * 0.4F;
		}

		if (this.U()) {
			this.bq = true;
			this.br = false;
			this.bs = 0.0F;
			this.bt = 0.0F;
		} else if ((this.bq || this.br) && this.br) {
			if (this.bs == 0.0F) {
				this.makeSound("mob.wolf.shake", this.bB(),
						(this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
			}

			this.bt = this.bs;
			this.bs += 0.05F;
			if (this.bt >= 2.0F) {
				this.bq = false;
				this.br = false;
				this.bt = 0.0F;
				this.bs = 0.0F;
			}

			if (this.bs > 0.4F) {
				float f = (float) this.getBoundingBox().b;
				int i = (int) (MathHelper.sin((this.bs - 0.4F) * 3.1415927F) * 7.0F);

				for (int j = 0; j < i; ++j) {
					float f1 = (this.random.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
					float f2 = (this.random.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;

					this.world.addParticle(EnumParticle.WATER_SPLASH, this.locX + f1, f + 0.8F, this.locZ + f2,
							this.motX, this.motY, this.motZ, Constants.EMPTY_ARRAY);
				}
			}
		}

	}

	@Override
	public float getHeadHeight() {
		return this.length * 0.8F;
	}

	@Override
	public int bQ() {
		return this.isSitting() ? 20 : super.bQ();
	}

	@Override
	public boolean damageEntity(DamageSource damagesource, float f) {
		if (this.isInvulnerable(damagesource)) {
			return false;
		} else {
			Entity entity = damagesource.getEntity();

			// CraftBukkit - moved into EntityLiving.d(DamageSource, float)
			// this.bm.setSitting(false);
			if (entity != null && !(entity instanceof EntityHuman) && !(entity instanceof EntityArrow)) {
				f = (f + 1.0F) / 2.0F;
			}

			return super.damageEntity(damagesource, f);
		}
	}

	@Override
	public boolean r(Entity entity) {
		boolean flag = entity.damageEntity(DamageSource.mobAttack(this),
				((int) this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue()));

		if (flag) {
			this.a(this, entity);
		}

		return flag;
	}

	@Override
	public void setTamed(boolean flag) {
		super.setTamed(flag);
		if (flag) {
			this.getAttributeInstance(GenericAttributes.maxHealth).setValue(20.0D);
		} else {
			this.getAttributeInstance(GenericAttributes.maxHealth).setValue(8.0D);
		}

		this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(4.0D);
	}

	@Override
	public boolean a(EntityHuman entityhuman) {
		ItemStack itemstack = entityhuman.inventory.getItemInHand();

		if (this.isTamed()) {
			if (itemstack != null) {
				if (itemstack.getItem() instanceof ItemFood) {
					ItemFood itemfood = (ItemFood) itemstack.getItem();

					if (itemfood.g() && this.datawatcher.getFloat(18) < 20.0F) {
						if (!entityhuman.abilities.canInstantlyBuild) {
							--itemstack.count;
						}

						this.heal(itemfood.getNutrition(itemstack),
								org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason.EATING); // CraftBukkit
						if (itemstack.count <= 0) {
							entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, null);
						}

						return true;
					}
				} else if (itemstack.getItem() == Items.DYE) {
					EnumColor enumcolor = EnumColor.fromInvColorIndex(itemstack.getData());

					if (enumcolor != this.getCollarColor()) {
						this.setCollarColor(enumcolor);
						if (!entityhuman.abilities.canInstantlyBuild && --itemstack.count <= 0) {
							entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, null);
						}

						return true;
					}
				}
			}

			if (this.e((EntityLiving) entityhuman) && !this.world.isClientSide && !this.d(itemstack)) {
				this.bm.setSitting(!this.isSitting());
				this.aY = false;
				this.navigation.n();
				this.setGoalTarget(null, TargetReason.FORGOT_TARGET, true); // CraftBukkit - reason
			}
		} else if (itemstack != null && itemstack.getItem() == Items.BONE && !this.isAngry()) {
			if (!entityhuman.abilities.canInstantlyBuild) {
				--itemstack.count;
			}

			if (itemstack.count <= 0) {
				entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, null);
			}

			if (!this.world.isClientSide) {
				// CraftBukkit - added event call and isCancelled check.
				if (this.random.nextInt(3) == 0
						&& !CraftEventFactory.callEntityTameEvent(this, entityhuman).isCancelled()) {
					this.setTamed(true);
					this.navigation.n();
					this.setGoalTarget(null, TargetReason.FORGOT_TARGET, true);
					this.bm.setSitting(true);
					this.setHealth(this.getMaxHealth()); // CraftBukkit - 20.0 -> getMaxHealth()
					this.setOwnerUUID(entityhuman.getUniqueID().toString());
					this.l(true);
					this.world.broadcastEntityEffect(this, (byte) 7);
				} else {
					this.l(false);
					this.world.broadcastEntityEffect(this, (byte) 6);
				}
			}

			return true;
		}

		return super.a(entityhuman);
	}

	@Override
	public boolean d(ItemStack itemstack) {
		return itemstack == null ? false
				: (!(itemstack.getItem() instanceof ItemFood) ? false : ((ItemFood) itemstack.getItem()).g());
	}

	@Override
	public int bV() {
		return 8;
	}

	public boolean isAngry() {
		return (this.datawatcher.getByte(16) & 2) != 0;
	}

	public void setAngry(boolean flag) {
		byte b0 = this.datawatcher.getByte(16);

		if (flag) {
			this.datawatcher.watch(16, Byte.valueOf((byte) (b0 | 2)));
		} else {
			this.datawatcher.watch(16, Byte.valueOf((byte) (b0 & -3)));
		}

	}

	public EnumColor getCollarColor() {
		return EnumColor.fromInvColorIndex(this.datawatcher.getByte(20) & 15);
	}

	public void setCollarColor(EnumColor enumcolor) {
		this.datawatcher.watch(20, Byte.valueOf((byte) (enumcolor.getInvColorIndex() & 15)));
	}

	public EntityWolf b(EntityAgeable entityageable) {
		EntityWolf entitywolf = new EntityWolf(this.world);
		String s = this.getOwnerUUID();

		if (s != null && s.trim().length() > 0) {
			entitywolf.setOwnerUUID(s);
			entitywolf.setTamed(true);
		}

		return entitywolf;
	}

	public void p(boolean flag) {
		if (flag) {
			this.datawatcher.watch(19, Byte.valueOf((byte) 1));
		} else {
			this.datawatcher.watch(19, Byte.valueOf((byte) 0));
		}

	}

	@Override
	public boolean mate(EntityAnimal entityanimal) {
		if (entityanimal == this) {
			return false;
		} else if (!this.isTamed()) {
			return false;
		} else if (!(entityanimal instanceof EntityWolf)) {
			return false;
		} else {
			EntityWolf entitywolf = (EntityWolf) entityanimal;

			return !entitywolf.isTamed() ? false
					: (entitywolf.isSitting() ? false : this.isInLove() && entitywolf.isInLove());
		}
	}

	public boolean cx() {
		return this.datawatcher.getByte(19) == 1;
	}

	@Override
	protected boolean isTypeNotPersistent() {
		return !this.isTamed() /* && this.ticksLived > 2400 */; // CraftBukkit
	}

	@Override
	public boolean a(EntityLiving entityliving, EntityLiving entityliving1) {
		if (!(entityliving instanceof EntityCreeper) && !(entityliving instanceof EntityGhast)) {
			if (entityliving instanceof EntityWolf) {
				EntityWolf entitywolf = (EntityWolf) entityliving;

				if (entitywolf.isTamed() && entitywolf.getOwner() == entityliving1) {
					return false;
				}
			}

			return entityliving instanceof EntityHuman && entityliving1 instanceof EntityHuman
					&& !((EntityHuman) entityliving1).a((EntityHuman) entityliving) ? false
							: !(entityliving instanceof EntityHorse) || !((EntityHorse) entityliving).isTame();
		} else {
			return false;
		}
	}

	@Override
	public boolean cb() {
		return !this.isAngry() && super.cb();
	}

	@Override
	public EntityAgeable createChild(EntityAgeable entityageable) {
		return this.b(entityageable);
	}
}
