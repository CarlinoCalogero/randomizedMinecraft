package net.nerdshelf.randomizedminecraft.recipe;

import java.util.Set;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.nerdshelf.randomizedminecraft.block.entity.custom.AbstractCurrencyFurnaceBlockEntity;

@OnlyIn(Dist.CLIENT)
public class CurrencySmeltingRecipeBookComponent extends AbstractCurrencyFurnaceRecipeBookComponent {
	private static final Component FILTER_NAME = Component.translatable("gui.recipebook.toggleRecipes.smeltable");

	protected Component getRecipeFilterName() {
		return FILTER_NAME;
	}

	protected Set<Item> getFuelItems() {
		return AbstractCurrencyFurnaceBlockEntity.getFuel().keySet();
	}
}