package net.nerdshelf.randomizedminecraft.client;

public class ClientCurrencyData {

	private static int playerCurrency;

	public static void setPlayerCurrency(int playerCurrency) {
		ClientCurrencyData.playerCurrency = playerCurrency;
	}

	public static int getPlayerCurrency() {
		return playerCurrency;
	}

}
