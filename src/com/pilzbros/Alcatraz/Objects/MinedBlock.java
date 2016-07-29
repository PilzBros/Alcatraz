package com.pilzbros.Alcatraz.Objects;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class MinedBlock 
{
	private Material material;
	private Block block;
	
	public MinedBlock (Material m, Block b)
	{
		material = m;
		block = b;
	}
	
	/**
	 * Returns material
	 * @return
	 */
	public Material getMaterial()
	{
		return material;
	}
	
	/**
	 * Returns block
	 * @return
	 */
	public Block getBlock()
	{
		return block;
	}

}
