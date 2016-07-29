package com.pilzbros.Alcatraz.Runnable;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.pilzbros.Alcatraz.Alcatraz;

public class PrisonSpawn extends BukkitRunnable
{
	private Player player;
	private Location location;
	
	public PrisonSpawn(Player p, Location l)
	{
		this.player = p;
		this.location = l;
	}
	
	@Override
	public void run()
	{
		if (player.getLocation().equals(location))
		{
			Alcatraz.prisonController.getPlayerPrison(player).getInmateManager().teleportInmateIn(player);
			player.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(player, "chatPrisonSpawnSuccess", "You've been sent back to the prison spawn!"));
		}
		else
		{
			player.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(player, "chatPrisonSpawnCancelled", "Prison spawn {0}cancelled{1}! You cannot move while waiting for prison spawn", ChatColor.RED, ChatColor.WHITE));
		}
	}
	
	
	
	

}
