package com.pilzbros.Alcatraz.Runnable;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import com.pilzbros.Alcatraz.Alcatraz;

public class LockPick extends BukkitRunnable 
{
	private Player player;
	private Block block;
	private Block relative;
	
	public LockPick(Block b, Block r, Player p)
	{
		this.player = p;
		this.block = b;
		this.relative = r;
	}
	
	@Override
	public void run() 
	{
		Random rand = new Random();
		int randomNum = rand.nextInt(1 - 0 + 1) + 0;
		
		if (randomNum == 1)
		{
			Bukkit.getScheduler().runTaskLater(Alcatraz.instance, new DoorAction(block, relative, true, player), 1);
			player.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(player, "chatLockPickSuccess", "Lock pick {0}{1}successful{2}!", ChatColor.GREEN, ChatColor.BOLD, ChatColor.WHITE));
        	player.sendMessage(Alcatraz.pluginPrefix + ChatColor.BLUE + Alcatraz.language.get(player, "chatLockPickClose", "The door will close in 3 seconds"));
		}
		else
		{
			player.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(player, "chatLockPickFail", "Lock pick {0}{1}failed{2}!", ChatColor.RED, ChatColor.BOLD, ChatColor.WHITE));
		}
		
		//Remove Lockpick
		Alcatraz.inventoryActions.remove((Inventory)player.getPlayer().getInventory(), Material.PRISMARINE_SHARD, 1, (short) -1);
	}

}
