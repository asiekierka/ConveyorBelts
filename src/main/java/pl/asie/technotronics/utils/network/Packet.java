package pl.asie.technotronics.utils.network;

import io.netty.buffer.ByteBuf;

public abstract class Packet {
	public abstract int getID();
	public abstract void readData(ByteBuf buf);
	public abstract void writeData(ByteBuf buf);
}
