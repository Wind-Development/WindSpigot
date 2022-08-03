package net.minecraft.server;

import java.util.List;

// CraftBukkit start
import org.bukkit.craftbukkit.command.ProxiedNativeCommandSender;
import org.bukkit.craftbukkit.command.VanillaCommandWrapper;
// CraftBukkit end

public class CommandExecute extends CommandAbstract {

	public CommandExecute() {
	}

	@Override
	public String getCommand() {
		return "execute";
	}

	@Override
	public int a() {
		return 2;
	}

	@Override
	public String getUsage(ICommandListener icommandlistener) {
		return "commands.execute.usage";
	}

	@Override
	public void execute(final ICommandListener icommandlistener, String[] astring) throws CommandException {
		if (astring.length < 5) {
			throw new ExceptionUsage("commands.execute.usage");
		} else {
			final Entity entity = a(icommandlistener, astring[0], Entity.class);
			final double d0 = b(entity.locX, astring[1], false);
			final double d1 = b(entity.locY, astring[2], false);
			final double d2 = b(entity.locZ, astring[3], false);
			final BlockPosition blockposition = new BlockPosition(d0, d1, d2);
			byte b0 = 4;

			if ("detect".equals(astring[4]) && astring.length > 10) {
				World world = entity.getWorld();
				double d3 = b(d0, astring[5], false);
				double d4 = b(d1, astring[6], false);
				double d5 = b(d2, astring[7], false);
				Block block = g(icommandlistener, astring[8]);
				int i = a(astring[9], -1, 15);
				BlockPosition blockposition1 = new BlockPosition(d3, d4, d5);
				IBlockData iblockdata = world.getType(blockposition1);

				if (iblockdata.getBlock() != block || i >= 0 && iblockdata.getBlock().toLegacyData(iblockdata) != i) {
					throw new CommandException("commands.execute.failed", "detect", entity.getName());
				}

				b0 = 10;
			}

			String s = a(astring, b0);
			ICommandListener icommandlistener1 = new ICommandListener() {
				@Override
				public String getName() {
					return entity.getName();
				}

				@Override
				public IChatBaseComponent getScoreboardDisplayName() {
					return entity.getScoreboardDisplayName();
				}

				@Override
				public void sendMessage(IChatBaseComponent ichatbasecomponent) {
					icommandlistener.sendMessage(ichatbasecomponent);
				}

				@Override
				public boolean a(int i, String s) {
					return icommandlistener.a(i, s);
				}

				@Override
				public BlockPosition getChunkCoordinates() {
					return blockposition;
				}

				@Override
				public Vec3D d() {
					return new Vec3D(d0, d1, d2);
				}

				@Override
				public World getWorld() {
					return entity.world;
				}

				@Override
				public Entity f() {
					return entity;
				}

				@Override
				public boolean getSendCommandFeedback() {
					MinecraftServer minecraftserver = MinecraftServer.getServer();

					return minecraftserver == null
							|| minecraftserver.worldServer[0].getGameRules().getBoolean("commandBlockOutput");
				}

				@Override
				public void a(CommandObjectiveExecutor.EnumCommandResult commandobjectiveexecutor_enumcommandresult,
						int i) {
					entity.a(commandobjectiveexecutor_enumcommandresult, i);
				}
			};
			ICommandHandler icommandhandler = MinecraftServer.getServer().getCommandHandler();

			try {
				// CraftBukkit start
				org.bukkit.command.CommandSender sender = null;
				if (icommandlistener instanceof DedicatedServer) {
					sender = MinecraftServer.getServer().server.getConsoleSender();
				} else if (icommandlistener instanceof CommandBlockListenerAbstract) {
					sender = ((CommandBlockListenerAbstract) icommandlistener).sender;
				} else if (VanillaCommandWrapper.lastSender != null) {
					sender = VanillaCommandWrapper.lastSender;
				} else if (icommandlistener.f() != null) {
					sender = icommandlistener.f().getBukkitEntity();
				} else {
					throw new CommandException("Unhandled executor " + icommandlistener.getClass().getSimpleName()
					);
				}
				int j = CommandBlockListenerAbstract.executeCommand(icommandlistener1,
						new ProxiedNativeCommandSender(icommandlistener1, sender, entity.getBukkitEntity()), s);
				// CraftBukkit end

				if (j < 1) {
					throw new CommandException("commands.execute.allInvocationsFailed", s);
				}
			} catch (Throwable throwable) {
				// CraftBukkit start
				if (throwable instanceof CommandException) {
					throw (CommandException) throwable;
				}
				// CraftBukkit end
				throw new CommandException("commands.execute.failed", s, entity.getName());
			}
		}
	}

	@Override
	public List<String> tabComplete(ICommandListener icommandlistener, String[] astring, BlockPosition blockposition) {
		return astring.length == 1 ? a(astring, MinecraftServer.getServer().getPlayers())
				: (astring.length > 1 && astring.length <= 4 ? a(astring, 1, blockposition)
						: (astring.length > 5 && astring.length <= 8 && "detect".equals(astring[4])
								? a(astring, 5, blockposition)
								: (astring.length == 9 && "detect".equals(astring[4])
										? a(astring, Block.REGISTRY.keySet())
										: null)));
	}

	@Override
	public boolean isListStart(String[] astring, int i) {
		return i == 0;
	}

	// CraftBukkit start - fix decompiler error
	@Override
	public int compareTo(ICommand o) {
		return a(o);
	}
	// CraftBukkit end
}
