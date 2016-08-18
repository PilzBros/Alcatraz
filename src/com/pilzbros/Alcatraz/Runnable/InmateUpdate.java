package com.pilzbros.Alcatraz.Runnable;

import java.util.Iterator;
import java.util.Map;

import org.bukkit.scheduler.BukkitRunnable;

import com.pilzbros.Alcatraz.Alcatraz;
import com.pilzbros.Alcatraz.Objects.Prison;

public class InmateUpdate extends BukkitRunnable 
{
	@Override
	public void run() 
	{
		for (Prison prison: Alcatraz.prisonController.getPrisons())
		{
			prison.getInmateManager().updateInmates();
			prison.getCellManager().updateCells();
		}
	}

}
