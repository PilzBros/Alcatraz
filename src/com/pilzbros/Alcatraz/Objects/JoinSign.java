package com.pilzbros.Alcatraz.Objects;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.libs.jline.internal.Log;

import com.pilzbros.Alcatraz.Alcatraz;


public class JoinSign
{
    private Prison prison;
    private Location sign;

    public JoinSign (Prison p, Location l)
    {
        this.prison = p;
        this.sign = l;
        this.update();
    }

    /**
     * Updates cell sign with latest information
     */
    public void update()
    {
        try
        {
            Sign sign = this.getSign();
            sign.setLine(0, ChatColor.RED + Alcatraz.signPrefix);
            sign.setLine(1, prison.getName());
            sign.setLine(2, ChatColor.GREEN + "" + prison.getInmateManager().numActiveInmates() + ChatColor.WHITE + " On / " + ChatColor.RED + prison.getInmateManager().numInactiveInmates() + ChatColor.WHITE + " Off"); //Empty message
            sign.setLine(3, "Click to join!");


            sign.update();
        }
        catch (Exception e)
        {
            Log.info(Alcatraz.consolePrefix + " Broken join sign in " + prison.getName() + " - removed from DB");
            Alcatraz.IO.removeSign(this.getSign().getLocation());
        }
    }

    public void setDeleted()
    {
        try
        {
            Sign sign = this.getSign();
            sign.setLine(0, ChatColor.RED + Alcatraz.signPrefix);
            sign.setLine(1, prison.getName());
            sign.setLine(2, ""); //Empty message
            sign.setLine(3, ChatColor.RED + "" + ChatColor.BOLD + "DELETED");


            sign.update();
        }
        catch (Exception e)
        {
            Log.info(Alcatraz.consolePrefix + " Broken join sign in " + prison.getName() + " - removed from DB");
            Alcatraz.IO.removeSign(this.getSign().getLocation());
        }

        Alcatraz.IO.removeSign(this.getSign().getLocation());
    }

    /**
     * Returns sign object
     * @return
     */
    public Sign getSign()
    {
        try
        {
            return (Sign)sign.getBlock().getState();
        }
        catch(Exception e)
        {
            Alcatraz.IO.removeSign(sign.getBlock().getLocation()); //Sign is broken, remove from DB
            return null;
        }
    }

    /**
     * Returns prison object
     * @return
     */
    public Prison getPrison()
    {
        return this.prison;
    }

    public void saveToDB()
    {
        Alcatraz.IO.newSign(prison,sign, "Join", "");
    }
}
