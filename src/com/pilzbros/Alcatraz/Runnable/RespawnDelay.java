package com.pilzbros.Alcatraz.Runnable;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.Door;
import org.bukkit.scheduler.BukkitRunnable;

import com.pilzbros.Alcatraz.Alcatraz;
import com.pilzbros.Alcatraz.Objects.Inmate;

public class RespawnDelay extends BukkitRunnable 
{
	private Inmate inmate;
	
	public RespawnDelay(Inmate i)
	{
		this.inmate = i;
	}
	
	
	@Override
	public void run() 
	{
		try 
		{
			Alcatraz.prisonController.getPlayerPrison(inmate.getPlayer()).getInmateManager().teleportInmateIn(inmate);
		} 
		catch (Exception e) 
		{
			Alcatraz.log.log(Level.WARNING, Alcatraz.consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(), "consoleRespawnError", "Error while attempting to teleport {0} after death", inmate.getPlayer().getName()));
		}
	}

}
