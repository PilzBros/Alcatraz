package com.pilzbros.Alcatraz.Crafting;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class CraftManager implements Listener {
	/**
	 * @author Plo457
	 */
	@EventHandler
	public void preCraftEvent(PrepareItemCraftEvent e){
		ItemStack[] items = e.getInventory().getMatrix().clone();
		e.getInventory().setResult(new ItemStack(Material.AIR));
		for (PloRecipe recipe : PloRecipeList.getShapedRecipes()){
			if (RecipeMatch.matches(Arrays.asList(items), recipe)){
				e.getInventory().setResult(recipe.getOutput());
				break;
			}
		}
	}
}
