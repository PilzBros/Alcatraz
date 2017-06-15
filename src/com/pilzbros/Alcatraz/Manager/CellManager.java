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

	/**
	 * Returns HashMap of cells
	 * @return
	 */
	public HashMap<String, PrisonCell> getCells()
	{
		return cells;
	}


	/**
	 * Returns number of cells
	 */
	public int getNumCells()
	{
		return cells.size();
	}

	/**
	 * Adds a cell to the prison
	 * @param cell
	 */
	public void addCell(PrisonCell cell)
	{
		cells.put(cell.getCellNumber(), cell);
	}

	/**
	 * Removes a cell from the prison
	 * @param num
	 */
	public void removeCell (String num)
	{
		cells.remove(num);
	}

	/**
	 * Returns cell object by supplied cell number
	 * @param num
	 * @return
	 */
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

	/**
	 * Returns if there is an empty cell
	 * @return
	 */
	public boolean isAvailableCell()
	{
		for(Entry<String, PrisonCell> cell : this.getCells().entrySet())
		{
			if (!cell.getValue().isOccupied())
			{
				return true;
			}
		}

		return false;
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
					i.setCell(cell.getValue());
					ic = cell.getValue();
					found = true;
				}
			}
		}
		
		return found;
	}

	/**
	 * Updates all cells in database
	 */
	public void updateCells()
	{
		for(Entry<String, PrisonCell> cell : this.getCells().entrySet()) 
		{	
			Alcatraz.IO.updateCell(cell.getValue());
		}
	}

	



}
