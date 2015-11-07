package pl.asie.technotronics.utils;

import io.netty.buffer.ByteBuf;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;

import pl.asie.technotronics.Technotronics;
import pl.asie.technotronics.utils.network.Packet;

public final class NetworkUtils {
	private NetworkUtils() {
		
	}
	
	public static void sendToAll(Packet message)
	{
		Technotronics.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
		Technotronics.channels.get(Side.SERVER).writeOutbound(message);
	}

	public static void sendTo(Packet message, EntityPlayerMP player)
	{
		Technotronics.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
		Technotronics.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
		Technotronics.channels.get(Side.SERVER).writeOutbound(message);
	}

	public static void sendToAllAround(Packet message, NetworkRegistry.TargetPoint point)
	{
		Technotronics.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
		Technotronics.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point);
		Technotronics.channels.get(Side.SERVER).writeOutbound(message);
	}

	public static void sendToDimension(Packet message, int dimensionId)
	{
		Technotronics.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION);
		Technotronics.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(dimensionId);
		Technotronics.channels.get(Side.SERVER).writeOutbound(message);
	}

	public static void sendToServer(Packet message)
	{
		Technotronics.channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
		Technotronics.channels.get(Side.CLIENT).writeOutbound(message);
	}

	public static void sendToAllAround(Packet packet, TileEntity entity,
								double d) {
		sendToAllAround(packet, new NetworkRegistry.TargetPoint(entity.getWorldObj().provider.dimensionId, entity.xCoord, entity.yCoord, entity.zCoord, d));
	}

	public static void sendToAllAround(Packet packet, Entity entity,
								double d) {
		sendToAllAround(packet, new NetworkRegistry.TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, d));
	}

	public static ItemStack readStack(ByteBuf data) {
		int stackSize = data.readUnsignedByte();
		if (stackSize > 0) {
			int itemId = data.readUnsignedShort();
			boolean hasNBT = (itemId >= 32768);
			itemId &= 32767;
			int itemDamage = data.readInt();
			ItemStack stack = new ItemStack(Item.getItemById(itemId), stackSize, itemDamage);
			return stack;
		} else {
			return null;
		}
	}

	public static void writeStack(ByteBuf data, ItemStack stack) {
		if (stack != null && stack.stackSize > 0) {
			data.writeByte(stack.stackSize);
			data.writeShort(Item.getIdFromItem(stack.getItem()) | (stack.hasTagCompound() ? 32768 : 0));
			data.writeInt(stack.getItemDamage());
		} else {
			data.writeByte(0);
		}
	}
}
