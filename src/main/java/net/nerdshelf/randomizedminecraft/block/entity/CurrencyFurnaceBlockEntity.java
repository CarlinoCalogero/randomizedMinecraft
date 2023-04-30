package net.nerdshelf.randomizedminecraft.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.nerdshelf.randomizedminecraft.screen.CurrencyFurnaceMenu;

public class CurrencyFurnaceBlockEntity extends AbstractCurrencyFurnaceBlockEntity {
	public CurrencyFurnaceBlockEntity(BlockPos p_155545_, BlockState p_155546_) {
		super(ModBlockEntities.CURRENCY_FURNACE.get(), p_155545_, p_155546_, RecipeType.SMELTING);
	}

	protected Component getDefaultName() {
		return Component.translatable("container.furnace");
	}

	protected AbstractContainerMenu createMenu(int p_59293_, Inventory p_59294_) {
		return new CurrencyFurnaceMenu(p_59293_, p_59294_, this, this.dataAccess);
	}
}