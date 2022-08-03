package net.minecraft.server;

import java.util.Date;
import java.util.UUID;

import com.eatthepath.uuid.FastUUID;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;

public class GameProfileBanEntry extends ExpirableListEntry<GameProfile> {

	public GameProfileBanEntry(GameProfile gameprofile) {
		this(gameprofile, null, (String) null, null, (String) null);
	}

	public GameProfileBanEntry(GameProfile gameprofile, Date date, String s, Date date1, String s1) {
		super(gameprofile, date, s, date1, s1); // Spigot
	}

	public GameProfileBanEntry(JsonObject jsonobject) {
		super(b(jsonobject), jsonobject);
	}

	@Override
	protected void a(JsonObject jsonobject) {
		if (this.getKey() != null) {
			jsonobject.addProperty("uuid", this.getKey().getId() == null ? "" : this.getKey().getId().toString());
			jsonobject.addProperty("name", this.getKey().getName());
			super.a(jsonobject);
		}
	}

	private static GameProfile b(JsonObject jsonobject) {
		// Spigot start
		// this whole method has to be reworked to account for the fact Bukkit only
		// accepts UUID bans and gives no way for usernames to be stored!
		UUID uuid = null;
		String name = null;
		if (jsonobject.has("uuid")) {
			String s = jsonobject.get("uuid").getAsString();

			try {
				uuid = FastUUID.parseUUID(s);
			} catch (Throwable throwable) {
			}

		}
		if (jsonobject.has("name")) {
			name = jsonobject.get("name").getAsString();
		}
		if (uuid != null || name != null) {
			return new GameProfile(uuid, name);
		} else {
			return null;
		}
		// Spigot End
	}
}
