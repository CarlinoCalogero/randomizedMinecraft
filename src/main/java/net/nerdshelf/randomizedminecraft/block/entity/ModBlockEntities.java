package net.nerdshelf.randomizedminecraft.block.entity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.nerdshelf.randomizedminecraft.RandomizedMinecraftMod;
import net.nerdshelf.randomizedminecraft.block.ModBlocks;
import net.nerdshelf.randomizedminecraft.block.entity.custom.BankVaultBlockEntity;
import net.nerdshelf.randomizedminecraft.block.entity.custom.CurrencyAnvilBlockEntity;
import net.nerdshelf.randomizedminecraft.block.entity.custom.CurrencyBlastFurnaceBlockEntity;
import net.nerdshelf.randomizedminecraft.block.entity.custom.CurrencyEnchantmentTableBlockEntity;
import net.nerdshelf.randomizedminecraft.block.entity.custom.CurrencyFurnaceBlockEntity;
import net.nerdshelf.randomizedminecraft.block.entity.custom.CurrencySmokerBlockEntity;

public class ModBlockEntities {
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister
			.create(ForgeRegistries.BLOCK_ENTITY_TYPES, RandomizedMinecraftMod.MOD_ID);

	public static final RegistryObject<BlockEntityType<CurrencyAnvilBlockEntity>> CURRENCY_ANVIL = BLOCK_ENTITIES
			.register("currency_anvil", () -> BlockEntityType.Builder
					.of(CurrencyAnvilBlockEntity::new, ModBlocks.CURRENCY_ANVIL.get()).build(null));

	public static final RegistryObject<BlockEntityType<CurrencyFurnaceBlockEntity>> CURRENCY_FURNACE = BLOCK_ENTITIES
			.register("currency_furnace", () -> BlockEntityType.Builder
					.of(CurrencyFurnaceBlockEntity::new, ModBlocks.CURRENCY_FURNACE.get()).build(null));

	public static final RegistryObject<BlockEntityType<CurrencyBlastFurnaceBlockEntity>> CURRENCY_BLAST_FURNACE = BLOCK_ENTITIES
			.register("currency_blast_furnace", () -> BlockEntityType.Builder
					.of(CurrencyBlastFurnaceBlockEntity::new, ModBlocks.CURRENCY_BLAST_FURNACE.get()).build(null));

	public static final RegistryObject<BlockEntityType<CurrencySmokerBlockEntity>> CURRENCY_SMOKER = BLOCK_ENTITIES
			.register("currency_smoker", () -> BlockEntityType.Builder
					.of(CurrencySmokerBlockEntity::new, ModBlocks.CURRENCY_BLAST_FURNACE.get()).build(null));

	public static final RegistryObject<BlockEntityType<BankVaultBlockEntity>> BANK_VAULT = BLOCK_ENTITIES.register(
			"bank_vault",
			() -> BlockEntityType.Builder.of(BankVaultBlockEntity::new, ModBlocks.BANK_VAULT.get()).build(null));

	public static final RegistryObject<BlockEntityType<CurrencyEnchantmentTableBlockEntity>> CURRENCY_ENCHANTING_TABLE = BLOCK_ENTITIES
			.register("currency_enchanting_table",
					() -> BlockEntityType.Builder
							.of(CurrencyEnchantmentTableBlockEntity::new, ModBlocks.CURRENCY_ENCHANTING_TABLE.get())
							.build(null));

	public static void register(IEventBus eventBus) {
		BLOCK_ENTITIES.register(eventBus);
	}
}
