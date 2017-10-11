package lilliputian.compat;

import lilliputian.potions.PotionLilliputian;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import rustic.common.blocks.crops.Herbs;
import rustic.common.crafting.AdvancedCondenserRecipe;
import rustic.common.crafting.BasicCondenserRecipe;
import rustic.common.crafting.Recipes;

public class RusticCompat {
	
	public static void initElixirs() {
		Recipes.condenserRecipes.add(new BasicCondenserRecipe(new PotionEffect(PotionLilliputian.SHRINKING_POTION, 3600, 0), new ItemStack(Blocks.BROWN_MUSHROOM), new ItemStack(Items.MUSHROOM_STEW)));
		Recipes.condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(PotionLilliputian.SHRINKING_POTION, 9600, 0), new ItemStack(Herbs.HORSETAIL), new ItemStack(Blocks.BROWN_MUSHROOM), new ItemStack(Items.MUSHROOM_STEW)));
		Recipes.condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(PotionLilliputian.SHRINKING_POTION, 1800, 1), new ItemStack(Herbs.MARSH_MALLOW), new ItemStack(Blocks.BROWN_MUSHROOM), new ItemStack(Items.MUSHROOM_STEW)));
		
		Recipes.condenserRecipes.add(new BasicCondenserRecipe(new PotionEffect(PotionLilliputian.GROWING_POTION, 3600, 0), new ItemStack(Blocks.RED_MUSHROOM), new ItemStack(Items.CAKE)));
		Recipes.condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(PotionLilliputian.GROWING_POTION, 9600, 0), new ItemStack(Herbs.HORSETAIL), new ItemStack(Blocks.RED_MUSHROOM), new ItemStack(Items.CAKE)));
		Recipes.condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(PotionLilliputian.GROWING_POTION, 1800, 1), new ItemStack(Herbs.MARSH_MALLOW), new ItemStack(Blocks.RED_MUSHROOM), new ItemStack(Items.CAKE)));
	}

}
