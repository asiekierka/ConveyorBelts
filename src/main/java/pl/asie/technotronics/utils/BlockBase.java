package pl.asie.technotronics.utils;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class BlockBase extends BlockContainer {
	private int renderId = 0;

	public BlockBase(Material material) {
		super(material);
	}

	@SideOnly(Side.CLIENT)
	public void setRenderer(ISimpleBlockRenderingHandler handler) {
		renderId = handler.getRenderId();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() {
		return renderId;
	}
}
