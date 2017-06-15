package com.pilzbros.Alcatraz.Manager;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import com.pilzbros.Alcatraz.Alcatraz;
import com.pilzbros.Alcatraz.Objects.Inmate;
import com.pilzbros.Alcatraz.Objects.Prison;

public class ScoreboardManager 
{
	private Prison prison;
	private Inmate inmate;
	private BoardManager statsBoard;
	private BoardManager dummy;
	
	public ScoreboardManager(Prison p, Inmate i)
	{
		this.prison = p;
		this.inmate = i;
		this.statsBoard = new BoardManager("PrisonStats", ChatColor.RED + "-- " + WordUtils.capitalize(inmate.getPrison().getName()) + " --", DisplaySlot.SIDEBAR);
		this.dummy = new BoardManager("", "", DisplaySlot.SIDEBAR);
	}
	
	/**
	 * Called by auto-check to updateInDatabase the boards for the inmate automatically
	 */
	public void updateBoards()
	{
		this.updateStats();
		this.displayStats();
	}

	/**
	 * Updates the inmates scores on the scoreboard
	 */
	private void updateStats()
	{
		statsBoard.setObjectiveScore(ChatColor.GREEN + Alcatraz.language.get(inmate.getPlayer(), "scoreboardMoney", "Money"), (int)inmate.getMoney());
		statsBoard.setObjectiveScore(ChatColor.GOLD + Alcatraz.language.get(inmate.getPlayer(), "scoreboardTimeIn", "Time In"), (int)inmate.getMinutesIn());
		statsBoard.setObjectiveScore(ChatColor.RED + Alcatraz.language.get(inmate.getPlayer(), "scoreboardTimeLeft", "Time Left"), (int)inmate.getMinutesLeft());
		statsBoard.setObjectiveScore(ChatColor.DARK_RED + Alcatraz.language.get(inmate.getPlayer(), "scoreboardKills", "Kills"), (int)inmate.getKills());
		statsBoard.setObjectiveScore(ChatColor.DARK_PURPLE + "" + ChatColor.STRIKETHROUGH + Alcatraz.language.get(inmate.getPlayer(), "scoreboardStrikes", "Strikes"), (int)inmate.getStrikes());
		statsBoard.setObjectiveScore(ChatColor.YELLOW + Alcatraz.language.get(inmate.getPlayer(), "scoreboardOnline", "Online"), (int)prison.getInmateManager().numActiveInmates());

		if (inmate.getCell() != null)
		{
			statsBoard.setObjectiveScore(ChatColor.BLUE + Alcatraz.language.get(inmate.getPlayer(), "scoreboardCell", "Cell #"), Integer.parseInt(inmate.getCell().getCellNumber()));
		}
	}

	/**
	 * Displays the scoreboard to the inmate
	 */
	public void displayStats()
	{
		if (inmate.getPlayer().isOnline())
		{
			if (!inmate.getPlayer().getScoreboard().equals(this.statsBoard))
			{
				statsBoard.setScoreboard(inmate.getPlayer());
				//playerTicker.setScoreboard(inmate.getPlayer());
			}
		}
	}

	/**
	 * Removes the scoreboard from display of the inmate
	 */
	public void removeScoreboard()
	{
		dummy.setScoreboard(inmate.getPlayer());
	}

	
	

}
