package pl.asie.technotronics.conveyor;

import net.minecraft.tileentity.TileEntity;

import pl.asie.technotronics.utils.NetworkUtils;

public class ConveyorPacketManager {
	public static final ConveyorPacketManager INSTANCE = new ConveyorPacketManager();

	private ConveyorPacketManager() {

	}

	public void insertItem(ITravellingObjectContainer container, TravellingObject object) {
		NetworkUtils.sendToAllAround(new PacketConveyorBelt(container, object, 0), (TileEntity) container, 64.0);
	}

	public void despawnItem(ITravellingObjectContainer container, TravellingObject object) {
		NetworkUtils.sendToAllAround(new PacketConveyorBelt(container, object, 2), (TileEntity) container, 64.0);
	}

	public void updateItemOutput(ITravellingObjectContainer container, TravellingObject object) {
		NetworkUtils.sendToAllAround(new PacketConveyorBelt(container, object, 1), (TileEntity) container, 64.0);
	}
}
