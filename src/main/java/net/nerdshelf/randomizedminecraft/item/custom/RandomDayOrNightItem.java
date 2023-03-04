package net.nerdshelf.randomizedminecraft.item.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RandomDayOrNightItem extends Item {

	public RandomDayOrNightItem(Properties properties) {
		super(properties);
		// TODO Auto-generated constructor stub
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

		// !level.isClientSide() we are on the server - all things should happen on the
		// server (things like spawning a mob and so on)
		if (!level.isClientSide() && hand == InteractionHand.MAIN_HAND) {
			// Output Day or Night
			outputDayOrNight(player);
			// Set a Cooldown
			player.getCooldowns().addCooldown(this, 20); // 20 ticks is equal to 1 second
		}

		return super.use(level, player, hand);
	}

	private String getRandomDayOrNight() {
		if (RandomSource.createNewThreadLocalInstance().nextBoolean()) {
			return "Day";
		}
		return "Night";
	}

	private void outputDayOrNight(Player player) {
		player.sendSystemMessage(Component.literal("Today you have to play in " + getRandomDayOrNight()));
	}

}
