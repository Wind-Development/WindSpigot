package net.minecraft.server;

public class BlockTNT extends Block {

	public static final BlockStateBoolean EXPLODE = BlockStateBoolean.of("explode");

	public BlockTNT() {
		super(Material.TNT);
		this.j(this.blockStateList.getBlockData().set(BlockTNT.EXPLODE, false));
		this.a(CreativeModeTab.d);
	}

	@Override
	public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
		super.onPlace(world, blockposition, iblockdata);
		if (world.isBlockIndirectlyPowered(blockposition)) {
			this.postBreak(world, blockposition, iblockdata.set(BlockTNT.EXPLODE, true));
			world.setAir(blockposition);
		}

	}

	@Override
	public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
		if (world.isBlockIndirectlyPowered(blockposition)) {
			this.postBreak(world, blockposition, iblockdata.set(BlockTNT.EXPLODE, true));
			world.setAir(blockposition);
		}

	}

	@Override
	public void wasExploded(World world, BlockPosition blockposition, Explosion explosion) {
		if (!world.isClientSide) {
			org.bukkit.Location loc = explosion.source instanceof EntityTNTPrimed
					? ((EntityTNTPrimed) explosion.source).sourceLoc
					: new org.bukkit.Location(world.getWorld(), blockposition.getX(), blockposition.getY(),
							blockposition.getZ()); // PaperSpigot
			// PaperSpigot start - Fix cannons
			double y = blockposition.getY();
			if (!world.paperSpigotConfig.fixCannons) {
				y += 0.5;
			}
			EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(loc, world, blockposition.getX() + 0.5F, y,
					blockposition.getZ() + 0.5F, explosion.getSource()); // PaperSpigot - add loc
			// PaperSpigot end

			entitytntprimed.fuseTicks = world.random.nextInt(entitytntprimed.fuseTicks / 4)
					+ entitytntprimed.fuseTicks / 8;
			world.addEntity(entitytntprimed);
		}
	}

	@Override
	public void postBreak(World world, BlockPosition blockposition, IBlockData iblockdata) {
		this.a(world, blockposition, iblockdata, (EnumDirection) null);
	}

	public void a(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving) {
		if (!world.isClientSide) {
			if (iblockdata.get(BlockTNT.EXPLODE).booleanValue()) {
				org.bukkit.Location loc = new org.bukkit.Location(world.getWorld(), blockposition.getX(),
						blockposition.getY(), blockposition.getZ()); // PaperSpigot
				// PaperSpigot start - Fix cannons
				double y = blockposition.getY();
				if (!world.paperSpigotConfig.fixCannons) {
					y += 0.5;
				}
				EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(loc, world, blockposition.getX() + 0.5F, y,
						blockposition.getZ() + 0.5F, entityliving); // PaperSpigot - add loc
				// PaperSpigot end

				world.addEntity(entitytntprimed);
				world.makeSound(entitytntprimed, "game.tnt.primed", 1.0F, 1.0F);
			}

		}
	}

	@Override
	public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman,
			EnumDirection enumdirection, float f, float f1, float f2) {
		if (entityhuman.bZ() != null) {
			Item item = entityhuman.bZ().getItem();

			if (item == Items.FLINT_AND_STEEL || item == Items.FIRE_CHARGE) {
				this.a(world, blockposition, iblockdata.set(BlockTNT.EXPLODE, true),
						(EntityLiving) entityhuman);
				world.setAir(blockposition);
				if (item == Items.FLINT_AND_STEEL) {
					entityhuman.bZ().damage(1, entityhuman);
				} else if (!entityhuman.abilities.canInstantlyBuild) {
					--entityhuman.bZ().count;
				}

				return true;
			}
		}

		return super.interact(world, blockposition, iblockdata, entityhuman, enumdirection, f, f1, f2);
	}

	@Override
	public void a(World world, BlockPosition blockposition, IBlockData iblockdata, Entity entity) {
		if (!world.isClientSide && entity instanceof EntityArrow) {
			EntityArrow entityarrow = (EntityArrow) entity;

			if (entityarrow.isBurning()) {
				// CraftBukkit start
				if (org.bukkit.craftbukkit.event.CraftEventFactory.callEntityChangeBlockEvent(entityarrow,
						blockposition.getX(), blockposition.getY(), blockposition.getZ(), Blocks.AIR, 0)
						.isCancelled()) {
					return;
				}
				// CraftBukkit end
				this.a(world, blockposition, world.getType(blockposition).set(BlockTNT.EXPLODE, true),
						entityarrow.shooter instanceof EntityLiving ? (EntityLiving) entityarrow.shooter : null);
				world.setAir(blockposition);
			}
		}

	}

	@Override
	public boolean a(Explosion explosion) {
		return false;
	}

	@Override
	public IBlockData fromLegacyData(int i) {
		return this.getBlockData().set(BlockTNT.EXPLODE, (i & 1) > 0);
	}

	@Override
	public int toLegacyData(IBlockData iblockdata) {
		return iblockdata.get(BlockTNT.EXPLODE) ? 1 : 0;
	}

	@Override
	protected BlockStateList getStateList() {
		return new BlockStateList(this, BlockTNT.EXPLODE);
	}
}
