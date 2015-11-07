package pl.asie.technotronics.utils.network;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import cpw.mods.fml.common.network.NetworkRegistry;

import pl.asie.technotronics.Technotronics;

@ChannelHandler.Sharable
public class PacketHandler extends SimpleChannelInboundHandler<Packet> {
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Packet msg) throws Exception {
		try {
			INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
			EntityPlayer player = Technotronics.proxy.getPlayerFromNetHandler(netHandler);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
