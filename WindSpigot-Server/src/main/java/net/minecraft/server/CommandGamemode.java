package net.minecraft.server;

import java.util.List;

public class CommandGamemode extends CommandAbstract {

	public CommandGamemode() {
	}

	@Override
	public String getCommand() {
		return "gamemode";
	}

	@Override
	public int a() {
		return 2;
	}

	@Override
	public String getUsage(ICommandListener icommandlistener) {
		return "commands.gamemode.usage";
	}

	@Override
	public void execute(ICommandListener icommandlistener, String[] astring) throws CommandException {
		if (astring.length <= 0) {
			throw new ExceptionUsage("commands.gamemode.usage");
		} else {
			WorldSettings.EnumGamemode worldsettings_enumgamemode = this.h(icommandlistener, astring[0]);
			EntityPlayer entityplayer = astring.length >= 2 ? a(icommandlistener, astring[1]) : b(icommandlistener);

			entityplayer.a(worldsettings_enumgamemode);
			// CraftBukkit start - handle event cancelling the change
			if (entityplayer.playerInteractManager.getGameMode() != worldsettings_enumgamemode) {
				icommandlistener.sendMessage(
						new ChatComponentText("Failed to set the gamemode of '" + entityplayer.getName() + "'"));
				return;
			}
			// CraftBukkit end

			entityplayer.fallDistance = 0.0F;
			if (icommandlistener.getWorld().getGameRules().getBoolean("sendCommandFeedback")) {
				entityplayer.sendMessage(new ChatMessage("gameMode.changed"));
			}

			ChatMessage chatmessage = new ChatMessage("gameMode." + worldsettings_enumgamemode.b());

			if (entityplayer != icommandlistener) {
				a(icommandlistener, this, 1, "commands.gamemode.success.other",
						entityplayer.getName(), chatmessage);
			} else {
				a(icommandlistener, this, 1, "commands.gamemode.success.self", chatmessage);
			}

		}
	}

	protected WorldSettings.EnumGamemode h(ICommandListener icommandlistener, String s) throws ExceptionInvalidNumber {
		return !s.equalsIgnoreCase(WorldSettings.EnumGamemode.SURVIVAL.b()) && !"s".equalsIgnoreCase(s)
				? (!s.equalsIgnoreCase(WorldSettings.EnumGamemode.CREATIVE.b()) && !"c".equalsIgnoreCase(s)
						? (!s.equalsIgnoreCase(WorldSettings.EnumGamemode.ADVENTURE.b()) && !"a".equalsIgnoreCase(s)
								? (!s.equalsIgnoreCase(WorldSettings.EnumGamemode.SPECTATOR.b())
										&& !"sp".equalsIgnoreCase(s)
												? WorldSettings
														.a(a(s, 0, WorldSettings.EnumGamemode.values().length - 2))
												: WorldSettings.EnumGamemode.SPECTATOR)
								: WorldSettings.EnumGamemode.ADVENTURE)
						: WorldSettings.EnumGamemode.CREATIVE)
				: WorldSettings.EnumGamemode.SURVIVAL;
	}

	@Override
	public List<String> tabComplete(ICommandListener icommandlistener, String[] astring, BlockPosition blockposition) {
		return astring.length == 1 ? a(astring, "survival", "creative", "adventure", "spectator")
				: (astring.length == 2 ? a(astring, this.d()) : null);
	}

	protected String[] d() {
		return MinecraftServer.getServer().getPlayers();
	}

	@Override
	public boolean isListStart(String[] astring, int i) {
		return i == 1;
	}

	// CraftBukkit start - fix decompiler error
	@Override
	public int compareTo(ICommand o) {
		return a(o);
	}
	// CraftBukkit end
}
