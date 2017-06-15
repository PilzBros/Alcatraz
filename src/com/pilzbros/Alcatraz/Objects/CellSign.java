package com.pilzbros.Alcatraz.Objects;

import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.libs.jline.internal.Log;

import com.pilzbros.Alcatraz.Alcatraz;


public class CellSign
{
	private Prison prison;
	private PrisonCell cell;
	private Location sign;
	
	public CellSign (Prison p, PrisonCell c, Location l)
	{
		this.prison = p;
		this.cell = c;
		this.sign = l;
		this.update();
	}
	
	/**
	 * Updates cell sign with latest information
	 */
	public void update()
	{
		try
		{
			Sign sign = this.getSign();
			sign.setLine(0, ChatColor.RED + prison.getName());
			sign.setLine(1, "Cell #" + ChatColor.GREEN + this.getCell().getCellNumber());

			if (cell.isOccupied())
			{
				OfflinePlayer inmate = Bukkit.getServer().getOfflinePlayer(UUID.fromString(cell.getInmate().getUUID()));

				sign.setLine(2, ChatColor.BLUE + inmate.getName()); //Player Name

				//Online status
				if (inmate.isOnline())
				{
					sign.setLine(3, ChatColor.GREEN + "" + ChatColor.BOLD + "Online");
				}
				else
				{
					sign.setLine(3, ChatColor.DARK_RED + "Offline");
				}
			}
			else
			{
				sign.setLine(2, ChatColor.ITALIC + "" + ChatColor.WHITE + "Vacant"); //Empty message
				sign.setLine(3, "");
			}

			sign.update();
		}
		catch (Exception e)
		{
			Log.info(Alcatraz.consolePrefix + " Broken cell sign in " + prison.getName() + " cell #" + cell.getCellNumber());
			Alcatraz.IO.removeSign(this.getSign().getLocation());
		}
	}
	
	/**
	 * Returns sign object
	 * @return
	 */
	public Sign getSign()
	{
		try
		{
			return (Sign)sign.getBlock().getState();
		}
		catch(Exception e)
		{
			Alcatraz.IO.removeSign(sign.getBlock().getLocation()); //Sign is broken, remove from DB
			return null;
		}
	}

	/**
	 * Returns prison cell object
	 * @return
	 */
	public PrisonCell getCell()
	{
		return this.cell;
	}

	/**
	 * Returns prison object
	 * @return
	 */
	public Prison getPrison()
	{
		return this.prison;
	}
}
