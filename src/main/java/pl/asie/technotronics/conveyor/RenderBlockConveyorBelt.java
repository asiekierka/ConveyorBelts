package pl.asie.technotronics.conveyor;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

import pl.asie.technotronics.utils.BlockRenderingHandlerBase;
import pl.asie.technotronics.utils.FakeBlock;

public class RenderBlockConveyorBelt extends BlockRenderingHandlerBase {
	private static final int[][] UV_TOP = {
			{0, 1, 2, 3},
			{2, 3, 0, 1},
			{1, 0, 3, 2},
			{3, 2, 1, 0}
	};

	// TODO: Rewrite eventually
	// or slopes won't render
	// but who cares about slopes? seriously man

	@Override
	public boolean renderBlock(IBlockAccess world, int x, int y, int z, Block block, int metadata, RenderBlocks renderer) {
		BlockConveyorBelt cb = (BlockConveyorBelt) block;
		int conveyorDirection = cb.getDirection(metadata).ordinal();
		int conveyorYDir = cb.getYDir(metadata).ordinal();

		if (conveyorDirection >= 4) {
			renderer.setRenderBounds(0.0F, 0.0625F, 0.0625F, 1.0F, 0.125F, 0.9375F);
		} else {
			renderer.setRenderBounds(0.0625F, 0.0625F, 0.0F, 0.9375F, 0.125F, 1.0F);
		}

		float[] coords = new float[] {
				x + (conveyorDirection >= 4 ? 0.0F : 0.0625F),
				y + (conveyorYDir == 0 ? 1.125F : 0.125F),
				z + (conveyorDirection < 4 ? 0.0F : 0.0625F),
				x + (conveyorDirection >= 4 ? 1.0F : 0.9375F),
				y + (conveyorYDir == 1 ? 1.125F : 0.125F),
				z + (conveyorDirection < 4 ? 1.0F : 0.9375F)
		};

		float[] u = new float[] {
				cb.conveyor.getInterpolatedU(1),
				cb.conveyor.getInterpolatedU(1),
				cb.conveyor.getInterpolatedU(14),
				cb.conveyor.getInterpolatedU(14),
		};

		float[] v = new float[]{
				cb.conveyor.getInterpolatedV(1),
				cb.conveyor.getInterpolatedV(14),
				cb.conveyor.getInterpolatedV(14),
				cb.conveyor.getInterpolatedV(1),
		};

		boolean isEW = conveyorDirection >= 4;
		boolean isPos = conveyorDirection == 3 || conveyorDirection == 5;

		Tessellator t = Tessellator.instance;
		t.setColorOpaque_F(1.0f, 1.0f, 1.0f);
		if (isEW) {
			t.addVertexWithUV(coords[0], coords[4 - (isPos ? 3 : 0)], coords[2], u[UV_TOP[metadata & 3][0]], v[UV_TOP[metadata & 3][0]]);
			t.addVertexWithUV(coords[0], coords[4 - (isPos ? 3 : 0)], coords[5], u[UV_TOP[metadata & 3][1]], v[UV_TOP[metadata & 3][1]]);
			t.addVertexWithUV(coords[3], coords[1 + (isPos ? 3 : 0)], coords[5], u[UV_TOP[metadata & 3][2]], v[UV_TOP[metadata & 3][2]]);
			t.addVertexWithUV(coords[3], coords[1 + (isPos ? 3 : 0)], coords[2], u[UV_TOP[metadata & 3][3]], v[UV_TOP[metadata & 3][3]]);
		} else {
			t.addVertexWithUV(coords[0], coords[4 - (isPos ? 3 : 0)], coords[2], u[UV_TOP[metadata & 3][0]], v[UV_TOP[metadata & 3][0]]);
			t.addVertexWithUV(coords[0], coords[1 + (isPos ? 3 : 0)], coords[5], u[UV_TOP[metadata & 3][1]], v[UV_TOP[metadata & 3][1]]);
			t.addVertexWithUV(coords[3], coords[1 + (isPos ? 3 : 0)], coords[5], u[UV_TOP[metadata & 3][2]], v[UV_TOP[metadata & 3][2]]);
			t.addVertexWithUV(coords[3], coords[4 - (isPos ? 3 : 0)], coords[2], u[UV_TOP[metadata & 3][3]], v[UV_TOP[metadata & 3][3]]);
		}

		FakeBlock.INSTANCE.setTexture(cb.conveyorBase);

		if (conveyorDirection >= 4) {
			renderer.setRenderBounds(0.5F - 0.0625F, 0.0F, 0.0625F, 0.5F + 0.0625F, 0.0625F, 0.1875F);
			renderer.renderStandardBlock(FakeBlock.INSTANCE, x, y, z);

			renderer.setRenderBounds(0.5F - 0.0625F, 0.0F, 1.0F - 0.1875F, 0.5F + 0.0625F, 0.0625F, 1.0F - 0.0625F);
			renderer.renderStandardBlock(FakeBlock.INSTANCE, x, y, z);

			FakeBlock.INSTANCE.setRenderMask(0x3E);

			renderer.setRenderBounds(0.0F, 0.125F, 0.0625F, 1.0F, 0.1875F, 0.125F);
			renderer.renderStandardBlockWithColorMultiplier(FakeBlock.INSTANCE, x, y, z, 1.0F, 1.0F, 1.0F); // no AO

			renderer.setRenderBounds(0.0F, 0.125F, 1.0F - 0.125F, 1.0F, 0.1875F, 1.0F - 0.0625F);
			renderer.renderStandardBlockWithColorMultiplier(FakeBlock.INSTANCE, x, y, z, 1.0F, 1.0F, 1.0F); // no AO
		} else {
			renderer.setRenderBounds(0.0625F, 0.0F, 0.5F - 0.0625F, 0.1875F, 0.0625F, 0.5F + 0.0625F);
			renderer.renderStandardBlock(FakeBlock.INSTANCE, x, y, z);

			renderer.setRenderBounds(1.0F - 0.1875F, 0.0F, 0.5F - 0.0625F, 1.0F - 0.0625F, 0.0625F, 0.5F + 0.0625F);
			renderer.renderStandardBlock(FakeBlock.INSTANCE, x, y, z);

			FakeBlock.INSTANCE.setRenderMask(0x3E);

			renderer.setRenderBounds(0.0625F, 0.125F, 0.0F, 0.125F, 0.1875F, 1.0F);
			renderer.renderStandardBlockWithColorMultiplier(FakeBlock.INSTANCE, x, y, z, 1.0F, 1.0F, 1.0F); // no AO

			renderer.setRenderBounds(1.0F - 0.125F, 0.125F, 0.0F, 1.0F - 0.0625F, 0.1875F, 1.0F);
			renderer.renderStandardBlockWithColorMultiplier(FakeBlock.INSTANCE, x, y, z, 1.0F, 1.0F, 1.0F); // no AO
		}

		renderer.uvRotateTop = renderer.uvRotateBottom = 0;

		return true;
	}
}
