package pl.asie.technotronics.utils;

import net.minecraft.block.Block;
import net.minecraftforge.oredict.OreDictionary;

public final class BlockKey {
	public final Block block;
	public final int meta;

	public BlockKey(Block block) {
		this(block, OreDictionary.WILDCARD_VALUE);
	}

	public BlockKey(Block block, int meta) {
		this.block = block;
		this.meta = meta;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof BlockKey)) {
			return false;
		}

		BlockKey other = (BlockKey) o;
		if (other.block != block) {
			return false;
		}

		return other.meta == OreDictionary.WILDCARD_VALUE || meta == OreDictionary.WILDCARD_VALUE || other.meta == meta;
	}

	@Override
	public int hashCode() {
		return Block.getIdFromBlock(block) * 17 + meta;
	}
}
