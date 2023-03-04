package net.nerdshelf.randomizedminecraft.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.nerdshelf.randomizedminecraft.RandomizedMinecraftMod;
import net.nerdshelf.randomizedminecraft.item.custom.RandomDayOrNightItem;

public class ModItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
			RandomizedMinecraftMod.MOD_ID);

	public static final RegistryObject<Item> RANDOM_DAY_OR_NIGHT = ITEMS.register("random_day_or_night",
			() -> new RandomDayOrNightItem(new Item.Properties().stacksTo(1).setNoRepair()));

	public static void register(IEventBus eventBus) {
		ITEMS.register(eventBus);
	}

}
