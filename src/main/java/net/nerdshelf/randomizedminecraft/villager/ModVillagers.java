package net.nerdshelf.randomizedminecraft.villager;

import java.lang.reflect.InvocationTargetException;

import com.google.common.collect.ImmutableSet;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.nerdshelf.randomizedminecraft.RandomizedMinecraftMod;
import net.nerdshelf.randomizedminecraft.block.ModBlocks;

public class ModVillagers {

	public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES,
			RandomizedMinecraftMod.MOD_ID);
	public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS = DeferredRegister
			.create(ForgeRegistries.VILLAGER_PROFESSIONS, RandomizedMinecraftMod.MOD_ID);

	/***
	 * POI == "Point Of Interest" where the villager will go to to get a certain
	 * villager profession makTickets -> how many different villagers can actually
	 * take a job from this particular POI validRange -> the range on where they can
	 * find it
	 */
	public static final RegistryObject<PoiType> JUMPY_BLOCK_POI = POI_TYPES.register("jumpy_block_poi",
			() -> new PoiType(ImmutableSet.copyOf(ModBlocks.JUMPY_BLOCK.get().getStateDefinition().getPossibleStates()),
					1, 1));

	public static final RegistryObject<VillagerProfession> JUMP_MASTER = VILLAGER_PROFESSIONS.register("jumpy_master",
			() -> new VillagerProfession("jumpy_master", x -> x.get() == JUMPY_BLOCK_POI.get(),
					x -> x.get() == JUMPY_BLOCK_POI.get(), ImmutableSet.of(), ImmutableSet.of(),
					SoundEvents.VILLAGER_WORK_ARMORER));

	public static void registerPOIs() {
		try {
			ObfuscationReflectionHelper.findMethod(PoiType.class, "registerBlockStates", PoiType.class).invoke(null,
					JUMPY_BLOCK_POI.get());
		} catch (InvocationTargetException | IllegalAccessException exception) {
			exception.printStackTrace();
		}
	}

	public static void register(IEventBus eventBus) {
		POI_TYPES.register(eventBus);
		VILLAGER_PROFESSIONS.register(eventBus);
	}

}
