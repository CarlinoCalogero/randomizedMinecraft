package net.nerdshelf.randomizedminecraft.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.nerdshelf.randomizedminecraft.screen.CurrencySmokerMenu;

public class CurrencySmokerBlockEntity extends AbstractCurrencyFurnaceBlockEntity {
	public CurrencySmokerBlockEntity(BlockPos p_155749_, BlockState p_155750_) {
		super(ModBlockEntities.CURRENCY_SMOKER.get(), p_155749_, p_155750_, RecipeType.SMOKING);
	}

	protected Component getDefaultName() {
		return Component.translatable("container.smoker");
	}

	protected int getBurnDuration(ItemStack p_59786_) {
		return super.getBurnDuration(p_59786_) / 2;
	}

	protected AbstractContainerMenu createMenu(int p_59783_, Inventory p_59784_) {
		return new CurrencySmokerMenu(p_59783_, p_59784_, this, this.dataAccess);
	}
}