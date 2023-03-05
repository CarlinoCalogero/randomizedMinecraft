package net.nerdshelf.randomizedminecraft.currency;

import net.minecraft.nbt.CompoundTag;

public class PlayerCurrency {

	private int currency;
	private final int MIN_CURRENCY = 0;

	public int getCurrency() {
		return currency;
	}

	public void addCurrency(int add) {
		this.currency = this.currency + add;
	}

	public void subCurrency(int sub) {
		this.currency = Math.max(this.currency - sub, MIN_CURRENCY);
	}

	public void copyCurrencyFrom(PlayerCurrency source) {
		this.currency = source.currency;
	}

	public void saveNBTData(CompoundTag nbt) {
		nbt.putInt("currency", this.currency);
	}

	public void loadNBTData(CompoundTag nbt) {
		this.currency = nbt.getInt("currency");
	}

}
