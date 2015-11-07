package pl.asie.technotronics;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;

public class CommonProxy {
	public void initRenderers() {

	}

	public EntityPlayer getPlayerFromNetHandler(INetHandler netHandler) {
		return netHandler instanceof NetHandlerPlayServer ? ((NetHandlerPlayServer) netHandler).playerEntity : null;
	}
}
