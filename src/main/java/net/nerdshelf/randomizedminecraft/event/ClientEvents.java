package net.nerdshelf.randomizedminecraft.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.nerdshelf.randomizedminecraft.RandomizedMinecraftMod;
import net.nerdshelf.randomizedminecraft.client.CurrencyHudOverlay;

public class ClientEvents {

	@Mod.EventBusSubscriber(modid = RandomizedMinecraftMod.MOD_ID, value = Dist.CLIENT)
	public static class ClientForgeEvents {

	}

	@Mod.EventBusSubscriber(modid = RandomizedMinecraftMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class ClientModBusEvents {

		@SubscribeEvent
		public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
			event.registerAboveAll("currency", CurrencyHudOverlay.HUD_CURRENCY);
		}

	}
}
