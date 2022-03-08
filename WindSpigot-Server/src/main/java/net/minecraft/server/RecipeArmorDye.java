package net.minecraft.server;

import java.util.ArrayList;

import com.google.common.collect.Lists;

public class RecipeArmorDye extends ShapelessRecipes implements IRecipe { // CraftBukkit - added extends

	// CraftBukkit start - Delegate to new parent class with bogus info
	public RecipeArmorDye() {
		super(new ItemStack(Items.LEATHER_HELMET, 0, 0), java.util.Arrays.asList(new ItemStack(Items.DYE, 0, 5)));
	}
	// CraftBukkit end

	@Override
	public boolean a(InventoryCrafting inventorycrafting, World world) {
		ItemStack itemstack = null;
		ArrayList arraylist = Lists.newArrayList();

		for (int i = 0; i < inventorycrafting.getSize(); ++i) {
			ItemStack itemstack1 = inventorycrafting.getItem(i);

			if (itemstack1 != null) {
				if (itemstack1.getItem() instanceof ItemArmor) {
					ItemArmor itemarmor = (ItemArmor) itemstack1.getItem();

					if (itemarmor.x_() != ItemArmor.EnumArmorMaterial.LEATHER || itemstack != null) {
						return false;
					}

					itemstack = itemstack1;
				} else {
					if (itemstack1.getItem() != Items.DYE) {
						return false;
					}

					arraylist.add(itemstack1);
				}
			}
		}

		return itemstack != null && !arraylist.isEmpty();
	}

	@Override
	public ItemStack craftItem(InventoryCrafting inventorycrafting) {
		ItemStack itemstack = null;
		int[] aint = new int[3];
		int i = 0;
		int j = 0;
		ItemArmor itemarmor = null;

		int k;
		int l;
		float f;
		float f1;
		int i1;

		for (k = 0; k < inventorycrafting.getSize(); ++k) {
			ItemStack itemstack1 = inventorycrafting.getItem(k);

			if (itemstack1 != null) {
				if (itemstack1.getItem() instanceof ItemArmor) {
					itemarmor = (ItemArmor) itemstack1.getItem();
					if (itemarmor.x_() != ItemArmor.EnumArmorMaterial.LEATHER || itemstack != null) {
						return null;
					}

					itemstack = itemstack1.cloneItemStack();
					itemstack.count = 1;
					if (itemarmor.d_(itemstack1)) {
						l = itemarmor.b(itemstack);
						f = (l >> 16 & 255) / 255.0F;
						f1 = (l >> 8 & 255) / 255.0F;
						float f2 = (l & 255) / 255.0F;

						i = (int) (i + Math.max(f, Math.max(f1, f2)) * 255.0F);
						aint[0] = (int) (aint[0] + f * 255.0F);
						aint[1] = (int) (aint[1] + f1 * 255.0F);
						aint[2] = (int) (aint[2] + f2 * 255.0F);
						++j;
					}
				} else {
					if (itemstack1.getItem() != Items.DYE) {
						return null;
					}

					float[] afloat = EntitySheep.a(EnumColor.fromInvColorIndex(itemstack1.getData()));
					int j1 = (int) (afloat[0] * 255.0F);
					int k1 = (int) (afloat[1] * 255.0F);

					i1 = (int) (afloat[2] * 255.0F);
					i += Math.max(j1, Math.max(k1, i1));
					aint[0] += j1;
					aint[1] += k1;
					aint[2] += i1;
					++j;
				}
			}
		}

		if (itemarmor == null) {
			return null;
		} else {
			k = aint[0] / j;
			int l1 = aint[1] / j;

			l = aint[2] / j;
			f = (float) i / (float) j;
			f1 = Math.max(k, Math.max(l1, l));
			k = (int) (k * f / f1);
			l1 = (int) (l1 * f / f1);
			l = (int) (l * f / f1);
			i1 = (k << 8) + l1;
			i1 = (i1 << 8) + l;
			itemarmor.b(itemstack, i1);
			return itemstack;
		}
	}

	@Override
	public int a() {
		return 10;
	}

	@Override
	public ItemStack b() {
		return null;
	}

	@Override
	public ItemStack[] b(InventoryCrafting inventorycrafting) {
		ItemStack[] aitemstack = new ItemStack[inventorycrafting.getSize()];

		for (int i = 0; i < aitemstack.length; ++i) {
			ItemStack itemstack = inventorycrafting.getItem(i);

			if (itemstack != null && itemstack.getItem().r()) {
				aitemstack[i] = new ItemStack(itemstack.getItem().q());
			}
		}

		return aitemstack;
	}
}
