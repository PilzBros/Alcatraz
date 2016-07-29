package com.pilzbros.Alcatraz.Plugins;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.empcraft.Placeholder;
import com.pilzbros.Alcatraz.Alcatraz;

public class ISS 
{
	public ISS()
	{
		   Alcatraz.ISP.addPlaceholder(new Placeholder("alcMoney")
		    {
		        @Override
		        public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) 
		        {
		        	if (Alcatraz.prisonController.isActivelyPlaying(player))
		        	{
		                return "$"+Alcatraz.prisonController.getPlayerPrison(player).getInmateManager().getInmate(player).getMoney();
		        	}
		        	else
		        	{
		        		return "-Not Playing-";
		        	}
		        }
		        });
		   
		 
		   
		   Alcatraz.ISP.addPlaceholder(new Placeholder("alcKills")
		    {
		        @Override
		        public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) 
		        {
		        	if (Alcatraz.prisonController.isActivelyPlaying(player))
		        	{
		                return ""+Alcatraz.prisonController.getPlayerPrison(player).getInmateManager().getInmate(player).getKills();
		        	}
		        	else
		        	{
		        		return "-Not Playing-";
		        	}
		        }
		        });
		   
		   Alcatraz.ISP.addPlaceholder(new Placeholder("alcMinLeft")
		    {
		        @Override
		        public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) 
		        {
		        	if (Alcatraz.prisonController.isActivelyPlaying(player))
		        	{
		                return ""+Alcatraz.prisonController.getPlayerPrison(player).getInmateManager().getInmate(player).getMinutesLeft();
		        	}
		        	else
		        	{
		        		return "-Not Playing-";
		        	}
		        }
		        });
		   
		   Alcatraz.ISP.addPlaceholder(new Placeholder("alcMinIn")
		    {
		        @Override
		        public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) 
		        {
		        	if (Alcatraz.prisonController.isActivelyPlaying(player))
		        	{
		                return ""+Alcatraz.prisonController.getPlayerPrison(player).getInmateManager().getInmate(player).getMinutesIn();
		        	}
		        	else
		        	{
		        		return "-Not Playing-";
		        	}
		        }
		        });
		   
		   Alcatraz.ISP.addPlaceholder(new Placeholder("alcCell")
		    {
		        @Override
		        public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) 
		        {
		        	if (Alcatraz.prisonController.isActivelyPlaying(player))
		        	{
		        		if (Alcatraz.prisonController.getPlayerPrison(player).getInmateManager().getInmate(player).getCell() != null)
		        		{
		        			return ""+Alcatraz.prisonController.getPlayerPrison(player).getInmateManager().getInmate(player).getCell().getCellNumber();
		        		}
		        		else
		        		{
		        			return "Unassigned";
		        		}
		        	}
		        	else
		        	{
		        		return "-Not Playing-";
		        	}
		        }
		        });
		   
		   Alcatraz.ISP.addPlaceholder(new Placeholder("alcCostMin")
		    {
		        @Override
		        public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) 
		        {
		        	if (Alcatraz.prisonController.isActivelyPlaying(player))
		        	{
		        		return "$"+Alcatraz.prisonController.getPlayerPrison(player).getInmateManager().getInmate(player).getMinuteCost();
		        	}
		        	else
		        	{
		        		return "-Not Playing-";
		        	}
		        }
		        });
		   
		   Alcatraz.ISP.addPlaceholder(new Placeholder("alcCostStrike")
		    {
		        @Override
		        public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) 
		        {
		        	if (Alcatraz.prisonController.isActivelyPlaying(player))
		        	{
		        		return "$"+Alcatraz.prisonController.getPlayerPrison(player).getInmateManager().getInmate(player).getStrikeCost();
		        	}
		        	else
		        	{
		        		return "-Not Playing-";
		        	}
		        }
		        });
		   
		   Alcatraz.ISP.addPlaceholder(new Placeholder("alcOnline")
		    {
		        @Override
		        public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) 
		        {
		        	if (Alcatraz.prisonController.isActivelyPlaying(player))
		        	{
		        		return ""+Alcatraz.prisonController.getPlayerPrison(player).getInmateManager().numActiveInmates();
		        	}
		        	else
		        	{
		        		return "-Not Playing-";
		        	}
		        }
		        });
		   
		   Alcatraz.ISP.addPlaceholder(new Placeholder("alcStrikes")
		    {
		        @Override
		        public String getValue(Player player, Location location,String[] modifiers, Boolean elevation) 
		        {
		        	if (Alcatraz.prisonController.isActivelyPlaying(player))
		        	{
		        		return ""+Alcatraz.prisonController.getPlayerPrison(player).getInmateManager().getInmate(player).getStrikes();
		        	}
		        	else
		        	{
		        		return "-Not Playing-";
		        	}
		        }
		        });
		   
		   
		   
		  
		        
		        
		   //Whitelist
		   Alcatraz.ISP.whitelistPlaceholder("alcMoney");
		   Alcatraz.ISP.whitelistPlaceholder("alcCell");
		   Alcatraz.ISP.whitelistPlaceholder("alcKills");
		   Alcatraz.ISP.whitelistPlaceholder("alcMinLeft");
		   Alcatraz.ISP.whitelistPlaceholder("alcMinIn");
		   Alcatraz.ISP.whitelistPlaceholder("alcCostMin");
		   Alcatraz.ISP.whitelistPlaceholder("alcCostStrike");
		   Alcatraz.ISP.whitelistPlaceholder("alcStrikes");
		   
	}

}
