package pl.asie.technotronics.utils;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

public class NullBlockAccess implements IBlockAccess {
	public static final NullBlockAccess INSTANCE = new NullBlockAccess();
	public Block block;
	public int metadata;

	private NullBlockAccess() {

	}

	@Override
	public Block getBlock(int x, int y, int z) {
		return x == 0 && y == 0 && z == 0 ? block : Blocks.air;
	}

	@Override
	public TileEntity getTileEntity(int p_147438_1_, int p_147438_2_, int p_147438_3_) {
		return null;
	}

	@Override
	public int getLightBrightnessForSkyBlocks(int p_72802_1_, int p_72802_2_, int p_72802_3_, int p_72802_4_) {
		return 0xF000F;
	}

	@Override
	public int getBlockMetadata(int x, int y, int z) {
		return x == 0 && y == 0 && z == 0 ? metadata : 0;
	}
	@Override
	public int isBlockProvidingPowerTo(int p_72879_1_, int p_72879_2_, int p_72879_3_, int p_72879_4_) {
		return 0;
	}

	@Override
	public boolean isAirBlock(int p_147437_1_, int p_147437_2_, int p_147437_3_) {
		return false;
	}

	@Override
	public BiomeGenBase getBiomeGenForCoords(int p_72807_1_, int p_72807_2_) {
		return null;
	}

	@Override
	public int getHeight() {
		return 256;
	}

	@Override
	public boolean extendedLevelsInChunkCache() {
		return false;
	}

	@Override
	public boolean isSideSolid(int x, int y, int z, ForgeDirection side, boolean _default) {
		return false;
	}
}
