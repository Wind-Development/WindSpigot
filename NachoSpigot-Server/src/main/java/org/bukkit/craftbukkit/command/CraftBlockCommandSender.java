package org.bukkit.craftbukkit.command;

import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.craftbukkit.util.CraftChatMessage;

import net.minecraft.server.CommandBlockListenerAbstract;
import net.minecraft.server.IChatBaseComponent;
import net.minecraft.server.ICommandListener;

/**
 * Represents input from a command block
 */
public class CraftBlockCommandSender extends ServerCommandSender implements BlockCommandSender {
	private final CommandBlockListenerAbstract commandBlock;

	public CraftBlockCommandSender(CommandBlockListenerAbstract commandBlockListenerAbstract) {
		super();
		this.commandBlock = commandBlockListenerAbstract;
	}

	@Override
	public Block getBlock() {
		return commandBlock.getWorld().getWorld().getBlockAt(commandBlock.getChunkCoordinates().getX(),
				commandBlock.getChunkCoordinates().getY(), commandBlock.getChunkCoordinates().getZ());
	}

	@Override
	public void sendMessage(String message) {
		for (IChatBaseComponent component : CraftChatMessage.fromString(message)) {
			commandBlock.sendMessage(component);
		}
	}

	@Override
	public void sendMessage(String[] messages) {
		for (String message : messages) {
			sendMessage(message);
		}
	}

	@Override
	public String getName() {
		return commandBlock.getName();
	}

	@Override
	public boolean isOp() {
		return true;
	}

	@Override
	public void setOp(boolean value) {
		throw new UnsupportedOperationException("Cannot change operator status of a block");
	}

	public ICommandListener getTileEntity() {
		return commandBlock;
	}
}
