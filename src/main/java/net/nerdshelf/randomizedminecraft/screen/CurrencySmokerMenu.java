package net.nerdshelf.randomizedminecraft.screen;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.RecipeType;
import net.nerdshelf.randomizedminecraft.screen.abstractcurrencyfurnace.AbstractCurrencyFurnaceMenu;

public class CurrencySmokerMenu extends AbstractCurrencyFurnaceMenu {
	public CurrencySmokerMenu(int p_40274_, Inventory p_40275_) {
		super(ModMenuTypes.CURRENCY_SMOKER_MENU.get(), RecipeType.SMOKING, RecipeBookType.SMOKER, p_40274_, p_40275_);
	}

	public CurrencySmokerMenu(int p_40277_, Inventory p_40278_, Container p_40279_, ContainerData p_40280_) {
		super(ModMenuTypes.CURRENCY_SMOKER_MENU.get(), RecipeType.SMOKING, RecipeBookType.SMOKER, p_40277_, p_40278_,
				p_40279_, p_40280_);
	}

	public CurrencySmokerMenu(int p_39532_, Inventory p_39533_, FriendlyByteBuf extraData) {
		super(ModMenuTypes.CURRENCY_SMOKER_MENU.get(), RecipeType.SMOKING, RecipeBookType.SMOKER, p_39532_, p_39533_);
	}

}