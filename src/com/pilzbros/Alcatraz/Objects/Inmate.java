package com.pilzbros.Alcatraz.Objects;

import java.util.UUID;

import org.bukkit.Bukkit;
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

	
	private boolean timeWarning; //boolean if should be warning, false if already warned
	private boolean strikeWarning;
	private long lastLoot;
	
	
	public Inmate(String pUUID, int pMinutesIn, int pMinutesLeft, int pStrikes, int pKills, double pMoney, Prison p)
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
		this.timeWarning = true;
		this.strikeWarning = true;
		this.directContactAttempts = 0;
		this.boardManager = new ScoreboardManager(p, this);
	}
	
	public String getUUID()
	{
		return this.userID;
	}
	
	public Player getPlayer()
	{
		UUID pID = UUID.fromString(this.userID);
		return Bukkit.getServer().getPlayer(pID);
	}
	
	public Prison getPrison()
	{
		return this.prison;
	}
	
	public int getMinutesIn()
	{
		return this.minutesIn;
	}
	
	public void setMinutesIn(int value)
	{
		this.minutesIn = value;
	}
	
	public int getMinutesLeft()
	{
		return this.minutesLeft;
	}
	
	public void setMinutesLeft(int value)
	{
		this.minutesLeft = value;
	}
	
	public int getStrikes()
	{
		return this.strikes;
	}
	
	public void addStrike(int i)
	{
		this.strikes += i;
	}
	
	public void setStrikes(int value)
	{
		this.strikes = value;
	}
	
	public int getKills()
	{
		return this.kills;
	}
	
	public double getMoney()
	{
		return this.money;
	}
	
	public void setMoney(double value)
	{
		this.money = value;
	}
	
	public void addMoney(double value)
	{
		this.setMoney(this.getMoney() + value);
	}
	
	public void addKill()
	{
		this.setKills(this.getKills() + 1);
	}
	
	public void setKills(int amount)
	{
		this.kills = amount;
	}
	
	public PrisonCell getCell()
	{
		return this.cell;
	}
	
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
	
	
	public ScoreboardManager getScoreboardManager()
	{
		return this.boardManager;
	}
	
	public void update()
	{
		Alcatraz.IO.updateInmate(this);
	}
	
	public boolean timeWarning()
	{
		return this.timeWarning;
	}
	
	public boolean strikeWarning()
	{
		return this.strikeWarning;
	}
	
	public void setTimeWarning(boolean v)
	{
		this.timeWarning = v;
	}
	
	public void setStrikeWarning(boolean v)
	{
		this.strikeWarning = v;
	}
	
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
	
	public long getLastLoot()
	{
		return this.lastLoot;
	}
	
	public void setLastLoot(long l)
	{
		this.lastLoot = l;
	}
	
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
	
	
}
