package net.nerdshelf.randomizedminecraft.event;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.nerdshelf.randomizedminecraft.RandomizedMinecraftMod;
import net.nerdshelf.randomizedminecraft.currency.PlayerCurrency;
import net.nerdshelf.randomizedminecraft.currency.PlayerCurrencyProvider;
import net.nerdshelf.randomizedminecraft.item.ModItems;
import net.nerdshelf.randomizedminecraft.networking.ModMessages;
import net.nerdshelf.randomizedminecraft.networking.packet.CurrencyManagementC2SPacket;

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

		@SubscribeEvent
		public static void onLivingDeath(LivingDeathEvent event) {

			int x = 10;

			if (event.getEntity() instanceof Blaze) {
				if (event.getSource().getEntity() instanceof Player) {
					ModMessages.sendToServer(new CurrencyManagementC2SPacket(4 * x));
					return;
				}
			}

			if (event.getEntity() instanceof Creeper) {
				if (event.getSource().getEntity() instanceof Player) {
					ModMessages.sendToServer(new CurrencyManagementC2SPacket(2 * x));
					return;
				}
			}

			if (event.getEntity() instanceof Drowned) {
				if (event.getSource().getEntity() instanceof Player) {
					ModMessages.sendToServer(new CurrencyManagementC2SPacket((int) (1.5 * x)));
					return;
				}
			}

			if (event.getEntity() instanceof ElderGuardian) {
				if (event.getSource().getEntity() instanceof Player) {
					ModMessages.sendToServer(new CurrencyManagementC2SPacket(6 * x));
					return;
				}
			}

			if (event.getEntity() instanceof Endermite) {
				if (event.getSource().getEntity() instanceof Player) {
					ModMessages.sendToServer(new CurrencyManagementC2SPacket(x));
					return;
				}
			}

			if (event.getEntity() instanceof Evoker) {
				if (event.getSource().getEntity() instanceof Player) {
					ModMessages.sendToServer(new CurrencyManagementC2SPacket(6 * x));
					return;
				}
			}

			if (event.getEntity() instanceof Ghast) {
				if (event.getSource().getEntity() instanceof Player) {
					ModMessages.sendToServer(new CurrencyManagementC2SPacket(3 * x));
					return;
				}
			}

			if (event.getEntity() instanceof Guardian) {
				if (event.getSource().getEntity() instanceof Player) {
					ModMessages.sendToServer(new CurrencyManagementC2SPacket(4 * x));
					return;
				}
			}

			if (event.getEntity() instanceof Hoglin) {
				if (event.getSource().getEntity() instanceof Player) {
					ModMessages.sendToServer(new CurrencyManagementC2SPacket(3 * x));
					return;
				}
			}

			if (event.getEntity() instanceof Husk) {
				if (event.getSource().getEntity() instanceof Player) {
					ModMessages.sendToServer(new CurrencyManagementC2SPacket(x));
					return;
				}
			}

			if (event.getEntity() instanceof MagmaCube) {
				if (event.getSource().getEntity() instanceof Player) {
					ModMessages.sendToServer(new CurrencyManagementC2SPacket(x));
					return;
				}
			}

			if (event.getEntity() instanceof Phantom) {
				if (event.getSource().getEntity() instanceof Player) {
					ModMessages.sendToServer(new CurrencyManagementC2SPacket((int) (1.5 * x)));
					return;
				}
			}

			if (event.getEntity() instanceof PiglinBrute) {
				if (event.getSource().getEntity() instanceof Player) {
					ModMessages.sendToServer(new CurrencyManagementC2SPacket(2 * x));
					return;
				}
			}

			if (event.getEntity() instanceof Pillager) {
				if (event.getSource().getEntity() instanceof Player) {
					ModMessages.sendToServer(new CurrencyManagementC2SPacket((int) (1.5 * x)));
					return;
				}
			}

			if (event.getEntity() instanceof Ravager) {
				if (event.getSource().getEntity() instanceof Player) {
					ModMessages.sendToServer(new CurrencyManagementC2SPacket(3 * x));
					return;
				}
			}

			if (event.getEntity() instanceof Shulker) {
				if (event.getSource().getEntity() instanceof Player) {
					ModMessages.sendToServer(new CurrencyManagementC2SPacket(3 * x));
					return;
				}
			}

			if (event.getEntity() instanceof Silverfish) {
				if (event.getSource().getEntity() instanceof Player) {
					ModMessages.sendToServer(new CurrencyManagementC2SPacket(x));
					return;
				}
			}

			if (event.getEntity() instanceof Skeleton) {
				if (event.getSource().getEntity() instanceof Player) {
					ModMessages.sendToServer(new CurrencyManagementC2SPacket((int) (1.5 * x)));
					return;
				}
			}

			if (event.getEntity() instanceof Slime) {
				if (event.getSource().getEntity() instanceof Player) {
					ModMessages.sendToServer(new CurrencyManagementC2SPacket(x));
					return;
				}
			}

			if (event.getEntity() instanceof Stray) {
				if (event.getSource().getEntity() instanceof Player) {
					ModMessages.sendToServer(new CurrencyManagementC2SPacket((int) (1.5 * x)));
					return;
				}
			}

			if (event.getEntity() instanceof Vex) {
				if (event.getSource().getEntity() instanceof Player) {
					ModMessages.sendToServer(new CurrencyManagementC2SPacket(x));
					return;
				}
			}

			if (event.getEntity() instanceof Vindicator) {
				if (event.getSource().getEntity() instanceof Player) {
					ModMessages.sendToServer(new CurrencyManagementC2SPacket(3 * x));
					return;
				}
			}

			if (event.getEntity() instanceof Warden) {
				if (event.getSource().getEntity() instanceof Player) {
					ModMessages.sendToServer(new CurrencyManagementC2SPacket(100 * x));
					return;
				}
			}

			if (event.getEntity() instanceof Witch) {
				if (event.getSource().getEntity() instanceof Player) {
					ModMessages.sendToServer(new CurrencyManagementC2SPacket(4 * x));
					return;
				}
			}

			if (event.getEntity() instanceof WitherSkeleton) {
				if (event.getSource().getEntity() instanceof Player) {
					ModMessages.sendToServer(new CurrencyManagementC2SPacket(3 * x));
					return;
				}
			}

			if (event.getEntity() instanceof Zoglin) {
				if (event.getSource().getEntity() instanceof Player) {
					ModMessages.sendToServer(new CurrencyManagementC2SPacket(3 * x));
					return;
				}
			}

			if (event.getEntity() instanceof Zombie) {
				if (event.getSource().getEntity() instanceof Player) {
					ModMessages.sendToServer(new CurrencyManagementC2SPacket(x));
					return;
				}
			}

			if (event.getEntity() instanceof ZombieVillager) {
				if (event.getSource().getEntity() instanceof Player) {
					ModMessages.sendToServer(new CurrencyManagementC2SPacket(x));
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
							}));
		}

	}

}
