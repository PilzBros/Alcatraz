package com.pilzbros.Alcatraz.Runnable;

import java.util.Iterator;
import java.util.Map;

import org.bukkit.scheduler.BukkitRunnable;

import com.pilzbros.Alcatraz.Alcatraz;
import com.pilzbros.Alcatraz.Objects.Prison;

public class MealService extends BukkitRunnable
{
	@Override
	public void run() 
	{
		if (System.currentTimeMillis() - Alcatraz.lastMealServiceCheck >= 3600000) //every hour
		{
			Alcatraz.lastMealServiceCheck = System.currentTimeMillis();
		
			for (Prison prison: Alcatraz.prisonController.getPrisons())
			{
			    prison.mealTime();
			}
		}
		else
		{
			return;
		}
	}
}