package pl.asie.technotronics.conveyor;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import pl.asie.technotronics.utils.BlockBase;
import pl.asie.technotronics.utils.EntityUtils;

public class BlockConveyorBelt extends BlockBase {
	public IIcon conveyor, conveyorBase;

	public BlockConveyorBelt() {
		super(Material.iron);
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
		setCreativeTab(CreativeTabs.tabTransport);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
		super.onBlockPlacedBy(world, x, y, z, entity, stack);
		world.setBlockMetadataWithNotify(x, y, z, EntityUtils.getFacingDirection(entity).ordinal() - 2, 1);
		updateUpDownMetadata(world, x, y, z);
	}

	protected void updateUpDownMetadata(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z) & 3;
		ForgeDirection dir = getDirection(meta);
		Block n = world.getBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
		if (!(n instanceof BlockConveyorBelt) && world.isAirBlock(x, y + 1, z)) {
			if (world.getBlock(x + dir.offsetX, y + dir.offsetY + 1, z + dir.offsetZ) instanceof BlockConveyorBelt) {
				meta |= 8;
			}
		}
		world.setBlockMetadataWithNotify(x, y, z, meta, 1);
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return conveyor;
	}

	@Override
	public void registerBlockIcons(IIconRegister r) {
		conveyor = r.registerIcon("technotronics:conveyor");
		conveyorBase = r.registerIcon("minecraft:coal_block");
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileConveyorBelt();
	}

	public ForgeDirection getDirection(int metadata) {
		return ForgeDirection.getOrientation((metadata & 3) + 2);
	}
	public ForgeDirection getYDir(int metadata) {
		return metadata >= 8 ? ForgeDirection.UP : (metadata >= 4 ? ForgeDirection.DOWN : ForgeDirection.UNKNOWN);
	}
}
