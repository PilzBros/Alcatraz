package com.pilzbros.Alcatraz.Objects;

import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.pilzbros.Alcatraz.Alcatraz;
import com.pilzbros.Alcatraz.IO.Setting;
import com.pilzbros.Alcatraz.IO.Settings;
import com.pilzbros.Alcatraz.Manager.ScoreboardManager;

public class Inmate 
{
	private String userID;
	
	
	private int minutesIn;
	private int minutesLeft;
	private int strikes;
	private int kills;
	private double money;
	private int directContactAttempts; //Not stored in DB
	private Prison prison;
	private PrisonCell cell;
	private ScoreboardManager boardManager;
	private boolean forced;

	//Warning booleans
	private boolean timeWarningGiven;
	private boolean strikeWarningGiven;
	private long lastLoot;

	public Inmate(String pUUID, int pMinutesIn, int pMinutesLeft, int pStrikes, int pKills, double pMoney, Prison p)
	{
		this(pUUID, pMinutesIn, pMinutesLeft, pStrikes, pKills, pMoney, p, false);
	}
	
	public Inmate(String pUUID, int pMinutesIn, int pMinutesLeft, int pStrikes, int pKills, double pMoney, Prison p, boolean forced)
	{
		this.userID = pUUID;
		this.minutesIn = pMinutesIn;
		this.minutesLeft = pMinutesLeft;
		this.strikes = pStrikes;
		this.kills = pKills;
		this.money = pMoney;
		this.prison = p;
		this.lastLoot = 0;
		this.cell = null;
		this.timeWarningGiven = true;
		this.strikeWarningGiven = true;
		this.directContactAttempts = 0;
		this.boardManager = new ScoreboardManager(p, this);
		this.forced = forced;
	}

	/**
	 * Get UUID of the inmate
	 * @return
	 */
	public String getUUID()
	{
		return this.userID;
	}

	/**
	 * Returns player object of the inmate
	 * @return
	 */
	public Player getPlayer()
	{
		UUID pID = UUID.fromString(this.userID);
		return Bukkit.getServer().getPlayer(pID);
	}

	/**
	 * Returns the prison object that the inmate is playing in
	 * @return
	 */
	public Prison getPrison()
	{
		return this.prison;
	}

	/**
	 * Returns if the inmate is being forced to be in the prison
	 * @return
	 */
	public boolean isForced() { return this.forced; }

	/**
	 * Returns the number of minutes that the inmate has been actively playing in the prison
	 * @return
	 */
	public int getMinutesIn()
	{
		return this.minutesIn;
	}

	/**
	 * Sets the number of minutes that the inmate has been actively playing in the prison
	 * @param value
	 */
	public void setMinutesIn(int value)
	{
		this.minutesIn = value;
	}

	/**
	 * Returns the number of minutes left
	 * @return
	 */
	public int getMinutesLeft()
	{
		return this.minutesLeft;
	}

	/**
	 * Sets the number of minutes left
	 * @param value
	 */
	public void setMinutesLeft(int value)
	{
		this.minutesLeft = value;
		this.checkMinutesLeft();
	}

	/**
	 * Checks to see if the inmate has reached the end of the line
	 */
	private void checkMinutesLeft()
	{
		if (getMinutesLeft() <= 0)
		{
			//Let the player know they've been executed
			getPlayer().sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(getPlayer(), "chatExecutedTime", "You've been executed for running out of time! Thanks for playing Alcatraz in {0}{1}", ChatColor.RED, getPrison().getName()));
			Alcatraz.titleManagerAPI.sendMessage(getPlayer(), Alcatraz.language.get(getPlayer(), "titleExecutedTime", "You've been {0} executed {1}", ChatColor.RED, ChatColor.WHITE), Alcatraz.language.get(getPlayer(), "titleExecutedTime2", "for running out of time!"));

			//Release the inmate
			getPrison().getInmateManager().releaseInmate(getPlayer());
		}
		else if (getMinutesLeft() <= 15 && !timeWarningGiven())
		{
			//The inmate has not been warning about time almost running out, remind them now
			getPlayer().sendMessage(Alcatraz.language.get(getPlayer(), "chatTimeLeftWarning", "{0} WARNING! {1} You're less than 15 minutes away from being executed. Purchase more time, if you can, to keep playing: ", ChatColor.RED, ChatColor.WHITE) + ChatColor.BLUE + "/alc buy minute [#]");
			setTimeWarningGiven(true);
		}
	}

	/**
	 * Returns if the time warning has been given to the player
	 * @return
	 */
	public boolean timeWarningGiven()
	{
		return this.timeWarningGiven;
	}

	/**
	 * Sets if the time warning has been given to the player
	 * @param v
	 */
	public void setTimeWarningGiven(boolean v)
	{
		this.timeWarningGiven = v;
	}

	/**
	 * Returns the number of strikes
	 * @return
	 */
	public int getStrikes()
	{
		return this.strikes;
	}

	/**
	 * Adds a strike
	 * @param i Number of strikes
	 */
	public void addStrike(int i)
	{
		this.strikes += i;
		this.checkStrikeLimit();
	}

	/**
	 * Sets the inmates total number of strikes
	 * @param value
	 */
	public void setStrikes(int value)
	{
		this.strikes = value;
		this.checkStrikeLimit();
	}

	/**
	 * Checks to see if the inmate's current strikes exceed the maximum allowed
	 */
	private void checkStrikeLimit()
	{
		if (getStrikes() >= Settings.getGlobalInt(Setting.MaxStrikes))
		{
			//The inmate has exceeded the maximum allowed strikes
			getPlayer().sendMessage(Alcatraz.pluginPrefix + Alcatraz.language.get(getPlayer(), "chatExecutedStrikes", "You've been executed for accumulating too many strikes! Thanks for playing Alcatraz in {0}{1}", ChatColor.RED, getPrison().getName()));
			Alcatraz.titleManagerAPI.sendMessage(getPlayer(), Alcatraz.language.get(getPlayer(), "titleExecutedStrikes", "You've been {0} executed {1}", ChatColor.RED, ChatColor.WHITE), Alcatraz.language.get(getPlayer(), "titleExecutedStrikes2", "for accumulating too many strikes!"));

			//Release the inmate from the game
			getPrison().getInmateManager().releaseInmate(getPlayer());
		}
		else
		{
			if (getStrikes() == Settings.getGlobalInt(Setting.MaxStrikes) - 1 && !strikeWarningGiven())
			{
				//Inmate has not gotten strike limit, remind them now
				getPlayer().sendMessage(Alcatraz.language.get(getPlayer(), "chatTimeLeftWarning", "{0} WARNING! {1} You're one strike away from being executed. Purchase a strike removal, if you can, to keep playing: ", ChatColor.RED, ChatColor.WHITE) + ChatColor.BLUE + "/alc buy strike [#]");
				setStrikeWarningGiven(true);
			}
		}
	}

	/**
	 * Returns boolean if the strike warning has been given to the player
	 * @return
	 */
	public boolean strikeWarningGiven()
	{
		return this.strikeWarningGiven;
	}

	/**
	 * Sets if the strike warning has been given to the player
	 * @param v
	 */
	public void setStrikeWarningGiven(boolean v)
	{
		this.strikeWarningGiven = v;
	}

	/**
	 * Returns the number of kills
	 * @return
	 */
	public int getKills()
	{
		return this.kills;
	}

	/**
	 * Adds a kill to the inmates stats
	 */
	public synchronized void addKill()
	{
		this.setKills(this.getKills() + 1);
	}

	/**
	 * Sets the number of kills
	 * @param amount
	 */
	public void setKills(int amount)
	{
		this.kills = amount;
	}

	/**
	 * Returns the balance of the inmate's prison account
	 * @return
	 */
	public double getMoney()
	{
		return this.money;
	}

	/**
	 * Adds provided amount to the inmate's current balance
	 * @param value
	 */
	public synchronized void addMoney(double value)
	{
		this.setMoney(this.getMoney() + value);
	}

	/**
	 * Sets provided amount as the inmate's current balance
	 * @param value
	 */
	public void setMoney(double value)
	{
		this.money = value;
	}

	/**
	 * Deposits vault balance of player (if any) into their inmate balance
	 */
	public void moneyVaultToInmate()
	{
		if (Alcatraz.econ.getBalance(this.getPlayer()) > 0)
		{
			double balance = Alcatraz.econ.getBalance(this.getPlayer());
			Alcatraz.econ.withdrawPlayer(this.getPlayer(), balance);
			this.addMoney(balance);
		}
	}

	/**
	 * Returns the inmates balance minus startup money to vault balance (if any)
	 */
	public void moneyInmateToVault()
	{
		Alcatraz.econ.depositPlayer(Bukkit.getOfflinePlayer(UUID.fromString(getPlayer().getUniqueId().toString())), getMoney() - Settings.getGlobalInt(Setting.DefaultMoney));
	}

	/**
	 * Reuturns the prison cell object for the inmate's cell
	 * @return
	 */
	public PrisonCell getCell()
	{
		return this.cell;
	}

	/**
	 * Sets the inmate's cell
	 * @param c
	 */
	public void setCell(PrisonCell c)
	{
		this.cell = c;
	}
	
	/**
	 * Returns boolean if the inmate has been assigned a cell
	 * @return
	 */
	public boolean hasCell()
	{
		if (cell == null)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	/**
	 * Returns the inmate's scoreboard manager
	 * @return
	 */
	public ScoreboardManager getScoreboardManager()
	{
		return this.boardManager;
	}

	/**
	 * Calculates and returns the cost per minute left
	 * @return
	 */
	public double getMinuteCost()
	{
		double cost = Settings.getGlobalInt(Setting.MinuteCost);
		
		if (this.getMinutesLeft() > 60)
		{
			cost = cost * 1.5;
		}
		else if (this.getMinutesLeft()<= 60 && this.getMinutesLeft() >= 30)
		{
			cost = cost * 3;
		}
		else if (this.getMinutesLeft() <= 10)
		{
			cost = cost * 7;
		}
		else if (this.getMinutesLeft() <= 1)
		{
			cost = cost * 15;
		}
		
		return cost;
		
	}

	/**
	 * Calculates and returns the cost per strike removal
	 * @return
	 */
	public double getStrikeCost()
	{
		double cost = Settings.getGlobalInt(Setting.StrikeCost);

		if (this.getStrikes() == Settings.getGlobalInt(Setting.MaxStrikes) -1)
		{
			cost = Settings.getGlobalInt(Setting.StrikeCost) * 5;
		}
		else if (this.getStrikes() == Settings.getGlobalInt(Setting.MaxStrikes) -2)
		{
			cost = Settings.getGlobalInt(Setting.StrikeCost) * 2;
		}
		else
		{
			cost = Settings.getGlobalInt(Setting.StrikeCost) * 1;
		}

		return cost;
	}
	
	public long getLastLoot()
	{
		return this.lastLoot;
	}
	
	public void setLastLoot(long l)
	{
		this.lastLoot = l;
	}

	/**
	 * Returns if the player is within the prison confines
	 * @return
	 */
	public boolean isWithinPrison()
	{
		double[] dim = new double[2];

		dim[0] = getPrison().getX1();
		dim[1] = getPrison().getX2();
		Arrays.sort(dim);
		if(getPlayer().getLocation().getX() > dim[1] || getPlayer().getLocation().getX() < dim[0])
			return false;

		dim[0] = getPrison().getY1();
		dim[1] = getPrison().getY2();
		Arrays.sort(dim);
		if(getPlayer().getLocation().getY() > dim[1] || getPlayer().getLocation().getY() < dim[0])
			return false;

		dim[0] = getPrison().getZ1();
		dim[1] = getPrison().getZ2();
		Arrays.sort(dim);
		if(getPlayer().getLocation().getZ() > dim[1] || getPlayer().getLocation().getZ() < dim[0])
			return false;

		return true;
	}

	
	/**
	 * Returns the number of direct contact attempts this inmate has
	 * @return
	 */
	public int getDirectContactAttempts()
	{
		return directContactAttempts;
	}
	
	/**
	 * Adds supplied number of direct contact attempts to inmate
	 * @param num
	 */
	public void addDirectContactAttempts(int num)
	{
		directContactAttempts += num;
	}
	
	/**
	 * Resets the inmate's direct contact attempts to 0
	 */
	public void resetDirectContactAttempts()
	{
		directContactAttempts = 0;
	}

	/**
	 * Updates inmate values in the database
	 */
	public void updateInDatabase()
	{
		Alcatraz.IO.updateInmate(this);
	}
	
}
