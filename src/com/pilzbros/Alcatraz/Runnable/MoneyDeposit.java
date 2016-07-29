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
			Iterator it = Alcatraz.prisonController.getPrisons().entrySet().iterator();
			while (it.hasNext()) 
			{
				Map.Entry entry = (Map.Entry) it.next();
				Prison prison = (Prison)entry.getValue();
				prison.getInmateManager().vaultDeposit();
			}
		}
	}
}
