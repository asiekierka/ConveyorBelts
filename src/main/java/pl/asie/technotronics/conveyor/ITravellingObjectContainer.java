package pl.asie.technotronics.conveyor;

import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public interface ITravellingObjectContainer {
	Collection<TravellingObject> getTravellingObjects();
	World world();
	int x();
	int y();
	int z();
	float speed();
	Block getNeighborBlock(ForgeDirection side);
	TileEntity getNeighborTile(ForgeDirection side);
}
