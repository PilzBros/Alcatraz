package com.pilzbros.Alcatraz.Objects;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.block.Chest;

import com.pilzbros.Alcatraz.Alcatraz;

public class PrisonCell 
{
	private Prison prison;
	private Inmate inmate;

	private String cellNumber;
	private Location chest;
	
	ArrayList<CellSign> signs;
	
	
	public PrisonCell(Prison p, String num)
	{
		this.prison = p;
		this.cellNumber = num;
		this.inmate = null;
		this.chest = null;
		this.signs = new ArrayList<CellSign>();
	}
	
	public Prison getPrison()
	{
		return this.prison;
	}
	
	public Inmate getInmate()
	{
		return this.inmate;
	}
	
	public void setInmate(Inmate i)
	{
		this.inmate = i;
	}
	
	public String getCellNumber()
	{
		return this.cellNumber;
	}
	
	public Chest getChest()
	{
		return (Chest)this.chest.getBlock().getState();
	}

	public ArrayList<CellSign> getSigns()
	{
		return this.signs;
	}

	public void addSign(CellSign sign)
	{
		this.signs.add(sign);
	}
	
	public void removeSign(CellSign sign)
	{
		this.signs.remove(signs);
	}
	
	public void setChest(Location c)
	{
		this.chest = c;
	}
	
	public void updateSigns()
	{
		for(CellSign s : this.getSigns()) 
		{
			s.update();
		}
	}
	
	/**
	 * Removes inmate from cell and sets cell as vacant
	 */
	public void removeInmate()
	{
		this.inmate = null;
		this.updateSigns();
	}
	
	public void update()
	{
		Alcatraz.IO.updateCell(this);
	}
	
	/**
	 * Returns boolean if the cell is currently occupied by an inmate
	 * @return
	 */
	public boolean isOccupied()
	{
		if (this.getInmate() == null)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	/*
	 * Chest c = (Chest) block.getState();
		Inventory i = c.getInventory();
		player.openInventory(i);
	 */
}
