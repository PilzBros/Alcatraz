package com.pilzbros.Alcatraz.Command;

import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.pilzbros.Alcatraz.Alcatraz;
import com.pilzbros.Alcatraz.IO.Setting;
import com.pilzbros.Alcatraz.IO.Settings;
import com.pilzbros.Alcatraz.Objects.Inmate;
import com.pilzbros.Alcatraz.Objects.Prison;
import com.pilzbros.Alcatraz.Runnable.PrisonSpawn;
import com.pilzbros.Alcatraz.Runnable.RespawnDelay;

public class UserCommand implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (sender.hasPermission("Alcatraz.user"))
		{
			if (args.length < 1)
			{
				if (Alcatraz.prisonController.isActivelyPlaying((Player)sender))
				{
					sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatCurrentlyPlayingDefault", "You're currently playing Alcatraz! To quit, execute {0}/alc quit", ChatColor.YELLOW));
				}
				else {
					sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatToPlay", "To play Alcatraz, execute {0} /alc play", ChatColor.YELLOW));
				}
			}
			else if (args.length >= 1)
			{
				if (args[0].equalsIgnoreCase("spawn"))
				{
					if (Alcatraz.prisonController.isActivelyPlaying((Player)sender))
					{
						sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatPrisonSpawn", "You'll be teleported to the prison spawn in 3 seconds...don't move!"));
						
						Player player = (Player)sender;
						Bukkit.getScheduler().runTaskLater(Alcatraz.instance, new PrisonSpawn(player, player.getLocation()), 60);
						
					}
					else
					{
						//Cannot spawn, they're not playing alcatraz
						sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatNotPlaying", "You're not currently playing Alcatraz!"));
						
					}
				}
				else if (args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("help"))
				{
					sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatAlcatrazCommands", "--- Alcatraz Commands ---"));
					sender.sendMessage(Alcatraz.language.get(sender, "chatAlcatrazCommandsPlay", "{0} /alcatraz play [arena] {1} - Play Alcatraz", ChatColor.BLUE, ChatColor.WHITE));
					sender.sendMessage(Alcatraz.language.get(sender, "chatAlcatrazCommandsQuit", "{0} /alcatraz quit {1} - Quit Alcatraz", ChatColor.BLUE, ChatColor.WHITE));
					sender.sendMessage(Alcatraz.language.get(sender, "chatAlcatrazCommandsSpawn", "{0} /alcatraz spawn {1} - Teleport to prison spawn", ChatColor.BLUE, ChatColor.WHITE));
					sender.sendMessage(Alcatraz.language.get(sender, "chatAlcatrazCommandsCost", "{0} /alcatraz cost {1} - Cost of items to purchase", ChatColor.BLUE, ChatColor.WHITE));
					sender.sendMessage(Alcatraz.language.get(sender, "chatAlcatrazCommandsBuy", "{0} /alcatraz buy {1} - Buy items", ChatColor.BLUE, ChatColor.WHITE));
					sender.sendMessage(Alcatraz.language.get(sender, "chatAlcatrazCommandsDeposit", "{0} /alcatraz deposit {1} - Cost of items to purchase", ChatColor.BLUE, ChatColor.WHITE));
				}
				else if (args[0].equalsIgnoreCase("play"))
				{
					if (args.length == 1)
					{
						sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatAlcatrazCommandsPlay2", "{0} /alcatraz play [prisonName]", ChatColor.GOLD));
						sender.sendMessage(Alcatraz.language.get(sender, "chatAlcatrazCommandsNameNeeded", "{0} You must provide the name of the prison you wish to play. Alcatraz prisons are listed below:", Alcatraz.pluginPrefix));
						
						int count = 1;
						for(Entry<String, Prison> a : Alcatraz.prisonController.prisons.entrySet()) 
						{
							sender.sendMessage(count + ". " + a.getValue().getName());
							count++;
						}
					}
					else if (args.length == 2)
					{
						if (Alcatraz.prisonController.prisonExists(args[1]))
						{
							if (Alcatraz.prisonController.getPrison(args[1]).getInmateManager().roomAvailable())
							{
								if (!Alcatraz.prisonController.isActivelyPlaying((Player)sender))
								{
									Alcatraz.prisonController.getPrison(args[1]).getInmateManager().newInmate((Player)sender);
								}
								else
								{
									//Already playing
									sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatAlcatrazAlreadyPlaying", "You're already playing Alcatraz! To quit: {0} /alcatraz quit", ChatColor.BLUE));
								}
							}
							else
							{
								//Prison is full
								sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatAlcatrazPrisonFull", "Prison {0}{1}{2} is currently full", ChatColor.RED, args[1], ChatColor.WHITE));
							}
						}
						else
						{
							sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatAlcatrazPrisonDNE", "Prison {0}{1}{2} does not exist", ChatColor.RED, args[1], ChatColor.WHITE));
						}
					}
				}
				else if (args[0].equalsIgnoreCase("cost"))
				{
					if (Alcatraz.prisonController.isPlaying((Player)sender))
					{
						Inmate inmate = Alcatraz.prisonController.getPlayerPrison((Player)sender).getInmateManager().getInmate((Player)sender);
						
						sender.sendMessage(Alcatraz.pluginPrefix + ChatColor.GREEN + "-- Alcatraz Cost Sheet --");
						sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatCostMinute", "Minute : ${0}", inmate.getMinuteCost()));
						sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatCostStrike", "Strike : ${0}", inmate.getStrikeCost()));
					}
					else
					{
						sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatNotPlaying", "You're not currently playing Alcatraz!"));
					}
				}
				else if (args[0].equalsIgnoreCase("buy"))
				{
					if (args.length == 3)
					{
						if (Alcatraz.prisonController.isPlaying((Player)sender))
						{
							if (args[1].equalsIgnoreCase("minute"))
							{
								int amount = Integer.parseInt(args[2]);
								
								Inmate inmate = Alcatraz.prisonController.getPlayerPrison((Player)sender).getInmateManager().getInmate((Player)sender);
								double cost = inmate.getMinuteCost() * amount;
								
								if (inmate.getMoney() >= cost)
								{
									inmate.setMoney(inmate.getMoney() - cost);
									inmate.setMinutesLeft(inmate.getMinutesLeft() + amount);
									
									sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatPurchaseMinutesSuccessful","Purchase of additional {0}{1}{2} minute(s) for ${3}{4}{5} total - {6}successful{7}!", ChatColor.YELLOW, amount, ChatColor.WHITE, ChatColor.RED, cost, ChatColor.WHITE, ChatColor.GREEN, ChatColor.WHITE));
								}
								else
								{
									sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatPurchaseBalanceError", "Insufficient funds! Balance ${0}{1}{2} / Needed ${3}{4}{5}", ChatColor.RED, inmate.getMoney(), ChatColor.WHITE, ChatColor.BLUE, cost, ChatColor.WHITE));
								}
							}
							else if (args[1].equalsIgnoreCase("strike"))
							{
								int amount = Integer.parseInt(args[2]);
								
								Inmate inmate = Alcatraz.prisonController.getPlayerPrison((Player)sender).getInmateManager().getInmate((Player)sender);
								double cost = inmate.getStrikeCost() * amount;
								
								if (inmate.getStrikes() > 0)
								{
									if (inmate.getMoney() >= cost)
									{
										inmate.setMoney(inmate.getMoney() - cost);
										inmate.setStrikes(inmate.getStrikes() - amount);
										
										sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatPurchaseStrikesSuccessful","Purchase of {0}{1}{2} strike removal(s) for ${3}{4}{5} total - {6}successful{7}!", ChatColor.YELLOW, amount, ChatColor.WHITE, ChatColor.RED, cost, ChatColor.WHITE, ChatColor.GREEN, ChatColor.WHITE));
									}
									else
									{
										sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatPurchaseBalanceError", "Insufficient funds! Balance ${0}{1}{2} / Needed ${3}{4}{5}", ChatColor.RED, inmate.getMoney(), ChatColor.WHITE, ChatColor.BLUE, cost, ChatColor.WHITE));
									}
								}
								else
								{
									sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatPurchaseStrikesNone", "You have no strikes to purchase a strike removal for!"));
								}
							}
							else
							{
								sender.sendMessage(Alcatraz.pluginPrefix + ChatColor.RED + Alcatraz.language.get(sender, "chatPurchaseStrikesNone", "Unknown item ") + ChatColor.YELLOW + args[1]);
							}
						}
						else
						{
							sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatNotPlaying", "You're not currently playing Alcatraz!"));
						}
					}
					else if (args.length == 2)
					{
						if (Alcatraz.prisonController.isPlaying((Player)sender))
						{
							if (args[1].equalsIgnoreCase("list"))
							{
								sender.sendMessage(Alcatraz.pluginPrefix + ChatColor.YELLOW + Alcatraz.language.get(sender, "chatPurchaseListHeader", "--- Prison Purchase List ---"));
								sender.sendMessage(Alcatraz.language.get(sender, "chatPurchaseMinute", "{0}Minute{1} - Adds a minute to your remaining time", ChatColor.GREEN, ChatColor.WHITE));
								sender.sendMessage(Alcatraz.language.get(sender, "chatPurchaseStrike", "{0}Strike{1} - Removes a strikes from your strike total", ChatColor.GREEN, ChatColor.WHITE));
							}
							else
							{
								//Unknown command
								sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatUnknownCommand", "Unknown command!"));
							}
						}
					}
					else
					{
						sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatBuyUsageCommand", "Buy Usage: {0} /alcatraz buy [item] [amount]", ChatColor.GOLD));
					}
				}
				else if (args[0].equalsIgnoreCase("quit"))
				{
					if (Alcatraz.prisonController.isPlaying((Player)sender))
					{
						Alcatraz.prisonController.getPlayerPrison((Player)sender).getInmateManager().releaseInmate((Player)sender);
						sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatThanks", "Thanks for playing Alcatraz"));
					}
					else
					{
						//Not currently playing Alcatraz
						sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatNotPlaying", "You're not currently playing Alcatraz!"));
					}
				}
				else
				{
					sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatUnknownCommand", "Unknown command!"));
				}
			}
			else
			{
				sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatUnknownCommand", "Unknown command!"));
			}
			
		}
		else
		{
			sender.sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(sender, "chatUnauthorized", "{0} You don't have permission to access Alcatraz!", ChatColor.RED));
		}
		return true;
	}

}
