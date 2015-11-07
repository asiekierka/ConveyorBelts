package pl.asie.technotronics.utils;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public final class ItemUtils {
	private ItemUtils() {
		
	}
	
	public static void spawnItemEntity(World world, double x, double y, double z, ItemStack stack, float mXm, float mYm, float mZm) {
		EntityItem entityItem = new EntityItem(world, x, y, z, stack);
		entityItem.delayBeforeCanPickup = 10;
		entityItem.lifespan = 6000;
		entityItem.motionX = (world.rand.nextDouble() - 0.5) * 2 * mXm;
		entityItem.motionY = (world.rand.nextDouble() - 0.5) * 2 * mYm;
		entityItem.motionZ = (world.rand.nextDouble() - 0.5) * 2 * mZm;
		world.spawnEntityInWorld(entityItem);
	}
}
