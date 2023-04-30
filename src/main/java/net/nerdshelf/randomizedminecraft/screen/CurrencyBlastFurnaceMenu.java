package net.nerdshelf.randomizedminecraft.screen;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.RecipeType;
import net.nerdshelf.randomizedminecraft.screen.abstractcurrencyfurnace.AbstractCurrencyFurnaceMenu;

public class CurrencyBlastFurnaceMenu extends AbstractCurrencyFurnaceMenu {
	public CurrencyBlastFurnaceMenu(int p_39079_, Inventory p_39080_) {
		super(ModMenuTypes.CURRENCY_BLAST_FURNACE_MENU.get(), RecipeType.BLASTING, RecipeBookType.BLAST_FURNACE,
				p_39079_, p_39080_);
	}

	public CurrencyBlastFurnaceMenu(int p_39082_, Inventory p_39083_, Container p_39084_, ContainerData p_39085_) {
		super(ModMenuTypes.CURRENCY_BLAST_FURNACE_MENU.get(), RecipeType.BLASTING, RecipeBookType.BLAST_FURNACE,
				p_39082_, p_39083_, p_39084_, p_39085_);
	}

	public CurrencyBlastFurnaceMenu(int p_39532_, Inventory p_39533_, FriendlyByteBuf extraData) {
		super(ModMenuTypes.CURRENCY_BLAST_FURNACE_MENU.get(), RecipeType.BLASTING, RecipeBookType.BLAST_FURNACE,
				p_39532_, p_39533_);
	}

}