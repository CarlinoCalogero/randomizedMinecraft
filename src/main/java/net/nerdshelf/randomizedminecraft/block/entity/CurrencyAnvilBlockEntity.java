package net.nerdshelf.randomizedminecraft.block.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.nerdshelf.randomizedminecraft.item.ModItems;
import net.nerdshelf.randomizedminecraft.screen.CurrencyAnvilMenu;

public class CurrencyAnvilBlockEntity extends BlockEntity implements MenuProvider {

	// it's basically the inventory of the block entity
	private final ItemStackHandler itemHandler = new ItemStackHandler(3) {
		@Override
		protected void onContentsChanged(int slot) {
			setChanged();
		}
	};

	// makes the inventory available via capabilities
	private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

	protected final ContainerData data; // used to send data to the menu. the menu is responsibile for synchronizing the
										// server data to the client
	private int progress = 0;
	private int maxProgress = 78;

	public CurrencyAnvilBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.CURRENCY_ANVIL.get(), pos, state);
		this.data = new ContainerData() {
			@Override
			public int get(int index) {
				return switch (index) {
				case 0 -> CurrencyAnvilBlockEntity.this.progress;
				case 1 -> CurrencyAnvilBlockEntity.this.maxProgress;
				default -> 0;
				};
			}

			@Override
			public void set(int index, int value) {
				switch (index) {
				case 0 -> CurrencyAnvilBlockEntity.this.progress = value;
				case 1 -> CurrencyAnvilBlockEntity.this.maxProgress = value;
				}
			}

			@Override
			public int getCount() {
				return 2; // number of variables of the container data (progress and maxProgress, so 2
							// variables)
			}
		};
	}

	@Override
	public Component getDisplayName() {
		return Component.literal("Currency Anvil");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
		return new CurrencyAnvilMenu(id, inventory, this, this.data);
	}

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return lazyItemHandler.cast();
		}

		return super.getCapability(cap, side);
	}

	@Override
	public void onLoad() {
		super.onLoad();
		lazyItemHandler = LazyOptional.of(() -> itemHandler);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		lazyItemHandler.invalidate();
	}

	/***
	 * saves the inventory
	 */
	@Override
	protected void saveAdditional(CompoundTag nbt) {
		nbt.put("inventory", itemHandler.serializeNBT());
		nbt.putInt("currency_anvil.progress", this.progress);

		super.saveAdditional(nbt);
	}

	/***
	 * loads the inventory
	 */
	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		itemHandler.deserializeNBT(nbt.getCompound("inventory"));
		progress = nbt.getInt("currency_anvil.progress");
	}

	/***
	 * drops all the contents of the block when it gets destroyed
	 */
	public void drops() {
		SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
		for (int i = 0; i < itemHandler.getSlots(); i++) {
			inventory.setItem(i, itemHandler.getStackInSlot(i));
		}

		Containers.dropContents(this.level, this.worldPosition, inventory);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, CurrencyAnvilBlockEntity pEntity) {
		if (level.isClientSide()) {
			return;
		}

		if (hasRecipe(pEntity)) {
			pEntity.progress++;
			setChanged(level, pos, state); // reloads if needed every time we add a progress

			if (pEntity.progress >= pEntity.maxProgress) {
				craftItem(pEntity);
			}
		} else {
			pEntity.resetProgress();
			setChanged(level, pos, state);
		}
	}

	private void resetProgress() {
		this.progress = 0;
	}

	private static void craftItem(CurrencyAnvilBlockEntity pEntity) {

		if (hasRecipe(pEntity)) {
			pEntity.itemHandler.extractItem(1, 1, false);
			pEntity.itemHandler.setStackInSlot(2, new ItemStack(ModItems.RANDOM_DAY_OR_NIGHT.get(),
					pEntity.itemHandler.getStackInSlot(2).getCount() + 1));

			pEntity.resetProgress();
		}
	}

	private static boolean hasRecipe(CurrencyAnvilBlockEntity entity) {
		SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
		for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
			inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
		}

		boolean hasRawGemInFirstSlot = entity.itemHandler.getStackInSlot(1).getItem() == Items.DIAMOND;

		return hasRawGemInFirstSlot && canInsertAmountIntoOutputSlot(inventory)
				&& canInsertItemIntoOutputSlot(inventory, new ItemStack(ModItems.RANDOM_DAY_OR_NIGHT.get(), 1));
	}

	private static boolean canInsertItemIntoOutputSlot(SimpleContainer inventory, ItemStack stack) {
		return inventory.getItem(2).getItem() == stack.getItem() || inventory.getItem(2).isEmpty();
	}

	private static boolean canInsertAmountIntoOutputSlot(SimpleContainer inventory) {
		return inventory.getItem(2).getMaxStackSize() > inventory.getItem(2).getCount();
	}
}