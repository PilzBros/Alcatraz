package com.pilzbros.Alcatraz.Runnable;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.material.Door;
import org.bukkit.scheduler.BukkitRunnable;

import com.pilzbros.Alcatraz.Alcatraz;

public class DoorAction extends BukkitRunnable 
{
	private Block door;
	private Block relative;
	private boolean action;
	private Player player;
	
	public DoorAction(Block b, Block r, boolean open, Player p)
	{
		this.door = b;
		this.relative = r;
		this.action = open;
		this.player = p;
	}
	
	
	@Override
	public void run() 
	{

		BlockState state = door.getState();
        Door door = (Door) state.getData();
        
        if (door.isTopHalf())
        {
  
        	BlockState state2 = relative.getState();
        	Door setDoor = (Door) state2.getData();
            
        	if (action == true)
        	{
        		setDoor.setOpen(true);
        		Bukkit.getScheduler().runTaskLater(Alcatraz.instance, new DoorAction(this.door, relative, false, player), 60);
            	
        	}
        	else
        	{
        		setDoor.setOpen(false);
        	}
        	
        	state2.update();
        } 
        else 
        {
        	if (action == true)
        	{
        		door.setOpen(true);
        		Bukkit.getScheduler().runTaskLater(Alcatraz.instance, new DoorAction(this.door, relative, false, player), 60);
        	}
        	else
        	{
        		door.setOpen(false);
        	}
        	
        	state.update();
        }
	}

}
