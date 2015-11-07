package pl.asie.technotronics.conveyor;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import pl.asie.technotronics.utils.FakeEntityItem;

public class RenderTileConveyorBelt extends TileEntitySpecialRenderer {
	private static final int[] DIR_ROTATION = {0, 0, 0, 180, 90, 270};

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float f) {
		/* TileEntity tile = DimensionManager.getWorld(tiles.getWorldObj().provider.dimensionId).getTileEntity(tiles.xCoord, tiles.yCoord, tiles.zCoord); */
		if (tile == null) {
			return;
		}

		for (TravellingObject o : ((TileConveyorBelt) tile).getTravellingObjects()) {
			if (o instanceof TravellingObjectItemStack) {
				ItemStack stack = ((TravellingObjectItemStack) o).getStack();
				GL11.glPushMatrix();

				if (stack.getItem() instanceof ItemBlock) {
					GL11.glTranslated(x + 0.5 + o.getX(), y + 0.375 + o.getY(), z + 0.5 + o.getZ());
					GL11.glScalef(0.749F, 0.749F, 0.749F);
					GL11.glRotatef(DIR_ROTATION[o.getMovementDir().ordinal()], 0, 1, 0);

					bindTexture(TextureMap.locationBlocksTexture);
					RenderBlocks.getInstance().renderBlockAsItem(Block.getBlockFromItem(stack.getItem()), stack.getItemDamage() & 15, 1.0F);
				} else {
					FakeEntityItem.INSTANCE.setEntityItemStack(stack);

					GL11.glTranslated(x + 0.5 + o.getX() - o.getMovementDir().offsetX * 0.25, y + 0.0626 + o.getY(), z + 0.5 + o.getZ() - o.getMovementDir().offsetZ * 0.25);
					GL11.glRotatef(DIR_ROTATION[o.getMovementDir().ordinal()] - 180, 0, 1, 0);
					GL11.glRotatef(90.0F, 1, 0, 0);
					GL11.glScalef(1.329F, 1.329F, 1.329F);

					FakeEntityItem.RENDERER.doRender(FakeEntityItem.INSTANCE, 0, 0, 0, 0.0F, 0.0F);
				}

				GL11.glPopMatrix();
			}
		}
	}
}
