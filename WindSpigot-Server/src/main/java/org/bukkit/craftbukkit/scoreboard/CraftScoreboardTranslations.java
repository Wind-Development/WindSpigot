package org.bukkit.craftbukkit.scoreboard;

import org.bukkit.scoreboard.DisplaySlot;

import com.google.common.collect.ImmutableBiMap;

import net.minecraft.server.Scoreboard;

class CraftScoreboardTranslations {
	static final int MAX_DISPLAY_SLOT = 3;
	static ImmutableBiMap<DisplaySlot, String> SLOTS = ImmutableBiMap.of(DisplaySlot.BELOW_NAME, "belowName",
			DisplaySlot.PLAYER_LIST, "list", DisplaySlot.SIDEBAR, "sidebar");

	private CraftScoreboardTranslations() {
	}

	static DisplaySlot toBukkitSlot(int i) {
		return SLOTS.inverse().get(Scoreboard.getSlotName(i));
	}

	static int fromBukkitSlot(DisplaySlot slot) {
		return Scoreboard.getSlotForName(SLOTS.get(slot));
	}

}
