package com.pilzbros.Alcatraz.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.pilzbros.Alcatraz.Objects.Prison;

public class PrisonController 
{
	public HashMap<String, Prison> prisons;
	
	
	/**
	 * Default constructor
	 */
	public PrisonController()
	{
		this.init();
	}
	
	/**
	 * Creates the controller's hash maps
	 */
	private void init()
	{
		prisons = new HashMap<String, Prison>();
		
	}

	/**
	 * Returns prisons
	 * @return
	 */
	public ArrayList<Prison> getPrisons()
	{
		return new ArrayList<>(this.prisons.values());
	}
	
	
	/**
	 * Adds supplied prison to the prisons hash map
	 */
	public void addPrison(Prison prison)
	{
		this.prisons.put(prison.getName(), prison);
	}
	
	/**
	 * Removes supplied prison from the prisons hash map
	 */
	public void removePrison(Prison prison)
	{
		this.prisons.remove(prison.getName());
	}

	/**
	 * Returns of the prison name exists
	 * @param name
	 * @return
	 */
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

	/**
	 * Returns the number of prisons
	 * @return
	 */
	public int getNumberPrisons()
	{
		return prisons.size();
	}

	/**
	 * Returns prison object by prison name
	 * @param name
	 * @return
	 */
	public Prison getPrison(String name)
	{
		return prisons.get(name.toLowerCase());
	}

	/**
	 * Returns prison object of supplied player
	 * @param player
	 * @return
	 */
	public Prison getPlayerPrison(Player player)
	{
		Prison playerPrison = null;
		Iterator it = prisons.entrySet().iterator();
		while (it.hasNext()) 
		{
		    Map.Entry entry = (Map.Entry) it.next();
		    Prison prison = (Prison)entry.getValue();
		    if (prison.getInmateManager().isAnInmate(player))
		    {
		    	playerPrison = prison;
		    }
		}
		
		return playerPrison;
	}

	/**
	 * Returns if there are any players currently playing across all prisons
	 * @return
	 */
	public boolean playersPlaying()
	{
		boolean players = false;
		
		Iterator it = prisons.entrySet().iterator();
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
		    if (prison.getInmateManager().isAnInmate(player))
		    {
		    	playerPrison = prison;
		    }
		}
		
		return playerPrison;
	}

	/**
	 * Returns if the supplied player is currently playing
	 * @param player
	 * @return
	 */
	public boolean isPlaying(Player player)
	{
		return isPlaying((OfflinePlayer)player);
	}

	/**
	 * Returns if the supplied player is currently playing
	 * @param player
	 * @return
	 */
	public boolean isPlaying(OfflinePlayer player)
	{
		boolean playing = false;
		Iterator it = prisons.entrySet().iterator();
		while (it.hasNext()) 
		{
		    Map.Entry entry = (Map.Entry) it.next();
		    Prison prison = (Prison)entry.getValue();
		    if (prison.getInmateManager().isAnInmate(player))
		    {
		    	playing = true;
		    }
		}
		
		return playing;
	}

	/**
	 * Returns if the supplied player is actively playing
	 * @param player
	 * @return
	 */
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

	/**
	 * Performs shutdown actions across all prisons
	 */
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

	/**
	 * Simulates player login for all inmates
	 */
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
