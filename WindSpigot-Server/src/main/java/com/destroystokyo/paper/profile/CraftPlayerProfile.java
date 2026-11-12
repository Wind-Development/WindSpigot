package com.destroystokyo.paper.profile;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.Validate;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.UserCache;

/**
 * CraftBukkit implementation of {@link PlayerProfile}.
 */
public class CraftPlayerProfile implements PlayerProfile {

	private UUID uniqueId;
	private String name;
	private final Set<ProfileProperty> properties = new HashSet<ProfileProperty>();

	public CraftPlayerProfile(UUID uniqueId, String name) {
		this.uniqueId = uniqueId;
		this.name = name;
	}

	public CraftPlayerProfile(GameProfile profile) {
		Validate.notNull(profile, "GameProfile cannot be null");
		this.uniqueId = profile.getId();
		this.name = profile.getName();
		PropertyMap map = profile.getProperties();
		for (Property property : map.values()) {
			this.properties.add(new ProfileProperty(property.getName(), property.getValue(), property.getSignature()));
		}
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String setName(String name) {
		String previous = this.name;
		this.name = name;
		return previous;
	}

	@Override
	public UUID getId() {
		return this.uniqueId;
	}

	@Override
	public UUID setId(UUID uuid) {
		UUID previous = this.uniqueId;
		this.uniqueId = uuid;
		return previous;
	}

	@Override
	public Set<ProfileProperty> getProperties() {
		return this.properties;
	}

	@Override
	public boolean hasProperty(String property) {
		if (property == null) {
			return false;
		}
		for (ProfileProperty profileProperty : this.properties) {
			if (profileProperty.getName().equals(property)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void setProperty(ProfileProperty property) {
		Validate.notNull(property, "Property cannot be null");
		removeProperty(property.getName());
		this.properties.add(property);
	}

	@Override
	public void setProperties(Collection<ProfileProperty> properties) {
		Validate.notNull(properties, "Properties cannot be null");
		for (ProfileProperty property : properties) {
			setProperty(property);
		}
	}

	@Override
	public boolean removeProperty(String property) {
		if (property == null) {
			return false;
		}
		boolean removed = false;
		Iterator<ProfileProperty> iterator = this.properties.iterator();
		while (iterator.hasNext()) {
			if (iterator.next().getName().equals(property)) {
				iterator.remove();
				removed = true;
			}
		}
		return removed;
	}

	@Override
	public void clearProperties() {
		this.properties.clear();
	}

	@Override
	public boolean isComplete() {
		return this.uniqueId != null && this.name != null;
	}

	@Override
	public boolean completeFromCache() {
		MinecraftServer server = MinecraftServer.getServer();
		if (server == null) {
			return isComplete();
		}
		UserCache cache = server.getUserCache();
		if (this.uniqueId == null && this.name != null) {
			GameProfile profile = cache.getProfile(this.name);
			if (profile != null) {
				this.uniqueId = profile.getId();
				if (this.name == null) {
					this.name = profile.getName();
				}
			}
		} else if (this.name == null && this.uniqueId != null) {
			GameProfile profile = cache.a(this.uniqueId);
			if (profile != null) {
				this.name = profile.getName();
			}
		}
		return isComplete();
	}

	@Override
	public boolean complete(boolean textures) {
		// 1.8 status-ping profiles: cache lookup only (no Mojang texture fill).
		return completeFromCache();
	}

	public GameProfile getGameProfile() {
		return asAuthlib(this);
	}

	public static PlayerProfile asBukkitCopy(GameProfile profile) {
		return new CraftPlayerProfile(profile);
	}

	public static GameProfile asAuthlib(PlayerProfile profile) {
		GameProfile gameProfile = new GameProfile(profile.getId(), profile.getName());
		PropertyMap map = gameProfile.getProperties();
		for (ProfileProperty property : profile.getProperties()) {
			map.put(property.getName(),
					new Property(property.getName(), property.getValue(), property.getSignature()));
		}
		return gameProfile;
	}

	/**
	 * Offline-mode UUID derived from the player name (Bukkit style).
	 */
	public static UUID offlineIdForName(String name) {
		return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof CraftPlayerProfile)) {
			return false;
		}
		CraftPlayerProfile other = (CraftPlayerProfile) obj;
		return Objects.equals(this.uniqueId, other.uniqueId) && Objects.equals(this.name, other.name)
				&& Objects.equals(this.properties, other.properties);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.uniqueId, this.name, this.properties);
	}
}
