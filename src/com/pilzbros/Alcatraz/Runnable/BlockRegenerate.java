package com.pilzbros.Alcatraz.Runnable;

import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import com.pilzbros.Alcatraz.Alcatraz;
import com.pilzbros.Alcatraz.Objects.Prison;

public class BlockRegenerate extends BukkitRunnable 
{
	private Block block;
	private Material material;
	private Prison prison;
	
	public BlockRegenerate(Prison p, Block b, Material m)
	{
		this.prison = p;
		this.block = b;
		this.material = m;
	}
	

	@Override
	public void run() 
	{
		try
		{
			prison.getStartPoint().getWorld().getBlockAt(block.getLocation()).setType(material);
		}
		catch (Exception e)
		{
			Alcatraz.log.log(Level.WARNING, Alcatraz.consolePrefix + "Error while attempting to regenerate mined block in " + prison.getName());
		}
	}

}
