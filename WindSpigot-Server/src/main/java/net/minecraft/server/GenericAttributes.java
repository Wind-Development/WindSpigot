package net.minecraft.server;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GenericAttributes {

	private static final Logger f = LogManager.getLogger();
	// Spigot start
	public static final IAttribute maxHealth = (new AttributeRanged(null, "generic.maxHealth", 20.0D, 0.1D,
			org.spigotmc.SpigotConfig.maxHealth)).a("Max Health").a(true);
	public static final IAttribute FOLLOW_RANGE = (new AttributeRanged(null, "generic.followRange", 32.0D,
			0.0D, 2048.0D)).a("Follow Range");
	public static final IAttribute c = (new AttributeRanged(null, "generic.knockbackResistance", 0.0D,
			0.0D, 1.0D)).a("Knockback Resistance");
	public static final IAttribute MOVEMENT_SPEED = (new AttributeRanged(null, "generic.movementSpeed",
			0.699999988079071D, 0.0D, org.spigotmc.SpigotConfig.movementSpeed)).a("Movement Speed").a(true);
	public static final IAttribute ATTACK_DAMAGE = new AttributeRanged(null, "generic.attackDamage", 2.0D,
			0.0D, org.spigotmc.SpigotConfig.attackDamage);
	// Spigot end

	public static NBTTagList a(AttributeMapBase attributemapbase) {
		NBTTagList nbttaglist = new NBTTagList();
		Iterator iterator = attributemapbase.a().iterator();

		while (iterator.hasNext()) {
			AttributeInstance attributeinstance = (AttributeInstance) iterator.next();

			nbttaglist.add(a(attributeinstance));
		}

		return nbttaglist;
	}

	private static NBTTagCompound a(AttributeInstance attributeinstance) {
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		IAttribute iattribute = attributeinstance.getAttribute();

		nbttagcompound.setString("Name", iattribute.getName());
		nbttagcompound.setDouble("Base", attributeinstance.b());
		Collection collection = attributeinstance.c();

		if (collection != null && !collection.isEmpty()) {
			NBTTagList nbttaglist = new NBTTagList();
			Iterator iterator = collection.iterator();

			while (iterator.hasNext()) {
				AttributeModifier attributemodifier = (AttributeModifier) iterator.next();

				if (attributemodifier.e()) {
					nbttaglist.add(a(attributemodifier));
				}
			}

			nbttagcompound.set("Modifiers", nbttaglist);
		}

		return nbttagcompound;
	}

	private static NBTTagCompound a(AttributeModifier attributemodifier) {
		NBTTagCompound nbttagcompound = new NBTTagCompound();

		nbttagcompound.setString("Name", attributemodifier.b());
		nbttagcompound.setDouble("Amount", attributemodifier.d());
		nbttagcompound.setInt("Operation", attributemodifier.c());
		nbttagcompound.setLong("UUIDMost", attributemodifier.a().getMostSignificantBits());
		nbttagcompound.setLong("UUIDLeast", attributemodifier.a().getLeastSignificantBits());
		return nbttagcompound;
	}

	public static void a(AttributeMapBase attributemapbase, NBTTagList nbttaglist) {
		for (int i = 0; i < nbttaglist.size(); ++i) {
			NBTTagCompound nbttagcompound = nbttaglist.get(i);
			AttributeInstance attributeinstance = attributemapbase.a(nbttagcompound.getString("Name"));

			if (attributeinstance != null) {
				a(attributeinstance, nbttagcompound);
			} else {
				GenericAttributes.f.warn("Ignoring unknown attribute \'" + nbttagcompound.getString("Name") + "\'");
			}
		}

	}

	private static void a(AttributeInstance attributeinstance, NBTTagCompound nbttagcompound) {
		attributeinstance.setValue(nbttagcompound.getDouble("Base"));
		if (nbttagcompound.hasKeyOfType("Modifiers", 9)) {
			NBTTagList nbttaglist = nbttagcompound.getList("Modifiers", 10);

			for (int i = 0; i < nbttaglist.size(); ++i) {
				AttributeModifier attributemodifier = a(nbttaglist.get(i));

				if (attributemodifier != null) {
					AttributeModifier attributemodifier1 = attributeinstance.a(attributemodifier.a());

					if (attributemodifier1 != null) {
						attributeinstance.c(attributemodifier1);
					}

					attributeinstance.b(attributemodifier);
				}
			}
		}

	}

	public static AttributeModifier a(NBTTagCompound nbttagcompound) {
		UUID uuid = new UUID(nbttagcompound.getLong("UUIDMost"), nbttagcompound.getLong("UUIDLeast"));

		try {
			return new AttributeModifier(uuid, nbttagcompound.getString("Name"), nbttagcompound.getDouble("Amount"),
					nbttagcompound.getInt("Operation"));
		} catch (Exception exception) {
			GenericAttributes.f.warn("Unable to create attribute: " + exception.getMessage());
			return null;
		}
	}
}
