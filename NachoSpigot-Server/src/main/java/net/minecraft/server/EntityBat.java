package net.minecraft.server;

import java.util.Calendar;

public class EntityBat extends EntityAmbient {
	private BlockPosition a;

	public EntityBat(World var1) {
		super(var1);
		this.setSize(0.5F, 0.9F);
		this.setAsleep(true);
	}

	@Override
	protected void h() {
		super.h();
		this.datawatcher.a(16, (byte) 0);
	}

	@Override
	protected float bB() {
		return 0.1F;
	}

	@Override
	protected float bC() {
		return super.bC() * 0.95F;
	}

	@Override
	protected String z() {
		return this.isAsleep() && this.random.nextInt(4) != 0 ? null : "mob.bat.idle";
	}

	@Override
	protected String bo() {
		return "mob.bat.hurt";
	}

	@Override
	protected String bp() {
		return "mob.bat.death";
	}

	@Override
	public boolean ae() {
		return false;
	}

	@Override
	protected void s(Entity var1) {
	}

	@Override
	protected void bL() {
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(GenericAttributes.maxHealth).setValue(6.0D);
	}

	public boolean isAsleep() {
		return (this.datawatcher.getByte(16) & 1) != 0;
	}

	public void setAsleep(boolean var1) {
		byte var2 = this.datawatcher.getByte(16);
		if (var1) {
			this.datawatcher.watch(16, (byte) (var2 | 1));
		} else {
			this.datawatcher.watch(16, (byte) (var2 & -2));
		}

	}

	@Override
	public void t_() {
		super.t_();
		if (this.isAsleep()) {
			this.motX = this.motY = this.motZ = 0.0D;
			this.locY = MathHelper.floor(this.locY) + 1.0D - this.length;
		} else {
			this.motY *= 0.6000000238418579D;
		}

	}

	@Override
	protected void E() {
		super.E();
		BlockPosition var1 = new BlockPosition(this);
		BlockPosition var2 = var1.up();
		if (this.isAsleep()) {
			if (!this.world.getType(var2).getBlock().isOccluding()) {
				this.setAsleep(false);
				this.world.a(null, 1015, var1, 0);
			} else {
				if (this.random.nextInt(200) == 0) {
					this.aK = this.random.nextInt(360);
				}

				if (this.world.findNearbyPlayer(this, 4.0D) != null) {
					this.setAsleep(false);
					this.world.a(null, 1015, var1, 0);
				}
			}
		} else {
			if (this.a != null && (!this.world.isEmpty(this.a) || this.a.getY() < 1)) {
				this.a = null;
			}

			if (this.a == null || this.random.nextInt(30) == 0
					|| this.a.c((int) this.locX, (int) this.locY, (int) this.locZ) < 4.0D) {
				this.a = new BlockPosition((int) this.locX + this.random.nextInt(7) - this.random.nextInt(7),
						(int) this.locY + this.random.nextInt(6) - 2,
						(int) this.locZ + this.random.nextInt(7) - this.random.nextInt(7));
			}

			double var3 = this.a.getX() + 0.5D - this.locX;
			double var5 = this.a.getY() + 0.1D - this.locY;
			double var7 = this.a.getZ() + 0.5D - this.locZ;
			this.motX += (Math.signum(var3) * 0.5D - this.motX) * 0.10000000149011612D;
			this.motY += (Math.signum(var5) * 0.699999988079071D - this.motY) * 0.10000000149011612D;
			this.motZ += (Math.signum(var7) * 0.5D - this.motZ) * 0.10000000149011612D;
			float var9 = (float) (MathHelper.b(this.motZ, this.motX) * 180.0D / 3.1415927410125732D) - 90.0F;
			float var10 = MathHelper.g(var9 - this.yaw);
			this.ba = 0.5F;
			this.yaw += var10;
			if (this.random.nextInt(100) == 0 && this.world.getType(var2).getBlock().isOccluding()) {
				this.setAsleep(true);
			}
		}

	}

	@Override
	protected boolean s_() {
		return false;
	}

	@Override
	public void e(float var1, float var2) {
	}

	@Override
	protected void a(double var1, boolean var3, Block var4, BlockPosition var5) {
	}

	@Override
	public boolean aI() {
		return true;
	}

	@Override
	public boolean damageEntity(DamageSource var1, float var2) {
		if (this.isInvulnerable(var1)) {
			return false;
		} else {
			if (!this.world.isClientSide && this.isAsleep()) {
				this.setAsleep(false);
			}

			return super.damageEntity(var1, var2);
		}
	}

	@Override
	public void a(NBTTagCompound var1) {
		super.a(var1);
		this.datawatcher.watch(16, var1.getByte("BatFlags"));
	}

	@Override
	public void b(NBTTagCompound var1) {
		super.b(var1);
		var1.setByte("BatFlags", this.datawatcher.getByte(16));
	}

	@Override
	public boolean bR() {
		BlockPosition pos = new BlockPosition(this.locX, this.getBoundingBox().b, this.locZ);
		if (pos.getY() >= this.world.F()) {
			return false;
		} else {
			byte var3 = 4;
			if (this.a(this.world.Y())) {
				var3 = 7;
			} else if (this.random.nextBoolean()) {
				return false;
			}

			int curLightLevel = this.world.getLightLevel(pos);
			return curLightLevel <= this.random.nextInt(var3) && super.bR();
		}
	}

	private boolean a(Calendar var1) {
		return var1.get(Calendar.MONTH) + 1 == 10 && var1.get(Calendar.DATE) >= 20
				|| var1.get(Calendar.MONTH) + 1 == 11 && var1.get(Calendar.DATE) <= 3;
	}

	@Override
	public float getHeadHeight() {
		return this.length / 2.0F;
	}
}
