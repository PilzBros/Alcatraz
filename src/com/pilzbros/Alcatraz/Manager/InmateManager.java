package com.pilzbros.Alcatraz.Manager;

import java.util.*;

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
	 * Prepares and adds inmate to the prison game
	 */
	public void newInmate(Player p)
	{
		this.newInmate(p, false);
	}

	/**
	 * Prepares and adds inmate to the prison game
	 */
	public void newInmate(Player p, boolean forced){
		if (!this.isAnInmate(p))
		{
			//Create Inmate object
			Inmate inmate = new Inmate(p.getUniqueId().toString(), 0, Settings.getGlobalInt(Setting.DefaultTime), 0, 0, Settings.getGlobalInt(Setting.DefaultMoney), prison, forced);

			//Perform vault deposit
			inmate.moneyVaultToInmate();

			//Store the inmate in the database
			Alcatraz.IO.newInmate(inmate);

			//Add to inmate manager hash maps
			this.addInmate(inmate);
			this.addActiveInmate(inmate);

			//Modify player game mode
			p.setGameMode(GameMode.SURVIVAL);
			p.setHealth(p.getMaxHealth());
			p.setFoodLevel(20);
			p.setExhaustion((float) 0.0);

			//Assign Cell
			if (!prison.getCellManager().assignCell(inmate))
			{
				p.sendMessage(Alcatraz.pluginPrefix + ChatColor.RED + Alcatraz.language.get(p, "cellAssignError", "There was an error while assigning you a cell"));
			}

			//Display Inmate Scoreboard
			inmate.getScoreboardManager().displayStats();

			//Clear Inmate Inventory
			if (Settings.getGlobalBoolean(Setting.ClearInventory))
			{
				inmate.getPlayer().getInventory().clear();
			}

			//Teleport the player into the prison
			teleportInmateIn(inmate);

			//Send welcome message
			Alcatraz.titleManagerAPI.sendMessage(p, Alcatraz.language.get(inmate.getPlayer(), "titleWelcomeNoCell1", "Welcome to {0}{1}", ChatColor.RED, WordUtils.capitalize(inmate.getPrison().getName())), "");
			p.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(inmate.getPlayer(), "chatWelcome", "{0}WARDEN: {1} Welcome, inmate, to {2}{3}{4}. We've setup your prison account with the basics, like money. Take a look around and find your cell!", ChatColor.GREEN, ChatColor.WHITE, ChatColor.RED, inmate.getPrison().getName(), ChatColor.WHITE));
		}
	}
	
	/**
	 * Adds provided inmate to hash map
	 */
	public void addInmate(Inmate i)
	{
		this.inmates.put(i.getUUID(), i);
	}

	/** Removes provided inmate from the hash map
	 * @param i
	 */
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

	/**
	 * Returns number of inmates not currently online, but still enrolled in the prison
	 * @return
	 */
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
	public boolean isAnInmate(Player player)
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
	public boolean isAnInmate(OfflinePlayer player)
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
	 * Updates scoreboards for all active inmates
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
	public boolean isRoomAvailable()
	{
		return prison.getCellManager().isAvailableCell();
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
	 * Release inmate from game
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

		//Return money to vault
		inmate.moneyInmateToVault();
		
		//Remove from Hashmaps
		this.makeInmateInActive(inmate);
		this.removeInmate(inmate);
		
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
	 * Release all inmates from prison
	 */
	public void releaseAllInmates()
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

	/**
	 * Make inmate inactive on logoff
	 */
	public void inmateLogoff(Player p)
	{
		if (inmateExists(p))
		{
			getInmate(p).getCell().updateSigns();
			makeInmateInActive(getInmate(p));
			getInmate(p).updateInDatabase();
		}
	}
	
	/**
	 * Make inmate active on log on
	 */
	public void inmateLogon(Player p)
	{
		if (inmateExists(p))
		{
			getInmate(p).getCell().updateSigns();
			makeInmateActive(getInmate(p));

			getInmate(p).updateInDatabase();

			//Send welcome back message
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
	 * Returns boolean if the passed inmate is active
	 */
	private boolean isInmateActive(Inmate i)
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
	 * Updates all inmates in database
	 */
	public void updateInmates()
	{
		ArrayList<Inmate> inmatesToUpdate = new ArrayList<>();
		inmatesToUpdate.addAll(inmates.values());

		for (Inmate inmate: inmatesToUpdate)
		{
		    inmate.updateInDatabase();
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

}


