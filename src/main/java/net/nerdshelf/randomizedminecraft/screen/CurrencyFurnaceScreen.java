package net.nerdshelf.randomizedminecraft.screen;

import net.minecraft.client.gui.screens.recipebook.SmeltingRecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.nerdshelf.randomizedminecraft.screen.abstractcurrencyfurnace.AbstractCurrencyFurnaceScreen;

@OnlyIn(Dist.CLIENT)
public class CurrencyFurnaceScreen extends AbstractCurrencyFurnaceScreen<CurrencyFurnaceMenu> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/furnace.png");

	public CurrencyFurnaceScreen(CurrencyFurnaceMenu p_98776_, Inventory p_98777_, Component p_98778_) {
		super(p_98776_, new SmeltingRecipeBookComponent(), p_98777_, p_98778_, TEXTURE);
	}
}