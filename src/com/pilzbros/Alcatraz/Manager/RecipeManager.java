package com.pilzbros.Alcatraz.Manager;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import com.pilzbros.Alcatraz.Alcatraz;
import com.pilzbros.Alcatraz.Crafting.ItemStackBuilder;
import com.pilzbros.Alcatraz.Crafting.PloRecipe;

public class RecipeManager 
{
	public void RecipeManafer()
	{
		//
	}
	
	public void loadRecipes()
	{
		//Prison Shank
		//
		String lore = ChatColor.GREEN + Alcatraz.language.get(Bukkit.getConsoleSender(), "recipeShankLore", "Nice and sharp...");
		new PloRecipe(new ItemStackBuilder(Material.BLAZE_ROD).setDisplayName(ChatColor.RED+Alcatraz.language.get(Bukkit.getConsoleSender(), "recipeShankName", "Prison Shank")).addLore(lore).build())
		.setSlot2(new ItemStack(Material.STICK))
		.setSlot5(new ItemStack(Material.STICK))
		.setSlot8(new ItemStack(Material.STICK))
		.register();
		
		//Lock Pick
		String pick = ChatColor.WHITE + Alcatraz.language.get(Bukkit.getConsoleSender(), "recipePickLore", "50% chance of picking any lock on a locked door or chest");
		new PloRecipe(new ItemStackBuilder(Material.PRISMARINE_SHARD).setDisplayName(ChatColor.RED+"Prison " + ChatColor.BLUE + Alcatraz.language.get(Bukkit.getConsoleSender(), "recipePickName", "Lock Pick")).addLore(pick).build())
		.setSlot2(new ItemStack(Material.IRON_INGOT))
		.setSlot5(new ItemStack(Material.STRING))
		.setSlot8(new ItemStack(Material.STICK))
		.register();
		
		//Fishing Pole to Iron Ingot
		new PloRecipe(new ItemStackBuilder(Material.IRON_INGOT).build())
		.setSlot5(new ItemStack(Material.FISHING_ROD))
		.register();
		
	}
	
	

}
