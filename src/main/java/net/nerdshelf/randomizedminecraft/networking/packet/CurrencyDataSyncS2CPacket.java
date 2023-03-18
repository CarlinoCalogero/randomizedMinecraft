package net.nerdshelf.randomizedminecraft.networking.packet;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.nerdshelf.randomizedminecraft.client.ClientCurrencyData;

public class CurrencyDataSyncS2CPacket {

	private final int currency;

	public CurrencyDataSyncS2CPacket(int currency) {
		this.currency = currency;
	}

	public CurrencyDataSyncS2CPacket(FriendlyByteBuf buf) {
		this.currency = buf.readInt();
	}

	public void toBytes(FriendlyByteBuf buf) {
		buf.writeInt(currency);
	}

	public boolean handle(Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			// HERE WE ARE ON THE CLIENT!
			ClientCurrencyData.setPlayerCurrency(currency);
		});
		return true;
	}

}
