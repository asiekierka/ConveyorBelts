package pl.asie.technotronics.utils.network;

import io.netty.buffer.ByteBuf;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.DimensionManager;

public abstract class PacketTile extends Packet {
	private TileEntity tile;

	public PacketTile() {

	}

	public PacketTile(TileEntity tile) {
		this.tile = tile;
	}

	public TileEntity getTile() {
		return tile;
	}

	@Override
	public void readData(ByteBuf buf) {
		int dimId = buf.readMedium();
		int x = buf.readInt();
		int y = buf.readUnsignedByte();
		int z = buf.readInt();

		World world;

		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			if (Minecraft.getMinecraft().theWorld.provider.dimensionId == dimId) {
				world = Minecraft.getMinecraft().theWorld;
			} else {
				return;
			}
		} else {
			world = DimensionManager.getWorld(dimId);
		}

		if (world != null) {
			tile = world.getTileEntity(x, y, z);
		}
	}

	@Override
	public void writeData(ByteBuf buf) {
		buf.writeMedium(tile != null ? tile.getWorldObj().provider.dimensionId : -9001);
		buf.writeInt(tile != null ? tile.xCoord : 0);
		buf.writeByte(tile != null ? tile.yCoord : 0);
		buf.writeInt(tile != null ? tile.zCoord : 0);
	}
}
