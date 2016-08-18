package com.pilzbros.Alcatraz.Listener;

import io.puharesource.mc.titlemanager.TitleManager;

import java.util.Random;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.material.Door;
import org.bukkit.block.Sign;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.fusesource.jansi.Ansi.Color;

import com.pilzbros.Alcatraz.Alcatraz;
import com.pilzbros.Alcatraz.Command.ChestAdd;
import com.pilzbros.Alcatraz.Command.PrisonCreation;
import com.pilzbros.Alcatraz.Command.SignAdd;
import com.pilzbros.Alcatraz.IO.Setting;
import com.pilzbros.Alcatraz.IO.Settings;
import com.pilzbros.Alcatraz.Manager.InmateManager;
import com.pilzbros.Alcatraz.Objects.Inmate;
import com.pilzbros.Alcatraz.Objects.Prison;
import com.pilzbros.Alcatraz.Runnable.DoorAction;
import com.pilzbros.Alcatraz.Runnable.LockPick;
import com.pilzbros.Alcatraz.Runnable.RespawnDelay;

public class PlayerListener implements Listener 
{
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		if (Alcatraz.prisonController.isActivelyPlaying(event.getPlayer()))
		{
			Alcatraz.prisonController.getPlayerPrison(event.getPlayer()).getInmateManager().inmateLogoff(event.getPlayer());
		}
	
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event)
    {
		if (Alcatraz.prisonController.isPlaying(event.getPlayer()))
		{
			Alcatraz.prisonController.getPlayerPrison(event.getPlayer()).getInmateManager().inmateLogon(event.getPlayer());
		}

    	if (Alcatraz.updateChecker.isUpdateNeeded() && event.getPlayer().hasPermission("Alcatraz.Admin"))
        {
            event.getPlayer().sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(event.getPlayer(), "chatLoginUpdateNeeded", "{0}Update! {1}A newer version of Alcatraz is available for update! Please update by visiting http://alcatraz.austinpilz.com", ChatColor.GREEN, ChatColor.WHITE));
        }

    }
	
	@EventHandler
	public void onKill(PlayerDeathEvent event)
	{
		if(event.getEntity() instanceof Player){}
		else
			return;
		
		if(event.getEntity().getKiller() instanceof Player)
		{
			Player killed = (Player)event.getEntity();
			
			if (Alcatraz.prisonController.isActivelyPlaying(killed))
			{
				event.setKeepInventory(true);
				event.setKeepLevel(true);
				

				Player killer = (Player)event.getEntity().getKiller();
				
				//Inventory
				Inventory ki = killed.getInventory();
				//killed.getInventory().clear(); //clear killed inventory
				killer.openInventory(ki); //let killer have dibs at spoils
				
				Inmate inmateKiller = Alcatraz.prisonController.getPlayerPrison(killer).getInmateManager().getInmate(killer);
				Inmate inmateKilled = Alcatraz.prisonController.getPlayerPrison(killed).getInmateManager().getInmate(killed);
				Alcatraz.prisonController.getPlayerPrison(killed).getInmateManager().inmateDeath(inmateKilled, inmateKiller);
			}
			else
			{
				Inmate inmateKilled = Alcatraz.prisonController.getPlayerPrison(killed).getInmateManager().getInmate(killed);
				Alcatraz.prisonController.getPlayerPrison(killed).getInmateManager().teleportInmateIn(inmateKilled);
			}
		}
		else
		{
			return;
		}
	}
		

	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{	
		if (Alcatraz.prisonController.isActivelyPlaying(event.getPlayer()))
		{
			//Delay respawn teleport by a few seconds
			Bukkit.getScheduler().runTaskLater(Alcatraz.instance, new RespawnDelay(Alcatraz.prisonController.getPlayerPrison(event.getPlayer()).getInmateManager().getInmate(event.getPlayer())), 5);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDamage(EntityDamageEvent e) 
	{
		if(e instanceof EntityDamageByEntityEvent)
		{
			EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
            
			if (event.getEntity() instanceof Player)
			{
				Player damaged = (Player)event.getEntity();
				
				if (Alcatraz.prisonController.isActivelyPlaying(damaged))
				{
					if(event.getDamager() instanceof Player)
		            {
		            	Inmate damager = Alcatraz.prisonController.getPlayerPrison((Player) event.getDamager()).getInmateManager().getInmate((Player) event.getDamager());
		            	
		            	if (!damager.getPlayer().getItemInHand().getType().equals(Material.BLAZE_ROD))
		            	{
		            		if (damager.getPlayer().getItemInHand().getType().equals(Material.AIR))
			            	{
			            		//Cancel
			            		event.setCancelled(true);
			            		
			            		//Direct contact
			            		if (damager.getDirectContactAttempts() < 2)
			            		{
			            			damager.getPlayer().sendMessage(Alcatraz.pluginPrefix + ChatColor.RED + Alcatraz.language.get(damager.getPlayer(), "chatNoDirectContact", "No direct contact, inmate!"));
			            			damager.addDirectContactAttempts(1);
			            		}
			            		else if (damager.getDirectContactAttempts() == 2)
			            		{
			            			//This is the third strike
			            			damager.getPlayer().sendMessage(Alcatraz.pluginPrefix + ChatColor.RED + Alcatraz.language.get(damager.getPlayer(), "chatDirectContactStrike", "Inmate, that's your third direct conatct warning. You've been awarded a strike!"));
			            			damager.addStrike(1);
			            			damager.resetDirectContactAttempts();
			            		}
			            	}
		            		else
		            		{
		            			//Hit with some un-approved object
		            			damager.getPlayer().sendMessage(Alcatraz.pluginPrefix + ChatColor.RED + Alcatraz.language.get(damager.getPlayer(), "chatCannotHit", "You cannot hit other players with this object!"));
			            		event.setCancelled(true);
		            		}
		            		
		            	}
		            }
				}
			}
		}
	}
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
	{
		if (Alcatraz.prisonController.isActivelyPlaying(event.getPlayer()))
		{
			if (!event.getPlayer().hasPermission("Alcatraz.Admin") || !event.getPlayer().hasPermission("Alcatraz.*"))
			{
				if (!event.getMessage().toLowerCase().startsWith("/alcatraz") && !event.getMessage().toLowerCase().startsWith("/alc") && !event.getMessage().toLowerCase().startsWith("/alcatrazadmin") && !event.getMessage().toLowerCase().startsWith("/alca"))
				{
					event.setCancelled(true);
					event.getPlayer().sendMessage(Alcatraz.pluginPrefix + ChatColor.RED + Alcatraz.language.get(event.getPlayer(), "chatCommandProhibited","You can only use in game commands while playing Alcatraz!"));
				}
			} //
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerTeleport(PlayerTeleportEvent event)
	{
		//See if player teleports outside of Alcatraz
		if (Alcatraz.prisonController.isActivelyPlaying(event.getPlayer()))
		{
			if (!Alcatraz.prisonController.getPlayerPrison(event.getPlayer()).getInmateManager().isWithinPrison(event.getPlayer(), event.getTo()))
			{
				event.setCancelled(true);
				event.getPlayer().sendMessage(Alcatraz.pluginPrefix + ChatColor.RED + Alcatraz.language.get(event.getPlayer(), "chatNoTeleport", "You cannot teleport outside of prison while playing!"));
			}
		}
		else
		{
			//Player is not playing Alcatraz
			event.setCancelled(false);
		}
	}
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event) 
	{
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getPlayer().getItemInHand().getTypeId() == Settings.getGlobalInt(Setting.SelectionTool))
		{
			if (PrisonCreation.players.containsKey(event.getPlayer().getName()))
			{
				PrisonCreation.select(event.getPlayer(), event.getClickedBlock());
				event.setCancelled(true);
			}
			else if (ChestAdd.players.containsKey(event.getPlayer().getName()))
			{
				ChestAdd.select(event.getPlayer(), event.getClickedBlock());
				event.setCancelled(true);
			}
			else if (SignAdd.players.containsKey(event.getPlayer().getName()))
			{
				SignAdd.select(event.getPlayer(), event.getClickedBlock());
				event.setCancelled(true);
			}
		}
		else if (Alcatraz.prisonController.isActivelyPlaying(event.getPlayer()))
		{
			try
			{
				if (!event.getClickedBlock().getType().equals(Material.CHEST))
				{
					if (event.getClickedBlock().getType().equals(Material.IRON_DOOR_BLOCK))
					{
						if (event.getPlayer().getInventory().contains(Material.PRISMARINE_SHARD))
						{
							Random rand = new Random();
							int randomNum = rand.nextInt(1 - 0 + 1) + 0;
							
						
							boolean opened = false;
								
							BlockState state = event.getClickedBlock().getState();
							Door door = (Door) state.getData();
			                    
							if (door.isTopHalf())
							{
			                    	Block set = event.getClickedBlock().getRelative(BlockFace.DOWN);
			                    	BlockState state2 = set.getState();
			                    	Door setDoor = (Door) state2.getData();
			                        if (setDoor.isOpen() == false) 
			                        {
			                            opened = true;
			                        }	
							} 
							else 
			               	{
			                    	if (door.isOpen() == false) 
			                    	{
			                    		opened = true;
			                    	}
			               	}
			                    
							if (opened == true)
							{
								Bukkit.getScheduler().runTaskLater(Alcatraz.instance, new LockPick(event.getClickedBlock(), event.getClickedBlock().getRelative(BlockFace.DOWN), event.getPlayer()), 40);
								event.getPlayer().sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(event.getPlayer(), "chatLockPickAttempt", "Attempting to pick the lock..."));
							}
							
						}
						else
						{
							event.getPlayer().sendMessage(Alcatraz.pluginPrefix + ChatColor.RED + Alcatraz.language.get(event.getPlayer(), "chatDoorLocked", "Locked! A lockpick is required to open this door"));
						}
					}
					else if (event.getClickedBlock().getType().equals(Material.SIGN) || event.getClickedBlock().getType().equals(Material.WALL_SIGN) || event.getClickedBlock().getType().equals(Material.SIGN_POST))
					{
						Sign s = (Sign)event.getClickedBlock().getState();
						String[] lines = s.getLines();
						
						if (lines[0].equalsIgnoreCase(ChatColor.RED + Alcatraz.signPrefix))
						{
							if (Alcatraz.prisonController.prisonExists(lines[1]))
							{
								Prison p = Alcatraz.prisonController.getPrison(lines[1]);
								
								if (lines[2].equalsIgnoreCase("raid"))
								{
									if (System.currentTimeMillis() - p.lastRaid >= 30000)
									{
										p.getInmateManager().raid();
										Alcatraz.prisonController.getPlayerPrison(event.getPlayer()).getInmateManager().teleportInmateIn(event.getPlayer());
									}
									else
									{
										event.getPlayer().sendMessage(Alcatraz.pluginPrefix + ChatColor.RED + Alcatraz.language.get(event.getPlayer(), "chatRaidCooldown", "Not enough time has passed since the last raid!"));
									}
								}
								else if (lines[2].equalsIgnoreCase("loot"))
								{
									p.getInmateManager().loot(p.getInmateManager().getInmate(event.getPlayer()));
				
								}
								else if (lines[3].equalsIgnoreCase("Click to join!"))
								{
									event.getPlayer().sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(event.getPlayer(), "chatAlcatrazAlreadyPlaying", "You're already playing Alcatraz! To quit: {0} /alcatraz quit", ChatColor.BLUE));
								}
								else
								{
									event.getPlayer().sendMessage(Alcatraz.pluginPrefix + ChatColor.RED + Alcatraz.language.get(event.getPlayer(), "chatUnknownSignCommand", "Unknown sign command!"));
								}
							}
							else
							{
								event.getPlayer().sendMessage(Alcatraz.pluginPrefix + ChatColor.RED + Alcatraz.language.get(event.getPlayer(), "chatPrisonDNE", "That prison does not exist!"));
							}
						}
	
					}
					else if (Alcatraz.prisonController.getPlayerPrison(event.getPlayer()).getInmateManager().getInmate(event.getPlayer()).getKills() < Settings.getGlobalInt(Setting.InteractKills))
					{
						event.setCancelled(true);
						event.getPlayer().sendMessage(Alcatraz.pluginPrefix + ChatColor.RED + Alcatraz.language.get(event.getPlayer(), "chatNoInteractInfo", "You are not allowed to interact with items until you've reached {0} kills", Settings.getGlobalInt(Setting.InteractKills)));
					}
				}
			}
			catch (Exception e)
			{
				//Ignore 
			}
		}
		else
		{
			//Not playing
			try {
				if (event.getClickedBlock().getType().equals(Material.SIGN) || event.getClickedBlock().getType().equals(Material.WALL_SIGN) || event.getClickedBlock().getType().equals(Material.SIGN_POST)) {
					Sign s = (Sign) event.getClickedBlock().getState();
					String[] lines = s.getLines();

					if (lines[0].equalsIgnoreCase(ChatColor.RED + Alcatraz.signPrefix)) {
						if (Alcatraz.prisonController.prisonExists(lines[1])) {
							Prison p = Alcatraz.prisonController.getPrison(lines[1]);

							if (lines[3].equalsIgnoreCase("Click to join!")) {
								p.getInmateManager().newInmate(event.getPlayer());
							}
						}
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
