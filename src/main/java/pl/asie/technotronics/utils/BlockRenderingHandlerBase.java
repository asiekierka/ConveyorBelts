package pl.asie.technotronics.utils;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public abstract class BlockRenderingHandlerBase implements ISimpleBlockRenderingHandler {
	private final int renderId;

	public BlockRenderingHandlerBase() {
		renderId = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(this);
	}

	public abstract boolean renderBlock(IBlockAccess world, int x, int y, int z, Block block, int metadata, RenderBlocks renderer);

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
		IBlockAccess oldAccess = renderer.blockAccess;
		NullBlockAccess.INSTANCE.block = block;
		NullBlockAccess.INSTANCE.metadata = metadata;

		renderer.blockAccess = NullBlockAccess.INSTANCE;
		renderBlock(null, 0, 0, 0, block, metadata, renderer);
		renderer.blockAccess = oldAccess;
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		return renderBlock(world, x, y, z, block, world.getBlockMetadata(x, y, z), renderer);
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return false;
	}

	@Override
	public int getRenderId() {
		return renderId;
	}
}
