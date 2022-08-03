package net.minecraft.server;

import java.util.Iterator;
import java.util.List;

public class CommandGamerule extends CommandAbstract {

	public CommandGamerule() {
	}

	@Override
	public String getCommand() {
		return "gamerule";
	}

	@Override
	public int a() {
		return 2;
	}

	@Override
	public String getUsage(ICommandListener icommandlistener) {
		return "commands.gamerule.usage";
	}

	@Override
	public void execute(ICommandListener icommandlistener, String[] astring) throws CommandException {
		GameRules gamerules = icommandlistener.getWorld().getGameRules(); // CraftBukkit - Use current world
		String s = astring.length > 0 ? astring[0] : "";
		String s1 = astring.length > 1 ? a(astring, 1) : "";

		switch (astring.length) {
		case 0:
			icommandlistener.sendMessage(new ChatComponentText(a(gamerules.getGameRules())));
			break;

		case 1:
			if (!gamerules.contains(s)) {
				throw new CommandException("commands.gamerule.norule", s);
			}

			String s2 = gamerules.get(s);

			icommandlistener.sendMessage((new ChatComponentText(s)).a(" = ").a(s2));
			icommandlistener.a(CommandObjectiveExecutor.EnumCommandResult.QUERY_RESULT, gamerules.c(s));
			break;

		default:
			if (gamerules.a(s, GameRules.EnumGameRuleType.BOOLEAN_VALUE) && !"true".equals(s1) && !"false".equals(s1)) {
				throw new CommandException("commands.generic.boolean.invalid", s1);
			}

			gamerules.set(s, s1);
			a(gamerules, s);
			a(icommandlistener, this, "commands.gamerule.success");
		}

	}

	public static void a(GameRules gamerules, String s) {
		if ("reducedDebugInfo".equals(s)) {
			int i = gamerules.getBoolean(s) ? 22 : 23;
			Iterator iterator = MinecraftServer.getServer().getPlayerList().v().iterator();

			while (iterator.hasNext()) {
				EntityPlayer entityplayer = (EntityPlayer) iterator.next();

				entityplayer.playerConnection.sendPacket(new PacketPlayOutEntityStatus(entityplayer, (byte) i));
			}
		}

	}

	@Override
	public List<String> tabComplete(ICommandListener icommandlistener, String[] astring, BlockPosition blockposition) {
		if (astring.length == 1) {
			return a(astring, this.d().getGameRules());
		} else {
			if (astring.length == 2) {
				GameRules gamerules = this.d();

				if (gamerules.a(astring[0], GameRules.EnumGameRuleType.BOOLEAN_VALUE)) {
					return a(astring, new String[] { "true", "false" });
				}
			}

			return null;
		}
	}

	private GameRules d() {
		return MinecraftServer.getServer().getWorldServer(0).getGameRules();
	}

	// CraftBukkit start - fix decompile error
	@Override
	public int compareTo(ICommand o) {
		return a(o);
	}
	// CraftBukkit end
}
