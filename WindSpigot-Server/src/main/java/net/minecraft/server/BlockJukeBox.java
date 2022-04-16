package net.minecraft.server;

public class BlockJukeBox extends BlockContainer {

	public static final BlockStateBoolean HAS_RECORD = BlockStateBoolean.of("has_record");

	protected BlockJukeBox() {
		super(Material.WOOD, MaterialMapColor.l);
		this.j(this.blockStateList.getBlockData().set(BlockJukeBox.HAS_RECORD, false));
		this.a(CreativeModeTab.c);
	}

	@Override
	public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman,
			EnumDirection enumdirection, float f, float f1, float f2) {
		if (iblockdata.get(BlockJukeBox.HAS_RECORD).booleanValue()) {
			this.dropRecord(world, blockposition, iblockdata);
			iblockdata = iblockdata.set(BlockJukeBox.HAS_RECORD, false);
			world.setTypeAndData(blockposition, iblockdata, 2);
			return true;
		} else {
			return false;
		}
	}

	public void a(World world, BlockPosition blockposition, IBlockData iblockdata, ItemStack itemstack) {
		if (!world.isClientSide) {
			TileEntity tileentity = world.getTileEntity(blockposition);

			if (tileentity instanceof BlockJukeBox.TileEntityRecordPlayer) {
				((BlockJukeBox.TileEntityRecordPlayer) tileentity)
						.setRecord(new ItemStack(itemstack.getItem(), 1, itemstack.getData()));
				world.setTypeAndData(blockposition, iblockdata.set(BlockJukeBox.HAS_RECORD, true), 2);
			}
		}
	}

	public void dropRecord(World world, BlockPosition blockposition, IBlockData iblockdata) {
		if (!world.isClientSide) {
			TileEntity tileentity = world.getTileEntity(blockposition);

			if (tileentity instanceof BlockJukeBox.TileEntityRecordPlayer) {
				BlockJukeBox.TileEntityRecordPlayer blockjukebox_tileentityrecordplayer = (BlockJukeBox.TileEntityRecordPlayer) tileentity;
				ItemStack itemstack = blockjukebox_tileentityrecordplayer.getRecord();

				if (itemstack != null) {
					world.triggerEffect(1005, blockposition, 0);
					world.a(blockposition, (String) null);
					blockjukebox_tileentityrecordplayer.setRecord((ItemStack) null);
					float f = 0.7F;
					double d0 = world.random.nextFloat() * f + (1.0F - f) * 0.5D;
					double d1 = world.random.nextFloat() * f + (1.0F - f) * 0.2D + 0.6D;
					double d2 = world.random.nextFloat() * f + (1.0F - f) * 0.5D;
					ItemStack itemstack1 = itemstack.cloneItemStack();
					EntityItem entityitem = new EntityItem(world, blockposition.getX() + d0, blockposition.getY() + d1,
							blockposition.getZ() + d2, itemstack1);

					entityitem.p();
					world.addEntity(entityitem);
				}
			}
		}
	}

	@Override
	public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
		this.dropRecord(world, blockposition, iblockdata);
		super.remove(world, blockposition, iblockdata);
	}

	@Override
	public void dropNaturally(World world, BlockPosition blockposition, IBlockData iblockdata, float f, int i) {
		if (!world.isClientSide) {
			super.dropNaturally(world, blockposition, iblockdata, f, 0);
		}
	}

	@Override
	public TileEntity a(World world, int i) {
		return new BlockJukeBox.TileEntityRecordPlayer();
	}

	@Override
	public boolean isComplexRedstone() {
		return true;
	}

	@Override
	public int l(World world, BlockPosition blockposition) {
		TileEntity tileentity = world.getTileEntity(blockposition);

		if (tileentity instanceof BlockJukeBox.TileEntityRecordPlayer) {
			ItemStack itemstack = ((BlockJukeBox.TileEntityRecordPlayer) tileentity).getRecord();

			if (itemstack != null) {
				return Item.getId(itemstack.getItem()) + 1 - Item.getId(Items.RECORD_13);
			}
		}

		return 0;
	}

	@Override
	public int b() {
		return 3;
	}

	@Override
	public IBlockData fromLegacyData(int i) {
		return this.getBlockData().set(BlockJukeBox.HAS_RECORD, i > 0);
	}

	@Override
	public int toLegacyData(IBlockData iblockdata) {
		return iblockdata.get(BlockJukeBox.HAS_RECORD).booleanValue() ? 1 : 0;
	}

	@Override
	protected BlockStateList getStateList() {
		return new BlockStateList(this, new IBlockState[] { BlockJukeBox.HAS_RECORD });
	}

	public static class TileEntityRecordPlayer extends TileEntity {

		private ItemStack record;

		public TileEntityRecordPlayer() {
		}

		@Override
		public void a(NBTTagCompound nbttagcompound) {
			super.a(nbttagcompound);
			if (nbttagcompound.hasKeyOfType("RecordItem", 10)) {
				this.setRecord(ItemStack.createStack(nbttagcompound.getCompound("RecordItem")));
			} else if (nbttagcompound.getInt("Record") > 0) {
				this.setRecord(new ItemStack(Item.getById(nbttagcompound.getInt("Record")), 1, 0));
			}

		}

		@Override
		public void b(NBTTagCompound nbttagcompound) {
			super.b(nbttagcompound);
			if (this.getRecord() != null) {
				nbttagcompound.set("RecordItem", this.getRecord().save(new NBTTagCompound()));
			}

		}

		public ItemStack getRecord() {
			return this.record;
		}

		public void setRecord(ItemStack itemstack) {
			// CraftBukkit start - There can only be one
			if (itemstack != null) {
				itemstack.count = 1;
			}
			// CraftBukkit end
			this.record = itemstack;
			this.update();
		}
	}
}
