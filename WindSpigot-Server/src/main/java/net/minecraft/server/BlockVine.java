package net.minecraft.server;

import java.util.Iterator;
import java.util.Random;

import org.bukkit.craftbukkit.event.CraftEventFactory; // CraftBukkit

public class BlockVine extends Block {

	public static final BlockStateBoolean UP = BlockStateBoolean.of("up");
	public static final BlockStateBoolean NORTH = BlockStateBoolean.of("north");
	public static final BlockStateBoolean EAST = BlockStateBoolean.of("east");
	public static final BlockStateBoolean SOUTH = BlockStateBoolean.of("south");
	public static final BlockStateBoolean WEST = BlockStateBoolean.of("west");
	public static final BlockStateBoolean[] Q = new BlockStateBoolean[] { BlockVine.UP, BlockVine.NORTH,
			BlockVine.SOUTH, BlockVine.WEST, BlockVine.EAST };

	public BlockVine() {
		super(Material.REPLACEABLE_PLANT);
		this.j(this.blockStateList.getBlockData().set(BlockVine.UP, false)
				.set(BlockVine.NORTH, false).set(BlockVine.EAST, false)
				.set(BlockVine.SOUTH, false).set(BlockVine.WEST, false));
		this.a(true);
		this.a(CreativeModeTab.c);
	}

	@Override
	public IBlockData updateState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
		return iblockdata.set(BlockVine.UP, iblockaccess.getType(blockposition.up()).getBlock().u());
	}

	@Override
	public void j() {
		this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public boolean c() {
		return false;
	}

	@Override
	public boolean d() {
		return false;
	}

	@Override
	public boolean a(World world, BlockPosition blockposition) {
		return true;
	}

	@Override
	public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
		float f = 0.0625F;
		float f1 = 1.0F;
		float f2 = 1.0F;
		float f3 = 1.0F;
		float f4 = 0.0F;
		float f5 = 0.0F;
		float f6 = 0.0F;
		boolean flag = false;

		if (iblockaccess.getType(blockposition).get(BlockVine.WEST)) {
			f4 = Math.max(f4, 0.0625F);
			f1 = 0.0F;
			f2 = 0.0F;
			f5 = 1.0F;
			f3 = 0.0F;
			f6 = 1.0F;
			flag = true;
		}

		if (iblockaccess.getType(blockposition).get(BlockVine.EAST)) {
			f1 = Math.min(f1, 0.9375F);
			f4 = 1.0F;
			f2 = 0.0F;
			f5 = 1.0F;
			f3 = 0.0F;
			f6 = 1.0F;
			flag = true;
		}

		if (iblockaccess.getType(blockposition).get(BlockVine.NORTH)) {
			f6 = Math.max(f6, 0.0625F);
			f3 = 0.0F;
			f1 = 0.0F;
			f4 = 1.0F;
			f2 = 0.0F;
			f5 = 1.0F;
			flag = true;
		}

		if (iblockaccess.getType(blockposition).get(BlockVine.SOUTH)) {
			f3 = Math.min(f3, 0.9375F);
			f6 = 1.0F;
			f1 = 0.0F;
			f4 = 1.0F;
			f2 = 0.0F;
			f5 = 1.0F;
			flag = true;
		}

		if (!flag && this.c(iblockaccess.getType(blockposition.up()).getBlock())) {
			f2 = Math.min(f2, 0.9375F);
			f5 = 1.0F;
			f1 = 0.0F;
			f4 = 1.0F;
			f3 = 0.0F;
			f6 = 1.0F;
		}

		this.a(f1, f2, f3, f4, f5, f6);
	}

	@Override
	public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
		return null;
	}

	@Override
	public boolean canPlace(World world, BlockPosition blockposition, EnumDirection enumdirection) {
		switch (BlockVine.SyntheticClass_1.a[enumdirection.ordinal()]) {
		case 1:
			return this.c(world.getType(blockposition.up()).getBlock());

		case 2:
		case 3:
		case 4:
		case 5:
			return this.c(world.getType(blockposition.shift(enumdirection.opposite())).getBlock());

		default:
			return false;
		}
	}

	private boolean c(Block block) {
		return block.d() && block.material.isSolid();
	}

	private boolean e(World world, BlockPosition blockposition, IBlockData iblockdata) {
		IBlockData iblockdata1 = iblockdata;
		Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

		while (iterator.hasNext()) {
			EnumDirection enumdirection = (EnumDirection) iterator.next();
			BlockStateBoolean blockstateboolean = getDirection(enumdirection);

			if (iblockdata.get(blockstateboolean)
					&& !this.c(world.getType(blockposition.shift(enumdirection)).getBlock())) {
				IBlockData iblockdata2 = world.getType(blockposition.up());

				if (iblockdata2.getBlock() != this || !iblockdata2.get(blockstateboolean)) {
					iblockdata = iblockdata.set(blockstateboolean, false);
				}
			}
		}

		if (d(iblockdata) == 0) {
			return false;
		} else {
			if (iblockdata1 != iblockdata) {
				world.setTypeAndData(blockposition, iblockdata, 2);
			}

			return true;
		}
	}

	@Override
	public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
		if (!world.isClientSide && !this.e(world, blockposition, iblockdata)) {
			this.b(world, blockposition, iblockdata, 0);
			world.setAir(blockposition);
		}

	}

	@Override
	public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
		if (!world.isClientSide) {
			if (world.random.nextInt(4) == 0) {
				byte b0 = 4;
				int i = 5;
				boolean flag = false;

				label188: for (int j = -b0; j <= b0; ++j) {
					for (int k = -b0; k <= b0; ++k) {
						for (int l = -1; l <= 1; ++l) {
							if (world.getType(blockposition.a(j, l, k)).getBlock() == this) {
								--i;
								if (i <= 0) {
									flag = true;
									break label188;
								}
							}
						}
					}
				}

				EnumDirection enumdirection = EnumDirection.a(random);
				BlockPosition blockposition1 = blockposition.up();
				EnumDirection enumdirection1;

				if (enumdirection == EnumDirection.UP && blockposition.getY() < 255 && world.isEmpty(blockposition1)) {
					if (!flag) {
						IBlockData iblockdata1 = iblockdata;
						Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

						while (iterator.hasNext()) {
							enumdirection1 = (EnumDirection) iterator.next();
							if (random.nextBoolean()
									|| !this.c(world.getType(blockposition1.shift(enumdirection1)).getBlock())) {
								iblockdata1 = iblockdata1.set(getDirection(enumdirection1), false);
							}
						}

						if (iblockdata1.get(BlockVine.NORTH)
								|| iblockdata1.get(BlockVine.EAST)
								|| iblockdata1.get(BlockVine.SOUTH)
								|| iblockdata1.get(BlockVine.WEST)) {
							// CraftBukkit start - Call BlockSpreadEvent
							// world.setTypeAndData(blockposition1, iblockdata1, 2);
							BlockPosition target = blockposition1;
							org.bukkit.block.Block source = world.getWorld().getBlockAt(blockposition.getX(),
									blockposition.getY(), blockposition.getZ());
							org.bukkit.block.Block block = world.getWorld().getBlockAt(target.getX(), target.getY(),
									target.getZ());
							CraftEventFactory.handleBlockSpreadEvent(block, source, this, toLegacyData(iblockdata1));
							// CraftBukkit end
						}

					}
				} else {
					BlockPosition blockposition2;

					if (enumdirection.k().c() && !iblockdata.get(getDirection(enumdirection))) {
						if (!flag) {
							blockposition2 = blockposition.shift(enumdirection);
							Block block = world.getType(blockposition2).getBlock();

							if (block.material == Material.AIR) {
								enumdirection1 = enumdirection.e();
								EnumDirection enumdirection2 = enumdirection.f();
								boolean flag1 = iblockdata.get(getDirection(enumdirection1));
								boolean flag2 = iblockdata.get(getDirection(enumdirection2));
								BlockPosition blockposition3 = blockposition2.shift(enumdirection1);
								BlockPosition blockposition4 = blockposition2.shift(enumdirection2);

								// CraftBukkit start - Call BlockSpreadEvent
								org.bukkit.block.Block source = world.getWorld().getBlockAt(blockposition.getX(),
										blockposition.getY(), blockposition.getZ());
								org.bukkit.block.Block bukkitBlock = world.getWorld().getBlockAt(blockposition2.getX(),
										blockposition2.getY(), blockposition2.getZ());

								if (flag1 && this.c(world.getType(blockposition3).getBlock())) {
									// world.setTypeAndData(blockposition2,
									// this.getBlockData().set(a(enumdirection1), true), 2);
									CraftEventFactory.handleBlockSpreadEvent(bukkitBlock, source, this,
											toLegacyData(this.getBlockData().set(getDirection(enumdirection1),
													true)));
								} else if (flag2 && this.c(world.getType(blockposition4).getBlock())) {
									// world.setTypeAndData(blockposition2,
									// this.getBlockData().set(a(enumdirection2), true), 2);
									CraftEventFactory.handleBlockSpreadEvent(bukkitBlock, source, this,
											toLegacyData(this.getBlockData().set(getDirection(enumdirection2),
													true)));
								} else if (flag1 && world.isEmpty(blockposition3)
										&& this.c(world.getType(blockposition.shift(enumdirection1)).getBlock())) {
									// world.setTypeAndData(blockposition3,
									// this.getBlockData().set(a(enumdirection.opposite()), true),
									// 2);
									bukkitBlock = world.getWorld().getBlockAt(blockposition3.getX(),
											blockposition3.getY(), blockposition3.getZ());
									CraftEventFactory.handleBlockSpreadEvent(bukkitBlock, source, this,
											toLegacyData(this.getBlockData().set(getDirection(enumdirection.opposite()),
													true)));
								} else if (flag2 && world.isEmpty(blockposition4)
										&& this.c(world.getType(blockposition.shift(enumdirection2)).getBlock())) {
									// world.setTypeAndData(blockposition4,
									// this.getBlockData().set(a(enumdirection.opposite()), true),
									// 2);
									bukkitBlock = world.getWorld().getBlockAt(blockposition4.getX(),
											blockposition4.getY(), blockposition4.getZ());
									CraftEventFactory.handleBlockSpreadEvent(bukkitBlock, source, this,
											toLegacyData(this.getBlockData().set(getDirection(enumdirection.opposite()),
													true)));
								} else if (this.c(world.getType(blockposition2.up()).getBlock())) {
									// world.setTypeAndData(blockposition2, this.getBlockData(), 2);
									CraftEventFactory.handleBlockSpreadEvent(bukkitBlock, source, this,
											toLegacyData(this.getBlockData()));
								}
								// CraftBukkit end
							} else if (block.material.k() && block.d()) {
								world.setTypeAndData(blockposition,
										iblockdata.set(getDirection(enumdirection), true), 2);
							}

						}
					} else if (blockposition.getY() > 1) {
						blockposition2 = blockposition.down();
						IBlockData iblockdata2 = world.getType(blockposition2);
						Block block1 = iblockdata2.getBlock();
						IBlockData iblockdata3;
						Iterator iterator1;
						EnumDirection enumdirection3;

						if (block1.material == Material.AIR) {
							iblockdata3 = iblockdata;
							iterator1 = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

							while (iterator1.hasNext()) {
								enumdirection3 = (EnumDirection) iterator1.next();
								if (random.nextBoolean()) {
									iblockdata3 = iblockdata3.set(getDirection(enumdirection3), false);
								}
							}

							if (iblockdata3.get(BlockVine.NORTH)
									|| iblockdata3.get(BlockVine.EAST)
									|| iblockdata3.get(BlockVine.SOUTH)
									|| iblockdata3.get(BlockVine.WEST)) {
								// CraftBukkit start - Call BlockSpreadEvent
								// world.setTypeAndData(blockposition2, iblockdata3, 2);
								org.bukkit.block.Block source = world.getWorld().getBlockAt(blockposition.getX(),
										blockposition.getY(), blockposition.getZ());
								org.bukkit.block.Block bukkitBlock = world.getWorld().getBlockAt(blockposition2.getX(),
										blockposition2.getY(), blockposition2.getZ());
								CraftEventFactory.handleBlockSpreadEvent(bukkitBlock, source, this,
										toLegacyData(iblockdata3));
								// CraftBukkit end
							}
						} else if (block1 == this) {
							iblockdata3 = iblockdata2;
							iterator1 = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

							while (iterator1.hasNext()) {
								enumdirection3 = (EnumDirection) iterator1.next();
								BlockStateBoolean blockstateboolean = getDirection(enumdirection3);

								if (random.nextBoolean() && iblockdata.get(blockstateboolean)) {
									iblockdata3 = iblockdata3.set(blockstateboolean, true);
								}
							}

							if (iblockdata3.get(BlockVine.NORTH)
									|| iblockdata3.get(BlockVine.EAST)
									|| iblockdata3.get(BlockVine.SOUTH)
									|| iblockdata3.get(BlockVine.WEST)) {
								world.setTypeAndData(blockposition2, iblockdata3, 2);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f,
			float f1, float f2, int i, EntityLiving entityliving) {
		IBlockData iblockdata = this.getBlockData().set(BlockVine.UP, false)
				.set(BlockVine.NORTH, false).set(BlockVine.EAST, false)
				.set(BlockVine.SOUTH, false).set(BlockVine.WEST, false);

		return enumdirection.k().c() ? iblockdata.set(getDirection(enumdirection.opposite()), true)
				: iblockdata;
	}

	@Override
	public Item getDropType(IBlockData iblockdata, Random random, int i) {
		return null;
	}

	@Override
	public int a(Random random) {
		return 0;
	}

	@Override
	public void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata,
			TileEntity tileentity) {
		if (!world.isClientSide && entityhuman.bZ() != null && entityhuman.bZ().getItem() == Items.SHEARS) {
			entityhuman.b(StatisticList.MINE_BLOCK_COUNT[Block.getId(this)]);
			a(world, blockposition, new ItemStack(Blocks.VINE, 1, 0));
		} else {
			super.a(world, entityhuman, blockposition, iblockdata, tileentity);
		}

	}

	@Override
	public IBlockData fromLegacyData(int i) {
		return this.getBlockData().set(BlockVine.SOUTH, (i & 1) > 0)
				.set(BlockVine.WEST, (i & 2) > 0).set(BlockVine.NORTH, (i & 4) > 0)
				.set(BlockVine.EAST, (i & 8) > 0);
	}

	@Override
	public int toLegacyData(IBlockData iblockdata) {
		int i = 0;

		if (iblockdata.get(BlockVine.SOUTH)) {
			i |= 1;
		}

		if (iblockdata.get(BlockVine.WEST)) {
			i |= 2;
		}

		if (iblockdata.get(BlockVine.NORTH)) {
			i |= 4;
		}

		if (iblockdata.get(BlockVine.EAST).booleanValue()) {
			i |= 8;
		}

		return i;
	}

	@Override
	protected BlockStateList getStateList() {
		return new BlockStateList(this,
				BlockVine.UP, BlockVine.NORTH, BlockVine.EAST, BlockVine.SOUTH, BlockVine.WEST);
	}

	public static BlockStateBoolean getDirection(EnumDirection enumdirection) {
		switch (BlockVine.SyntheticClass_1.a[enumdirection.ordinal()]) {
		case 1:
			return BlockVine.UP;

		case 2:
			return BlockVine.NORTH;

		case 3:
			return BlockVine.SOUTH;

		case 4:
			return BlockVine.EAST;

		case 5:
			return BlockVine.WEST;

		default:
			throw new IllegalArgumentException(enumdirection + " is an invalid choice");
		}
	}

	public static int d(IBlockData iblockdata) {
		int i = 0;
		BlockStateBoolean[] ablockstateboolean = BlockVine.Q;
		int j = ablockstateboolean.length;

		for (int k = 0; k < j; ++k) {
			BlockStateBoolean blockstateboolean = ablockstateboolean[k];

			if (iblockdata.get(blockstateboolean).booleanValue()) {
				++i;
			}
		}

		return i;
	}

	static class SyntheticClass_1 {

		static final int[] a = new int[EnumDirection.values().length];

		static {
			try {
				BlockVine.SyntheticClass_1.a[EnumDirection.UP.ordinal()] = 1;
			} catch (NoSuchFieldError nosuchfielderror) {
				;
			}

			try {
				BlockVine.SyntheticClass_1.a[EnumDirection.NORTH.ordinal()] = 2;
			} catch (NoSuchFieldError nosuchfielderror1) {
				;
			}

			try {
				BlockVine.SyntheticClass_1.a[EnumDirection.SOUTH.ordinal()] = 3;
			} catch (NoSuchFieldError nosuchfielderror2) {
				;
			}

			try {
				BlockVine.SyntheticClass_1.a[EnumDirection.EAST.ordinal()] = 4;
			} catch (NoSuchFieldError nosuchfielderror3) {
				;
			}

			try {
				BlockVine.SyntheticClass_1.a[EnumDirection.WEST.ordinal()] = 5;
			} catch (NoSuchFieldError nosuchfielderror4) {
				;
			}

		}
	}
}
