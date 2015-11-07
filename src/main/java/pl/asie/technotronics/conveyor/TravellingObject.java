package pl.asie.technotronics.conveyor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.netty.buffer.ByteBuf;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import pl.asie.technotronics.Technotronics;

public abstract class TravellingObject {
	public static final BiMap<Integer, Class<? extends TravellingObject>> CLASS_MAP = HashBiMap.create();
	private static short maxId = (short) Technotronics.RANDOM.nextInt(2000);

	protected ForgeDirection input, inputY;
	protected ForgeDirection output;
	protected ITravellingObjectContainer owner;

	private short id;
	private ForgeDirection[] allowedOutputs;
	private boolean metCenter, reached;
	private float dX, dY, dZ;

	public TravellingObject() {
		id = maxId++;
	}

	public TravellingObject(ForgeDirection input, ForgeDirection inputY) {
		this();
		reset(input, inputY);
	}

	public short getId() {
		return id;
	}

	public static TravellingObject fromNBT(NBTTagCompound nbt) {
		Class<? extends TravellingObject> cls = CLASS_MAP.get(new Integer(nbt.getByte("type")));
		if (cls != null) {
			try {
				TravellingObject o = cls.newInstance();
				o.readFromNBT(nbt);
				return o;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static TravellingObject fromPacket(ByteBuf buf) {
		Class<? extends TravellingObject> cls = CLASS_MAP.get(new Integer(buf.readUnsignedByte()));
		if (cls != null) {
			try {
				TravellingObject o = cls.newInstance();
				o.readData(buf);
				return o;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public void readData(ByteBuf data) {
		id = data.readShort();
		parseData(data.readUnsignedShort(), data.readUnsignedByte());
		dX = data.readFloat();
		dY = data.readFloat();
		dZ = data.readFloat();
	}

	public void writeData(ByteBuf data) {
		data.writeByte(getDataTypeID());
		data.writeShort(id);
		data.writeShort(getDataFlags());
		data.writeByte(getDataSides());
		data.writeFloat(dX);
		data.writeFloat(dY);
		data.writeFloat(dZ);
	}

	private void parseData(int flags, int sides) {
		input = ForgeDirection.getOrientation(flags & 7);
		inputY = ForgeDirection.getOrientation((flags >> 3) & 7);
		output = ForgeDirection.getOrientation((flags >> 6) & 7);
		metCenter = ((flags >> 9) & 1) != 0;
		reached = ((flags >> 10) & 1) != 0;

		Set<ForgeDirection> dirs = EnumSet.noneOf(ForgeDirection.class);
		for (int i = 0; i <= 6; i++) {
			if ((sides & (1 << i)) != 0) {
				dirs.add(ForgeDirection.getOrientation(i));
			}
		}
		allowedOutputs = dirs.toArray(new ForgeDirection[dirs.size()]);
	}

	private int getDataFlags() {
		int flags = 0;
		flags |= input.ordinal() | (inputY.ordinal() << 3) | (output.ordinal() << 6);
		flags |= metCenter ? (1 << 9) : 0;
		flags |= reached ? (1 << 10) : 0;
		return flags;
	}

	private int getDataSides() {
		int sides = 0;
		for (ForgeDirection d : allowedOutputs) {
			sides |= 1 << d.ordinal();
		}
		return sides;
	}

	private byte getDataTypeID() {
		return CLASS_MAP.inverse().get(this.getClass()).byteValue();
	}

	public void readFromNBT(NBTTagCompound nbt) {
		parseData(nbt.getShort("flags"), nbt.getByte("outputs"));

		dX = nbt.getFloat("x");
		dY = nbt.getFloat("y");
		dZ = nbt.getFloat("z");
	}

	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setByte("type", getDataTypeID());
		nbt.setShort("flags", (short) getDataFlags());
		nbt.setByte("outputs", (byte) getDataSides());

		nbt.setFloat("x", dX);
		nbt.setFloat("y", dY);
		nbt.setFloat("z", dZ);
	}

	public boolean reachedCenter() {
		return metCenter;
	}

	public boolean isMovingToEnd() {
		return metCenter && output != ForgeDirection.UNKNOWN;
	}

	public void setOwner(ITravellingObjectContainer owner) {
		this.owner = owner;
	}

	public void reset(ForgeDirection input, ForgeDirection inputY) {
		this.input = input;
		this.inputY = inputY;
		this.output = ForgeDirection.UNKNOWN;
		this.metCenter = input == ForgeDirection.UNKNOWN;
		if (this.metCenter) {
			setPositionToCenter();
		} else {
			this.dX = input.offsetX * 0.5F;
			this.dY = inputY == ForgeDirection.DOWN ? 1.125F : 0.125F;
			this.dZ = input.offsetZ * 0.5F;
		}
		this.reached = false;
	}

	public void setAllowedOutputs(ForgeDirection[] outputs) {
		this.allowedOutputs = outputs;
	}

	public boolean reachedEnd() {
		return reached;
	}

	public ForgeDirection getMovementDir() {
		return metCenter && output != ForgeDirection.UNKNOWN ? output : (input != ForgeDirection.UNKNOWN ? input.getOpposite() : (allowedOutputs.length > 0 ? allowedOutputs[0] : ForgeDirection.EAST));
	}

	public ForgeDirection getInput() {
		return input;
	}

	public ForgeDirection getInputHeight() {
		return inputY;
	}

	public ForgeDirection getOutput() {
		return output;
	}

	public float getX() {
		return dX;
	}

	public float getY() {
		return dY;
	}

	public float getZ() {
		return dZ;
	}

	public boolean tick() {
		if (reached) {
			return true;
		}

		if (metCenter && output == ForgeDirection.UNKNOWN) {
			chooseOutput();
			if (output == ForgeDirection.UNKNOWN) {
				return false;
			} else if (!owner.world().isRemote) {
				ConveyorPacketManager.INSTANCE.updateItemOutput(owner, this);
			}
		}

		this.dY += inputY.offsetY * owner.speed();

		if (metCenter) {
			this.dX += output.offsetX * owner.speed();
			this.dZ += output.offsetZ * owner.speed();

			if (Math.abs(dX) >= Math.abs(output.offsetX * 0.5F) && Math.abs(dZ) >= Math.abs(output.offsetZ * 0.5F)) {
				dX = output.offsetX * 0.5F;
				dY = inputY == ForgeDirection.UP ? 1.125F : 0.125F;
				dZ = output.offsetZ * 0.5F;
				reached = true;
				return true;
			}
		} else {
			float oldX = dX;
			float oldZ = dZ;

			this.dX -= input.offsetX * owner.speed();
			this.dZ -= input.offsetZ * owner.speed();

			if ((dX == 0.0F || ((oldX != 0.0F && oldX * dX <= 0))) && (dZ == 0.0F || (oldZ != 0.0F && oldZ * dZ <= 0))) {
				setPositionToCenter();
				metCenter = true;
			}
		}

		return false;
	}

	protected boolean chooseOutput() {
		List<ForgeDirection> conveyorSides = new ArrayList<ForgeDirection>();
		List<ForgeDirection> machineSides = new ArrayList<ForgeDirection>();
		List<ForgeDirection> emptySides = new ArrayList<ForgeDirection>();
		List<ForgeDirection> sides;

		for (ForgeDirection d : allowedOutputs) {
			if (moveTo(d, true)) {
				TileEntity tile = owner.getNeighborTile(d);
				if (tile instanceof ITravellingObjectContainer) {
					conveyorSides.add(d);
				} else if (tile != null) {
					machineSides.add(d);
				} else {
					emptySides.add(d);
				}
			}
		}

		sides = machineSides.size() > 0 ? machineSides : (conveyorSides.size() > 0 ? conveyorSides : emptySides);
		if (sides.size() > 0) {
			Collections.shuffle(sides);
			output = sides.get(0);
			return true;
		}
		return false;
	}

	protected boolean onRemove() {
		return moveTo(getOutput(), false);
	}

	protected boolean moveTo(ForgeDirection side, boolean simulate) {
		TileEntity tile = owner.getNeighborTile(side);
		if (tile instanceof TileConveyorBelt) {
			return ((TileConveyorBelt) tile).injectObject(this, output.getOpposite(), true, simulate);
		}
		return false;
	}

	protected void setOutput(ForgeDirection output) {
		this.output = output;
	}

	protected void setPositionToCenter() {
		dX = 0.0F;
		dZ = 0.0F;
		dY = inputY != ForgeDirection.UNKNOWN ? 0.625F : 0.125F;
	}
}
