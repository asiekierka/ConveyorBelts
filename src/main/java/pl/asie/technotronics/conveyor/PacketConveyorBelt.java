package pl.asie.technotronics.conveyor;

import io.netty.buffer.ByteBuf;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import pl.asie.technotronics.utils.network.PacketTile;

public class PacketConveyorBelt extends PacketTile {
	private int mode;
	private ITravellingObjectContainer owner;
	private TravellingObject object;

	public PacketConveyorBelt() {

	}

	public PacketConveyorBelt(ITravellingObjectContainer owner, TravellingObject object, int mode) {
		super((TileEntity) owner);
		this.mode = mode;
		this.owner = owner;
		this.object = object;
	}

	@Override
	public int getID() {
		return 0;
	}

	@Override
	public void readData(ByteBuf buf) {
		super.readData(buf);
		if (getTile() instanceof ITravellingObjectContainer) {
			owner = (ITravellingObjectContainer) getTile();
		} else {
			return;
		}

		mode = buf.readUnsignedByte();
		if (mode == 0) {
			object = TravellingObject.fromPacket(buf);
			if (object != null) {
				object.setOwner(owner);
				owner.getTravellingObjects().add(object);
			}
		} else {
			short objectId = buf.readShort();
			for (TravellingObject o : owner.getTravellingObjects()) {
				if (o.getId() == objectId) {
					object = o;
					break;
				}
			}
			if (object != null) {
				if (mode == 1) {
					object.setOutput(ForgeDirection.getOrientation(buf.readUnsignedByte()));
					object.setPositionToCenter();
				} else if (mode == 2) {
					owner.getTravellingObjects().remove(object);
				}
			}
		}
	}

	@Override
	public void writeData(ByteBuf buf) {
		super.writeData(buf);

		buf.writeByte(mode);
		if (mode == 0) {
			object.writeData(buf);
		} else {
			buf.writeShort(object.getId());
			if (mode == 1) {
				buf.writeByte(object.getOutput().ordinal());
			}
		}
	}
}
