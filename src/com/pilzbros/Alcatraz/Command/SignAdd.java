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
import com.pilzbros.Alcatraz.Objects.CellSign;
import com.pilzbros.Alcatraz.Objects.Prison;
import com.pilzbros.Alcatraz.Objects.PrisonCell;



public class SignAdd 
{
	private int state;
	public static HashMap<String,CreationPlayer> players = new HashMap<String,CreationPlayer>();
	
	public SignAdd()
	{
		state = 0;
	}
	
	public static void selectstart(Player player, Prison p, int cellNumber, String type)
	{
		if (players.containsKey(player.getName()))
		{
			players.remove(player.getName());
		}
		
		player.sendMessage(Alcatraz.pluginPrefix + ChatColor.AQUA + "---------- "+Alcatraz.language.get(player, "chatCellSignHeader", "Alcatraz Add Cell")+" ----------");
		player.sendMessage(ChatColor.GREEN + Alcatraz.language.get(player, "chatCellSignAdd", "Select the sign for cell #{0}{1}{2} with the selection tool ({3}{4}{5})", ChatColor.RED, cellNumber, ChatColor.WHITE, ChatColor.YELLOW, Material.getMaterial(Settings.getGlobalInt(Setting.SelectionTool)) ,ChatColor.WHITE));
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
				sign(player,block);
				break;
		}
	}
	
	private static void sign(Player player, Block block)
	{
		CreationPlayer cr = players.get(player.getName());
		
		//Verify that it is a block
		if (block.getType().equals(Material.SIGN) || block.getType().equals(Material.SIGN_POST) || block.getType().equals(Material.WALL_SIGN))
		{
		
			cr.sign = block.getLocation();
			cr.state++;
			
			if (cr.type.equalsIgnoreCase("cell"))
			{
				PrisonCell cell = Alcatraz.prisonController.getPrison(cr.prison.getName()).getCellManager().getCell(Integer.toString(cr.cellNumber));
				CellSign sign = new CellSign(Alcatraz.prisonController.getPrison(cr.prison.getName()), cell, block.getLocation());
				cell.addSign(sign);
				Alcatraz.IO.newCellSign(sign);
				player.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(player, "chatCellSignAddSuccess", "Cell #{0}{1}{2} sign added {3}successfully{4}!", ChatColor.GREEN, cell.getCellNumber(), ChatColor.WHITE, ChatColor.GREEN, ChatColor.WHITE)) ;
			}
			else
			{
				//Unknown sign type?
			}
			
			//Remove player
			players.remove(player.getName());
		}
		else
		{
			//Notify of error
			player.sendMessage(Alcatraz.pluginPrefix + ChatColor.RED + Alcatraz.language.get(player, "chatSignAddWrongType", "Wrong block type {0}{1}{2} selected! You must select a sign with the selection tool. Please try your selection again", ChatColor.AQUA, block.getType().toString(), ChatColor.RED));
		}
		
		
		
	}


	
	private static class CreationPlayer
	{
		public int state;
		
		private Prison prison;
		private String type;
		private int cellNumber;
		private Location sign;
		
		public CreationPlayer()
		{
			state = 1;
		}
}

}
