package net.nerdshelf.randomizedminecraft.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class CurrencyHudOverlay {

	public static final IGuiOverlay HUD_CURRENCY = ((gui, poseStack, partialTick, width, height) -> {
		int x = width / 2;
		int y = height;

		Minecraft minecraft = Minecraft.getInstance();

		GuiComponent.drawString(poseStack, minecraft.font,
				Component.literal("+" + ClientCurrencyData.getPlayerCurrency()).withStyle(ChatFormatting.YELLOW),
				x - 94 + 9, y - 54, 0);

	});

}
