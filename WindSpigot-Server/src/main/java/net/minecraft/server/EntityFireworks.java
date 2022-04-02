package net.minecraft.server;

import dev.cobblesword.nachospigot.commons.Constants;

public class EntityFireworks extends Entity {

	private int ticksFlown;
	public int expectedLifespan;

	// Spigot Start
	@Override
	public void inactiveTick() {
		this.ticksFlown += 1;
		super.inactiveTick();
	}
	// Spigot End

	public EntityFireworks(World world) {
		super(world);
		this.setSize(0.25F, 0.25F);
	}

	@Override
	protected void h() {
		this.datawatcher.add(8, 5);
	}

	public EntityFireworks(World world, double d0, double d1, double d2, ItemStack itemstack) {
		super(world);
		this.ticksFlown = 0;
		this.setSize(0.25F, 0.25F);
		this.setPosition(d0, d1, d2);
		int i = 1;

		if (itemstack != null && itemstack.hasTag()) {
			this.datawatcher.watch(8, itemstack);
			NBTTagCompound nbttagcompound = itemstack.getTag();
			NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Fireworks");

			if (nbttagcompound1 != null) {
				i += nbttagcompound1.getByte("Flight");
			}
		}

		this.motX = this.random.nextGaussian() * 0.001D;
		this.motZ = this.random.nextGaussian() * 0.001D;
		this.motY = 0.05D;
		this.expectedLifespan = 10 * i + this.random.nextInt(6) + this.random.nextInt(7);
	}

	@Override
	public void t_() {
		this.P = this.locX;
		this.Q = this.locY;
		this.R = this.locZ;
		super.t_();
		this.motX *= 1.15D;
		this.motZ *= 1.15D;
		this.motY += 0.04D;
		this.move(this.motX, this.motY, this.motZ);
		float f = MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ);

		this.yaw = (float) (MathHelper.b(this.motX, this.motZ) * 180.0D / 3.1415927410125732D);

		for (this.pitch = (float) (MathHelper.b(this.motY, f) * 180.0D / 3.1415927410125732D); this.pitch
				- this.lastPitch < -180.0F; this.lastPitch -= 360.0F) {
        }

		while (this.pitch - this.lastPitch >= 180.0F) {
			this.lastPitch += 360.0F;
		}

		while (this.yaw - this.lastYaw < -180.0F) {
			this.lastYaw -= 360.0F;
		}

		while (this.yaw - this.lastYaw >= 180.0F) {
			this.lastYaw += 360.0F;
		}

		this.pitch = this.lastPitch + (this.pitch - this.lastPitch) * 0.2F;
		this.yaw = this.lastYaw + (this.yaw - this.lastYaw) * 0.2F;
		if (this.ticksFlown == 0 && !this.R()) {
			this.world.makeSound(this, "fireworks.launch", 3.0F, 1.0F);
		}

		++this.ticksFlown;
		if (this.world.isClientSide && this.ticksFlown % 2 < 2) {
			this.world.addParticle(EnumParticle.FIREWORKS_SPARK, this.locX, this.locY - 0.3D, this.locZ,
					this.random.nextGaussian() * 0.05D, -this.motY * 0.5D, this.random.nextGaussian() * 0.05D,
					Constants.EMPTY_ARRAY);
		}

		if (!this.world.isClientSide && this.ticksFlown > this.expectedLifespan) {
			if (!org.bukkit.craftbukkit.event.CraftEventFactory.callFireworkExplodeEvent(this).isCancelled()) {
				this.world.broadcastEntityEffect(this, (byte) 17);
			}
			this.die();
		}

	}

	@Override
	public void b(NBTTagCompound nbttagcompound) {
		nbttagcompound.setInt("Life", this.ticksFlown);
		nbttagcompound.setInt("LifeTime", this.expectedLifespan);
		ItemStack itemstack = this.datawatcher.getItemStack(8);

		if (itemstack != null) {
			NBTTagCompound nbttagcompound1 = new NBTTagCompound();

			itemstack.save(nbttagcompound1);
			nbttagcompound.set("FireworksItem", nbttagcompound1);
		}

	}

	@Override
	public void a(NBTTagCompound nbttagcompound) {
		this.ticksFlown = nbttagcompound.getInt("Life");
		this.expectedLifespan = nbttagcompound.getInt("LifeTime");
		NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("FireworksItem");

		if (nbttagcompound1 != null) {
			ItemStack itemstack = ItemStack.createStack(nbttagcompound1);

			if (itemstack != null) {
				this.datawatcher.watch(8, itemstack);
			}
		}

	}

	@Override
	public float c(float f) {
		return super.c(f);
	}

	@Override
	public boolean aD() {
		return false;
	}
}
