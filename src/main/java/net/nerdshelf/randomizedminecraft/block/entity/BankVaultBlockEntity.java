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

			int x = 10;

			int numberOfSlots = pEntity.numberOfSlots;
			int currencyToBeGiven = 0;

			for (int i = 0; i < numberOfSlots; i++) {

				ItemStack item = pEntity.itemHandler.getStackInSlot(i);

				// Rarity coefficient: 101
				if (item.getItem() == Items.SLIME_BALL) {
					currencyToBeGiven += item.getCount() * 10 * x;
				}

				// Rarity coefficient: 119
				if (item.getItem() == Items.MAGMA_CREAM) {
					currencyToBeGiven += item.getCount() * 11 * x;
				}

				// Rarity coefficient: 120
				if (item.getItem() == Items.IRON_INGOT) {
					currencyToBeGiven += item.getCount() * 12 * x;
				}

				// Rarity coefficient: 138
				if (item.getItem() == Items.PRISMARINE_CRYSTALS) {
					currencyToBeGiven += item.getCount() * 13 * x;
				}

				// Rarity coefficient: 148
				if (item.getItem() == Items.REDSTONE) {
					currencyToBeGiven += item.getCount() * 14 * x;
				}

				// Rarity coefficient: 160
				if (item.getItem() == Items.GOLD_INGOT) {
					currencyToBeGiven += item.getCount() * 16 * x;
				}

				// Rarity coefficient: 180
				if (item.getItem() == Items.LAPIS_LAZULI) {
					currencyToBeGiven += item.getCount() * 18 * x;
				}

				// Rarity coefficient: 192
				if (item.getItem() == Items.ENDER_PEARL) {
					currencyToBeGiven += item.getCount() * 19 * x;
				}

				// Rarity coefficient: 200
				if (item.getItem() == Items.HEART_OF_THE_SEA) {
					currencyToBeGiven += item.getCount() * 20 * x;
				}

				// Rarity coefficient: 231
				if (item.getItem() == Items.POISONOUS_POTATO) {
					currencyToBeGiven += item.getCount() * 23 * x;
				}

				// Rarity coefficient: 239
				if (item.getItem() == Items.NAME_TAG) {
					currencyToBeGiven += item.getCount() * 23 * x;
				}

				// Rarity coefficient: 250
				if (item.getItem() == Items.DIAMOND) {
					currencyToBeGiven += item.getCount() * 25 * x;
				}

				// Rarity coefficient: 252
				if (item.getItem() == Items.SADDLE) {
					currencyToBeGiven += item.getCount() * 25 * x;
				}

				// Rarity coefficient: 300
				if (item.getItem() == Items.SADDLE) {
					currencyToBeGiven += item.getCount() * 30 * x;
				}

				// Rarity coefficient: 325
				if (item.getItem() == Items.ENDER_EYE) {
					currencyToBeGiven += item.getCount() * 32 * x;
				}

				// Rarity coefficient: 330
				if (item.getItem() == Items.GHAST_TEAR) {
					currencyToBeGiven += item.getCount() * 33 * x;
				}

				// Rarity coefficient: 370
				if (item.getItem() == Items.BLAZE_ROD) {
					currencyToBeGiven += item.getCount() * 37 * x;
				}

				// Rarity coefficient: 390
				if (item.getItem() == Items.PHANTOM_MEMBRANE) {
					currencyToBeGiven += item.getCount() * 39 * x;
				}

				// Rarity coefficient: 400
				if (item.getItem() == Items.WITHER_SKELETON_SKULL) {
					currencyToBeGiven += item.getCount() * 40 * x;
				}

				// Rarity coefficient: 540
				if (item.getItem() == Items.EMERALD) {
					currencyToBeGiven += item.getCount() * 54 * x;
				}

				// Rarity coefficient: 550
				if (item.getItem() == Items.ENCHANTED_GOLDEN_APPLE) {
					currencyToBeGiven += item.getCount() * 55 * x;
				}

				// Rarity coefficient: 680
				if (item.getItem() == Items.END_CRYSTAL) {
					currencyToBeGiven += item.getCount() * 68 * x;
				}

				// Rarity coefficient: 550
				if (item.getItem() == Items.ENCHANTED_GOLDEN_APPLE) {
					currencyToBeGiven += item.getCount() * 55 * x;
				}

				// Rarity coefficient: 1000
				if (item.getItem() == Items.NETHER_STAR) {
					currencyToBeGiven += item.getCount() * 100 * x;
				}

				// Rarity coefficient: 1350
				if (item.getItem() == Items.DRAGON_BREATH) {
					currencyToBeGiven += item.getCount() * 135 * x;
				}

				// Rarity coefficient: 1370
				if (item.getItem() == Items.LINGERING_POTION) {
					currencyToBeGiven += item.getCount() * 137 * x;
				}

				// Rarity coefficient: 1600
				if (item.getItem() == Items.DRAGON_EGG) {
					currencyToBeGiven += item.getCount() * 160 * x;
				}

				// Rarity coefficient: 1660
				if (item.getItem() == Items.ELYTRA) {
					currencyToBeGiven += item.getCount() * 166 * x;
				}

				pEntity.itemHandler.setStackInSlot(i, new ItemStack(Items.AIR));
			}

			if (currencyToBeGiven != 0) {
				ModMessages.sendToServer(new CurrencyManagementC2SPacket(currencyToBeGiven));
			}

			BankVaultBlockEntity.isGuiWasClosed = false;

		}
	}

	public void giveCurrency(BankVaultBlockEntity pEntity) {
		BankVaultBlockEntity.isGuiWasClosed = true;
	}

}