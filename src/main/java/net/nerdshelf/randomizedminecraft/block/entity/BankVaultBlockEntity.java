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
import net.nerdshelf.randomizedminecraft.networking.ModMessages;
import net.nerdshelf.randomizedminecraft.networking.packet.CurrencyManagementC2SPacket;
import net.nerdshelf.randomizedminecraft.screen.BankVaultMenu;

public class BankVaultBlockEntity extends BlockEntity implements MenuProvider {

	private final int numberOfSlots = 21;
	private static boolean isGuiWasClosed = false;
	private Player currentPlayer = null;

	// it's basically the inventory of the block entity
	private final ItemStackHandler itemHandler = new ItemStackHandler(numberOfSlots) {
		@Override
		protected void onContentsChanged(int slot) {
			setChanged();
		}
	};

	// makes the inventory available via capabilities
	private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

	protected final ContainerData data; // used to send data to the menu. the menu is responsibile for synchronizing the
										// server data to the client

	public BankVaultBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.BANK_VAULT.get(), pos, state);
		this.data = new ContainerData() {
			@Override
			public int get(int index) {
				return switch (index) {
				default -> 0;
				};
			}

			@Override
			public void set(int index, int value) {
				switch (index) {
				}
			}

			@Override
			public int getCount() {
				return 0; // number of variables of the container data (progress and maxProgress, so 2
							// variables)
			}
		};
	}

	@Override
	public Component getDisplayName() {
		return Component.literal("Bank Vault");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
		this.currentPlayer = player;
		return new BankVaultMenu(id, inventory, this, this.data);
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
		super.saveAdditional(nbt);
	}

	/***
	 * loads the inventory
	 */
	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		itemHandler.deserializeNBT(nbt.getCompound("inventory"));
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

	public static void tick(Level level, BlockPos pos, BlockState state, BankVaultBlockEntity pEntity) {

		if (level.isClientSide()) {
			return;
		}

		setChanged(level, pos, state); // reloads if needed every time we add a progress

		if (BankVaultBlockEntity.isGuiWasClosed) {

			System.out.println("Clearing");

			int numberOfSlots = pEntity.numberOfSlots;

			int currencyToBeGiven = 0;

			for (int i = 0; i < numberOfSlots; i++) {

				ItemStack item = pEntity.itemHandler.getStackInSlot(i);

				if (item.getItem() == Items.DIAMOND) {
					currencyToBeGiven += item.getCount() * 10;
				}

				pEntity.itemHandler.setStackInSlot(i, new ItemStack(Items.AIR));
			}

			ModMessages.sendToServer(new CurrencyManagementC2SPacket(currencyToBeGiven));
			BankVaultBlockEntity.isGuiWasClosed = false;

		}
	}

	public void giveCurrency(BankVaultBlockEntity pEntity) {
		BankVaultBlockEntity.isGuiWasClosed = true;
	}

}