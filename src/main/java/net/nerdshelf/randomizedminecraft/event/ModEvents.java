package net.nerdshelf.randomizedminecraft.event;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.BlastFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SmokerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import net.minecraftforge.event.level.BlockEvent.EntityPlaceEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.nerdshelf.randomizedminecraft.RandomizedMinecraftMod;
import net.nerdshelf.randomizedminecraft.block.ModBlocks;
import net.nerdshelf.randomizedminecraft.currency.PlayerCurrency;
import net.nerdshelf.randomizedminecraft.currency.PlayerCurrencyProvider;
import net.nerdshelf.randomizedminecraft.item.ModItems;
import net.nerdshelf.randomizedminecraft.networking.ModMessages;
import net.nerdshelf.randomizedminecraft.networking.packet.CurrencyDataSyncS2CPacket;
import net.nerdshelf.randomizedminecraft.networking.packet.CurrencyManagementC2SPacket;
import net.nerdshelf.randomizedminecraft.player.CustomFoodStats;
import net.nerdshelf.randomizedminecraft.villager.ModVillagers;

public class ModEvents {

	@Mod.EventBusSubscriber(modid = RandomizedMinecraftMod.MOD_ID)
	public static class ForgeEvents {

		/**
		 * Used to override player fields
		 * 
		 *
		 * @see EntityJoinLevelEvent
		 * @param event The data for the {@link EntityJoinLevelEvent} event
		 */
		@SubscribeEvent
		public static void onWorldJoin(EntityJoinLevelEvent event) {
			if (!event.getLevel().isClientSide() && event.getEntity() instanceof Player player) {

				ObfuscationReflectionHelper.setPrivateValue(Player.class, player, new CustomFoodStats(), "foodData");

			}
		}

		@SubscribeEvent
		public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
			if (event.getObject() instanceof Player) {
				if (!event.getObject().getCapability(PlayerCurrencyProvider.PLAYER_CURRENCY).isPresent()) {
					event.addCapability(new ResourceLocation(RandomizedMinecraftMod.MOD_ID, "properties"),
							new PlayerCurrencyProvider());
				}
			}

		}

		@SubscribeEvent
		public static void onPlayerCloned(PlayerEvent.Clone event) {

			/***
			 * `event.getOriginal()` gets the data of the player entity before dying, but
			 * `invalidateCaps()` (which deletes all capabilities) is called when the player
			 * dies. So if we don't call `event.getOriginal().reviveCaps()` before accessing
			 * the player's capabilities we get `null`. ReviveCaps gets the old values of
			 * the capabilities registered with the player entity. At the same time we
			 * should call `event.getOriginal().invalidateCaps()` after accessing the old
			 * values of the capabilities to invalidate the old capabilities and use the new
			 * ones.
			 */
			event.getOriginal().reviveCaps();
			event.getOriginal().getCapability(PlayerCurrencyProvider.PLAYER_CURRENCY).ifPresent(oldStore -> {
				/***
				 * `event.getEntity()` gets the newly cloned player (respawned player), and we
				 * must use this method to access the `newStore` instead of
				 * `event.getOriginal()`, that gets the player's entity before dying, because we
				 * have to overwrite the default value (`newStore.currency = 0`) with the old
				 * one inside of `oldStore` with `copyFrom`.
				 */
				event.getEntity().getCapability(PlayerCurrencyProvider.PLAYER_CURRENCY).ifPresent(newStore -> {
					newStore.copyCurrencyFrom(oldStore);
				});
			});
			/***
			 * call `event.getOriginal().invalidateCaps()` after accessing the old values of
			 * the capabilities to invalidate the old capabilities and use the new ones.
			 */
			event.getOriginal().invalidateCaps();

			/*
			 * [START] - Decrease player currency by 50% if player is coming back from the
			 * END
			 */
			Entity entity = event.getEntity();

			if (entity.getLevel().isClientSide()) {
				return;
			}

			if (event.getOriginal().getLevel().dimension() == Level.END && entity instanceof Player) {
				entity.getCapability(PlayerCurrencyProvider.PLAYER_CURRENCY).ifPresent(currency -> {
					ModMessages.sendToServer(new CurrencyManagementC2SPacket((int) -(0.5 * currency.getCurrency())));
				});
			}
			/*
			 * [END] - Decrease player currency by 50% if player is coming back from the END
			 */

		}

		@SubscribeEvent
		public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {

			event.register(PlayerCurrency.class);

		}

		@SubscribeEvent
		public static void onPlayerJoinWorld(EntityJoinLevelEvent event) {
			if (!event.getLevel().isClientSide()) {
				if (event.getEntity() instanceof ServerPlayer player) {
					player.getCapability(PlayerCurrencyProvider.PLAYER_CURRENCY).ifPresent(currency -> {
						ModMessages.sendToPlayer(new CurrencyDataSyncS2CPacket(currency.getCurrency()), player);
					});
				}
			}
		}

		/***
		 * Changes villagers' trades
		 * 
		 * @param event
		 */
		@SubscribeEvent
		public static void addCustomTrades(VillagerTradesEvent event) {

			// Add trades to toolsmith villager
			if (event.getType() == VillagerProfession.TOOLSMITH) {
				Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
				ItemStack stack = new ItemStack(ModItems.RANDOM_DAY_OR_NIGHT.get(), 1);
				int villagerLevel = 1;

				trades.get(villagerLevel)
						.add((trader, rand) -> new MerchantOffer(new ItemStack(Items.EMERALD, 2), stack, 10, 8, 0.02F));
			}

			// Add trades to jumpy master villager
			if (event.getType() == ModVillagers.JUMP_MASTER.get()) {
				Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
				ItemStack stack = new ItemStack(ModBlocks.ZIRCON_BLOCK.get(), 15);
				int villagerLevel = 1;

				trades.get(villagerLevel)
						.add((trader, rand) -> new MerchantOffer(new ItemStack(Items.EMERALD, 5), stack, 10, 8, 0.02F));

				trades.get(2).add((trader, rand) -> new MerchantOffer(new ItemStack(Items.GOLD_INGOT, 10),
						new ItemStack(ModBlocks.JUMPY_BLOCK.get(), 1), 10, 8, 0.02F));
			}

		}

		/***
		 * Prevents experience orbs from spawning
		 * 
		 * @param event
		 */
		@SubscribeEvent
		public static void handleAddExperienceOrb(EntityJoinLevelEvent event) {
			Entity entity = event.getEntity();

			if (entity instanceof ExperienceOrb) {
				event.setCanceled(true);
			}
		}

		/***
		 * Prevents player from levelling up
		 * 
		 * @param event
		 */
		@SubscribeEvent
		public static void giveExperienceLevels(PlayerXpEvent event) {
			event.setCanceled(true);
		}

		/***
		 * Prevents player from picking up experience orb
		 * 
		 * @param event
		 */
		@SubscribeEvent
		public static void playerTouch(PlayerXpEvent event) {
			event.setCanceled(true);
		}

		/***
		 * Prevents player from getting experience point
		 * 
		 * @param event
		 */
		@SubscribeEvent
		public static void giveExperiencePoints(PlayerXpEvent event) {
			event.setCanceled(true);
		}

		@SubscribeEvent
		public static void replaceBlockOnPlace(EntityPlaceEvent event) {

			if (event.getPlacedBlock().getBlock() instanceof AnvilBlock) {
				if (!event.getEntity().getLevel().isClientSide()) {
					BlockState blockState = ModBlocks.CURRENCY_ANVIL.get().defaultBlockState();
					Direction entityDirection = event.getEntity().getDirection();
					if (entityDirection == Direction.EAST || entityDirection == Direction.WEST) {
						blockState = blockState.rotate(Rotation.CLOCKWISE_90);
					}
					event.getLevel().setBlock(event.getPos(), blockState, 0);
				}
			}

			if (event.getPlacedBlock().getBlock() instanceof FurnaceBlock) {
				if (!event.getEntity().getLevel().isClientSide()) {
					BlockState blockState = ModBlocks.CURRENCY_FURNACE.get().defaultBlockState();
					Direction entityDirection = event.getEntity().getDirection();
					if (entityDirection == Direction.WEST) {
						blockState = blockState.rotate(Rotation.CLOCKWISE_90);
					}
					if (entityDirection == Direction.EAST) {
						blockState = blockState.rotate(Rotation.COUNTERCLOCKWISE_90);
					}
					if (entityDirection == Direction.NORTH) {
						blockState = blockState.rotate(Rotation.CLOCKWISE_180);
					}
					event.getLevel().setBlock(event.getPos(), blockState, 0);
				}
			}

			if (event.getPlacedBlock().getBlock() instanceof BlastFurnaceBlock) {
				if (!event.getEntity().getLevel().isClientSide()) {
					BlockState blockState = ModBlocks.CURRENCY_BLAST_FURNACE.get().defaultBlockState();
					Direction entityDirection = event.getEntity().getDirection();
					if (entityDirection == Direction.WEST) {
						blockState = blockState.rotate(Rotation.CLOCKWISE_90);
					}
					if (entityDirection == Direction.EAST) {
						blockState = blockState.rotate(Rotation.COUNTERCLOCKWISE_90);
					}
					if (entityDirection == Direction.NORTH) {
						blockState = blockState.rotate(Rotation.CLOCKWISE_180);
					}
					event.getLevel().setBlock(event.getPos(), blockState, 0);
				}
			}

			if (event.getPlacedBlock().getBlock() instanceof SmokerBlock) {
				if (!event.getEntity().getLevel().isClientSide()) {
					BlockState blockState = ModBlocks.CURRENCY_SMOKER.get().defaultBlockState();
					Direction entityDirection = event.getEntity().getDirection();
					if (entityDirection == Direction.WEST) {
						blockState = blockState.rotate(Rotation.CLOCKWISE_90);
					}
					if (entityDirection == Direction.EAST) {
						blockState = blockState.rotate(Rotation.COUNTERCLOCKWISE_90);
					}
					if (entityDirection == Direction.NORTH) {
						blockState = blockState.rotate(Rotation.CLOCKWISE_180);
					}
					event.getLevel().setBlock(event.getPos(), blockState, 0);
				}
			}

		}

		/**
		 * Decreases player's currency by 20% on Player's death
		 * 
		 * @param event
		 */
		@SubscribeEvent
		public static void onPlayerDeath(LivingDeathEvent event) {

			Entity entity = event.getEntity();

			if (entity.getLevel().isClientSide()) {
				return;
			}

			if (entity instanceof Player) {
				entity.getCapability(PlayerCurrencyProvider.PLAYER_CURRENCY).ifPresent(currency -> {
					ModMessages.sendToServer(new CurrencyManagementC2SPacket((int) -(0.2 * currency.getCurrency())));
				});
			}

		}

		/**
		 * Decreases player's currency by 20% if player is going to/coming back from the
		 * NETHER Decreases player's currency by 50% if player is going to the END If
		 * searching for the method that decreases player's currency by 50% if player is
		 * coming back from the END see
		 * {@link net.nerdshelf.randomizedminecraft.event.ModEvents.ForgeEvents#onPlayerCloned()}
		 * 
		 * @param event
		 */
		@SubscribeEvent
		public static void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {

			Entity entity = event.getEntity();

			if (entity.getLevel().isClientSide()) {
				return;
			}

			if (entity instanceof Player) {
				entity.getCapability(PlayerCurrencyProvider.PLAYER_CURRENCY).ifPresent(currency -> {

					if (event.getFrom().toString().equalsIgnoreCase(Level.NETHER.toString())
							|| event.getTo().toString().equalsIgnoreCase(Level.NETHER.toString())) {
						ModMessages
								.sendToServer(new CurrencyManagementC2SPacket((int) -(0.2 * currency.getCurrency())));
					}

					if (event.getTo().toString().equalsIgnoreCase(Level.END.toString())) {
						ModMessages
								.sendToServer(new CurrencyManagementC2SPacket((int) -(0.5 * currency.getCurrency())));
					}

				});
			}

		}

		/**
		 * Extracts a pattern from a string
		 * 
		 * @param string  is the string inside which the pattern must be found
		 * @param pattern is the pattern that we are searching for
		 * @return a String representing the pattern found
		 * @throws Exception
		 */
		private static String findPattern(String string, String pattern) throws Exception {

			try {

				Matcher matcher = Pattern.compile(pattern).matcher(string);
				matcher.find();
				return matcher.group();

			} catch (Exception e) {
				// String does not contain the pattern
				throw new Exception();
			}

		}

		/**
		 * Checks if harvested crop is at MaxAge
		 * 
		 * @param blockState is the crop that is being harvested
		 * @param maxAge     is Crop MaxAge
		 * @return true if the crop is at MaxAge, false otherwise
		 */
		private static boolean isCropAtMaxAge(BlockState blockState, int maxAge) {

			try {

				String string = blockState.toString();

				String agePattern = "age=[0-9]+";
				String numberPattern = "\\d+";

				string = findPattern(string, agePattern);
				string = findPattern(string, numberPattern);

				int age = Integer.valueOf(string);
				return age == maxAge;

			} catch (Exception e) {
				System.out.println("no pattern found");
				return false;
			}

		}

		/**
		 * Player earns currency when breaking a block that, normally, would have given
		 * experience Player earns specific currency when harvesting crops
		 * 
		 * @param event
		 */
		@SubscribeEvent
		public static void onBreakingBlock(BreakEvent event) {

			Player player = event.getPlayer();

			if (player.getLevel().isClientSide()) {
				return;
			}

			if (player instanceof Player) {
				player.getCapability(PlayerCurrencyProvider.PLAYER_CURRENCY).ifPresent(currency -> {

					int[] amount = { 1, 2, 5, 10, 20, 50 };
					BlockState blockState = event.getState();
					Block block = blockState.getBlock();

					if (block == Blocks.WHEAT && ((CropBlock) block).isMaxAge(blockState)) {
						ModMessages.sendToServer(new CurrencyManagementC2SPacket(amount[0]));
						return;
					}

					if (block == Blocks.BEETROOTS && ((CropBlock) block).isMaxAge(blockState)) {
						ModMessages.sendToServer(new CurrencyManagementC2SPacket(amount[1]));
						return;
					}

					if (block == Blocks.CARROTS && ((CropBlock) block).isMaxAge(blockState)) {
						ModMessages.sendToServer(new CurrencyManagementC2SPacket(amount[1]));
						return;
					}

					if (block == Blocks.POTATOES && ((CropBlock) block).isMaxAge(blockState)) {
						ModMessages.sendToServer(new CurrencyManagementC2SPacket(amount[1]));
						return;
					}

					if (block == Blocks.MELON) {
						ModMessages.sendToServer(new CurrencyManagementC2SPacket(amount[2]));
						return;
					}

					if (block == Blocks.PUMPKIN) {
						ModMessages.sendToServer(new CurrencyManagementC2SPacket(amount[2]));
						return;
					}

					if (block == Blocks.BAMBOO) {
						ModMessages.sendToServer(new CurrencyManagementC2SPacket(amount[1]));
						return;
					}

					if (block == Blocks.COCOA && isCropAtMaxAge(blockState, CocoaBlock.MAX_AGE)) {
						ModMessages.sendToServer(new CurrencyManagementC2SPacket(amount[3]));
						return;
					}

					if (block == Blocks.SUGAR_CANE) {
						ModMessages.sendToServer(new CurrencyManagementC2SPacket(amount[1]));
						return;
					}

					if (block == Blocks.CACTUS) {
						ModMessages.sendToServer(new CurrencyManagementC2SPacket(amount[1]));
						return;
					}

					if (block == Blocks.NETHER_WART && isCropAtMaxAge(blockState, NetherWartBlock.MAX_AGE)) {
						ModMessages.sendToServer(new CurrencyManagementC2SPacket(amount[4]));
						return;
					}

					if (block == Blocks.CHORUS_PLANT) {
						ModMessages.sendToServer(new CurrencyManagementC2SPacket(amount[5]));
						return;
					}

					int dropExp = event.getExpToDrop();
					if (dropExp > 0) {
						ModMessages.sendToServer(new CurrencyManagementC2SPacket(dropExp * 10));
					}

				});
			}

		}

		/**
		 * Gives a name to killed mob to represent the currency earned by the player and
		 * also gives currency to the player
		 * 
		 * @param x      is amount to be added to player current currency
		 * @param entity is mob killed
		 */
		private static void handleMobKill(int x, LivingEntity entity) {
			// Show how much currency the player as gained by setting the mob name to the
			// gained currency
			entity.setCustomName(Component.literal("+" + x).withStyle(ChatFormatting.YELLOW));
			// Handle the currency increase
			ModMessages.sendToServer(new CurrencyManagementC2SPacket(x));
		}

		/**
		 * Increases player currency by x value when a certain entity is killed
		 * 
		 * @param event
		 */
		@SubscribeEvent
		public static void onLivingDeath(LivingDeathEvent event) {

			int x = 10;

			if (event.getEntity() instanceof Blaze) {
				if (event.getSource().getEntity() instanceof Player) {
					handleMobKill(4 * x, event.getEntity());
					return;
				}
			}

			if (event.getEntity() instanceof Creeper) {
				if (event.getSource().getEntity() instanceof Player) {
					handleMobKill(2 * x, event.getEntity());
					return;
				}
			}

			if (event.getEntity() instanceof Drowned) {
				if (event.getSource().getEntity() instanceof Player) {
					handleMobKill((int) (1.5 * x), event.getEntity());
					return;
				}
			}

			if (event.getEntity() instanceof ElderGuardian) {
				if (event.getSource().getEntity() instanceof Player) {
					handleMobKill(6 * x, event.getEntity());
					return;
				}
			}

			if (event.getEntity() instanceof Endermite) {
				if (event.getSource().getEntity() instanceof Player) {
					handleMobKill(x, event.getEntity());
					return;
				}
			}

			if (event.getEntity() instanceof Evoker) {
				if (event.getSource().getEntity() instanceof Player) {
					handleMobKill(6 * x, event.getEntity());
					return;
				}
			}

			if (event.getEntity() instanceof Ghast) {
				if (event.getSource().getEntity() instanceof Player) {
					handleMobKill(3 * x, event.getEntity());
					return;
				}
			}

			if (event.getEntity() instanceof Guardian) {
				if (event.getSource().getEntity() instanceof Player) {
					handleMobKill(4 * x, event.getEntity());
					return;
				}
			}

			if (event.getEntity() instanceof Hoglin) {
				if (event.getSource().getEntity() instanceof Player) {
					handleMobKill(3 * x, event.getEntity());
					return;
				}
			}

			if (event.getEntity() instanceof Husk) {
				if (event.getSource().getEntity() instanceof Player) {
					handleMobKill(x, event.getEntity());
					return;
				}
			}

			if (event.getEntity() instanceof MagmaCube) {
				if (event.getSource().getEntity() instanceof Player) {
					handleMobKill(x, event.getEntity());
					return;
				}
			}

			if (event.getEntity() instanceof Phantom) {
				if (event.getSource().getEntity() instanceof Player) {
					handleMobKill((int) (1.5 * x), event.getEntity());
					return;
				}
			}

			if (event.getEntity() instanceof PiglinBrute) {
				if (event.getSource().getEntity() instanceof Player) {
					handleMobKill(2 * x, event.getEntity());
					return;
				}
			}

			if (event.getEntity() instanceof Pillager) {
				if (event.getSource().getEntity() instanceof Player) {
					handleMobKill((int) (1.5 * x), event.getEntity());
					return;
				}
			}

			if (event.getEntity() instanceof Ravager) {
				if (event.getSource().getEntity() instanceof Player) {
					handleMobKill(3 * x, event.getEntity());
					return;
				}
			}

			if (event.getEntity() instanceof Shulker) {
				if (event.getSource().getEntity() instanceof Player) {
					handleMobKill(3 * x, event.getEntity());
					return;
				}
			}

			if (event.getEntity() instanceof Silverfish) {
				if (event.getSource().getEntity() instanceof Player) {
					handleMobKill(x, event.getEntity());
					return;
				}
			}

			if (event.getEntity() instanceof Skeleton) {
				if (event.getSource().getEntity() instanceof Player) {
					handleMobKill((int) (1.5 * x), event.getEntity());
					return;
				}
			}

			if (event.getEntity() instanceof Slime) {
				if (event.getSource().getEntity() instanceof Player) {
					handleMobKill(x, event.getEntity());
					return;
				}
			}

			if (event.getEntity() instanceof Stray) {
				if (event.getSource().getEntity() instanceof Player) {
					handleMobKill((int) (1.5 * x), event.getEntity());
					return;
				}
			}

			if (event.getEntity() instanceof Vex) {
				if (event.getSource().getEntity() instanceof Player) {
					handleMobKill(x, event.getEntity());
					return;
				}
			}

			if (event.getEntity() instanceof Vindicator) {
				if (event.getSource().getEntity() instanceof Player) {
					handleMobKill(3 * x, event.getEntity());
					return;
				}
			}

			if (event.getEntity() instanceof Warden) {
				if (event.getSource().getEntity() instanceof Player) {
					handleMobKill(100 * x, event.getEntity());
					return;
				}
			}

			if (event.getEntity() instanceof Witch) {
				if (event.getSource().getEntity() instanceof Player) {
					handleMobKill(4 * x, event.getEntity());
					return;
				}
			}

			if (event.getEntity() instanceof WitherSkeleton) {
				if (event.getSource().getEntity() instanceof Player) {
					handleMobKill(3 * x, event.getEntity());
					return;
				}
			}

			if (event.getEntity() instanceof Zoglin) {
				if (event.getSource().getEntity() instanceof Player) {
					handleMobKill(3 * x, event.getEntity());
					return;
				}
			}

			if (event.getEntity() instanceof Zombie) {
				if (event.getSource().getEntity() instanceof Player) {
					handleMobKill(x, event.getEntity());
					return;
				}
			}

			if (event.getEntity() instanceof ZombieVillager) {
				if (event.getSource().getEntity() instanceof Player) {
					handleMobKill(x, event.getEntity());
					return;
				}
			}

		}

	}

	@Mod.EventBusSubscriber(modid = RandomizedMinecraftMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class ModEventBusEvents {

		// Create Mod Creative Tab
		@SubscribeEvent
		public static void buildContents(CreativeModeTabEvent.Register event) {
			event.registerCreativeModeTab(new ResourceLocation(RandomizedMinecraftMod.MOD_ID, "randomizedminecraftmod"),
					builder ->
					// Set name of tab to display
					builder.title(Component
							.translatable("item_group." + RandomizedMinecraftMod.MOD_ID + ".randomizedminecraftmod"))
							// Set icon of creative tab
							.icon(() -> new ItemStack(ModItems.RANDOM_DAY_OR_NIGHT.get()))
							// Add default items to tab
							.displayItems((enabledFlags, populator, hasPermissions) -> {
								populator.accept(ModItems.RANDOM_DAY_OR_NIGHT.get());
								populator.accept(ModBlocks.ZIRCON_BLOCK.get());
								populator.accept(ModBlocks.JUMPY_BLOCK.get());
								populator.accept(ModBlocks.CURRENCY_ANVIL.get());
								populator.accept(ModBlocks.CURRENCY_FURNACE.get());
								populator.accept(ModBlocks.CURRENCY_BLAST_FURNACE.get());
								populator.accept(ModBlocks.CURRENCY_SMOKER.get());
								populator.accept(ModBlocks.BANK_VAULT.get());
								populator.accept(ModBlocks.CURRENCY_ENCHANTING_TABLE.get());
							}));
		}

	}

}
