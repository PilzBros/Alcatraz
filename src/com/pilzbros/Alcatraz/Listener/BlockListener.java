package com.pilzbros.Alcatraz.Listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;

import com.pilzbros.Alcatraz.Alcatraz;

public class BlockListener implements Listener
{
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent event)
	{
		if (Alcatraz.prisonController.isActivelyPlaying(event.getPlayer()))
		{
			if (Alcatraz.prisonController.getPlayerPrison(event.getPlayer()).getMiningManager().isBreakable(event.getBlock().getType()))
			{
				Alcatraz.prisonController.getPlayerPrison(event.getPlayer()).getMiningManager().addBroken(event.getBlock(), event.getBlock().getType());
			}
			else
			{
				event.getPlayer().sendMessage(Alcatraz.pluginPrefix + ChatColor.RED + Alcatraz.language.get(event.getPlayer(), "chatNoBlockBreak", "You cannot break this type of block in prison!"));
				event.setCancelled(true);
			}
		}
	}
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlace(SignChangeEvent event)
	{
		if (Alcatraz.prisonController.isActivelyPlaying(event.getPlayer()))
		{
			if (!event.getPlayer().hasPermission("Alcatraz.Admin"))
			{
				event.getPlayer().sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(event.getPlayer(), "chatBlockNoPlace", "You cannot place blocks in prison!"));
				event.setCancelled(true);
			}
		}
		else
		{
			if (event.getBlock().getType().equals(Material.WALL_SIGN) || event.getBlock().getType().equals(Material.SIGN_POST))
			{
	
				Sign s = (Sign)event.getBlock().getState();
				String[] lines = event.getLines();
				
				if (lines[0].equalsIgnoreCase("[Alcatraz]"))
				{
					if (event.getPlayer().hasPermission("Alcatraz.Admin"))
					{
						if (Alcatraz.prisonController.prisonExists(lines[1])) {
							event.setCancelled(true);

							if (lines[2].equalsIgnoreCase("Join")) {
								s.setLine(0, ChatColor.RED + Alcatraz.signPrefix);
								s.setLine(1, lines[1]);
								s.setLine(2, lines[2]);
								//s.setLine(3,"");
								s.update();
								Alcatraz.prisonController.getPrison(lines[1]).getJoinSignManager().createNewJoinSign(event.getBlock().getLocation());
							} else {

								s.setLine(0, ChatColor.RED + Alcatraz.signPrefix);
								s.setLine(1, lines[1]);
								s.setLine(2, lines[2]);
								//s.setLine(3,"");
								s.update();

								event.getPlayer().sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(event.getPlayer(), "chatSignAddSuccess", "Sign added successfully!"));
							}
						}
						else
						{
							event.setCancelled(true);
							event.getPlayer().sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(event.getPlayer(), "chatPrisonDNE", "That prison does not exist!"));
						}
					}
					else
					{
						event.setCancelled(true);
						event.getPlayer().sendMessage(Alcatraz.pluginPrefix + ChatColor.RED + Alcatraz.language.get(event.getPlayer(), "chatSignNoPerms", "You don't have permission to add Alcatraz signs"));
					}
				}
			}
		}
	}
}
