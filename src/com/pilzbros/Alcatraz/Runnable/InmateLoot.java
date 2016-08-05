package com.pilzbros.Alcatraz.Runnable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitRunnable;

import com.pilzbros.Alcatraz.Alcatraz;
import com.pilzbros.Alcatraz.Objects.Inmate;

public class InmateLoot implements Runnable
{
	private Inmate looter;
	private Inmate looted;
	private String action;
	
	public InmateLoot(Inmate l, Inmate l2, String a)
	{
		this.looter = l;
		this.looted = l2;
		this.action = a;
	}
	

	@Override
	public void run() 
	{
		if (this.action.equalsIgnoreCase("open"))
		{
			looter.getPlayer().openInventory(looted.getPlayer().getInventory());
			
			//schedule
			Bukkit.getScheduler().runTaskLater(Alcatraz.instance, new InmateLoot(looter, looted, "close"), 100);
		}
		else if (this.action.equalsIgnoreCase("close"))
		{
			looter.getPlayer().closeInventory();
			looter.getPlayer().sendMessage(Alcatraz.pluginPrefix  + Alcatraz.language.get(looter.getPlayer(), "chatLooterSuccess", "Looting of {0}{1}{2} {3}successful{4}!", ChatColor.AQUA,  looted.getPlayer().getName(), ChatColor.WHITE, ChatColor.GREEN, ChatColor.WHITE));
		
			looted.getPlayer().sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(looter.getPlayer(), "chatLootedNotify", "You've been looted by {0}{1}{2}!", ChatColor.RED,  looter.getPlayer().getName(), ChatColor.WHITE));
		}
	}

}
