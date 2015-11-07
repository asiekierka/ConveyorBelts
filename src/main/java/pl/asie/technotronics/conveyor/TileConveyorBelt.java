package pl.asie.technotronics.conveyor;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import pl.asie.technotronics.Technotronics;
import pl.asie.technotronics.utils.inventory.InventoryIterator;
import pl.asie.technotronics.utils.inventory.InventorySlot;

// the fakest inventory in the world!
public class TileConveyorBelt extends TileEntity implements ITravellingObjectContainer, IInventory, ISidedInventory {
	private static final int[] SLOTS_OK = new int[] { 0 };
	private static final int[] SLOTS_NOT_OK = new int[] { };
	private final Set<TravellingObject> travelingObjects = new HashSet<TravellingObject>();
	private TileEntity takenBy;
	private int takenByTicks;
	private int ticker = Technotronics.RANDOM.nextInt(256);

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		NBTTagList tags = new NBTTagList();
		for (TravellingObject o : travelingObjects) {
			NBTTagCompound c = new NBTTagCompound();
			o.writeToNBT(c);
			tags.appendTag(c);
		}
		nbt.setTag("objects", tags);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		travelingObjects.clear();
		NBTTagList tags = nbt.getTagList("objects", 10);
		for (int i = 0; i < tags.tagCount(); i++) {
			TravellingObject o = TravellingObject.fromNBT(tags.getCompoundTagAt(i));
			if (o != null) {
				o.setOwner(this);
				travelingObjects.add(o);
			}
		}
	}

	@Override
	public Set<TravellingObject> getTravellingObjects() {
		return travelingObjects;
	}

	private ForgeDirection getDirection() {
		return ((BlockConveyorBelt) getBlockType()).getDirection(getBlockMetadata());
	}

	private ForgeDirection getYDirection() {
		return ((BlockConveyorBelt) getBlockType()).getYDir(getBlockMetadata());
	}

	private void tryExtract() {
		ForgeDirection dir = getDirection();
		TileEntity inTile = worldObj.getTileEntity(xCoord - dir.offsetX, yCoord - dir.offsetY, zCoord - dir.offsetZ);
		if (inTile instanceof IInventory) {
			InventoryIterator iter = new InventoryIterator((IInventory) inTile, dir.getOpposite());
			while (iter.hasNext()) {
				InventorySlot slot = iter.next();
				ItemStack stack = slot.remove(1, false);
				if (stack != null) {
					TravellingObject o = new TravellingObjectItemStack(dir.getOpposite(), getYDirection(), stack);
					injectObject(o, dir.getOpposite(), false, false);
				}
			}
		}
	}

	@Override
	public void updateEntity() {
		ticker++;

		if (!worldObj.isRemote) {
			if (takenByTicks > 1) {
				takenByTicks--;
			} else if (takenByTicks == 1) {
				takenByTicks--;
				takenBy = null;
			}

			if (travelingObjects.size() == 0) {
				tryExtract();

				if (travelingObjects.size() == 0) {
					if ((ticker % 16) == 0) {
						List<EntityItem> items = worldObj.getEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.getBoundingBox(
								xCoord, yCoord + 0.0625, zCoord, xCoord + 1, yCoord + 0.5625, zCoord + 1
						));
						if (items.size() > 0) {
							EntityItem item = items.get(0);
							ItemStack stack = item.getEntityItem();
							if (stack.stackSize > 1) {
								stack = stack.copy();
								stack.stackSize = 1;
							}
							TravellingObject o = new TravellingObjectItemStack(ForgeDirection.UNKNOWN, getYDirection(), stack);
							if (injectObject(o, ForgeDirection.UNKNOWN, false, false)) {
								if (item.getEntityItem().stackSize <= 1) {
									item.setDead();
								} else {
									item.getEntityItem().stackSize--;
								}
							}
						}
					}
				}
			}
		}

		Iterator<TravellingObject> objectIterator = travelingObjects.iterator();
		while (objectIterator.hasNext()) {
			TravellingObject o = objectIterator.next();
			o.tick();
			if (o.reachedEnd()) {
				if (o.onRemove()) {
					objectIterator.remove();
					ConveyorPacketManager.INSTANCE.despawnItem(this, o);
				}
			}
		}
	}

	protected boolean injectObject(TravellingObject object, ForgeDirection side, boolean reset, boolean simulate) {
		if (worldObj.isRemote) {
			return false;
		}

		if (simulate) {
			if (takenByTicks > 1 && (takenBy == null || !takenBy.isInvalid())) {
				return false;
			}

			for (TravellingObject o : travelingObjects) {
				if (!o.isMovingToEnd()) {
					return false;
				}
			}
		} else {
			if (travelingObjects.size() > 0) {
				return false;
			}
		}

		if (!simulate) {
			if (reset) {
				object.reset(side, getYDirection());
			}
			object.setAllowedOutputs(new ForgeDirection[]{getDirection()});
			object.setOwner(this);
			travelingObjects.add(object);
			if (!worldObj.isRemote) {
				ConveyorPacketManager.INSTANCE.insertItem(this, object);
			}
			takenBy = null;
			takenByTicks = 0;
		} else {
			takenBy = getNeighborTile(side);
			takenByTicks = 33;
		}
		return true;
	}

	@Override
	public World world() {
		return worldObj;
	}

	@Override
	public int x() {
		return xCoord;
	}

	@Override
	public int y() {
		return yCoord;
	}

	@Override
	public int z() {
		return zCoord;
	}

	@Override
	public float speed() {
		return (1.0F / 32.0F);
	}

	@Override
	public Block getNeighborBlock(ForgeDirection side) {
		return worldObj.getBlock(xCoord + side.offsetX, yCoord + getYDirection().offsetY, zCoord + side.offsetZ);
	}

	@Override
	public TileEntity getNeighborTile(ForgeDirection side) {
		return worldObj.getTileEntity(xCoord + side.offsetX, yCoord + getYDirection().offsetY, zCoord + side.offsetZ);
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return null;
	}

	@Override
	public ItemStack decrStackSize(int slot, int count) {
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		TravellingObject o = new TravellingObjectItemStack(ForgeDirection.UNKNOWN, getYDirection(), stack);
		injectObject(o, ForgeDirection.UNKNOWN, false, false);
	}

	@Override
	public String getInventoryName() {
		return null;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
		return true;
	}

	@Override
	public void openInventory() {

	}

	@Override
	public void closeInventory() {

	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		TravellingObject o = new TravellingObjectItemStack(ForgeDirection.UNKNOWN, getYDirection(), stack);
		return injectObject(o, ForgeDirection.UNKNOWN, false, true);
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return side > 0 ? SLOTS_OK : SLOTS_NOT_OK;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {
		return side > 0;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {
		return false;
	}
}
