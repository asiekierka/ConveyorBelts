package pl.asie.technotronics.utils;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

public final class EntityUtils {
	private EntityUtils() {

	}

	public static ForgeDirection getFacingDirection(EntityLivingBase entity) {
		ForgeDirection[] orientationTable = {
				ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.NORTH, ForgeDirection.EAST
		};
		int orientationIndex = MathHelper.floor_double((entity.rotationYaw / 90.0) + 0.5) & 3;
		return orientationTable[orientationIndex];
	}
}
