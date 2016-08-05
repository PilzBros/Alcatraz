package com.pilzbros.Alcatraz.Controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.pilzbros.Alcatraz.Alcatraz;
import com.pilzbros.Alcatraz.Objects.Prison;

public class PrisonController 
{
	public HashMap<String, Prison> prisons;
	
	
	/*
	 * Default constructor
	 */
	public PrisonController()
	{
		this.init();
	}
	
	/*
	 * Creates the controller's hashmaps
	 */
	private void init()
	{
		prisons = new HashMap<String, Prison>();
		
	}
	
	public HashMap<String, Prison> getPrisons()
	{
		return this.prisons;
	}
	
	
	/*
	 * Adds supplied prison to the prisons hashmap
	 */
	public void addPrison(Prison prison)
	{
		this.prisons.put(prison.getName(), prison);
	}
	
	/*
	 * Removes supplied prison from the prisons hashmap
	 */
	public void removePrison(Prison prison)
	{
		this.prisons.remove(prison.getName());
	}
	
	public boolean prisonExists(String name)
	{
		if (this.prisons.containsKey(name.toLowerCase()))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public int getNumberPrisons()
	{
		return prisons.size();
	}

	public Prison getPrison(String name)
	{
		return prisons.get(name.toLowerCase());
	}
	
	public Prison getPlayerPrison(Player player)
	{
		Prison playerPrison = null;
		Iterator it = prisons.entrySet().iterator();
		while (it.hasNext()) 
		{
		    Map.Entry entry = (Map.Entry) it.next();
		    Prison prison = (Prison)entry.getValue();
		    if (prison.getInmateManager().isPlaying(player))
		    {
		    	playerPrison = prison;
		    }
		}
		
		return playerPrison;
	}
	
	public boolean playersPlaying()
	{
		boolean players = false;
		
		Iterator it = Alcatraz.prisonController.getPrisons().entrySet().iterator();
		while (it.hasNext()) 
		{
		    Map.Entry entry = (Map.Entry) it.next();
		    Prison prison = (Prison)entry.getValue();
		    if (prison.getInmateManager().numActiveInmates() > 0)
		    {
		    	players = true;
		    }
		}
		
		return players;
	}
	
	/**
	 * Returns prison for player's current Prison
	 * @param player OfflinePlayer
	 * @return Player Alcatraz prison
	 */
	public Prison getPlayerPrison(OfflinePlayer player)
	{
		Prison playerPrison = null;
		Iterator it = prisons.entrySet().iterator();
		while (it.hasNext()) 
		{
		    Map.Entry entry = (Map.Entry) it.next();
		    Prison prison = (Prison)entry.getValue();
		    if (prison.getInmateManager().isPlaying(player))
		    {
		    	playerPrison = prison;
		    }
		}
		
		return playerPrison;
	}
	
	
	public boolean isPlaying(Player player)
	{
		boolean playing = false;
		Iterator it = prisons.entrySet().iterator();
		while (it.hasNext()) 
		{
		    Map.Entry entry = (Map.Entry) it.next();
		    Prison prison = (Prison)entry.getValue();
		    if (prison.getInmateManager().isPlaying(player))
		    {
		    	playing = true;
		    }
		}
		
		return playing;
	}
	
	public boolean isPlaying(OfflinePlayer player)
	{
		boolean playing = false;
		Iterator it = prisons.entrySet().iterator();
		while (it.hasNext()) 
		{
		    Map.Entry entry = (Map.Entry) it.next();
		    Prison prison = (Prison)entry.getValue();
		    if (prison.getInmateManager().isPlaying(player))
		    {
		    	playing = true;
		    }
		}
		
		return playing;
	}

	public boolean isActivelyPlaying(Player player)
	{
		boolean playing = false;
		Iterator it = prisons.entrySet().iterator();
		while (it.hasNext()) 
		{
		    Map.Entry entry = (Map.Entry) it.next();
		    Prison prison = (Prison)entry.getValue();
		    if (prison.getInmateManager().isActivelyPlaying(player))
		    {
		    	playing = true;
		    }
		}
		
		return playing;
	}
	public void autoCheck()
	{
		Iterator it = prisons.entrySet().iterator();
		while (it.hasNext()) 
		{
		    Map.Entry entry = (Map.Entry) it.next();
		    Prison prison = (Prison)entry.getValue();
		    prison.autoCheck();
		}
	}
	
	public void shutdownActions()
	{
		Iterator it = prisons.entrySet().iterator();
		while (it.hasNext()) 
		{
		    Map.Entry entry = (Map.Entry) it.next();
		    Prison prison = (Prison)entry.getValue();
		    prison.shutdownActions();
		}
	}
	
	public void checkPlayerReload()
	{
		for(Player p : Bukkit.getServer().getOnlinePlayers()) 
		{
			if (this.isPlaying(p))
			{
				this.getPlayerPrison(p).getInmateManager().inmateLogon(p);
			}
		}
	}

}
