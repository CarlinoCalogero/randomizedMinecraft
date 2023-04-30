package net.nerdshelf.randomizedminecraft.screen;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.RecipeType;
import net.nerdshelf.randomizedminecraft.screen.abstractcurrencyfurnace.AbstractCurrencyFurnaceMenu;

public class CurrencyFurnaceMenu extends AbstractCurrencyFurnaceMenu {

	public CurrencyFurnaceMenu(int p_39532_, Inventory p_39533_) {
		super(ModMenuTypes.CURRENCY_FURNACE_MENU.get(), RecipeType.SMELTING, RecipeBookType.FURNACE, p_39532_,
				p_39533_);
	}

	public CurrencyFurnaceMenu(int p_39535_, Inventory p_39536_, Container p_39537_, ContainerData p_39538_) {
		super(ModMenuTypes.CURRENCY_FURNACE_MENU.get(), RecipeType.SMELTING, RecipeBookType.FURNACE, p_39535_, p_39536_,
				p_39537_, p_39538_);
	}
	
	public CurrencyFurnaceMenu(int p_39532_, Inventory p_39533_, FriendlyByteBuf extraData) {
		super(ModMenuTypes.CURRENCY_FURNACE_MENU.get(), RecipeType.SMELTING, RecipeBookType.FURNACE, p_39532_,
				p_39533_);
	}
}