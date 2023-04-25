package net.nerdshelf.randomizedminecraft.block.entity;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.nerdshelf.randomizedminecraft.currency.PlayerCurrencyProvider;
import net.nerdshelf.randomizedminecraft.networking.ModMessages;
import net.nerdshelf.randomizedminecraft.networking.packet.CurrencyManagementC2SPacket;
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
	private int cost;
	private int isSlotOEmpty;
	private int isCrafted;
	private int isSlot2Empty;
	private int currentPlayerCurrency;
	private int isSlot1Empty;
	private int wasCraftedButPlayerCannotAfford;

	private static String itemName;
	private Player currentPlayer = null;
	private boolean isCurrentPlayerInCreative;

	public CurrencyAnvilBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.CURRENCY_ANVIL.get(), pos, state);
		this.data = new ContainerData() {
			@Override
			public int get(int index) {
				return switch (index) {
				case 0 -> CurrencyAnvilBlockEntity.this.cost;
				case 1 -> CurrencyAnvilBlockEntity.this.isSlotOEmpty;
				case 2 -> CurrencyAnvilBlockEntity.this.isCrafted;
				case 3 -> CurrencyAnvilBlockEntity.this.isSlot2Empty;
				case 4 -> CurrencyAnvilBlockEntity.this.currentPlayerCurrency;
				case 5 -> CurrencyAnvilBlockEntity.this.isSlot1Empty;
				case 6 -> CurrencyAnvilBlockEntity.this.wasCraftedButPlayerCannotAfford;
				default -> 0;
				};
			}

			@Override
			public void set(int index, int value) {
				switch (index) {
				case 0 -> CurrencyAnvilBlockEntity.this.cost = value;
				case 1 -> CurrencyAnvilBlockEntity.this.isSlotOEmpty = value;
				case 2 -> CurrencyAnvilBlockEntity.this.isCrafted = value;
				case 3 -> CurrencyAnvilBlockEntity.this.isSlot2Empty = value;
				case 4 -> CurrencyAnvilBlockEntity.this.currentPlayerCurrency = value;
				case 5 -> CurrencyAnvilBlockEntity.this.isSlot1Empty = value;
				case 6 -> CurrencyAnvilBlockEntity.this.wasCraftedButPlayerCannotAfford = value;
				}
			}

			@Override
			public int getCount() {
				return 7; // number of variables of the container data (progress and maxProgress, so 2
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
		this.currentPlayer = player;
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
		nbt.putInt("currency_anvil.cost", this.cost);
		nbt.putInt("currency_anvil.isSlotOEmpty", this.isSlotOEmpty);
		nbt.putInt("currency_anvil.isCrafted", this.isCrafted);
		nbt.putInt("currency_anvil.isSlot2Empty", this.isSlot2Empty);
		nbt.putInt("currency_anvil.currentPlayerCurrency", this.currentPlayerCurrency);
		nbt.putInt("currency_anvil.isSlot1Empty", this.isSlot1Empty);
		nbt.putInt("currency_anvil.wasCraftedButPlayerCannotAfford", this.wasCraftedButPlayerCannotAfford);

		super.saveAdditional(nbt);
	}

	/***
	 * loads the inventory
	 */
	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		itemHandler.deserializeNBT(nbt.getCompound("inventory"));
		cost = nbt.getInt("currency_anvil.cost");
		isSlotOEmpty = nbt.getInt("currency_anvil.isSlotOEmpty");
		isCrafted = nbt.getInt("currency_anvil.isCrafted");
		isSlot2Empty = nbt.getInt("currency_anvil.isSlot2Empty");
		currentPlayerCurrency = nbt.getInt("currency_anvil.currentPlayerCurrency");
		isSlot1Empty = nbt.getInt("currency_anvil.isSlot1Empty");
		wasCraftedButPlayerCannotAfford = nbt.getInt("currency_anvil.wasCraftedButPlayerCannotAfford");
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

		if (pEntity.currentPlayer != null) {
			pEntity.currentPlayer.getCapability(PlayerCurrencyProvider.PLAYER_CURRENCY).ifPresent(currency -> {
				pEntity.currentPlayerCurrency = currency.getCurrency();
			});
			pEntity.isCurrentPlayerInCreative = pEntity.currentPlayer.getAbilities().instabuild;
		}

		if (pEntity.itemHandler.getStackInSlot(0) == ItemStack.EMPTY) {
			pEntity.isSlotOEmpty = 1;
		} else {
			pEntity.isSlotOEmpty = 0;
		}

		if (pEntity.itemHandler.getStackInSlot(1) == ItemStack.EMPTY) {
			pEntity.isSlot1Empty = 1;
		} else {
			pEntity.isSlot1Empty = 0;
		}

		if (pEntity.itemHandler.getStackInSlot(2) == ItemStack.EMPTY) {
			pEntity.isSlot2Empty = 1;
		} else {
			pEntity.isSlot2Empty = 0;
		}

		if (pEntity.currentPlayer != null) {
			// System.out.println("cost: " + pEntity.cost + ", name: " +
			// CurrencyAnvilBlockEntity.itemName + ", isCrafted: " + pEntity.isCrafted);

			// 1 0 0
			if ((pEntity.itemHandler.getStackInSlot(0) != ItemStack.EMPTY)
					&& (pEntity.itemHandler.getStackInSlot(1) == ItemStack.EMPTY)
					&& (pEntity.itemHandler.getStackInSlot(2) == ItemStack.EMPTY)) {
				System.out.println("1 0 0");
				if (pEntity.isCrafted == 1) {
					System.out.println("-------------------------------");
					onTake(pEntity);
				}
			}

			// 1 1 0
			if ((pEntity.itemHandler.getStackInSlot(0) != ItemStack.EMPTY)
					&& (pEntity.itemHandler.getStackInSlot(1) != ItemStack.EMPTY)
					&& (pEntity.itemHandler.getStackInSlot(2) == ItemStack.EMPTY)) {
				System.out.println("1 1 0");
				if (pEntity.isCrafted == 1) {
					System.out.println("-------------------------------");
					onTake(pEntity);
				}
			}

			createResult(pEntity);
			setChanged(level, pos, state); // reloads if needed every time we add a progress

		}

	}

	private void resetCost() {
		this.cost = 0;
	}

	private void resetName() {
		CurrencyAnvilBlockEntity.itemName = null;
	}

	protected static void onTake(CurrencyAnvilBlockEntity pEntity) {

		ItemStack itemStack0 = pEntity.itemHandler.getStackInSlot(0);
		ItemStack itemStack1 = pEntity.itemHandler.getStackInSlot(1);

		if (itemStack0 != ItemStack.EMPTY) {
			pEntity.itemHandler.setStackInSlot(0, ItemStack.EMPTY);
		}

		if (itemStack1 != ItemStack.EMPTY) {
			pEntity.itemHandler.setStackInSlot(1, ItemStack.EMPTY);
		}

		if (!pEntity.currentPlayer.getAbilities().instabuild) {
			ModMessages.sendToServer(new CurrencyManagementC2SPacket(-pEntity.cost));
		}

		pEntity.isCrafted = 0;
		pEntity.resetCost();
		pEntity.resetName();
	}

	public static void createResult(CurrencyAnvilBlockEntity pEntity) {
		ItemStack itemstack = pEntity.itemHandler.getStackInSlot(0);
		pEntity.cost = 1;
		int i = 0;
		int j = 0;
		int k = 0;
		int repairItemCountCost;

		pEntity.wasCraftedButPlayerCannotAfford = 0;

		if (itemstack.isEmpty()) {
			pEntity.itemHandler.setStackInSlot(2, ItemStack.EMPTY);
			pEntity.isCrafted = 0;
			pEntity.cost = 0;
		} else {
			ItemStack itemstack1 = itemstack.copy();
			ItemStack itemstack2 = pEntity.itemHandler.getStackInSlot(1);
			Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemstack1);
			j += itemstack.getBaseRepairCost() + (itemstack2.isEmpty() ? 0 : itemstack2.getBaseRepairCost());
			repairItemCountCost = 0;
			boolean flag = false;

			// if (!net.minecraftforge.common.ForgeHooks.onAnvilChange(this, itemstack,
			// itemstack2, resultSlots, itemName, j, this.player)) return;
			if (!itemstack2.isEmpty()) {
				flag = itemstack2.getItem() == Items.ENCHANTED_BOOK
						&& !EnchantedBookItem.getEnchantments(itemstack2).isEmpty();
				if (itemstack1.isDamageableItem() && itemstack1.getItem().isValidRepairItem(itemstack, itemstack2)) {
					int l2 = Math.min(itemstack1.getDamageValue(), itemstack1.getMaxDamage() / 4);
					if (l2 <= 0) {
						pEntity.itemHandler.setStackInSlot(2, ItemStack.EMPTY);
						pEntity.isCrafted = 0;
						pEntity.cost = 0;
						return;
					}

					int i3;
					for (i3 = 0; l2 > 0 && i3 < itemstack2.getCount(); ++i3) {
						int j3 = itemstack1.getDamageValue() - l2;
						itemstack1.setDamageValue(j3);
						++i;
						l2 = Math.min(itemstack1.getDamageValue(), itemstack1.getMaxDamage() / 4);
					}

					repairItemCountCost = i3;
				} else {
					if (!flag && (!itemstack1.is(itemstack2.getItem()) || !itemstack1.isDamageableItem())) {
						pEntity.itemHandler.setStackInSlot(2, ItemStack.EMPTY);
						pEntity.isCrafted = 0;
						pEntity.cost = 0;
						return;
					}

					if (itemstack1.isDamageableItem() && !flag) {
						int l = itemstack.getMaxDamage() - itemstack.getDamageValue();
						int i1 = itemstack2.getMaxDamage() - itemstack2.getDamageValue();
						int j1 = i1 + itemstack1.getMaxDamage() * 12 / 100;
						int k1 = l + j1;
						int l1 = itemstack1.getMaxDamage() - k1;
						if (l1 < 0) {
							l1 = 0;
						}

						if (l1 < itemstack1.getDamageValue()) {
							itemstack1.setDamageValue(l1);
							i += 2;
						}
					}

					Map<Enchantment, Integer> map1 = EnchantmentHelper.getEnchantments(itemstack2);
					boolean flag2 = false;
					boolean flag3 = false;

					for (Enchantment enchantment1 : map1.keySet()) {
						if (enchantment1 != null) {
							int i2 = map.getOrDefault(enchantment1, 0);
							int j2 = map1.get(enchantment1);
							j2 = i2 == j2 ? j2 + 1 : Math.max(j2, i2);
							boolean flag1 = enchantment1.canEnchant(itemstack);
							if (itemstack.is(Items.ENCHANTED_BOOK)) {
								flag1 = true;
							}

							for (Enchantment enchantment : map.keySet()) {
								if (enchantment != enchantment1 && !enchantment1.isCompatibleWith(enchantment)) {
									flag1 = false;
									++i;
								}
							}

							if (!flag1) {
								flag3 = true;
							} else {
								flag2 = true;
								if (j2 > enchantment1.getMaxLevel()) {
									j2 = enchantment1.getMaxLevel();
								}

								map.put(enchantment1, j2);
								int k3 = 0;
								switch (enchantment1.getRarity()) {
								case COMMON:
									k3 = 1;
									break;
								case UNCOMMON:
									k3 = 2;
									break;
								case RARE:
									k3 = 4;
									break;
								case VERY_RARE:
									k3 = 8;
								}

								if (flag) {
									k3 = Math.max(1, k3 / 2);
								}

								i += k3 * j2;
								if (itemstack.getCount() > 1) {
									i = 40;
								}
							}
						}
					}

					if (flag3 && !flag2) {
						pEntity.itemHandler.setStackInSlot(2, ItemStack.EMPTY);
						pEntity.isCrafted = 0;
						pEntity.cost = 0;
						return;
					}
				}
			}

			if (StringUtils.isBlank(CurrencyAnvilBlockEntity.itemName)) {
				if (itemstack.hasCustomHoverName()) {
					k = 1;
					i += k;
					itemstack1.resetHoverName();
				}
			} else if (!CurrencyAnvilBlockEntity.itemName.equals(itemstack.getHoverName().getString())) {
				k = 1;
				i += k;
				itemstack1.setHoverName(Component.literal(CurrencyAnvilBlockEntity.itemName));
			}
			if (flag && !itemstack1.isBookEnchantable(itemstack2)) {
				itemstack1 = ItemStack.EMPTY;
			}

			pEntity.cost = j + i;
			if (i <= 0) {
				itemstack1 = ItemStack.EMPTY;
			}

			if (k == i && k > 0 && pEntity.data.get(0) >= 40) {
				pEntity.cost = 39;
			}

			if (!itemstack1.isEmpty()) {
				int k2 = itemstack1.getBaseRepairCost();
				if (!itemstack2.isEmpty() && k2 < itemstack2.getBaseRepairCost()) {
					k2 = itemstack2.getBaseRepairCost();
				}

				if (k != i || k == 0) {
					k2 = calculateIncreasedRepairCost(k2);
				}

				itemstack1.setRepairCost(k2);
				EnchantmentHelper.setEnchantments(map, itemstack1);
			}

			if (itemstack1 != ItemStack.EMPTY && pEntity.cost > pEntity.currentPlayerCurrency
					&& !pEntity.isCurrentPlayerInCreative) {
				itemstack1 = ItemStack.EMPTY;
				pEntity.wasCraftedButPlayerCannotAfford = 1;
			}

			pEntity.itemHandler.setStackInSlot(2, itemstack1);
			if (itemstack1 == ItemStack.EMPTY) {
				pEntity.isCrafted = 0;
			} else {
				pEntity.isCrafted = 1;
			}

		}
	}

	public static int calculateIncreasedRepairCost(int p_39026_) {
		return p_39026_ * 2 + 1;
	}

	public void setItemName(String name) {

		System.out.println("SetItemName: " + name);

		String newName = name;
		ItemStack itemStack = this.itemHandler.getStackInSlot(0);
		if (itemStack != null && (itemStack != ItemStack.EMPTY) && !itemStack.hasCustomHoverName()
				&& name.equals(itemStack.getHoverName().getString())) {
			newName = "";
		}

		CurrencyAnvilBlockEntity.itemName = newName;

		System.out.println("InsideItemName: " + name);

		if (this.itemHandler.getStackInSlot(2) != ItemStack.EMPTY) {
			ItemStack itemstack = this.itemHandler.getStackInSlot(2);
			if (StringUtils.isBlank(name)) {
				itemstack.resetHoverName();
			} else {
				itemstack.setHoverName(Component.literal(CurrencyAnvilBlockEntity.itemName));
			}
		}

		createResult(this);
	}

}