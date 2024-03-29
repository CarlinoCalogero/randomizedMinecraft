package net.nerdshelf.randomizedminecraft.networking;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.nerdshelf.randomizedminecraft.RandomizedMinecraftMod;
import net.nerdshelf.randomizedminecraft.networking.packet.CurrencyDataSyncS2CPacket;
import net.nerdshelf.randomizedminecraft.networking.packet.CurrencyManagementC2SPacket;

public class ModMessages {
	private static SimpleChannel INSTANCE;

	private static int packetId = 0;

	private static int id() {
		return packetId++;
	}

	public static void register() {
		SimpleChannel net = NetworkRegistry.ChannelBuilder
				.named(new ResourceLocation(RandomizedMinecraftMod.MOD_ID, "messages"))
				.networkProtocolVersion(() -> "1.0").clientAcceptedVersions(s -> true).serverAcceptedVersions(s -> true)
				.simpleChannel();

		INSTANCE = net;

		net.messageBuilder(CurrencyManagementC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
				.decoder(CurrencyManagementC2SPacket::new).encoder(CurrencyManagementC2SPacket::toBytes)
				.consumerMainThread(CurrencyManagementC2SPacket::handle).add();

		net.messageBuilder(CurrencyDataSyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
				.decoder(CurrencyDataSyncS2CPacket::new).encoder(CurrencyDataSyncS2CPacket::toBytes)
				.consumerMainThread(CurrencyDataSyncS2CPacket::handle).add();

	}

	public static <MSG> void sendToServer(MSG message) {
		INSTANCE.sendToServer(message);
	}

	public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
		INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
	}
}
