package net.minecraft.server;

import java.util.Random;

public class EntitySilverfish extends EntityMonster {

	private EntitySilverfish.PathfinderGoalSilverfishWakeOthers a;

	public EntitySilverfish(World world) {
		super(world);
		this.setSize(0.4F, 0.3F);
		this.goalSelector.a(1, new PathfinderGoalFloat(this));
		this.goalSelector.a(3, this.a = new EntitySilverfish.PathfinderGoalSilverfishWakeOthers(this));
		this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, EntityHuman.class, 1.0D, false));
		this.goalSelector.a(5, new EntitySilverfish.PathfinderGoalSilverfishHideInBlock(this));
		this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, true, new Class[0]));
		this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));
	}

	@Override
	public double am() {
		return 0.2D;
	}

	@Override
	public float getHeadHeight() {
		return 0.1F;
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(GenericAttributes.maxHealth).setValue(8.0D);
		this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.25D);
		this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(1.0D);
	}

	@Override
	protected boolean s_() {
		return false;
	}

	@Override
	protected String z() {
		return "mob.silverfish.say";
	}

	@Override
	protected String bo() {
		return "mob.silverfish.hit";
	}

	@Override
	protected String bp() {
		return "mob.silverfish.kill";
	}

	@Override
	public boolean damageEntity(DamageSource damagesource, float f) {
		if (this.isInvulnerable(damagesource)) {
			return false;
		} else {
			if (damagesource instanceof EntityDamageSource || damagesource == DamageSource.MAGIC) {
				this.a.f();
			}

			return super.damageEntity(damagesource, f);
		}
	}

	@Override
	protected void a(BlockPosition blockposition, Block block) {
		this.makeSound("mob.silverfish.step", 0.15F, 1.0F);
	}

	@Override
	protected Item getLoot() {
		return null;
	}

	@Override
	public void t_() {
		this.aI = this.yaw;
		super.t_();
	}

	@Override
	public float a(BlockPosition blockposition) {
		return this.world.getType(blockposition.down()).getBlock() == Blocks.STONE ? 10.0F : super.a(blockposition);
	}

	@Override
	protected boolean n_() {
		return true;
	}

	@Override
	public boolean bR() {
		if (super.bR()) {
			EntityHuman entityhuman = this.world.findNearbyPlayer(this, 5.0D);

			return entityhuman == null;
		} else {
			return false;
		}
	}

	@Override
	public EnumMonsterType getMonsterType() {
		return EnumMonsterType.ARTHROPOD;
	}

	static class PathfinderGoalSilverfishHideInBlock extends PathfinderGoalRandomStroll {

		private final EntitySilverfish silverfish;
		private EnumDirection b;
		private boolean c;

		public PathfinderGoalSilverfishHideInBlock(EntitySilverfish entitysilverfish) {
			super(entitysilverfish, 1.0D, 10);
			this.silverfish = entitysilverfish;
			this.a(1);
		}

		@Override
		public boolean a() {
			if (this.silverfish.getGoalTarget() != null) {
				return false;
			} else if (!this.silverfish.getNavigation().m()) {
				return false;
			} else {
				Random random = this.silverfish.bc();

				if (random.nextInt(10) == 0) {
					this.b = EnumDirection.a(random);
					BlockPosition blockposition = (new BlockPosition(this.silverfish.locX, this.silverfish.locY + 0.5D,
							this.silverfish.locZ)).shift(this.b);
					IBlockData iblockdata = this.silverfish.world.getType(blockposition);

					if (BlockMonsterEggs.d(iblockdata)) {
						this.c = true;
						return true;
					}
				}

				this.c = false;
				return super.a();
			}
		}

		@Override
		public boolean b() {
			return this.c ? false : super.b();
		}

		@Override
		public void c() {
			if (!this.c) {
				super.c();
			} else {
				World world = this.silverfish.world;
				BlockPosition blockposition = (new BlockPosition(this.silverfish.locX, this.silverfish.locY + 0.5D,
						this.silverfish.locZ)).shift(this.b);
				IBlockData iblockdata = world.getType(blockposition);

				if (BlockMonsterEggs.d(iblockdata)) {
					// CraftBukkit start
					if (org.bukkit.craftbukkit.event.CraftEventFactory
							.callEntityChangeBlockEvent(this.silverfish, blockposition.getX(), blockposition.getY(),
									blockposition.getZ(), Blocks.MONSTER_EGG,
									Block.getId(Block.getById(iblockdata.getBlock().toLegacyData(iblockdata))))
							.isCancelled()) {
						return;
					}
					// CraftBukkit end
					world.setTypeAndData(blockposition, Blocks.MONSTER_EGG.getBlockData().set(BlockMonsterEggs.VARIANT,
							BlockMonsterEggs.EnumMonsterEggVarient.a(iblockdata)), 3);
					this.silverfish.y();
					this.silverfish.die();
				}

			}
		}
	}

	static class PathfinderGoalSilverfishWakeOthers extends PathfinderGoal {

		private EntitySilverfish silverfish;
		private int b;

		public PathfinderGoalSilverfishWakeOthers(EntitySilverfish entitysilverfish) {
			this.silverfish = entitysilverfish;
		}

		public void f() {
			if (this.b == 0) {
				this.b = 20;
			}

		}

		@Override
		public boolean a() {
			return this.b > 0;
		}

		@Override
		public void e() {
			--this.b;
			if (this.b <= 0) {
				World world = this.silverfish.world;
				Random random = this.silverfish.bc();
				BlockPosition blockposition = new BlockPosition(this.silverfish);

				for (int i = 0; i <= 5 && i >= -5; i = i <= 0 ? 1 - i : 0 - i) {
					for (int j = 0; j <= 10 && j >= -10; j = j <= 0 ? 1 - j : 0 - j) {
						for (int k = 0; k <= 10 && k >= -10; k = k <= 0 ? 1 - k : 0 - k) {
							BlockPosition blockposition1 = blockposition.a(j, i, k);
							IBlockData iblockdata = world.getType(blockposition1);

							if (iblockdata.getBlock() == Blocks.MONSTER_EGG) {
								// CraftBukkit start
								if (org.bukkit.craftbukkit.event.CraftEventFactory
										.callEntityChangeBlockEvent(this.silverfish, blockposition1.getX(),
												blockposition1.getY(), blockposition1.getZ(), Blocks.AIR, 0)
										.isCancelled()) {
									continue;
								}
								// CraftBukkit end
								if (world.getGameRules().getBoolean("mobGriefing")) {
									world.setAir(blockposition1, true);
								} else {
									world.setTypeAndData(blockposition1, iblockdata.get(BlockMonsterEggs.VARIANT).d(),
											3);
								}

								if (random.nextBoolean()) {
									return;
								}
							}
						}
					}
				}
			}

		}
	}
}
