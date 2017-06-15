package com.pilzbros.Alcatraz.Runnable;

import java.util.Iterator;
import java.util.Map;

import org.bukkit.scheduler.BukkitRunnable;

import com.pilzbros.Alcatraz.Alcatraz;
import com.pilzbros.Alcatraz.Objects.Prison;

public class MoneyDeposit extends BukkitRunnable
{
	@Override
	public void run() 
	{
		if (Alcatraz.prisonController.playersPlaying())
		{
			for (Prison prison: Alcatraz.prisonController.getPrisons())
			{
				//Removed
			}
		}
	}
}
