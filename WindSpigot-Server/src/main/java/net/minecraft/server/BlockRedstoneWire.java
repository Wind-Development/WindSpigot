package net.minecraft.server;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import org.bukkit.event.block.BlockRedstoneEvent; // CraftBukkit

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class BlockRedstoneWire extends Block {

	public static final BlockStateEnum<BlockRedstoneWire.EnumRedstoneWireConnection> NORTH = BlockStateEnum.of("north",
			BlockRedstoneWire.EnumRedstoneWireConnection.class);
	public static final BlockStateEnum<BlockRedstoneWire.EnumRedstoneWireConnection> EAST = BlockStateEnum.of("east",
			BlockRedstoneWire.EnumRedstoneWireConnection.class);
	public static final BlockStateEnum<BlockRedstoneWire.EnumRedstoneWireConnection> SOUTH = BlockStateEnum.of("south",
			BlockRedstoneWire.EnumRedstoneWireConnection.class);
	public static final BlockStateEnum<BlockRedstoneWire.EnumRedstoneWireConnection> WEST = BlockStateEnum.of("west",
			BlockRedstoneWire.EnumRedstoneWireConnection.class);
	public static final BlockStateInteger POWER = BlockStateInteger.of("power", 0, 15);
	private boolean Q = true;
	private final Set<BlockPosition> R = Sets.newHashSet();

	public BlockRedstoneWire() {
		super(Material.ORIENTABLE);
		this.j(this.blockStateList.getBlockData()
				.set(BlockRedstoneWire.NORTH, BlockRedstoneWire.EnumRedstoneWireConnection.NONE)
				.set(BlockRedstoneWire.EAST, BlockRedstoneWire.EnumRedstoneWireConnection.NONE)
				.set(BlockRedstoneWire.SOUTH, BlockRedstoneWire.EnumRedstoneWireConnection.NONE)
				.set(BlockRedstoneWire.WEST, BlockRedstoneWire.EnumRedstoneWireConnection.NONE)
				.set(BlockRedstoneWire.POWER, Integer.valueOf(0)));
		this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
	}

	@Override
	public IBlockData updateState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
		iblockdata = iblockdata.set(BlockRedstoneWire.WEST, this.c(iblockaccess, blockposition, EnumDirection.WEST));
		iblockdata = iblockdata.set(BlockRedstoneWire.EAST, this.c(iblockaccess, blockposition, EnumDirection.EAST));
		iblockdata = iblockdata.set(BlockRedstoneWire.NORTH, this.c(iblockaccess, blockposition, EnumDirection.NORTH));
		iblockdata = iblockdata.set(BlockRedstoneWire.SOUTH, this.c(iblockaccess, blockposition, EnumDirection.SOUTH));
		return iblockdata;
	}

	private BlockRedstoneWire.EnumRedstoneWireConnection c(IBlockAccess iblockaccess, BlockPosition blockposition,
			EnumDirection enumdirection) {
		BlockPosition blockposition1 = blockposition.shift(enumdirection);
		Block block = iblockaccess.getType(blockposition.shift(enumdirection)).getBlock();

		if (!a(iblockaccess.getType(blockposition1), enumdirection)
				&& (block.u() || !d(iblockaccess.getType(blockposition1.down())))) {
			Block block1 = iblockaccess.getType(blockposition.up()).getBlock();

			return !block1.u() && block.u() && d(iblockaccess.getType(blockposition1.up()))
					? BlockRedstoneWire.EnumRedstoneWireConnection.UP
					: BlockRedstoneWire.EnumRedstoneWireConnection.NONE;
		} else {
			return BlockRedstoneWire.EnumRedstoneWireConnection.SIDE;
		}
	}

	@Override
	public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
		return null;
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
	public boolean canPlace(World world, BlockPosition blockposition) {
		return World.a(world, blockposition.down())
				|| world.getType(blockposition.down()).getBlock() == Blocks.GLOWSTONE;
	}

	private IBlockData e(World world, BlockPosition blockposition, IBlockData iblockdata) {
		iblockdata = this.a(world, blockposition, blockposition, iblockdata);
		ArrayList arraylist = Lists.newArrayList(this.R);

		this.R.clear();
		Iterator iterator = arraylist.iterator();

		while (iterator.hasNext()) {
			BlockPosition blockposition1 = (BlockPosition) iterator.next();

			world.applyPhysics(blockposition1, this);
		}

		return iblockdata;
	}

	private IBlockData a(World world, BlockPosition blockposition, BlockPosition blockposition1,
			IBlockData iblockdata) {
		IBlockData iblockdata1 = iblockdata;
		int i = iblockdata.get(BlockRedstoneWire.POWER).intValue();
		byte b0 = 0;
		int j = this.getPower(world, blockposition1, b0);

		this.Q = false;
		int k = world.A(blockposition);

		this.Q = true;
		if (k > 0 && k > j - 1) {
			j = k;
		}

		int l = 0;
		Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

		while (iterator.hasNext()) {
			EnumDirection enumdirection = (EnumDirection) iterator.next();
			BlockPosition blockposition2 = blockposition.shift(enumdirection);
			boolean flag = blockposition2.getX() != blockposition1.getX()
					|| blockposition2.getZ() != blockposition1.getZ();

			if (flag) {
				l = this.getPower(world, blockposition2, l);
			}

			if (world.getType(blockposition2).getBlock().isOccluding()
					&& !world.getType(blockposition.up()).getBlock().isOccluding()) {
				if (flag && blockposition.getY() >= blockposition1.getY()) {
					l = this.getPower(world, blockposition2.up(), l);
				}
			} else if (!world.getType(blockposition2).getBlock().isOccluding() && flag
					&& blockposition.getY() <= blockposition1.getY()) {
				l = this.getPower(world, blockposition2.down(), l);
			}
		}

		if (l > j) {
			j = l - 1;
		} else if (j > 0) {
			--j;
		} else {
			j = 0;
		}

		if (k > j - 1) {
			j = k;
		}

		// CraftBukkit start
		if (i != j) {
			BlockRedstoneEvent event = new BlockRedstoneEvent(
					world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()), i,
					j);
			world.getServer().getPluginManager().callEvent(event);

			j = event.getNewCurrent();
		}
		// CraftBukkit end

		if (i != j) {
			iblockdata = iblockdata.set(BlockRedstoneWire.POWER, Integer.valueOf(j));
			if (world.getType(blockposition) == iblockdata1) {
				world.setTypeAndData(blockposition, iblockdata, 2);
			}

			this.R.add(blockposition);
			// PaperSpigot start - Fix cannons
			if (world.paperSpigotConfig.fixCannons) {
				this.R.add(blockposition.shift(EnumDirection.WEST));
				this.R.add(blockposition.shift(EnumDirection.EAST));
				this.R.add(blockposition.shift(EnumDirection.DOWN));
				this.R.add(blockposition.shift(EnumDirection.UP));
				this.R.add(blockposition.shift(EnumDirection.NORTH));
				this.R.add(blockposition.shift(EnumDirection.SOUTH));
				return iblockdata;
			}
			// PaperSpigot end
			EnumDirection[] aenumdirection = EnumDirection.values();
			int i1 = aenumdirection.length;

			for (int j1 = 0; j1 < i1; ++j1) {
				EnumDirection enumdirection1 = aenumdirection[j1];

				this.R.add(blockposition.shift(enumdirection1));
			}
		}

		return iblockdata;
	}

	private void e(World world, BlockPosition blockposition) {
		if (world.getType(blockposition).getBlock() == this) {
			world.applyPhysics(blockposition, this);
			// PaperSpigot start - Fix cannons
			if (world.paperSpigotConfig.fixCannons) {
				world.applyPhysics(blockposition.shift(EnumDirection.WEST), this);
				world.applyPhysics(blockposition.shift(EnumDirection.EAST), this);
				world.applyPhysics(blockposition.shift(EnumDirection.NORTH), this);
				world.applyPhysics(blockposition.shift(EnumDirection.SOUTH), this);
				world.applyPhysics(blockposition.shift(EnumDirection.DOWN), this);
				world.applyPhysics(blockposition.shift(EnumDirection.UP), this);
				return;
			}
			// PaperSpigot end
			EnumDirection[] aenumdirection = EnumDirection.values();
			int i = aenumdirection.length;

			for (int j = 0; j < i; ++j) {
				EnumDirection enumdirection = aenumdirection[j];

				world.applyPhysics(blockposition.shift(enumdirection), this);
			}

		}
	}

	@Override
	public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
		if (!world.isClientSide) {
			this.e(world, blockposition, iblockdata);
			Iterator iterator = EnumDirection.EnumDirectionLimit.VERTICAL.iterator();

			EnumDirection enumdirection;

			while (iterator.hasNext()) {
				enumdirection = (EnumDirection) iterator.next();
				world.applyPhysics(blockposition.shift(enumdirection), this);
			}

			iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

			while (iterator.hasNext()) {
				enumdirection = (EnumDirection) iterator.next();
				this.e(world, blockposition.shift(enumdirection));
			}

			iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

			while (iterator.hasNext()) {
				enumdirection = (EnumDirection) iterator.next();
				BlockPosition blockposition1 = blockposition.shift(enumdirection);

				if (world.getType(blockposition1).getBlock().isOccluding()) {
					this.e(world, blockposition1.up());
				} else {
					this.e(world, blockposition1.down());
				}
			}

		}
	}

	@Override
	public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
		super.remove(world, blockposition, iblockdata);
		if (!world.isClientSide) {
			EnumDirection[] aenumdirection = EnumDirection.values();
			int i = aenumdirection.length;

			for (int j = 0; j < i; ++j) {
				EnumDirection enumdirection = aenumdirection[j];

				world.applyPhysics(blockposition.shift(enumdirection), this);
			}

			this.e(world, blockposition, iblockdata);
			Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

			EnumDirection enumdirection1;

			while (iterator.hasNext()) {
				enumdirection1 = (EnumDirection) iterator.next();
				this.e(world, blockposition.shift(enumdirection1));
			}

			iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

			while (iterator.hasNext()) {
				enumdirection1 = (EnumDirection) iterator.next();
				BlockPosition blockposition1 = blockposition.shift(enumdirection1);

				if (world.getType(blockposition1).getBlock().isOccluding()) {
					this.e(world, blockposition1.up());
				} else {
					this.e(world, blockposition1.down());
				}
			}

		}
	}

	public int getPower(World world, BlockPosition blockposition, int i) {
		if (world.getType(blockposition).getBlock() != this) {
			return i;
		} else {
			int j = world.getType(blockposition).get(BlockRedstoneWire.POWER).intValue();

			return j > i ? j : i;
		}
	}

	@Override
	public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
		if (!world.isClientSide) {
			if (this.canPlace(world, blockposition)) {
				this.e(world, blockposition, iblockdata);
			} else {
				this.b(world, blockposition, iblockdata, 0);
				world.setAir(blockposition);
			}

		}
	}

	@Override
	public Item getDropType(IBlockData iblockdata, Random random, int i) {
		return Items.REDSTONE;
	}

	@Override
	public int b(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata,
			EnumDirection enumdirection) {
		return !this.Q ? 0 : this.a(iblockaccess, blockposition, iblockdata, enumdirection);
	}

	@Override
	public int a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata,
			EnumDirection enumdirection) {
		if (!this.Q) {
			return 0;
		} else {
			int i = iblockdata.get(BlockRedstoneWire.POWER).intValue();

			if (i == 0) {
				return 0;
			} else if (enumdirection == EnumDirection.UP) {
				return i;
			} else {
				EnumSet enumset = EnumSet.noneOf(EnumDirection.class);
				Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

				while (iterator.hasNext()) {
					EnumDirection enumdirection1 = (EnumDirection) iterator.next();

					if (this.d(iblockaccess, blockposition, enumdirection1)) {
						enumset.add(enumdirection1);
					}
				}

				if (enumdirection.k().c() && enumset.isEmpty()) {
					return i;
				} else if (enumset.contains(enumdirection) && !enumset.contains(enumdirection.f())
						&& !enumset.contains(enumdirection.e())) {
					return i;
				} else {
					return 0;
				}
			}
		}
	}

	private boolean d(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
		BlockPosition blockposition1 = blockposition.shift(enumdirection);
		IBlockData iblockdata = iblockaccess.getType(blockposition1);
		Block block = iblockdata.getBlock();
		boolean flag = block.isOccluding();
		boolean flag1 = iblockaccess.getType(blockposition.up()).getBlock().isOccluding();

		return !flag1 && flag && e(iblockaccess, blockposition1.up()) || (a(iblockdata, enumdirection) || (block == Blocks.POWERED_REPEATER && iblockdata.get(BlockDirectional.FACING) == enumdirection || !flag && e(iblockaccess, blockposition1.down())));
	}

	protected static boolean e(IBlockAccess iblockaccess, BlockPosition blockposition) {
		return d(iblockaccess.getType(blockposition));
	}

	protected static boolean d(IBlockData iblockdata) {
		return a(iblockdata, (EnumDirection) null);
	}

	protected static boolean a(IBlockData iblockdata, EnumDirection enumdirection) {
		Block block = iblockdata.getBlock();

		if (block == Blocks.REDSTONE_WIRE) {
			return true;
		} else if (Blocks.UNPOWERED_REPEATER.e(block)) {
			EnumDirection enumdirection1 = iblockdata.get(BlockDirectional.FACING);

			return enumdirection1 == enumdirection || enumdirection1.opposite() == enumdirection;
		} else {
			return block.isPowerSource() && enumdirection != null;
		}
	}

	@Override
	public boolean isPowerSource() {
		return this.Q;
	}

	@Override
	public IBlockData fromLegacyData(int i) {
		return this.getBlockData().set(BlockRedstoneWire.POWER, Integer.valueOf(i));
	}

	@Override
	public int toLegacyData(IBlockData iblockdata) {
		return iblockdata.get(BlockRedstoneWire.POWER).intValue();
	}

	@Override
	protected BlockStateList getStateList() {
		return new BlockStateList(this, new IBlockState[] { BlockRedstoneWire.NORTH, BlockRedstoneWire.EAST,
				BlockRedstoneWire.SOUTH, BlockRedstoneWire.WEST, BlockRedstoneWire.POWER });
	}

	enum EnumRedstoneWireConnection implements INamable {

		UP("up"), SIDE("side"), NONE("none");

		private final String d;

		EnumRedstoneWireConnection(String s) {
			this.d = s;
		}

		@Override
		public String toString() {
			return this.getName();
		}

		@Override
		public String getName() {
			return this.d;
		}
	}
}
