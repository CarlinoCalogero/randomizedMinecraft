package net.nerdshelf.randomizedminecraft.screen;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.nerdshelf.randomizedminecraft.RandomizedMinecraftMod;

public class ModMenuTypes {
	public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES,
			RandomizedMinecraftMod.MOD_ID);

	public static final RegistryObject<MenuType<CurrencyAnvilMenu>> CURRENCY_ANVIL_MENU = registerMenuType(
			CurrencyAnvilMenu::new, "currency_anvil_menu");
	
	public static final RegistryObject<MenuType<CurrencyFurnaceMenu>> CURRENCY_FURNACE_MENU = registerMenuType(
			CurrencyFurnaceMenu::new, "currency_furnace_menu");

	private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(
			IContainerFactory<T> factory, String name) {
		return MENUS.register(name, () -> IForgeMenuType.create(factory));
	}

	public static void register(IEventBus eventBus) {
		MENUS.register(eventBus);
	}
}