package net.minecraft.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// CraftBukkit start
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftShapelessRecipe;
// CraftBukkit end

import com.google.common.collect.Lists;

public class ShapelessRecipes implements IRecipe {

	public final ItemStack result; // Spigot
	private final List<ItemStack> ingredients;

	public ShapelessRecipes(ItemStack itemstack, List<ItemStack> list) {
		this.result = itemstack;
		this.ingredients = list;
	}

	// CraftBukkit start
	@Override
	@SuppressWarnings("unchecked")
	public org.bukkit.inventory.ShapelessRecipe toBukkitRecipe() {
		CraftItemStack result = CraftItemStack.asCraftMirror(this.result);
		CraftShapelessRecipe recipe = new CraftShapelessRecipe(result, this);
		for (ItemStack stack : this.ingredients) {
			if (stack != null) {
				recipe.addIngredient(org.bukkit.craftbukkit.util.CraftMagicNumbers.getMaterial(stack.getItem()),
						stack.getData());
			}
		}
		return recipe;
	}
	// CraftBukkit end

	@Override
	public ItemStack b() {
		return this.result;
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

	@Override
	public boolean a(InventoryCrafting inventorycrafting, World world) {
		ArrayList arraylist = Lists.newArrayList(this.ingredients);

		for (int i = 0; i < inventorycrafting.h(); ++i) {
			for (int j = 0; j < inventorycrafting.i(); ++j) {
				ItemStack itemstack = inventorycrafting.c(j, i);

				if (itemstack != null) {
					boolean flag = false;
					Iterator iterator = arraylist.iterator();

					while (iterator.hasNext()) {
						ItemStack itemstack1 = (ItemStack) iterator.next();

						if (itemstack.getItem() == itemstack1.getItem()
								&& (itemstack1.getData() == 32767 || itemstack.getData() == itemstack1.getData())) {
							flag = true;
							arraylist.remove(itemstack1);
							break;
						}
					}

					if (!flag) {
						return false;
					}
				}
			}
		}

		return arraylist.isEmpty();
	}

	@Override
	public ItemStack craftItem(InventoryCrafting inventorycrafting) {
		return this.result.cloneItemStack();
	}

	@Override
	public int a() {
		return this.ingredients.size();
	}

	// Spigot start
	@Override
	public java.util.List<ItemStack> getIngredients() {
		return java.util.Collections.unmodifiableList(ingredients);
	}
	// Spigot end
}
