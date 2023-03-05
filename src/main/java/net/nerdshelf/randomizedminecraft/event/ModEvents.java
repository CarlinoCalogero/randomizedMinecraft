package net.nerdshelf.randomizedminecraft.event;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
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

			if (event.getEntity() instanceof Zombie) {
				if (event.getSource().getEntity() instanceof Player) {
					ModMessages.sendToServer(new CurrencyManagementC2SPacket(x));
				}
			}

			if (event.getEntity() instanceof Skeleton) {
				if (event.getSource().getEntity() instanceof Player) {
					ModMessages.sendToServer(new CurrencyManagementC2SPacket((int) (1.5 * x)));
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
