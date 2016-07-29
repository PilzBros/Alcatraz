package com.pilzbros.Alcatraz.Command;



import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.pilzbros.Alcatraz.Alcatraz;
import com.pilzbros.Alcatraz.IO.Setting;
import com.pilzbros.Alcatraz.IO.Settings;
import com.pilzbros.Alcatraz.Objects.Prison;



public class PrisonCreation 
{
	private int state;
	public static HashMap<String,CreationPlayer> players = new HashMap<String,CreationPlayer>();
	
	public PrisonCreation()
	{
		state = 0;
	}
	
	public static void selectstart(Player player, String name)
	{
		if (players.containsKey(player.getName()))
		{
			players.remove(player.getName());
		}
		
		player.sendMessage(Alcatraz.pluginPrefix + ChatColor.AQUA + Alcatraz.language.get(player, "arenaCreationHeader1", "----------Alcatraz Arena Creation----------"));
		player.sendMessage(ChatColor.GREEN + Alcatraz.language.get(player, "arenaCreationSelectPoint1", "Select the first point (lower point) of the prison perimeter with the selection tool ({0}{1}{2})", ChatColor.YELLOW, Material.getMaterial(Settings.getGlobalInt(Setting.SelectionTool)) ,ChatColor.WHITE));
		player.sendMessage(ChatColor.AQUA + "--------------------------------------");
		players.put(player.getName(), new CreationPlayer());
		players.get(player.getName()).name = name.toLowerCase();
	}
	
	public static void select(Player player, Block block)
	{
		switch (players.get(player.getName()).state)
		{
			case 1:
				firstpoint(player,block);
				break;
			case 2:
				secondpoint(player,block);
				break;
			case 3:
				startPoint(player);
				break;
			case 4:
				returnPoint(player);
				break;	
		}
	}
	
	private static void firstpoint(Player player, Block block)
	{
		player.sendMessage(Alcatraz.pluginPrefix + ChatColor.YELLOW + Alcatraz.language.get(player, "arenaCreationHeader1", "----------Alcatraz Arena Creation----------"));
		player.sendMessage(ChatColor.GREEN + Alcatraz.language.get(player, "arenaCreationSelectPoint2", "First point selected! Select the second point (highest point) of the prison perimeter with the selection tool ({0}{1}{2})", ChatColor.YELLOW, Material.getMaterial(Settings.getGlobalInt(Setting.SelectionTool)) ,ChatColor.WHITE));
		player.sendMessage(ChatColor.YELLOW + "---------------------------------------");
		CreationPlayer cr = players.get(player.getName());
		cr.b1 = block;
		cr.state++;
		
	}

	private static void secondpoint(Player player, Block block)
	{
		player.sendMessage(Alcatraz.pluginPrefix + ChatColor.YELLOW + Alcatraz.language.get(player, "arenaCreationHeader1", "----------Alcatraz Arena Creation----------"));
		player.sendMessage(ChatColor.GREEN + Alcatraz.language.get(player, "arenaCreationSelectPoint3", "Second point selected! Now go inside the prison, and click anywhere to set your current location as the starting point with the selection tool ({0}{1}{2})", ChatColor.YELLOW, Material.getMaterial(Settings.getGlobalInt(Setting.SelectionTool)) ,ChatColor.WHITE));
		player.sendMessage(ChatColor.YELLOW + "----------------------------------------");
		CreationPlayer cr = players.get(player.getName());
		cr.b2 = block;
		cr.state++;
		
	}
	
	
	private static void startPoint(Player player)
	{
		player.sendMessage(Alcatraz.pluginPrefix + ChatColor.GOLD + Alcatraz.language.get(player, "arenaCreationHeader1", "----------Alcatraz Arena Creation----------"));
		player.sendMessage(ChatColor.GREEN + Alcatraz.language.get(player, "arenaCreationSelectPoint4", "Start point selected! Now click anywhere to set your current location as the return location for when a player leaves the game with the selection tool ({0}{1}{2})", ChatColor.YELLOW, Material.getMaterial(Settings.getGlobalInt(Setting.SelectionTool)) ,ChatColor.WHITE));
		player.sendMessage(ChatColor.GOLD + "----------------------------------------");
		CreationPlayer cr = players.get(player.getName());
		cr.startPoint = player.getLocation();
		cr.state++;
		
	}
	
	private static void returnPoint(Player player)
	{
	
		CreationPlayer cr = players.get(player.getName());
		cr.returnPoint = player.getLocation();
		cr.state++;
		
		//Store it
		Prison prison = new Prison(cr.name, (int)Settings.getGlobalInt(Setting.DefaultMaxInmates), cr.b1.getX(), cr.b1.getY(), cr.b1.getZ(), cr.b2.getX(), cr.b2.getY(), cr.b2.getZ(), cr.startPoint, cr.returnPoint);
		Alcatraz.IO.newPrison(prison);
		Alcatraz.prisonController.addPrison(prison);
		
		player.sendMessage(Alcatraz.pluginPrefix + ChatColor.GREEN + Alcatraz.language.get(player, "arenaCreationFinished", "Prison [{0}{1}{2}] created successfully", ChatColor.RED, cr.name, ChatColor.GREEN));
		
		players.remove(player.getName());
		
	}

	
	private static class CreationPlayer
	{
		public int state;
		
		private String name;
		private Block b1;
		private Block b2;
		private Location startPoint;
		private Location returnPoint;
	
		
		public CreationPlayer()
		{
			state = 1;
		}
}

}
