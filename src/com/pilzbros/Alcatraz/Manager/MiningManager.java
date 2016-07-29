package com.pilzbros.Alcatraz.Manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.pilzbros.Alcatraz.Alcatraz;
import com.pilzbros.Alcatraz.Componenets.Queue;
import com.pilzbros.Alcatraz.IO.Setting;
import com.pilzbros.Alcatraz.IO.Settings;
import com.pilzbros.Alcatraz.Objects.MinedBlock;
import com.pilzbros.Alcatraz.Objects.Prison;
import com.pilzbros.Alcatraz.Runnable.BlockRegenerate;
import com.pilzbros.Alcatraz.Runnable.DoorAction;

public class MiningManager 
{
	private Prison prison;
	public ArrayList<Material> breakableBlocks;
	public Queue<MinedBlock> brokenBlocks;
	
	public MiningManager(Prison p)
	{
		this.prison = p;
		breakableBlocks = new ArrayList<Material>();
		brokenBlocks = new Queue<MinedBlock>();
		initBreakable();
	}
	
	/**
	 * Initializes breakable blocks list
	 */
	public void initBreakable()
	{
		breakableBlocks.add(Material.DIRT);
		breakableBlocks.add(Material.COBBLESTONE);
		breakableBlocks.add(Material.LEAVES);
		breakableBlocks.add(Material.STONE);
		breakableBlocks.add(Material.DIAMOND_ORE);
		breakableBlocks.add(Material.GOLD_ORE);
		breakableBlocks.add(Material.EMERALD_ORE);
		breakableBlocks.add(Material.COAL_ORE);
		breakableBlocks.add(Material.LAPIS_ORE);
		breakableBlocks.add(Material.REDSTONE_ORE);
		breakableBlocks.add(Material.OBSIDIAN);
		breakableBlocks.add(Material.IRON_ORE);
	}
	
	public void autoCheck()
	{
		if (brokenBlocks.size() >= Settings.getGlobalInt(Setting.RegenerateBlocks))
		{
			regenerate();
		}
	}
	
	/**
	 * Adds block and material to list of blocks broken to later be regenerated
	 * @param b
	 * @param m
	 */
	public void addBroken(Block b, Material m)
	{
		brokenBlocks.enqueue(new MinedBlock(m, b));
	}
		
	/**
	 * Regenerates all broken blocks all at once
	 */
	public synchronized void forceRegenerate()
	{
		int count = 0;
		while (!brokenBlocks.isEmpty()) 
		{
			try
			{
				MinedBlock minedBlock = brokenBlocks.dequeue();
				prison.getStartPoint().getWorld().getBlockAt(minedBlock.getBlock().getLocation()).setType(minedBlock.getMaterial());
				count++;
			}
			catch (Exception e)
			{
				Alcatraz.log.log(Level.WARNING, Alcatraz.consolePrefix + "Error while attempting to froce regenerate mined block in " + prison.getName());
			}
		}
		
		//Log regenerate results
		if (count > 0)
		{
			Alcatraz.log.log(Level.INFO, Alcatraz.consolePrefix + "Force regenerated " + count + " blocks in " + prison.getName());
		}
	}
	
	/**
	 * Regenerates all broken blocks
	 */
	public void regenerate()
	{
		int ticks = 1, count = 0;
		while (!brokenBlocks.isEmpty()) 
		{
			
			MinedBlock minedBlock = brokenBlocks.dequeue();
		    Bukkit.getScheduler().runTaskLater(Alcatraz.instance, new BlockRegenerate(prison, minedBlock.getBlock(), minedBlock.getMaterial()), ticks);
		    
		    ticks += 3;
		    count++;
		}
		
		//Log regenerate results
		if (count > 0)
		{
			Alcatraz.log.log(Level.INFO, Alcatraz.consolePrefix + "Regenerated " + count + " blocks in " + prison.getName());
		}
	}
	
	public ArrayList<Material> getBreakableBlocks()
	{
		return this.breakableBlocks;
	}

	/**
	 * Returns boolean if that type of material is breakable
	 * @param m
	 * @return
	 */
	public boolean isBreakable(Material m)
	{
		if (this.breakableBlocks.contains(m))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

}
