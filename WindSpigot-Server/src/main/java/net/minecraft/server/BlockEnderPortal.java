package net.minecraft.server;

import java.util.List;
import java.util.Random;

import org.bukkit.event.entity.EntityPortalEnterEvent; // CraftBukkit

public class BlockEnderPortal extends BlockContainer {

	protected BlockEnderPortal(Material material) {
		super(material);
		this.a(1.0F);
	}

	@Override
	public TileEntity a(World world, int i) {
		return new TileEntityEnderPortal();
	}

	@Override
	public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
		float f = 0.0625F;

		this.a(0.0F, 0.0F, 0.0F, 1.0F, f, 1.0F);
	}

	@Override
	public void a(World world, BlockPosition blockposition, IBlockData iblockdata, AxisAlignedBB axisalignedbb,
			List<AxisAlignedBB> list, Entity entity) {
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
	public int a(Random random) {
		return 0;
	}

	@Override
	public void a(World world, BlockPosition blockposition, IBlockData iblockdata, Entity entity) {
		if (entity.vehicle == null && entity.passenger == null && !world.isClientSide) {
			// CraftBukkit start - Entity in portal
			EntityPortalEnterEvent event = new EntityPortalEnterEvent(entity.getBukkitEntity(), new org.bukkit.Location(
					world.getWorld(), blockposition.getX(), blockposition.getY(), blockposition.getZ()));
			world.getServer().getPluginManager().callEvent(event);
			// CraftBukkit end
			entity.c(1);
		}

	}

	@Override
	public MaterialMapColor g(IBlockData iblockdata) {
		return MaterialMapColor.E;
	}
}
