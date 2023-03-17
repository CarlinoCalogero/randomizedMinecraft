package net.nerdshelf.randomizedminecraft.player;

import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

public class CustomFoodStats extends FoodData {

	@Override
	public void tick(Player player) {

		int foodLevel = getFoodLevel();
		float saturationLevel = getSaturationLevel();
		float exhaustionLevel = getExhaustionLevel();
		int tickTimer = ObfuscationReflectionHelper.getPrivateValue(FoodData.class, player.getFoodData(), "tickTimer");

//		System.out.println("\n\n\nOVERRIDDEN" + "\nFoodLevel: " + foodLevel + "\nFoodSaturationLevel: "
//				+ saturationLevel + "\nExhaustionLevel: " + exhaustionLevel + "\nTickTimer:  " + tickTimer
//				+ "\nLastFoodLevel: " + getLastFoodLevel() + "\n\n\n");

		Difficulty difficulty = player.level.getDifficulty();
		ObfuscationReflectionHelper.setPrivateValue(FoodData.class, player.getFoodData(), foodLevel, "lastFoodLevel");
		if (exhaustionLevel > 4.0F) {
			setExhaustion(exhaustionLevel - 4.0F);
			if (saturationLevel > 0.0F) {
				setSaturation(Math.max(saturationLevel - 1.0F, 0.0F));
			} else if (difficulty != Difficulty.PEACEFUL) {
				setFoodLevel(Math.max(foodLevel - 1, 0));
			}
		}

		if (saturationLevel > 0.0F && player.isHurt() && foodLevel >= 20) {
			ObfuscationReflectionHelper.setPrivateValue(FoodData.class, player.getFoodData(), ++tickTimer, "tickTimer");
			tickTimer = ObfuscationReflectionHelper.getPrivateValue(FoodData.class, player.getFoodData(), "tickTimer");
			if (tickTimer >= 10) {
				float f = Math.min(saturationLevel, 6.0F);
				addExhaustion(f);
				ObfuscationReflectionHelper.setPrivateValue(FoodData.class, player.getFoodData(), 0, "tickTimer");
				tickTimer = ObfuscationReflectionHelper.getPrivateValue(FoodData.class, player.getFoodData(),
						"tickTimer");
			}
		} else if (foodLevel >= 18 && player.isHurt()) {
			ObfuscationReflectionHelper.setPrivateValue(FoodData.class, player.getFoodData(), ++tickTimer, "tickTimer");
			tickTimer = ObfuscationReflectionHelper.getPrivateValue(FoodData.class, player.getFoodData(), "tickTimer");
			if (tickTimer >= 80) {
				addExhaustion(6.0F);
				ObfuscationReflectionHelper.setPrivateValue(FoodData.class, player.getFoodData(), 0, "tickTimer");
				tickTimer = ObfuscationReflectionHelper.getPrivateValue(FoodData.class, player.getFoodData(),
						"tickTimer");
			}
		} else if (foodLevel <= 0) {
			ObfuscationReflectionHelper.setPrivateValue(FoodData.class, player.getFoodData(), ++tickTimer, "tickTimer");
			tickTimer = ObfuscationReflectionHelper.getPrivateValue(FoodData.class, player.getFoodData(), "tickTimer");
			if (tickTimer >= 80) {
				if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD
						|| player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
					player.hurt(DamageSource.STARVE, 1.0F);
				}

				ObfuscationReflectionHelper.setPrivateValue(FoodData.class, player.getFoodData(), 0, "tickTimer");
				tickTimer = ObfuscationReflectionHelper.getPrivateValue(FoodData.class, player.getFoodData(),
						"tickTimer");
			}
		} else {
			ObfuscationReflectionHelper.setPrivateValue(FoodData.class, player.getFoodData(), 0, "tickTimer");
			tickTimer = ObfuscationReflectionHelper.getPrivateValue(FoodData.class, player.getFoodData(), "tickTimer");
		}

	}

}
