package net.nerdshelf.randomizedminecraft.screen;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.nerdshelf.randomizedminecraft.block.ModBlocks;
import net.nerdshelf.randomizedminecraft.block.entity.custom.BankVaultBlockEntity;

public class BankVaultMenu extends AbstractContainerMenu {

	public final BankVaultBlockEntity blockEntity;
	private final Level level;
	private final ContainerData data; // used for synchronization

	public BankVaultMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
		// the number in "new SimpleContainerData(2)" has to match the number in the
		// getCount() method of the Block Entity class
		this(id, inv, inv.player.level.getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(0));
	}

	public BankVaultMenu(int id, Inventory inv, BlockEntity entity, ContainerData data) {
		super(ModMenuTypes.BANK_VAULT_MENU.get(), id);
		checkContainerSize(inv, 21); // the number has to match with the number passed to the method new
										// ItemStackHandler(3) in the Block Entity class
		blockEntity = (BankVaultBlockEntity) entity;
		this.level = inv.player.level;
		this.data = data;

		addPlayerInventory(inv);
		addPlayerHotbar(inv);

		this.blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
			// these are the custom slots where the player can insert the items
			// Row 1
			this.addSlot(new SlotItemHandler(handler, 0, 26, 17));
			this.addSlot(new SlotItemHandler(handler, 1, 44, 17));
			this.addSlot(new SlotItemHandler(handler, 2, 62, 17));
			this.addSlot(new SlotItemHandler(handler, 3, 80, 17));
			this.addSlot(new SlotItemHandler(handler, 4, 98, 17));
			this.addSlot(new SlotItemHandler(handler, 5, 116, 17));
			this.addSlot(new SlotItemHandler(handler, 6, 134, 17));

			// Row 2
			this.addSlot(new SlotItemHandler(handler, 7, 26, 35));
			this.addSlot(new SlotItemHandler(handler, 8, 44, 35));
			this.addSlot(new SlotItemHandler(handler, 9, 62, 35));
			this.addSlot(new SlotItemHandler(handler, 10, 80, 35));
			this.addSlot(new SlotItemHandler(handler, 11, 98, 35));
			this.addSlot(new SlotItemHandler(handler, 12, 116, 35));
			this.addSlot(new SlotItemHandler(handler, 13, 134, 35));

			// Row 3
			this.addSlot(new SlotItemHandler(handler, 14, 26, 53));
			this.addSlot(new SlotItemHandler(handler, 15, 44, 53));
			this.addSlot(new SlotItemHandler(handler, 16, 62, 53));
			this.addSlot(new SlotItemHandler(handler, 17, 80, 53));
			this.addSlot(new SlotItemHandler(handler, 18, 98, 53));
			this.addSlot(new SlotItemHandler(handler, 19, 116, 53));
			this.addSlot(new SlotItemHandler(handler, 20, 134, 53));
		});

		addDataSlots(data);
	}

	// CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
	// must assign a slot number to each of the slots used by the GUI.
	// For this container, we can see both the tile inventory's slots as well as the
	// player inventory slots and the hotbar.
	// Each time we add a Slot to the container, it automatically increases the
	// slotIndex, which means
	// 0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 -
	// 8)
	// 9 - 35 = player inventory slots (which map to the InventoryPlayer slot
	// numbers 9 - 35)
	// 36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 -
	// 8)
	private static final int HOTBAR_SLOT_COUNT = 9;
	private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
	private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
	private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
	private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
	private static final int VANILLA_FIRST_SLOT_INDEX = 0;
	private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

	// THIS YOU HAVE TO DEFINE!
	private static final int TE_INVENTORY_SLOT_COUNT = 3; // must be the number of slots you have!

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		Slot sourceSlot = slots.get(index);
		if (sourceSlot == null || !sourceSlot.hasItem())
			return ItemStack.EMPTY; // EMPTY_ITEM
		ItemStack sourceStack = sourceSlot.getItem();
		ItemStack copyOfSourceStack = sourceStack.copy();

		// Check if the slot clicked is one of the vanilla container slots
		if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
			// This is a vanilla container slot so merge the stack into the tile inventory
			if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX,
					TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT, false)) {
				return ItemStack.EMPTY; // EMPTY_ITEM
			}
		} else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
			// This is a TE slot so merge the stack into the players inventory
			if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT,
					false)) {
				return ItemStack.EMPTY;
			}
		} else {
			System.out.println("Invalid slotIndex:" + index);
			return ItemStack.EMPTY;
		}
		// If stack size == 0 (the entire stack was moved) set slot contents to null
		if (sourceStack.getCount() == 0) {
			sourceSlot.set(ItemStack.EMPTY);
		} else {
			sourceSlot.setChanged();
		}
		sourceSlot.onTake(playerIn, sourceStack);
		return copyOfSourceStack;
	}

	/***
	 * makes the block right-clickable and so that the GUI can be shown to the
	 * player
	 */
	@Override
	public boolean stillValid(Player player) {
		return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player,
				ModBlocks.BANK_VAULT.get());
	}

	private void addPlayerInventory(Inventory playerInventory) {
		for (int i = 0; i < 3; ++i) {
			for (int l = 0; l < 9; ++l) {
				this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
			}
		}
	}

	private void addPlayerHotbar(Inventory playerInventory) {
		for (int i = 0; i < 9; ++i) {
			this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
		}
	}

	/* THIS MENU METHODS */

}