package net.nerdshelf.randomizedminecraft.recipe;

import java.util.Set;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.nerdshelf.randomizedminecraft.block.entity.AbstractCurrencyFurnaceBlockEntity;

@OnlyIn(Dist.CLIENT)
public class CurrencyBlastingRecipeBookComponent extends AbstractCurrencyFurnaceRecipeBookComponent {
	private static final Component FILTER_NAME = Component.translatable("gui.recipebook.toggleRecipes.blastable");

	protected Component getRecipeFilterName() {
		return FILTER_NAME;
	}

	protected Set<Item> getFuelItems() {
		return AbstractCurrencyFurnaceBlockEntity.getFuel().keySet();
	}
}