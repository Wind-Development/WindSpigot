package net.minecraft.server;

import java.util.List;

import com.velocitypowered.natives.encryption.VelocityCipher;
import com.velocitypowered.natives.util.MoreByteBufUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

public class PacketDecrypter extends MessageToMessageDecoder<ByteBuf> {
	private final VelocityCipher cipher; // Paper

	public PacketDecrypter(VelocityCipher cipher) { // Paper
		this.cipher = cipher; // Paper
	}

	@Override
	protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list)
			throws Exception {
		// Paper start
		ByteBuf compatible = MoreByteBufUtils.ensureCompatible(channelHandlerContext.alloc(), cipher, byteBuf);
		try {
			cipher.process(compatible);
			list.add(compatible);
		} catch (Exception e) {
			compatible.release(); // compatible will never be used if we throw an exception
			throw e;
		}
		// Paper end
	}

	// Paper start
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) {
		cipher.close();
	}
	// Paper end
}
