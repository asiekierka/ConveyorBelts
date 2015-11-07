package pl.asie.technotronics.utils.inventory;

import net.minecraft.item.ItemStack;

public interface IStackFilter {
	boolean matches(ItemStack stack);
}
