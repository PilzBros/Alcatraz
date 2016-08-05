package com.pilzbros.Alcatraz.Manager;

import java.util.HashMap;
import java.util.Map.Entry;

import com.pilzbros.Alcatraz.Alcatraz;
import com.pilzbros.Alcatraz.Objects.Inmate;
import com.pilzbros.Alcatraz.Objects.Prison;
import com.pilzbros.Alcatraz.Objects.PrisonCell;

public class CellManager 
{
	private Prison prison;
	private HashMap<String, PrisonCell> cells;
	
	public CellManager(Prison p)
	{
		this.prison = p;
		this.cells = new HashMap<String, PrisonCell>();
	}
	
	public void addCell(PrisonCell cell)
	{
		cells.put(cell.getCellNumber(), cell);
	}

	public void removeCell(PrisonCell cell)
	{
		removeCell(cell.getCellNumber());
	}
	
	public void removeCell (String num)
	{
		cells.remove(num);
	}

	public PrisonCell getCell (String num)
	{
		if (cells.containsKey(num))
		{
			return cells.get(num);
		}
		else
		{
			return null;
		}
	}

	public boolean cellExists (String num)
	{
		if (cells.containsKey(num))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean cellExists(int num)
	{
		return cellExists(Integer.toString(num));
	}
	
	public HashMap<String, PrisonCell> getCells()
	{
		return cells;
	}

	public void updateCellSigns()
	{
		for(Entry<String, PrisonCell> cell : this.getCells().entrySet()) 
		{
			cell.getValue().updateSigns();
		}
	}
	
	public boolean assignCell(Inmate i)
	{
		boolean found = false;
		PrisonCell ic = null;
		
		for(Entry<String, PrisonCell> cell : this.getCells().entrySet()) 
		{	
			if (found == false)
			{
				if (!cell.getValue().isOccupied())
				{
					cell.getValue().setInmate(i);
					cell.getValue().updateSigns();
					i.setCell(cell.getValue());
					ic = cell.getValue();
					found = true;
				}
			}
		}
		
		return found;
	}

	/*
	 * Updates all cells in database
	 */
	public void updateCells()
	{
		for(Entry<String, PrisonCell> cell : this.getCells().entrySet()) 
		{	
			Alcatraz.IO.updateCell(cell.getValue());
		}
	}
	
	/**
	 * Returns number of cells
	 */
	public int getNumCells()
	{
		int count = 0;
		
		for(Entry<String, PrisonCell> cell : this.getCells().entrySet()) 
		{	
			count++;
		}
		
		return count;
	}
	



}
