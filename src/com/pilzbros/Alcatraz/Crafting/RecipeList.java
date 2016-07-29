package com.pilzbros.Alcatraz.Crafting;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


public class RecipeList {
	/**
	 * @author Plo457
	 */
	public RecipeList(){
		// Example:
		new PloRecipe(new ItemStackBuilder(Material.STONE).setDisplayName("Solid Stone").build())
		.setSlot5(new ItemStack(Material.STONE))
		.setSlot8(new ItemStack(Material.STONE))
		.register();
		// This automatically adds the recipe to the list, and registers it with bukkit
		// Remember to put 'new RecipeList()' in your onEnable of your plugin
	}
}
