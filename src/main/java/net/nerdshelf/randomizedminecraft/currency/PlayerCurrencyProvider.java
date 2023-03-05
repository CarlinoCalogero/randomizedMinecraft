package net.nerdshelf.randomizedminecraft.currency;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public class PlayerCurrencyProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

	public static Capability<PlayerCurrency> PLAYER_CURRENCY = CapabilityManager
			.get(new CapabilityToken<PlayerCurrency>() {
			});

	private PlayerCurrency currency = null;
	private final LazyOptional<PlayerCurrency> optional = LazyOptional.of(this::createPlayerCurrency);

	private PlayerCurrency createPlayerCurrency() {
		if (this.currency == null) {
			this.currency = new PlayerCurrency();
		}

		return this.currency;

	}

	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {

		if (cap == PLAYER_CURRENCY) {
			return optional.cast();
		}

		return LazyOptional.empty();
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag nbt = new CompoundTag();
		createPlayerCurrency().saveNBTData(nbt);
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {

		createPlayerCurrency().loadNBTData(nbt);

	}

}
