package net.minecraft.server;

import java.util.List;
import java.util.Random;

import org.bukkit.event.block.BlockRedstoneEvent; // CraftBukkit

import com.google.common.base.Predicate;

public class BlockMinecartDetector extends BlockMinecartTrackAbstract {

	public static final BlockStateEnum<BlockMinecartTrackAbstract.EnumTrackPosition> SHAPE = BlockStateEnum.a("shape",
			BlockMinecartTrackAbstract.EnumTrackPosition.class, new Predicate() {
				public boolean a(
						BlockMinecartTrackAbstract.EnumTrackPosition blockminecarttrackabstract_enumtrackposition) {
					return blockminecarttrackabstract_enumtrackposition != BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_EAST
							&& blockminecarttrackabstract_enumtrackposition != BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_WEST
							&& blockminecarttrackabstract_enumtrackposition != BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_EAST
							&& blockminecarttrackabstract_enumtrackposition != BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_WEST;
				}

				@Override
				public boolean apply(Object object) {
					return this.a((BlockMinecartTrackAbstract.EnumTrackPosition) object);
				}
			});
	public static final BlockStateBoolean POWERED = BlockStateBoolean.of("powered");

	public BlockMinecartDetector() {
		super(true);
		this.j(this.blockStateList.getBlockData().set(BlockMinecartDetector.POWERED, false)
				.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_SOUTH));
		this.a(true);
	}

	@Override
	public int a(World world) {
		return 20;
	}

	@Override
	public boolean isPowerSource() {
		return true;
	}

	@Override
	public void a(World world, BlockPosition blockposition, IBlockData iblockdata, Entity entity) {
		if (!world.isClientSide) {
			if (!iblockdata.get(BlockMinecartDetector.POWERED).booleanValue()) {
				this.e(world, blockposition, iblockdata);
			}
		}
	}

	@Override
	public void a(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
	}

	@Override
	public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
		if (!world.isClientSide && iblockdata.get(BlockMinecartDetector.POWERED).booleanValue()) {
			this.e(world, blockposition, iblockdata);
		}
	}

	@Override
	public int a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata,
			EnumDirection enumdirection) {
		return iblockdata.get(BlockMinecartDetector.POWERED).booleanValue() ? 15 : 0;
	}

	@Override
	public int b(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata,
			EnumDirection enumdirection) {
		return !iblockdata.get(BlockMinecartDetector.POWERED).booleanValue() ? 0
				: (enumdirection == EnumDirection.UP ? 15 : 0);
	}

	private void e(World world, BlockPosition blockposition, IBlockData iblockdata) {
		boolean flag = iblockdata.get(BlockMinecartDetector.POWERED).booleanValue();
		boolean flag1 = false;
		List list = this.a(world, blockposition, EntityMinecartAbstract.class, new Predicate[0]);

		if (!list.isEmpty()) {
			flag1 = true;
		}

		// CraftBukkit start
		if (flag != flag1) {
			org.bukkit.block.Block block = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(),
					blockposition.getZ());

			BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(block, flag ? 15 : 0, flag1 ? 15 : 0);
			world.getServer().getPluginManager().callEvent(eventRedstone);

			flag1 = eventRedstone.getNewCurrent() > 0;
		}
		// CraftBukkit end

		if (flag1 && !flag) {
			world.setTypeAndData(blockposition, iblockdata.set(BlockMinecartDetector.POWERED, true),
					3);
			world.applyPhysics(blockposition, this);
			world.applyPhysics(blockposition.down(), this);
			world.b(blockposition, blockposition);
		}

		if (!flag1 && flag) {
			world.setTypeAndData(blockposition, iblockdata.set(BlockMinecartDetector.POWERED, false),
					3);
			world.applyPhysics(blockposition, this);
			world.applyPhysics(blockposition.down(), this);
			world.b(blockposition, blockposition);
		}

		if (flag1) {
			world.a(blockposition, this, this.a(world));
		}

		world.updateAdjacentComparators(blockposition, this);
	}

	@Override
	public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
		super.onPlace(world, blockposition, iblockdata);
		this.e(world, blockposition, iblockdata);
	}

	@Override
	public IBlockState<BlockMinecartTrackAbstract.EnumTrackPosition> n() {
		return BlockMinecartDetector.SHAPE;
	}

	@Override
	public boolean isComplexRedstone() {
		return true;
	}

	@Override
	public int l(World world, BlockPosition blockposition) {
		if (world.getType(blockposition).get(BlockMinecartDetector.POWERED).booleanValue()) {
			List list = this.a(world, blockposition, EntityMinecartCommandBlock.class, new Predicate[0]);

			if (!list.isEmpty()) {
				return ((EntityMinecartCommandBlock) list.get(0)).getCommandBlock().j();
			}

			List list1 = this.a(world, blockposition, EntityMinecartAbstract.class,
					new Predicate[] { IEntitySelector.c });

			if (!list1.isEmpty()) {
				return Container.b((IInventory) list1.get(0));
			}
		}

		return 0;
	}

	protected <T extends EntityMinecartAbstract> List<T> a(World world, BlockPosition blockposition, Class<T> oclass,
			Predicate<Entity>... apredicate) {
		AxisAlignedBB axisalignedbb = this.a(blockposition);

		return apredicate.length != 1 ? world.a(oclass, axisalignedbb) : world.a(oclass, axisalignedbb, apredicate[0]);
	}

	private AxisAlignedBB a(BlockPosition blockposition) {
		float f = 0.2F;

		return new AxisAlignedBB(blockposition.getX() + 0.2F, blockposition.getY(), blockposition.getZ() + 0.2F,
				blockposition.getX() + 1 - 0.2F, blockposition.getY() + 1 - 0.2F, blockposition.getZ() + 1 - 0.2F);
	}

	@Override
	public IBlockData fromLegacyData(int i) {
		return this.getBlockData()
				.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.a(i & 7))
				.set(BlockMinecartDetector.POWERED, (i & 8) > 0);
	}

	@Override
	public int toLegacyData(IBlockData iblockdata) {
		byte b0 = 0;
		int i = b0 | iblockdata.get(BlockMinecartDetector.SHAPE).a();

		if (iblockdata.get(BlockMinecartDetector.POWERED)) {
			i |= 8;
		}

		return i;
	}

	@Override
	protected BlockStateList getStateList() {
		return new BlockStateList(this,
				BlockMinecartDetector.SHAPE, BlockMinecartDetector.POWERED);
	}
}
