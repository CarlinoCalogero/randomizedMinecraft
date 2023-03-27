package net.nerdshelf.randomizedminecraft.screen;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.nerdshelf.randomizedminecraft.RandomizedMinecraftMod;

public class ModMenuTypes {

	public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES,
			RandomizedMinecraftMod.MOD_ID);

	public static final RegistryObject<MenuType<CustomAnvilMenu>> CUSTOM_ANVIL_MENU = MENUS.register("custom_anvil_menu",
			() -> new MenuType<CustomAnvilMenu>(CustomAnvilMenu::new));

	public static void register(IEventBus eventBus) {
		MENUS.register(eventBus);
	}

}
