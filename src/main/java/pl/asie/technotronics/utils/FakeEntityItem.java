package pl.asie.technotronics.utils;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;

public final class FakeEntityItem extends EntityItem {
	public static final FakeEntityItem INSTANCE = new FakeEntityItem();
	public static final RenderItem RENDERER = new RenderItem() {
		@Override
		public boolean shouldBob() {
			return false;
		}

		@Override
		public boolean shouldSpreadItems() {
			return false;
		}
	};

	static {
		RENDERER.setRenderManager(RenderManager.instance);
	}

	private ItemStack stack;

	private FakeEntityItem() {
		super(null);
		this.hoverStart = 0;
	}

	@Override
	public ItemStack getEntityItem() {
		return this.stack;
	}

	@Override
	public void setEntityItemStack(ItemStack stack) {
		this.stack = stack;
	}
}
