package com.pilzbros.Alcatraz.Manager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.pilzbros.Alcatraz.Alcatraz;
import com.pilzbros.Alcatraz.IO.Setting;
import com.pilzbros.Alcatraz.IO.Settings;
import com.pilzbros.Alcatraz.Objects.Inmate;
import com.pilzbros.Alcatraz.Objects.Prison;
import com.pilzbros.Alcatraz.Objects.PrisonCell;
import com.pilzbros.Alcatraz.Runnable.InmateLoot;

public class InmateManager 
{
	private Prison prison;
	private HashMap<String, Inmate> inmates;
	private HashMap<String, Inmate> activeInmates;
	
	public InmateManager(Prison p)
	{
		this.prison = p;
		this.inmates = new HashMap<String, Inmate>();
		this.activeInmates = new HashMap<String, Inmate>();
	}
	
	/*
	 * Adds provided inmate to hashmap, does not prepare them
	 */
	public void addInmate(Inmate i)
	{
		this.inmates.put(i.getUUID(), i);
	}
	
	public void removeInmate(Inmate i)
	{
		this.inmates.remove(i.getUUID());
	}
	
	/**
	 * Returns number of currently active and online inmates
	 * @return
	 */
	public int numActiveInmates()
	{
		return activeInmates.size();
	}

	public int numInactiveInmates() { return inmates.size() - activeInmates.size(); }
	/**
	 * Returns total number of inmates, active + inactive
	 * @return
	 */
	public int numTotalInmates()
	{
		return inmates.size();
	}

	/**
	 * Returns boolean if the supplied player is currently playing Alcatraz in this prison
	 * @param player
	 * @return
	 */
	public boolean isPlaying(Player player)
	{
		if (this.inmates.containsKey(player.getUniqueId().toString()))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Returns boolean if the supplied offline player is playing Alcatraz, although offline
	 * @param player
	 * @return
	 */
	public boolean isPlaying(OfflinePlayer player)
	{
		if (this.inmates.containsKey(player.getUniqueId().toString()))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Returns boolean if the player is an active inmate
	 * @param player
	 * @return
	 */
	public boolean isActivelyPlaying(Player player)
	{
		if (this.activeInmates.containsKey(player.getUniqueId().toString()))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 *	Inmate auto check
	 */
	public synchronized void autoCheck()
	{
		this.checkInmates();
	}
	
	/**
	 * Updates scoreboards for all inmates
	 */
	public void updateInmateScoreboards()
	{
		Iterator it = activeInmates.entrySet().iterator();
		while (it.hasNext()) 
		{
		    Map.Entry entry = (Map.Entry) it.next();
		    Inmate inmate = (Inmate)entry.getValue();
		    inmate.getScoreboardManager().updateBoards();
		}
	}

	/**
	 * Returns boolean if there is room available for more inmates
	 */
	public boolean roomAvailable()
	{
		if (inmates.size() < prison.getCellManager().getNumCells())
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Prepares and adds inmate to the prison game
	 */
	public void newInmate(Player p) {
		if (!this.isPlaying(p)) {
			//Create Inmate
			Inmate inmate = new Inmate(p.getUniqueId().toString(), 0, Settings.getGlobalInt(Setting.DefaultTime), 0, 0, Settings.getGlobalInt(Setting.DefaultMoney), prison);

			//Add to hashmaps
			this.addInmate(inmate);
			this.addActiveInmate(inmate);

			//Gamemode
			p.setGameMode(GameMode.SURVIVAL);
			p.setHealth(p.getMaxHealth());
			p.setFoodLevel(20);
			p.setExhaustion((float) 0.0);

			//Assign Cell
			if (!prison.getCellManager().assignCell(inmate)) {
				p.sendMessage(Alcatraz.pluginPrefix + ChatColor.RED + Alcatraz.language.get(p, "cellAssignError", "There was an error while assigning you a cell"));
			}

			//Scoreboards
			inmate.getScoreboardManager().displayStats();

			//Inventory
			if (Settings.getGlobalBoolean(Setting.ClearInventory)) {
				inmate.getPlayer().getInventory().clear();
			}

			//Teleport
			teleportInmateIn(inmate);

			//IO
			Alcatraz.IO.newInmate(inmate);

			//Update Inmate
			inmate.update();


			//Send welcome message
			Alcatraz.titleManagerAPI.sendMessage(p, Alcatraz.language.get(inmate.getPlayer(), "titleWelcomeNoCell1", "Welcome to {0}{1}", ChatColor.RED, WordUtils.capitalize(inmate.getPrison().getName())), "");
			p.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(inmate.getPlayer(), "chatWelcome", "{0}WARDEN: {1} Welcome, inmate, to {2}{3}{4}. We've setup your prison account with the basics, like money. Take a look around and find your cell!", ChatColor.GREEN, ChatColor.WHITE, ChatColor.RED, inmate.getPrison().getName(), ChatColor.WHITE));
		}
	}
	
	
	/**
	 * Teleports the inmate to the prison starting point
	 */
	public void teleportInmateIn(Inmate i)
	{
		i.getPlayer().teleport(prison.getStartPoint());
	}
	
	/**
	 * Teleports the inmate to the prison starting point
	 */
	public void teleportInmateIn(Player p)
	{
		p.teleport(prison.getStartPoint());
	}
	
	/**
	 * Teleports inmate to the prison return point
	 */
	private void teleportInmateOut(Inmate i)
	{
		i.getPlayer().teleport(prison.getReturnPoint());
	}
	
	/**
	 * Removes inmate from game
	 */
	public void releaseInmate(Player p)
	{
		this.releaseInmate(p.getUniqueId().toString());
	}
	
	/**
	 * Releases inmate from game
	 * @param p Offline player entity
	 */
	public void releaseInmate(OfflinePlayer p)
	{
		this.releaseInmate(p.getUniqueId().toString());
	}
	
	/**
	 * Releases player from Alcatraz
	 * @param playerUUID Players UUID
	 */
	private void releaseInmate(String playerUUID)
	{
		Inmate inmate = this.inmates.get(playerUUID);
		OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));
		
		//Remove From Cell
		if (inmate.hasCell())
		{
			inmate.getCell().removeInmate();
			inmate.setCell(null);
		}
		
		//Remove from Hashmaps
		this.makeInmateInActive(inmate);
		this.removeInmate(inmate);
		
		//Restore Money
		Alcatraz.econ.depositPlayer(Bukkit.getOfflinePlayer(UUID.fromString(playerUUID)), inmate.getMoney() - Settings.getGlobalInt(Setting.DefaultMoney));
		
		//Only if they're online
		if (player.isOnline())
		{
			//Teleport them out
			teleportInmateOut(inmate);
			
			//Remove Scoreboard
			inmate.getScoreboardManager().removeScoreboard();
		}
	
		//IO Remove
		Alcatraz.IO.deleteInmate(inmate);
	}
	
	/**
	 * Make inmate inactive on logoff
	 */
	public void inmateLogoff(Player p)
	{
		if (inmateExists(p))
		{
			makeInmateInActive(getInmate(p));
			getInmate(p).getCell().updateSigns();
		}
	}
	
	/**
	 * Make inmate active on log on
	 */
	public void inmateLogon(Player p)
	{
		if (inmateExists(p))
		{
			makeInmateActive(getInmate(p));
			getInmate(p).getCell().updateSigns();
			p.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(p, "chatWelcomeBack", "Welcome back to Alcatraz!"));
		}
	}
	
	/**
	 * Makes inmate inactive
	 */
	private void addActiveInmate(Inmate i)
	{
		this.activeInmates.put(i.getUUID(), i);
	}
	
	/**
	 * Makes inmate active
	 */
	private void makeInmateActive(Inmate i)
	{
		this.addActiveInmate(i);
	}
	
	/**
	 * Removes supplied inmate from active inmates hashmap
	 */
	private void makeInmateInActive(Inmate i)
	{
		this.activeInmates.remove(i.getUUID());
	}

	/**
	 * Returns inmate object for supplied player
	 */
	public Inmate getInmate(Player p)
	{
		return this.inmates.get(p.getUniqueId().toString());
	}
	
	/**
	 * Returns inmate object for supplied player by UUID
	 * @param uuid
	 * @return
	 */
	public Inmate getInmate(String uuid)
	{
		return this.inmates.get(uuid);
	}
	
	/**
	 * Returns boolean if inmate is active/inactive in Alcatraz
	 * @param uuid
	 * @return
	 */
	public boolean inmateExists(String uuid)
	{
		if (this.inmates.containsKey(uuid))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Returns boolean if inmate is active/inactive in Alcatraz
	 * @param p Player object
	 * @return boolean if active/inactive
	 */
	public boolean inmateExists(Player p)
	{
		if (this.inmates.containsKey(p.getUniqueId().toString()))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Returns boolean if the passed inmate is active
	 */
	private boolean inmateActive(Inmate i)
	{
		if (this.activeInmates.containsValue(i))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Performs minute alterations to inmate counters
	 */
	public void minutePass()
	{
		Iterator it = activeInmates.entrySet().iterator();
		while (it.hasNext()) 
		{
		    Map.Entry entry = (Map.Entry) it.next();
		    Inmate inmate = (Inmate)entry.getValue();
		    inmate.setMinutesIn(inmate.getMinutesIn() + 1);
		    inmate.setMinutesLeft(inmate.getMinutesLeft() - 1);
		}
	}
	
	/**
	 * Sends supplied message to all active (online) inmates
	 * @param message
	 */
	public void messageActiveInmates(String message)
	{
		Iterator it = activeInmates.entrySet().iterator();
		while (it.hasNext()) 
		{
		    Map.Entry entry = (Map.Entry) it.next();
		    Inmate inmate = (Inmate)entry.getValue();
		    inmate.getPlayer().sendMessage(Alcatraz.pluginPrefix + ChatColor.WHITE + message);
		}
	}
	
	/**
	 * Deposits all money from vault account into prison account for all inmates
	 */
	public synchronized void vaultDeposit()
	{
		Iterator it = activeInmates.entrySet().iterator();
		while (it.hasNext()) 
		{
		    Map.Entry entry = (Map.Entry) it.next();
		    Inmate inmate = (Inmate)entry.getValue();
		    
		    if (Alcatraz.econ.getBalance(inmate.getPlayer()) > 0)
		    {
		    	double balance = Alcatraz.econ.getBalance(inmate.getPlayer());
		    	Alcatraz.econ.withdrawPlayer(inmate.getPlayer(), balance);
		    	inmate.addMoney(balance);
		    }
		}
	}
	
	/**
	 * Updates all inmates in database
	 */
	public void updateInmates()
	{
		Iterator it = activeInmates.entrySet().iterator();
		while (it.hasNext()) 
		{
		    Map.Entry entry = (Map.Entry) it.next();
		    Inmate inmate = (Inmate)entry.getValue();
		    inmate.update();
		}
		
		Iterator it2 = inmates.entrySet().iterator();
		while (it.hasNext()) 
		{
		    Map.Entry entry = (Map.Entry) it2.next();
		    Inmate inmate = (Inmate)entry.getValue();
		    inmate.update();
		}
	}
	
	/**
	 * Kills inmate by another inmate
	 * @param killed
	 * @param killer
	 */
	public void inmateDeath(Inmate killed, Inmate killer)
	{
		//Killed
		teleportInmateIn(killed);
		killed.getPlayer().sendMessage(Alcatraz.pluginPrefix + ChatColor.RED + Alcatraz.language.get(killer.getPlayer(), "chatKilled", "You were killed by ") + ChatColor.BLUE + killer.getPlayer().getName());
		
		//Killer
		killer.addKill();
		killer.getPlayer().sendMessage(Alcatraz.pluginPrefix + ChatColor.GREEN + Alcatraz.language.get(killer.getPlayer(), "chatKiller", "You killed ") + ChatColor.BLUE + killed.getPlayer().getName());

		this.messageActiveInmates(killer.getPlayer().getName() + " murdered " + ChatColor.RED + killed.getPlayer().getName() + ChatColor.WHITE + ", marking their kill #" + ChatColor.RED + killer.getKills());
	}
	
	/**
	 * Performs raid on all inmates inventories
	 */
	public void raid()
	{
		prison.lastRaid = System.currentTimeMillis();
		this.messageActiveInmates(ChatColor.RED + Alcatraz.language.get(Bukkit.getConsoleSender(), "chatRaidCommence", "Attention all inmates, a raid is under way!"));
		
		Iterator it = activeInmates.entrySet().iterator();
		while (it.hasNext()) 
		{
		    Map.Entry entry = (Map.Entry) it.next();
		    Inmate inmate = (Inmate)entry.getValue();
		    
		    if (inmate.getPlayer().getInventory().contains(Material.BLAZE_ROD))
		    {
		    	inmate.getPlayer().sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(inmate.getPlayer(), "chatRaidCaught1", "{0}WARDEN: {1} You've been caught with a shank on your person during the raid. You've been written up and have received two strikes for your insolence", ChatColor.GREEN, ChatColor.WHITE));
		    	
		    	inmate.addStrike(2);
		    	inmate.getPlayer().getInventory().remove(Material.BLAZE_ROD);
		    }
		    else if (inmate.getPlayer().getInventory().contains(Material.PRISMARINE_SHARD))
		    {
		    	inmate.getPlayer().sendMessage(Alcatraz.pluginPrefix +Alcatraz.language.get(inmate.getPlayer(), "chatRaidCaught2", "{0}WARDEN: {1} You've been caught with a lock pick on your person during the raid. You've been written up and have received one strike for your insolence", ChatColor.GREEN, ChatColor.WHITE));
		    	
		    	inmate.addStrike(1);
		    	inmate.getPlayer().getInventory().remove(Material.PRISMARINE_SHARD);
		    }
		}
		
		this.messageActiveInmates(Alcatraz.pluginPrefix + ChatColor.BLUE + Alcatraz.language.get(Bukkit.getConsoleSender(), "chatRaidOver", "The raid is now over"));
		
		
	}

	/**
	 * Checks all inmates for things like time remaining and strikes
	 */
	public void checkInmates()
	{
		if (activeInmates.size() > 0) {
			Iterator it = activeInmates.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				Inmate inmate = (Inmate) entry.getValue();
				boolean removed = false;

				//Check Strikes
				if (inmate.getStrikes() >= Settings.getGlobalInt(Setting.MaxStrikes)) {
					inmate.getPlayer().sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(inmate.getPlayer(), "chatExecutedStrikes", "You've been executed for accumulating too many strikes! Thanks for playing Alcatraz in {0}{1}", ChatColor.RED, inmate.getPrison().getName()));
					Alcatraz.titleManagerAPI.sendMessage(inmate.getPlayer(), Alcatraz.language.get(inmate.getPlayer(), "titleExecutedStrikes", "You've been {0} executed {1}", ChatColor.RED, ChatColor.WHITE), Alcatraz.language.get(inmate.getPlayer(), "titleExecutedStrikes2", "for accumulating too many strikes!"));

					this.releaseInmate(inmate.getPlayer());
					removed = true;
					return;
				} else {
					if (inmate.getStrikes() == Settings.getGlobalInt(Setting.MaxStrikes) - 1 && inmate.strikeWarning()) {
						inmate.getPlayer().sendMessage(Alcatraz.language.get(inmate.getPlayer(), "chatTimeLeftWarning", "{0} WARNING! {1} You're one strike away from being executed. Purchase a strike removal, if you can, to keep playing: ", ChatColor.RED, ChatColor.WHITE) + ChatColor.BLUE + "/alc buy strike [#]");
						inmate.setStrikeWarning(false);
					}
				}

				if (removed == false) {
					if (inmate.getMinutesLeft() <= 0) {
						inmate.getPlayer().sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(inmate.getPlayer(), "chatExecutedTime", "You've been executed for running out of time! Thanks for playing Alcatraz in {0}{1}", ChatColor.RED, inmate.getPrison().getName()));
						Alcatraz.titleManagerAPI.sendMessage(inmate.getPlayer(), Alcatraz.language.get(inmate.getPlayer(), "titleExecutedTime", "You've been {0} executed {1}", ChatColor.RED, ChatColor.WHITE), Alcatraz.language.get(inmate.getPlayer(), "titleExecutedTime2", "for running out of time!"));

						this.releaseInmate(inmate.getPlayer());
						return;
					} else if (inmate.getMinutesLeft() <= 15 && inmate.timeWarning()) {
						inmate.getPlayer().sendMessage(Alcatraz.language.get(inmate.getPlayer(), "chatTimeLeftWarning", "{0} WARNING! {1} You're less than 15 minutes away from being executed. Purchase more time, if you can, to keep playing: ", ChatColor.RED, ChatColor.WHITE) + ChatColor.BLUE + "/alc buy minute [#]");
						inmate.setTimeWarning(false);
					}
				}
			}
		}
	}
	
	/**
	 * Performs loot for supplied inmate
	 * @param inmate
	 */
	public void loot(Inmate inmate)
	{
		if (System.currentTimeMillis() - inmate.getLastLoot() >= 30000)
		{
			Random rand = new Random();
			int randomNum = rand.nextInt(4 - 0 + 1) + 0;
			
			if (randomNum != 3)
			{
			
				if (this.activeInmates.size() >= 2)
				{
					int lootedPos = rand.nextInt((this.activeInmates.size() - 1) - 0 + 1) + 0;
					
					try
					{
						
						HashMap<String, Inmate> tmp = (HashMap<String, Inmate>)activeInmates.clone();
						tmp.remove(inmate.getPlayer().getUniqueId().toString());
						Object[] values = tmp.values().toArray();
						
						
						Inmate looted = (Inmate)values[rand.nextInt((values.length - 1) - 0 + 1) + 0];
						Player lootd = looted.getPlayer(); //just for try({} testing
						
						
						inmate.getPlayer().sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(inmate.getPlayer(), "chatLooting", "Looting ") + ChatColor.GREEN + looted.getPlayer().getName() + ChatColor.WHITE + ".....");
								
						Bukkit.getScheduler().runTaskLater(Alcatraz.instance, new InmateLoot(inmate, looted, "open"), 40);
	
						
					}
					catch (Exception e)
					{
						inmate.getPlayer().sendMessage(Alcatraz.pluginPrefix + ChatColor.RED + Alcatraz.language.get(inmate.getPlayer(), "chatLootError","There was an error while attempting to loot! Please try again later or notify an admin"));
					}
				}
				else
				{
					inmate.getPlayer().sendMessage(Alcatraz.pluginPrefix + ChatColor.RED + Alcatraz.language.get(inmate.getPlayer(), "chatLootOffline", "There's nobody else currently playing to loot!"));
				}
				
				inmate.setLastLoot(System.currentTimeMillis());
			}
			else
			{
				//Warden caught them attempting to loot
				inmate.getPlayer().sendMessage(Alcatraz.pluginPrefix + ChatColor.RED + Alcatraz.language.get(inmate.getPlayer(), "chatLootCaught", "You've been caught attempting to loot another player! You've been awarded a strike for your stupidity"));
				this.teleportInmateIn(inmate);
				inmate.addStrike(1);
			}
		}
		else
		{
			inmate.getPlayer().sendMessage(Alcatraz.pluginPrefix + ChatColor.RED + Alcatraz.language.get(inmate.getPlayer(), "chatLootTime", "Not enough time has passed since your last looting attempt!"));
			return;
		}
	}
	
	/**
	 * Checks if player is within the bounds of the prison
	 * @param player
	 * @param location
	 * @return
	 */
	public boolean isWithinPrison(Player player, Location location)
	{
		/*
		boolean x = true;
		boolean y = true;
		boolean z = true;
		
		Prison playerPrison = Alcatraz.prisonController.getPlayerPrison(player);
		
		//X
		if (playerPrison.getX1() > playerPrison.getX2())
		{
			if (location.getBlockX() > playerPrison.getX1() || location.getBlockX() < playerPrison.getX2())
			{
				x = false;
			}
		}
		else
		{
			if (location.getBlockX() < playerPrison.getX1() || location.getBlockX() > playerPrison.getX2())
			{
				x = false;
			}
		}
		
		//Y
		if (playerPrison.getY1() > playerPrison.getY2())
		{
			if (location.getBlockY() > playerPrison.getY1() || location.getBlockY() < playerPrison.getY2())
			{
				y = false;
			}
		}
		else
		{
			if (location.getBlockY() < playerPrison.getY1() || location.getBlockY() > playerPrison.getY2())
			{
				y = false;
			}
		}
		
		//Z
		if (playerPrison.getZ1() > playerPrison.getZ2())
		{
			if (location.getBlockZ() > playerPrison.getZ1() || location.getBlockZ() < playerPrison.getZ2())
			{
				z = false;
			}
		}
		else
		{
			if (location.getBlockZ() < playerPrison.getZ1() || location.getBlockZ() > playerPrison.getZ2())
			{
				z = false;
			}
		}
		
		
		//Final check
		if (x == true && y == true && z == true)
		{
			return true;
		}
		else
		{
			return false;
		}
		*/
		
		return true;
	}
	
	/**
	 * Checks the current location of each inmate to see if they have escaped
	 */
	public void checkLocation()
	{
		Iterator it = activeInmates.entrySet().iterator();
		while (it.hasNext()) 
		{
		    Map.Entry entry = (Map.Entry) it.next();
		    Inmate inmate = (Inmate)entry.getValue();
		    
		    if (!isWithinPrison(inmate.getPlayer(), inmate.getPlayer().getLocation()))
		    {
		    	teleportInmateIn(inmate);
		    	inmate.getPlayer().sendMessage(Alcatraz.pluginPrefix + ChatColor.RED + Alcatraz.language.get(inmate.getPlayer(), "escapeAttempt", "Nice escape attempt! You've been awarded a strike for your efforts"));
		    	inmate.addStrike(1);
		    }
		}
	}

	/**
	 * Release all inmates from prison (quit game)
	 */
	public void releaseInmates()
	{
		Iterator it = activeInmates.entrySet().iterator();
		while (it.hasNext()) 
		{
		    Map.Entry entry = (Map.Entry) it.next();
		    Inmate inmate = (Inmate)entry.getValue();
		    
		    it.remove(); //Avoid error
		    
		    this.releaseInmate(inmate.getPlayer());
		}
	}
}


