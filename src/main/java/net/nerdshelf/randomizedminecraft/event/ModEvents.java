package net.nerdshelf.randomizedminecraft.event;

import java.util.List;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.nerdshelf.randomizedminecraft.RandomizedMinecraftMod;
import net.nerdshelf.randomizedminecraft.block.ModBlocks;
import net.nerdshelf.randomizedminecraft.currency.PlayerCurrency;
import net.nerdshelf.randomizedminecraft.currency.PlayerCurrencyProvider;
import net.nerdshelf.randomizedminecraft.item.ModItems;
import net.nerdshelf.randomizedminecraft.networking.ModMessages;
import net.nerdshelf.randomizedminecraft.networking.packet.CurrencyManagementC2SPacket;
import net.nerdshelf.randomizedminecraft.villager.ModVillagers;

public class ModEvents {

	@Mod.EventBusSubscriber(modid = RandomizedMinecraftMod.MOD_ID)
	public static class ForgeEvents {

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
			if (event.isWasDeath()) {
				event.getOriginal().getCapability(PlayerCurrencyProvider.PLAYER_CURRENCY).ifPresent(oldStore -> {
					event.getOriginal().getCapability(PlayerCurrencyProvider.PLAYER_CURRENCY).ifPresent(newStore -> {
						newStore.copyCurrencyFrom(oldStore);
					});
				});
			}
		}

		@SubscribeEvent
		public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {

			event.register(PlayerCurrency.class);

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

		/***
		 * Increases player currency by x value when a certain entity is killed
		 * 
		 * @param x
		 * @param entity
		 */
		private static void handleMobKill(int x, LivingEntity entity) {
			// Show how much currency the player as gained by setting the mob name to the
			// gained currency
			entity.setCustomName(Component.literal("+" + x).withStyle(ChatFormatting.YELLOW));
			// Handle the currency increase
			ModMessages.sendToServer(new CurrencyManagementC2SPacket(x));
		}

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
							}));
		}

	}

}
