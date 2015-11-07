package pl.asie.technotronics.utils;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public final class FakeBlock extends Block {
	public static final FakeBlock INSTANCE = new FakeBlock();

	private int sideMask = 0x3F;
	private IIcon texture;
	private IIcon[] textureArray;

	private FakeBlock() {
		super(Material.circuits);
	}

	@Override
	public IIcon getIcon(int side, int metadata) {
		return textureArray != null ? textureArray[side % textureArray.length] : texture;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess access, int x, int y, int z, int side) {
		return (sideMask & (1 << side)) != 0;
	}

	public void setRenderMask(int sideMask) {
		this.sideMask = sideMask;
	}

	public void setRenderAllSides() {
		this.sideMask = 0x3F;
	}

	public void setRenderOneSide(int side) {
		this.sideMask = 1 << side;
	}

	public void setTexture(IIcon texture) {
		textureArray = null;
		this.texture = texture;
	}

	public void setTextureArray(IIcon[] textureArray) {
		if (textureArray.length == 6) {
			this.textureArray = textureArray;
		}
	}
}
