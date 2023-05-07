package net.nerdshelf.randomizedminecraft.block;

import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.nerdshelf.randomizedminecraft.RandomizedMinecraftMod;
import net.nerdshelf.randomizedminecraft.block.custom.BankVaultBlock;
import net.nerdshelf.randomizedminecraft.block.custom.CurrencyAnvilBlock;
import net.nerdshelf.randomizedminecraft.block.custom.CurrencyBlastFurnaceBlock;
import net.nerdshelf.randomizedminecraft.block.custom.CurrencyEnchantmentTableBlock;
import net.nerdshelf.randomizedminecraft.block.custom.CurrencyFurnaceBlock;
import net.nerdshelf.randomizedminecraft.block.custom.CurrencySmokerBlock;
import net.nerdshelf.randomizedminecraft.block.custom.JumpyBlock;
import net.nerdshelf.randomizedminecraft.item.ModItems;

public class ModBlocks {

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
			RandomizedMinecraftMod.MOD_ID);

	public static final RegistryObject<Block> ZIRCON_BLOCK = registerBlock("zircon_block",
			() -> new Block(BlockBehaviour.Properties.of(Material.STONE).strength(6f).requiresCorrectToolForDrops()));

	public static final RegistryObject<Block> JUMPY_BLOCK = registerBlock("jumpy_block", () -> new JumpyBlock(
			BlockBehaviour.Properties.of(Material.STONE).strength(6f).requiresCorrectToolForDrops()));

	public static final RegistryObject<Block> BANK_VAULT = registerBlock("bank_vault", () -> new BankVaultBlock(
			BlockBehaviour.Properties.of(Material.STONE).strength(6f).requiresCorrectToolForDrops()));

	public static final RegistryObject<Block> CURRENCY_ANVIL = registerBlock("currency_anvil",
			() -> new CurrencyAnvilBlock(BlockBehaviour.Properties.of(Material.HEAVY_METAL, MaterialColor.METAL)
					.requiresCorrectToolForDrops().strength(5.0F, 1200.0F).sound(SoundType.ANVIL)));

	public static final RegistryObject<Block> CURRENCY_FURNACE = registerBlock("currency_furnace",
			() -> new CurrencyFurnaceBlock(BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops()
					.strength(3.5F).lightLevel(litBlockEmission(13))));

	public static final RegistryObject<Block> CURRENCY_BLAST_FURNACE = registerBlock("currency_blast_furnace",
			() -> new CurrencyBlastFurnaceBlock(BlockBehaviour.Properties.of(Material.STONE)
					.requiresCorrectToolForDrops().strength(3.5F).lightLevel(litBlockEmission(13))));

	public static final RegistryObject<Block> CURRENCY_SMOKER = registerBlock("currency_smoker",
			() -> new CurrencySmokerBlock(BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops()
					.strength(3.5F).lightLevel(litBlockEmission(13))));

	public static final RegistryObject<Block> CURRENCY_ENCHANTING_TABLE = registerBlock("currency_enchanting_table",
			() -> new CurrencyEnchantmentTableBlock(
					BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_RED).requiresCorrectToolForDrops()
							.lightLevel((p_187437_) -> {
								return 7;
							}).strength(5.0F, 1200.0F)));

	private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
		RegistryObject<T> toReturn = BLOCKS.register(name, block);
		registerBlockItem(name, toReturn);
		return toReturn;
	}

	private static ToIntFunction<BlockState> litBlockEmission(int p_50760_) {
		return (p_50763_) -> {
			return p_50763_.getValue(BlockStateProperties.LIT) ? p_50760_ : 0;
		};
	}

	private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
		return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
	}

	public static void register(IEventBus eventBus) {
		BLOCKS.register(eventBus);
	}
}
