package com.pilzbros.Alcatraz.Runnable;

import java.util.Iterator;
import java.util.Map;

import org.bukkit.scheduler.BukkitRunnable;

import com.pilzbros.Alcatraz.Alcatraz;
import com.pilzbros.Alcatraz.Objects.Prison;

public class MinuteCounter implements Runnable
{
	@Override
	public void run() 
	{
		if (System.currentTimeMillis() - Alcatraz.lastMinuteCheck >= 60000)
		{
			Alcatraz.lastMinuteCheck = System.currentTimeMillis();

			for (Prison prison: Alcatraz.prisonController.getPrisons())
			{
			    prison.getInmateManager().minutePass();
			}
		}
		else
		{
			return;
		}
	}
}