package com.pilzbros.Alcatraz.Crafting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RecipeMatch {
	/**
	 * @author Plo457
	 */
	public static boolean matches(List<ItemStack> items, PloRecipe recipe){
		for (int i = 0;i < 9;i++){
			ItemStack it = items.get(i).clone();
			if (it.hasItemMeta()){
				ItemMeta im = it.getItemMeta();
				im.setLore(new ArrayList<String>());
				for (Entry<Enchantment, Integer> enchant : im.getEnchants().entrySet()){
					im.removeEnchant(enchant.getKey());
				}
				if (it.getType().getMaxDurability() > 30){
					it.setDurability((short) 0);
				}
				it.setItemMeta(im);
			}
			items.set(i,it);
		}
		/*for (int i = 0;i < 9;i++){
			ItemStack it = recipe.getSlots()[i];
			if (it.hasItemMeta()){
				ItemMeta im = it.getItemMeta();
				im.setLore(new ArrayList<String>());
				for (Entry<Enchantment, Integer> enchant : im.getEnchants().entrySet()){
					im.removeEnchant(enchant.getKey());
				}
				if (it.getType().getMaxDurability() > 30){
					it.setDurability((short) 0);
				}
				it.setItemMeta(im);
			}
			recipe.setSlot(i+1, it);
		}*/
		boolean match = true;
		for (int i = 0;i < 9;i++){
			//Bukkit.broadcastMessage("&e-------");
			//Bukkit.broadcastMessage("&c"+items.get(i).getType().toString()+" &e== &6"+recipe.getSlots()[i].getType().toString());
			/*if (items.get(i).hasItemMeta() && items.get(i).getItemMeta().hasDisplayName()){
				Bukkit.broadcastMessage("&cCT:HasDisplay:"+items.get(i).getItemMeta().getDisplayName());
			}
			if (recipe.getSlots()[i].hasItemMeta() && recipe.getSlots()[i].getItemMeta().hasDisplayName()){
				Bukkit.broadcastMessage("&6RP:HasDisplay:"+recipe.getSlots()[i].getItemMeta().getDisplayName());
			}*/
			if ((items.get(i).hasItemMeta() && items.get(i).getItemMeta().hasDisplayName() && recipe.getSlots()[i].hasItemMeta() && recipe.getSlots()[i].getItemMeta().hasDisplayName() && recipe.getSlots()[i].getItemMeta().getDisplayName().equalsIgnoreCase(items.get(i).getItemMeta().getDisplayName()))){
				//Bukkit.broadcastMessage("DisplayName: True");
			} else if (items.get(i).hasItemMeta() == false && recipe.getSlots()[i].hasItemMeta() == false){
				//Bukkit.broadcastMessage("DisplayName: True");
			} else if (items.get(i).hasItemMeta() == false && recipe.getSlots()[i].hasItemMeta() == false && items.get(i).getItemMeta().hasDisplayName() == false && recipe.getSlots()[i].getItemMeta().hasDisplayName() == false){
				//Bukkit.broadcastMessage("DisplayName: True");
			} else {
				//Bukkit.broadcastMessage("DisplayName: False");
				match = false;
			}
			if (!(items.get(i).getType() == recipe.getSlots()[i].getType())){
				match = false;
				//Bukkit.broadcastMessage("Type: False");
			} else {
				//Bukkit.broadcastMessage("Type: True");
			}
			/*if (items.get(i) != recipe.getSlots()[i]){
				match = false;
				Bukkit.broadcastMessage("&4False!");
			} else {
				Bukkit.broadcastMessage("&2True!");
			}*/
		}
		return match;
	}
}
