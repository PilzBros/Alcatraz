package com.pilzbros.Alcatraz.Runnable;

import java.util.Iterator;
import java.util.Map;

import org.bukkit.scheduler.BukkitRunnable;

import com.pilzbros.Alcatraz.Alcatraz;
import com.pilzbros.Alcatraz.Objects.Prison;

public class PrisonCheck extends BukkitRunnable
{
	@Override
	public void run() 
	{
		Iterator it = Alcatraz.prisonController.getPrisons().entrySet().iterator();
		while (it.hasNext()) 
		{
			Map.Entry entry = (Map.Entry) it.next();
			Prison prison = (Prison)entry.getValue();
			prison.autoCheck();
		}
	}

}
