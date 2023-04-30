package net.nerdshelf.randomizedminecraft;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.nerdshelf.randomizedminecraft.block.ModBlocks;
import net.nerdshelf.randomizedminecraft.block.entity.ModBlockEntities;
import net.nerdshelf.randomizedminecraft.item.ModItems;
import net.nerdshelf.randomizedminecraft.networking.ModMessages;
import net.nerdshelf.randomizedminecraft.screen.CurrencyAnvilScreen;
import net.nerdshelf.randomizedminecraft.screen.CurrencyBlastFurnaceScreen;
import net.nerdshelf.randomizedminecraft.screen.CurrencyFurnaceScreen;
import net.nerdshelf.randomizedminecraft.screen.CurrencySmokerScreen;
import net.nerdshelf.randomizedminecraft.screen.ModMenuTypes;
import net.nerdshelf.randomizedminecraft.villager.ModVillagers;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(RandomizedMinecraftMod.MOD_ID)
public class RandomizedMinecraftMod {
	// Define mod id in a common place for everything to reference
	public static final String MOD_ID = "randomizedminecraftmod";

	public RandomizedMinecraftMod() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		// Register the Deferred Register to the mod event bus so items get registered
		ModItems.register(modEventBus);

		// Register the Deferred Register to the mod event bus so blocks get registered
		ModBlocks.register(modEventBus);

		// Register the mod villagers
		ModVillagers.register(modEventBus);

		// Register the Deferred Register to the mod event bus so blocks entities get
		// registered
		ModBlockEntities.register(modEventBus);

		// Register the Deferred Register to the mod event bus so menus get registered
		ModMenuTypes.register(modEventBus);

		// Register the commonSetup method for modloading
		modEventBus.addListener(this::commonSetup);

		// Register ourselves for server and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);

	}

	private void commonSetup(final FMLCommonSetupEvent event) {

		event.enqueueWork(() -> {
			ModVillagers.registerPOIs();
		});

		// Register packets
		ModMessages.register();
	}

	// You can use EventBusSubscriber to automatically register all static methods
	// in the class annotated with @SubscribeEvent
	@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class ClientModEvents {
		@SubscribeEvent
		public static void onClientSetup(FMLClientSetupEvent event) {

			MenuScreens.register(ModMenuTypes.CURRENCY_ANVIL_MENU.get(), CurrencyAnvilScreen::new);
			MenuScreens.register(ModMenuTypes.CURRENCY_FURNACE_MENU.get(), CurrencyFurnaceScreen::new);
			MenuScreens.register(ModMenuTypes.CURRENCY_BLAST_FURNACE_MENU.get(), CurrencyBlastFurnaceScreen::new);
			MenuScreens.register(ModMenuTypes.CURRENCY_SMOKER_MENU.get(), CurrencySmokerScreen::new);
		}
	}
}
