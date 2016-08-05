package com.pilzbros.Alcatraz.Command;

import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pilzbros.Alcatraz.Alcatraz;
import com.pilzbros.Alcatraz.Objects.Prison;
import com.pilzbros.Alcatraz.Objects.PrisonCell;


public class AdminCommand implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (sender.hasPermission("Alcatraz.Admin") || sender.hasPermission("Alcatraz.*"))
		{
			if (args.length < 1)
			{
				sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.pluginName + " v" + ChatColor.GREEN + Alcatraz.pluginVersion);
			}
			else
			{
				if (args[0].equalsIgnoreCase("reload"))
				{
					Alcatraz.IO.LoadSettings();
					sender.sendMessage(Alcatraz.pluginPrefix + ChatColor.GREEN + Alcatraz.language.get(sender, "chatSettingsReloaded", "Settings reloaded!"));
					
				}
				else if (args[0].equalsIgnoreCase("here"))
				{
					if (PrisonCreation.players.containsKey(sender.getName()))
					{
						PrisonCreation.select((Player)sender, Bukkit.getPlayer(sender.getName()).getLocation().getBlock());
					}
					else
					{
						sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatSetupHereNotStarted", "You are not currently setting up a prison"));
					}
				}
				else if (args[0].equalsIgnoreCase("update"))
				{
					if (Alcatraz.updateChecker.isUpdateNeeded())
					{
						sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatUpdateNeeded", "You are running Alcatraz v{0} and the latest version is v{1}. Visit the Spigot page to update!", Alcatraz.pluginVersion, Alcatraz.updateChecker.getLatestVersion()));
					}
					else
					{
						sender.sendMessage(Alcatraz.pluginPrefix + ChatColor.GREEN + Alcatraz.language.get(sender, "chatNoUpdateNeeded", "Alcatraz v{0} is the latest version!", Alcatraz.pluginVersion));
					}

					}
				else if (args[0].equalsIgnoreCase("setup"))
				{
					if (args.length == 2)
					{
						if (!Alcatraz.prisonController.prisonExists(args[1]))
						{
							PrisonCreation.selectstart((Player) sender, args[1]);
						}
						else
						{
							sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatPrisonAlreadyExist", "Prison [{0}{1}{2}] already exists!", ChatColor.RED, args[1], ChatColor.WHITE));
						}
					}
					else
					{
						sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatPrisonAdminSetupCommand", "To setup prison:") + ChatColor.GOLD + "/alca setup [ArenaName]");
					}
				}
				else if (args[0].equalsIgnoreCase("delete"))
				{
					if (args.length == 2)
					{
						if (Alcatraz.prisonController.prisonExists(args[1]))
						{
							Prison p = Alcatraz.prisonController.getPrison(args[1]);
							p.delete();
							
							//Send success message
							sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatPrisonDeletedSuccess", "Prison [{0}{1}{2}] removed {3}successfuly{4}!", ChatColor.RED, args[1], ChatColor.WHITE, ChatColor.GREEN, ChatColor.WHITE));
						}
						else
						{
							//The prison does not exist
							sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatAdminPrisonDNE", "Prison [{0}{1}{2}] does not exist!", ChatColor.RED, args[1], ChatColor.WHITE));
						}
					}
					else
					{
						//Send setup command
						sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatPrisonAdminDeleteCommand", "To delete prison:") + ChatColor.GOLD + "/alca delete [ArenaName]");
					}
				}
				else if (args[0].equalsIgnoreCase("kick"))
				{
					if (args.length == 3)
					{
						if (Alcatraz.prisonController.prisonExists(args[1]))
						{
							//Make sure they've played on this server
							if (Bukkit.getOfflinePlayer(args[2]).hasPlayedBefore())
							{
								//See if they're in a game of Alcatraz
								if (Alcatraz.prisonController.isPlaying(Bukkit.getOfflinePlayer(args[2])))
								{
									Alcatraz.prisonController.getPlayerPrison(Bukkit.getOfflinePlayer(args[2])).getInmateManager().releaseInmate(Bukkit.getOfflinePlayer(args[2]));
									sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatAdminPlayerKickedSuccess", "Player [{0}{1}{2}] has been kicked from {3} successfuly{4}!", ChatColor.RED, args[2], ChatColor.WHITE, ChatColor.GREEN, ChatColor.WHITE));
								}
								else
								{
									//Not playing Alcatraz
									sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatAdminPlayerNotPlaying", "Player [{0}{1}{2}] is not currently playing Alcatraz!", ChatColor.RED, args[2], ChatColor.WHITE));
								}
							}
							else
							{
								//Has not played on the server before
								sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatAdminPlayerDNE", "Player [{0}{1}{2}] is not playing nor have they ever played on this server!", ChatColor.RED, args[2], ChatColor.WHITE));
							}
						}
						else
						{
							//Prison does not exist
							sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatAdminPrisonDNE", "Prison [{0}{1}{2}] does not exist!", ChatColor.RED, args[1], ChatColor.WHITE));
						}
					}
					else
					{
						sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatKickSyntaxError", "{0}Syntax error!{1} To kick player: ", ChatColor.RED, ChatColor.WHITE) + ChatColor.GOLD + "/alca kick [prisonName] [playerName]");
					}
				}
				else if (args[0].equalsIgnoreCase("set"))
				{
					if (args.length >= 4)
					{
						if (Bukkit.getOfflinePlayer(args[1]).isOnline())
						{
							Player player = Bukkit.getPlayer(args[1]);
							if (Alcatraz.prisonController.isPlaying(player))
							{
								int amount = Integer.valueOf(args[3]);
								if (args[2].equalsIgnoreCase("kills"))
								{
									Alcatraz.prisonController.getPlayerPrison(player).getInmateManager().getInmate(player).setKills(amount);
									sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatAdminPlayerUpdateKills", "Updated {0}{1}{2}'s {3}{4}{5} to {6}{7}{8} successfully{9}!", ChatColor.RED, player.getName(), ChatColor.WHITE, ChatColor.AQUA, "kills", ChatColor.WHITE, ChatColor.GREEN, amount, ChatColor.GREEN, ChatColor.WHITE));
								}
								else if (args[2].equalsIgnoreCase("timein"))
								{
									Alcatraz.prisonController.getPlayerPrison(player).getInmateManager().getInmate(player).setMinutesIn(amount);
									sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatAdminPlayerUpdateTI", "Updated {0}{1}{2}'s {3}{4}{5} to {6}{7}{8} minutes successfully{9}!", ChatColor.RED, player.getName(), ChatColor.WHITE, ChatColor.AQUA, "time in", ChatColor.WHITE, ChatColor.GREEN, amount, ChatColor.GREEN, ChatColor.WHITE));
								}
								else if (args[2].equalsIgnoreCase("timeleft"))
								{
									Alcatraz.prisonController.getPlayerPrison(player).getInmateManager().getInmate(player).setMinutesLeft(amount);
									sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatAdminPlayerUpdateTL", "Updated {0}{1}{2}'s {3}{4}{5} to {6}{7}{8} minutes successfully{9}!", ChatColor.RED, player.getName(), ChatColor.WHITE, ChatColor.AQUA, "time left", ChatColor.WHITE, ChatColor.GREEN, amount, ChatColor.GREEN, ChatColor.WHITE));
								}
								else if (args[2].equalsIgnoreCase("strikes"))
								{
									Alcatraz.prisonController.getPlayerPrison(player).getInmateManager().getInmate(player).setStrikes(amount);
									sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatAdminPlayerUpdateStrikes", "Updated {0}{1}{2}'s {3}{4}{5} to {6}{7}{8} successfully{9}!", ChatColor.RED, player.getName(), ChatColor.WHITE, ChatColor.AQUA, "strikes", ChatColor.WHITE, ChatColor.GREEN, amount, ChatColor.GREEN, ChatColor.WHITE));
								}
								else if (args[2].equalsIgnoreCase("money"))
								{
									Alcatraz.prisonController.getPlayerPrison(player).getInmateManager().getInmate(player).setMoney(amount);
									sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatAdminPlayerUpdateMoney", "Updated {0}{1}{2}'s {3}{4}{5} to ${6}{7}{8} successfully{9}!", ChatColor.RED, player.getName(), ChatColor.WHITE, ChatColor.AQUA, "money", ChatColor.WHITE, ChatColor.GREEN, amount, ChatColor.GREEN, ChatColor.WHITE));
								}
								else
								{
									//Unknown setting to update
									sender.sendMessage(Alcatraz.pluginPrefix + ChatColor.RED + Alcatraz.language.get(sender, "chatAdminUpdateSettingUnknown", "Unknown setting to update!"));
								}
							}
							else
							{
								//The player isn't playing Alcatraz
								sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatAdminPlayerNotPlaying", "Player [{0}{1}{2}] is not currently playing Alcatraz!", ChatColor.RED, args[1], ChatColor.WHITE));
							}
						}
						else
						{
							//The player isn't online
							sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatAdminPlayerNotOnlineForUpdate", "Player [{0}{1}{2}] is not currently online!", ChatColor.RED, args[1], ChatColor.WHITE));
						}
					}
					else
					{
						//Incorrect syntax
						sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatAdminSetPlayerSyntaxError", "{0}Syntax error!", ChatColor.RED) + ChatColor.GOLD + "/alca set [playerName] [setting] [amount]");
					}
				}
				else if (args[0].equalsIgnoreCase("add"))
				{
					if (args.length >= 4)
					{
						if (Alcatraz.prisonController.prisonExists(args[1]))
						{
							if (args[2].equalsIgnoreCase("chest"))
							{
								if (args[3].equalsIgnoreCase("food") || args[3].equalsIgnoreCase("random") || args[3].equalsIgnoreCase("reward"))
								{
									ChestAdd.selectstart((Player) sender, Alcatraz.prisonController.getPrison(args[1]), 0, args[3]);
								}
								else if (args[3].equalsIgnoreCase("cell"))
								{
									if (args.length >= 5)
									{
										ChestAdd.selectstart((Player) sender, Alcatraz.prisonController.getPrison(args[1]), Integer.parseInt(args[4]), args[2]);
									}
									else
									{
										sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatAdminAddChestMissingCell", "{0}Error! {1} A cell number must be provided to add a cell chest", ChatColor.RED, ChatColor.WHITE));
									}
								}
								else
								{
									sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatAdminAddChestUnknownType", "{0}Error! {1} Unknown chest type. Valid chest types: ", ChatColor.RED, ChatColor.WHITE) + ChatColor.GOLD + "food/random/reward");
								}
							}
							else if (args[2].equalsIgnoreCase("cell"))
							{
								if (Alcatraz.prisonController.getPrison(args[1]).getCellManager().cellExists(args[3]))
								{
									//The cell already exists
									sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatAdminAddCellAE", "Cell #{0}{1}{2} already exists in {3}{4}", ChatColor.AQUA, args[3], ChatColor.WHITE, ChatColor.RED, args[1]));
								}
								else
								{
									//Add the new cell
									PrisonCell newCell = new PrisonCell(Alcatraz.prisonController.getPrison(args[1]), args[3]);
									Alcatraz.prisonController.getPrison(args[1]).getCellManager().addCell(newCell);
									Alcatraz.IO.newCell(newCell.getPrison(), newCell.getCellNumber());
									sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatAdminAddCellSuccess", "Cell #{0}{1}{2} added to {3}{4}{5} successfully{6}!", ChatColor.AQUA, args[3], ChatColor.WHITE, ChatColor.RED, args[1], ChatColor.GREEN, ChatColor.WHITE));
								}
							}
							else if (args[2].equalsIgnoreCase("sign"))
							{
								if (args.length >= 5)
								{
									if (Alcatraz.prisonController.getPrison(args[1]).getCellManager().cellExists(Integer.parseInt(args[4])))
									{
										SignAdd.selectstart((Player) sender, Alcatraz.prisonController.getPrison(args[1]), Integer.parseInt(args[4]), args[3]);
									}
									else
									{
										sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatAdminPrisonCellNumDNE", "Prison [{0}{1}{2}] cell #{3}{4}{5} does not exist!", ChatColor.RED, args[1], ChatColor.WHITE, ChatColor.GREEN, args[4], ChatColor.WHITE));
									}
								}
								else
								{
									//
									sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatAdminPrisonAddSignSyntax", "{0}Syntax error! {1} /alca add [prisonName] sign [cell#]", ChatColor.RED, ChatColor.WHITE));
								}
							}
							else
							{
								sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatAdminPrisonAddObjectUnknown", "{0}Syntax error! {1} Unknown item", ChatColor.RED, ChatColor.WHITE));
							}
						}
						else
						{
							sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatAdminPrisonDNE", "Prison [{0}{1}{2}] does not exist!", ChatColor.RED, args[1], ChatColor.WHITE));
						}
					}
					else
					{
						sender.sendMessage(Alcatraz.pluginPrefix + "Usage: /alca add [Jail] [Item] [Type] [Optional: Cell #]");
					}
				}
				else if (args[0].equalsIgnoreCase("prisons"))
				{
					if (Alcatraz.prisonController.getNumberPrisons() > 0)
					{
						sender.sendMessage(Alcatraz.pluginPrefix + "---- Alcatraz Prisons ----");
						int count = 1;
						for(Entry<String, Prison> a : Alcatraz.prisonController.prisons.entrySet()) 
						{
							sender.sendMessage(count + ". " + a.getValue().getName());
							count++;
						}
					}
					else
					{
						sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatAdminNoPrisonsList", "There are prisons to display!"));
					}	
				}
				else if (args[0].equalsIgnoreCase("prison"))
				{
					if (args.length > 2)
					{
						if (Alcatraz.prisonController.prisonExists(args[1]))
						{
							if (args.length == 3)
							{
									if (args[2].equalsIgnoreCase("cells"))
									{
									
										Prison p = Alcatraz.prisonController.getPrison(args[1]);
												
										sender.sendMessage(Alcatraz.pluginPrefix + "---- Alcatraz Prison "+p.getName()+" Cells----");
										int count = 1;
										for(Entry<String, PrisonCell> c : p.getCellManager().getCells().entrySet()) 
										{
											if (c.getValue().isOccupied())
											{
												//Has a inmate
												if (Bukkit.getOfflinePlayer(c.getValue().getInmate().getPlayer().getUniqueId()).isOnline())
												{
													//Online
													sender.sendMessage("#" + c.getValue().getCellNumber() + " - " + c.getValue().getInmate().getPlayer().getName() + ChatColor.GREEN + " Online");
												}
												{
													//Offline
													sender.sendMessage("#" + c.getValue().getCellNumber() + " - " + Bukkit.getOfflinePlayer(c.getValue().getInmate().getPlayer().getUniqueId()).getName());
												}
											}
											else
											{
												//Vacant
												sender.sendMessage(count + ". #" + c.getValue().getCellNumber() + " - " + Alcatraz.language.get(sender, "chatPrisonCellListEmpty", "Empty"));
											}
		
											count++;
										}
									}
									else
									{
										//Unknown prison information
										sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatAdminPrisonOptionSyntaxError", "{0}Unknown prison option! {1}Available Options: ", ChatColor.RED, ChatColor.GOLD) +  "cells");
									}
								}
								else
								{
									//Unknown prison command
									sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatAdminPrisonOptionSyntaxError", "{0}Syntax error! {1}", ChatColor.RED, ChatColor.GOLD) +  "/alca prison [prisonName] [option]");
								}
						}
						else
						{
							//Incorrect prison command syntax
							sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatAdminPrisonDNE", "Prison [{0}{1}{2}] does not exist!", ChatColor.RED, args[1], ChatColor.WHITE));
						}
					}
					else
					{
						//Prison does not exist
						sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatAdminPrisonOptionSyntaxError", "{0}Syntax error! {1}", ChatColor.RED, ChatColor.GOLD) +  "/alca prison [prisonName] [option]");
					}
				}
				else
				{
					//Unknown command
					sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatUnknownCommand", "Unknown command!!"));
				}
			}
		}
		else
		{
			sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatUnauthorizedAdmin", "{0} You don't have permission to access Alcatraz administration!", ChatColor.RED));
		}
		
		return true;
	}

}
