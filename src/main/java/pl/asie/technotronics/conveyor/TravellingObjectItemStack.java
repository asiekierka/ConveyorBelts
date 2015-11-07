package pl.asie.technotronics.conveyor;

import io.netty.buffer.ByteBuf;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.common.util.ForgeDirection;

import pl.asie.technotronics.Technotronics;
import pl.asie.technotronics.utils.ItemUtils;
import pl.asie.technotronics.utils.NetworkUtils;
import pl.asie.technotronics.utils.inventory.InventoryUtils;

public class TravellingObjectItemStack extends TravellingObject {
	private ItemStack stack;

	public TravellingObjectItemStack() {
		super();
	}

	public TravellingObjectItemStack(ForgeDirection input, ForgeDirection inputY, ItemStack stack) {
		super(input, inputY);
		this.stack = stack;
	}

	public ItemStack getStack() {
		return stack;
	}

	@Override
	public void readData(ByteBuf data) {
		super.readData(data);
		stack = NetworkUtils.readStack(data);
	}

	@Override
	public void writeData(ByteBuf data) {
		super.writeData(data);
		NetworkUtils.writeStack(data, stack);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		stack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("stack"));
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		NBTTagCompound stackNBT = new NBTTagCompound();
		stack.writeToNBT(stackNBT);
		nbt.setTag("stack", stackNBT);
	}

	@Override
	protected boolean moveTo(ForgeDirection side, boolean simulate) {
		if (super.moveTo(side, simulate)) {
			return true;
		}

		Block block = owner.getNeighborBlock(side);

		if (block.isAir(owner.world(), owner.x() + side.offsetX, owner.y(), owner.z() + side.offsetZ)) {
			if (!simulate) {
				TileEntity t = owner.world().getTileEntity(owner.x() + side.offsetX, owner.y() - 1, owner.z() + side.offsetZ);

				if (t == null || !Technotronics.HOPPERS.contains(t.getClass())) {
					if (stack.tryPlaceItemIntoWorld(FakePlayerFactory.getMinecraft((WorldServer) owner.world()), owner.world(), owner.x() + side.offsetX, owner.y() + side.offsetY, owner.z() + side.offsetZ, side.getOpposite().ordinal(), 0.0F, 0.0F, 0.0F)) {
						return true;
					}
				}

				ItemUtils.spawnItemEntity(owner.world(), owner.x() + 0.5 + side.offsetX, owner.y() + 0.25, owner.z() + 0.5 + side.offsetZ, stack, 0.02F, 0.03F, 0.02F);
			}
			return true;
		} else {
			TileEntity tile = owner.getNeighborTile(side);
			int added = 0;

			if (tile instanceof IInventory) {
				added += InventoryUtils.addStack((IInventory) tile, side.getOpposite(), stack.copy(), simulate);
			}

			if (!simulate) {
				stack.stackSize -= added;
				return stack.stackSize == 0;
			}

			return stack.stackSize == added;
		}
	}

}
