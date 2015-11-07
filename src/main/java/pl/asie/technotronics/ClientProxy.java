package pl.asie.technotronics;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import cpw.mods.fml.client.registry.ClientRegistry;

import pl.asie.technotronics.conveyor.RenderBlockConveyorBelt;
import pl.asie.technotronics.conveyor.RenderTileConveyorBelt;
import pl.asie.technotronics.conveyor.TileConveyorBelt;

/**
 * Created by asie on 8/31/15.
 */
public class ClientProxy extends CommonProxy {
	@Override
	public void initRenderers() {
		Technotronics.conveyorBelt.setRenderer(new RenderBlockConveyorBelt());
		ClientRegistry.bindTileEntitySpecialRenderer(TileConveyorBelt.class, new RenderTileConveyorBelt());
	}

	@Override
	public EntityPlayer getPlayerFromNetHandler(INetHandler netHandler) {
		EntityPlayer player = super.getPlayerFromNetHandler(netHandler);
		return player != null ? player : Minecraft.getMinecraft().thePlayer;
	}
}
