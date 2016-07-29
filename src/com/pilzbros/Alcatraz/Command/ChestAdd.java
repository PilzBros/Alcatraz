package com.pilzbros.Alcatraz.Command;



import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.pilzbros.Alcatraz.Alcatraz;
import com.pilzbros.Alcatraz.IO.Setting;
import com.pilzbros.Alcatraz.IO.Settings;
import com.pilzbros.Alcatraz.Objects.Prison;



public class ChestAdd 
{
	@SuppressWarnings("unused")
	private int state;
	public static HashMap<String,CreationPlayer> players = new HashMap<String,CreationPlayer>();
	
	public ChestAdd()
	{
		state = 0;
	}
	
	public static void selectstart(Player player, Prison p, int cellNumber, String type)
	{
		if (players.containsKey(player.getName()))
		{
			players.remove(player.getName());
		}
		
		player.sendMessage(Alcatraz.pluginPrefix + ChatColor.AQUA + "---------- "+Alcatraz.language.get(player, "chatChestAddHeader", "Alcatraz Add Chest")+" ----------");
		player.sendMessage(ChatColor.GREEN + Alcatraz.language.get(player, "chatChestAdd", "Select the {0}{1}{2} chest with the selection tool ({3}{4}{5})", ChatColor.LIGHT_PURPLE, type, ChatColor.WHITE, ChatColor.YELLOW, Material.getMaterial(Settings.getGlobalInt(Setting.SelectionTool)) ,ChatColor.WHITE));
		player.sendMessage(ChatColor.AQUA + "--------------------------------------");
		players.put(player.getName(), new CreationPlayer());
		players.get(player.getName()).prison = p;
		players.get(player.getName()).type = type;
		players.get(player.getName()).cellNumber = cellNumber;
	}
	
	public static void select(Player player, Block block)
	{
		switch (players.get(player.getName()).state)
		{
			case 1:
				chest(player,block);
				break;
		}
	}
	
	private static void chest(Player player, Block block)
	{
		CreationPlayer cr = players.get(player.getName());
		
		//Verify that it is a block
		if (block.getType().equals(Material.CHEST) || block.getType().equals(Material.TRAPPED_CHEST))
		{
			//Store data
			cr.chest = block.getLocation();
			cr.state++;

			//Store it
			Alcatraz.IO.newChest(block.getLocation(), players.get(player.getName()).type, players.get(player.getName()).prison, players.get(player.getName()).cellNumber);
			
			//Determine chest type and add to appropriate prison data structure
			if(players.get(player.getName()).type.equalsIgnoreCase("food"))
			{
				Alcatraz.prisonController.getPrison(players.get(player.getName()).prison.getName()).getChestManager().addFoodChest(block.getLocation());
			}
			else if(players.get(player.getName()).type.equalsIgnoreCase("random"))
			{
				Alcatraz.prisonController.getPrison(players.get(player.getName()).prison.getName()).getChestManager().addRandomChest(block.getLocation());	
			}
			else if(players.get(player.getName()).type.equalsIgnoreCase("cell"))
			{
				//TODO 
			}
			
			//Notify of success
			player.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(player, "chatChestAddSuccess", "{0}{1}{2} chest added to {3}{4}{5} successfully{7}!", ChatColor.LIGHT_PURPLE, players.get(player.getName()).type, ChatColor.WHITE, ChatColor.RED, players.get(player.getName()).prison.getName(), ChatColor.WHITE, ChatColor.GREEN, ChatColor.WHITE)) ;
		
			//Remove from players list
			players.remove(player.getName());
		}
		else
		{
			//Notify of error
			player.sendMessage(Alcatraz.pluginPrefix + ChatColor.RED + Alcatraz.language.get(player, "chatChestAddWrongType", "Wrong block type {0}{1}{2} selected! You must select a chest with the selection tool. Please try your selection again", ChatColor.AQUA, block.getType().toString(), ChatColor.RED));
		}
	}


	
	private static class CreationPlayer
	{
		public int state;
		
		private Prison prison;
		private String type;
		private int cellNumber;
		private Location chest;
		
		public CreationPlayer()
		{
			state = 1;
		}
}

}
