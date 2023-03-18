package net.nerdshelf.randomizedminecraft.networking.packet;

import java.util.function.Supplier;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.nerdshelf.randomizedminecraft.currency.PlayerCurrencyProvider;
import net.nerdshelf.randomizedminecraft.networking.ModMessages;

public class CurrencyManagementC2SPacket {

	private static final String MESSAGE_INCREASE_CURRENCY = "message.randomizedminecraftmod.increase_currency";

	private static int AMOUNT = 0;

	public CurrencyManagementC2SPacket(int amount) {
		CurrencyManagementC2SPacket.AMOUNT = amount;
	}

	public CurrencyManagementC2SPacket(FriendlyByteBuf buf) {

	}

	public void toBytes(FriendlyByteBuf buf) {

	}

	public boolean handle(Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			// HERE WE ARE ON THE SERVER!
			ServerPlayer player = context.getSender();

			// Notify the player that currency has been increased
			player.sendSystemMessage(Component.translatable(MESSAGE_INCREASE_CURRENCY).withStyle(ChatFormatting.GOLD));

			// Increase the currency of player
			player.getCapability(PlayerCurrencyProvider.PLAYER_CURRENCY).ifPresent(currency -> {
				player.sendSystemMessage(
						Component.literal("+" + CurrencyManagementC2SPacket.AMOUNT).withStyle(ChatFormatting.YELLOW));
				currency.addCurrency(CurrencyManagementC2SPacket.AMOUNT);
				player.sendSystemMessage(Component.literal("Current Currency: " + currency.getCurrency())
						.withStyle(ChatFormatting.YELLOW));

				// Sync currency data between server and client
				ModMessages.sendToPlayer(new CurrencyDataSyncS2CPacket(currency.getCurrency()), player);
			});

			// Output the current currency

		});
		return true;
	}

}
