package net.nerdshelf.randomizedminecraft.event;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
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
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
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
			if (event.isWasDeath()) {

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
			}
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
		public static void onAnvilChange(AnvilUpdateEvent event) {

			ItemStack inputLeft = event.getLeft();
			ItemStack inputRight = event.getRight();

			SimpleContainer inputSlots = new SimpleContainer(2);
			inputSlots.addItem(inputLeft);
			inputSlots.addItem(inputRight);

			event.getPlayer().sendSystemMessage(
					Component.literal("cost: " + createResult(inputSlots, event.getPlayer(), event.getName())));

		}

		/***
		 * 
		 * this is literally the function {@link AnvilMenu#createResult()} with some
		 * adjustments
		 */
		public static int createResult(SimpleContainer inputs, Player player, String itemName) {

			Container inputSlots = inputs;
			DataSlot cost = DataSlot.standalone();
			int repairItemCountCost;

			ItemStack itemstack = inputSlots.getItem(0);
			cost.set(1);
			int i = 0;
			int j = 0;
			int k = 0;
			if (itemstack.isEmpty()) {
				cost.set(0);
			} else {
				ItemStack itemstack1 = itemstack.copy();
				ItemStack itemstack2 = inputSlots.getItem(1);
				Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemstack1);
				j += itemstack.getBaseRepairCost() + (itemstack2.isEmpty() ? 0 : itemstack2.getBaseRepairCost());
				repairItemCountCost = 0;
				boolean flag = false;

				if (!itemstack2.isEmpty()) {
					flag = itemstack2.getItem() == Items.ENCHANTED_BOOK
							&& !EnchantedBookItem.getEnchantments(itemstack2).isEmpty();
					if (itemstack1.isDamageableItem()
							&& itemstack1.getItem().isValidRepairItem(itemstack, itemstack2)) {
						int l2 = Math.min(itemstack1.getDamageValue(), itemstack1.getMaxDamage() / 4);
						if (l2 <= 0) {
							cost.set(0);
							return cost.get();
						}

						int i3;
						for (i3 = 0; l2 > 0 && i3 < itemstack2.getCount(); ++i3) {
							int j3 = itemstack1.getDamageValue() - l2;
							itemstack1.setDamageValue(j3);
							++i;
							l2 = Math.min(itemstack1.getDamageValue(), itemstack1.getMaxDamage() / 4);
						}

						repairItemCountCost = i3;
					} else {
						if (!flag && (!itemstack1.is(itemstack2.getItem()) || !itemstack1.isDamageableItem())) {
							cost.set(0);
							return cost.get();
						}

						if (itemstack1.isDamageableItem() && !flag) {
							int l = itemstack.getMaxDamage() - itemstack.getDamageValue();
							int i1 = itemstack2.getMaxDamage() - itemstack2.getDamageValue();
							int j1 = i1 + itemstack1.getMaxDamage() * 12 / 100;
							int k1 = l + j1;
							int l1 = itemstack1.getMaxDamage() - k1;
							if (l1 < 0) {
								l1 = 0;
							}

							if (l1 < itemstack1.getDamageValue()) {
								itemstack1.setDamageValue(l1);
								i += 2;
							}
						}

						Map<Enchantment, Integer> map1 = EnchantmentHelper.getEnchantments(itemstack2);
						boolean flag2 = false;
						boolean flag3 = false;

						for (Enchantment enchantment1 : map1.keySet()) {
							if (enchantment1 != null) {
								int i2 = map.getOrDefault(enchantment1, 0);
								int j2 = map1.get(enchantment1);
								j2 = i2 == j2 ? j2 + 1 : Math.max(j2, i2);
								boolean flag1 = enchantment1.canEnchant(itemstack);
								if (player.getAbilities().instabuild || itemstack.is(Items.ENCHANTED_BOOK)) {
									flag1 = true;
								}

								for (Enchantment enchantment : map.keySet()) {
									if (enchantment != enchantment1 && !enchantment1.isCompatibleWith(enchantment)) {
										flag1 = false;
										++i;
									}
								}

								if (!flag1) {
									flag3 = true;
								} else {
									flag2 = true;
									if (j2 > enchantment1.getMaxLevel()) {
										j2 = enchantment1.getMaxLevel();
									}

									map.put(enchantment1, j2);
									int k3 = 0;
									switch (enchantment1.getRarity()) {
									case COMMON:
										k3 = 1;
										break;
									case UNCOMMON:
										k3 = 2;
										break;
									case RARE:
										k3 = 4;
										break;
									case VERY_RARE:
										k3 = 8;
									}

									if (flag) {
										k3 = Math.max(1, k3 / 2);
									}

									i += k3 * j2;
									if (itemstack.getCount() > 1) {
										i = 40;
									}
								}
							}
						}

						if (flag3 && !flag2) {
							cost.set(0);
							return cost.get();
						}
					}
				}

				if (StringUtils.isBlank(itemName)) {
					if (itemstack.hasCustomHoverName()) {
						k = 1;
						i += k;
						itemstack1.resetHoverName();
					}
				} else if (!itemName.equals(itemstack.getHoverName().getString())) {
					k = 1;
					i += k;
					itemstack1.setHoverName(Component.literal(itemName));
				}
				if (flag && !itemstack1.isBookEnchantable(itemstack2))
					itemstack1 = ItemStack.EMPTY;

				cost.set(j + i);
				if (i <= 0) {
					itemstack1 = ItemStack.EMPTY;
				}

				if (k == i && k > 0 && cost.get() >= 40) {
					cost.set(39);
				}

				if (cost.get() >= 40 && !player.getAbilities().instabuild) {
					itemstack1 = ItemStack.EMPTY;
				}

				if (!itemstack1.isEmpty()) {
					int k2 = itemstack1.getBaseRepairCost();
					if (!itemstack2.isEmpty() && k2 < itemstack2.getBaseRepairCost()) {
						k2 = itemstack2.getBaseRepairCost();
					}

					if (k != i || k == 0) {
						k2 = calculateIncreasedRepairCost(k2);
					}

					itemstack1.setRepairCost(k2);
					EnchantmentHelper.setEnchantments(map, itemstack1);
				}

			}
			return cost.get();
		}

		/***
		 * 
		 * this is literally the function
		 * {@link AnvilMenu#calculateIncreasedRepairCost()}
		 * 
		 */
		public static int calculateIncreasedRepairCost(int p_39026_) {
			return p_39026_ * 2 + 1;
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
