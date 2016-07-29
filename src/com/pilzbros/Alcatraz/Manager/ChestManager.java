package com.pilzbros.Alcatraz.Manager;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.pilzbros.Alcatraz.Alcatraz;
import com.pilzbros.Alcatraz.Objects.Prison;

public class ChestManager 
{
	@SuppressWarnings("unused")
	private Prison prison;
	
	private ArrayList<Location> rewardChests;
	private ArrayList<Location> randomChests;
	private ArrayList<Location> foodChests;
	
	public ChestManager(Prison p)
	{
		this.prison = p;
		this.randomChests = new ArrayList<>();
		this.rewardChests = new ArrayList<>();
		this.foodChests = new ArrayList<>();
	}
	
	public void addRandomChest(Location l)
	{
		this.randomChests.add(l);
	}
	
	public void addFoodChest(Location l)
	{
		this.foodChests.add(l);
	}
	
	public void addRewardChest(Location l)
	{
		this.rewardChests.add(l);
	}

	public void regenerateRandomChests()
	{
		Random randomValue = new Random();
		
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		
		//Random
		items.add(new ItemStack(Material.FISHING_ROD, randomValue.nextInt(2 - 0 + 1) + 0));
		items.add(new ItemStack(Material.WOOL, randomValue.nextInt(2 - 0 + 1) + 0));
		items.add(new ItemStack(Material.IRON_INGOT, randomValue.nextInt(2 - 0 + 1) + 0));
		items.add(new ItemStack(Material.STRING, randomValue.nextInt(2 - 0 + 1) + 0));
		items.add(new ItemStack(Material.SPIDER_EYE, randomValue.nextInt(2 - 0 + 1) + 0));
		items.add(new ItemStack(Material.GOLDEN_APPLE, randomValue.nextInt(2 - 0 + 1) + 0));
		items.add(new ItemStack(Material.MELON, randomValue.nextInt(2 - 0 + 1) + 0));
		items.add(new ItemStack(Material.STICK, randomValue.nextInt(75 - 7 + 1) + 7));
		
		//Armor
		items.add(new ItemStack(Material.DIAMOND_BOOTS, randomValue.nextInt(1 - 0 + 1) + 0));
		items.add(new ItemStack(Material.LEATHER_CHESTPLATE, randomValue.nextInt(2 - 0 + 1) + 0));
		items.add(new ItemStack(Material.BOWL, randomValue.nextInt(2 - 0 + 1) + 0));
		
		//Tools
		items.add(new ItemStack(Material.IRON_PICKAXE, randomValue.nextInt(1 - 0 + 1) + 0));
		items.add(new ItemStack(Material.GOLD_PICKAXE, randomValue.nextInt(1 - 0 + 1) + 0));
		items.add(new ItemStack(Material.DIAMOND_PICKAXE, randomValue.nextInt(1 - 0 + 1) + 0));
		
		//Potion
		items.add(new ItemStack(Material.POISONOUS_POTATO, randomValue.nextInt(2 - 0 + 1) + 0));
		items.add(new ItemStack(Material.POTION, randomValue.nextInt(2 - 0 + 1) + 0, (short) 8197)); //healing
		items.add(new ItemStack(Material.POTION, randomValue.nextInt(2 - 0 + 1) + 0, (short) 16388)); //poison splash
		items.add(new ItemStack(Material.POTION, randomValue.nextInt(2 - 0 + 1) + 0, (short) 16392)); //weakness splash
		items.add(new ItemStack(Material.POTION, randomValue.nextInt(2 - 0 + 1) + 0, (short) 16394)); //slowness

		
		for (int i = 0; i < randomChests.size(); i++) 
		{
			try
			{
			    Location loc = randomChests.get(i);
			   
			    
			    Chest chest = (Chest)loc.getBlock().getState();
			    chest.getInventory().clear();
			    
			    for (int x = 0; x < items.size()-1; x++)
			    {
			    	int arrayLoc = randomValue.nextInt((items.size()-1) - 0 + 1) + 0;
			    	
			    	if (items.get(arrayLoc).getAmount() > 0)
			    	{
			    		chest.getInventory().addItem(items.get(arrayLoc));
			    	}
			    }
			    
			    chest.update();
			    
			}
			catch (Exception e)
			{
				//IO remove chest
				Alcatraz.log.log(Level.WARNING, "Error updating random chests, removing the rottom one...");
				Alcatraz.IO.removeChest(foodChests.get(i));
			}    
		}
	}
	
	@SuppressWarnings("unused")
	public void regenerateFoodChests()
	{
		Random randomValue = new Random();
		Random ran = new Random();
		
		int numChicken = randomValue.nextInt((7 - 0) + 1) + 0;
		int numApple = randomValue.nextInt((50 - 0) + 1) + 0;
		 
		//Chicken
		ItemStack chicken = new ItemStack(Material.COOKED_CHICKEN, 1);
		ItemMeta c = chicken.getItemMeta();
	    c.setDisplayName("Prison Chicken");
	    chicken.setItemMeta(c);
	    
	    //Rotten Apples
	    ItemStack apple = new ItemStack(Material.APPLE, numApple);
		ItemMeta a = apple.getItemMeta();
		apple.addUnsafeEnchantment(Enchantment.THORNS, 3);
	    a.setDisplayName(ChatColor.DARK_RED + "Prison Rotten Apple");
	    apple.setItemMeta(a);
	    
	    //Shank
	    ItemStack shank = new ItemStack(Material.BLAZE_ROD, 1);
		ItemMeta s = shank.getItemMeta();
		shank.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);
		shank.addUnsafeEnchantment(Enchantment.KNOCKBACK, 4);
	    s.setDisplayName(ChatColor.RED + "Prison Shank");
	    shank.setItemMeta(s);
	    
	    //Random
	   
	    
		for (int i = 0; i < foodChests.size(); i++) 
		{
			try
			{
			    Location loc = foodChests.get(i);
			    
			    Chest chest = (Chest)loc.getBlock().getState();
			    chest.getInventory().clear();
			    chest.getInventory().addItem(chicken);
			    chest.getInventory().addItem(apple);
			    
			    //Random
			    if (ran.nextInt(2) == 1)
			    {
			    	chest.getInventory().addItem(shank);
			    }
			    
			    chest.update();
			}
			catch (Exception e)
			{
				//IO remove chest
				Alcatraz.log.log(Level.WARNING, Alcatraz.consolePrefix + "Error updating meal chest");
				Alcatraz.IO.removeChest(foodChests.get(i));
			}    
		}
	}
	
	public void regenerateRewardChests()
	{
		//
	}

}
