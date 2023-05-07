package net.nerdshelf.randomizedminecraft.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.nerdshelf.randomizedminecraft.block.entity.ModBlockEntities;
import net.nerdshelf.randomizedminecraft.screen.CurrencyBlastFurnaceMenu;

public class CurrencyBlastFurnaceBlockEntity extends AbstractCurrencyFurnaceBlockEntity {
	public CurrencyBlastFurnaceBlockEntity(BlockPos p_155225_, BlockState p_155226_) {
		super(ModBlockEntities.CURRENCY_BLAST_FURNACE.get(), p_155225_, p_155226_, RecipeType.BLASTING);
	}

	protected Component getDefaultName() {
		return Component.translatable("container.blast_furnace");
	}

	protected int getBurnDuration(ItemStack p_58852_) {
		return super.getBurnDuration(p_58852_) / 2;
	}

	protected AbstractContainerMenu createMenu(int p_58849_, Inventory p_58850_) {
		return new CurrencyBlastFurnaceMenu(p_58849_, p_58850_, this, this.dataAccess);
	}
}