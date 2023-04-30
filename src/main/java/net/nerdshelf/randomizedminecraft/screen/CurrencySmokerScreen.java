package net.nerdshelf.randomizedminecraft.screen;

import net.minecraft.client.gui.screens.recipebook.SmokingRecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.nerdshelf.randomizedminecraft.screen.abstractcurrencyfurnace.AbstractCurrencyFurnaceScreen;

@OnlyIn(Dist.CLIENT)
public class CurrencySmokerScreen extends AbstractCurrencyFurnaceScreen<CurrencySmokerMenu> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/smoker.png");

	public CurrencySmokerScreen(CurrencySmokerMenu p_99300_, Inventory p_99301_, Component p_99302_) {
		super(p_99300_, new SmokingRecipeBookComponent(), p_99301_, p_99302_, TEXTURE);
	}
}