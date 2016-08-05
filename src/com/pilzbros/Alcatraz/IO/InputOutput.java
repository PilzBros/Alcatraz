package com.pilzbros.Alcatraz.IO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.logging.Level;

import com.pilzbros.Alcatraz.Objects.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.pilzbros.Alcatraz.Alcatraz;


public class InputOutput
{
    public static YamlConfiguration global;
    private static Connection connection;
    
	public InputOutput()
	{
		if (!Alcatraz.instance.getDataFolder().exists()) 
		{
			try 
			{
				(Alcatraz.instance.getDataFolder()).mkdir();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		global = new YamlConfiguration();
	}
   
	public void LoadSettings()
	{
    	try {
    		if (!new File(Alcatraz.instance.getDataFolder(),"global.yml").exists()) global.save(new File(Alcatraz.instance.getDataFolder(),"global.yml"));

    		global.load(new File(Alcatraz.instance.getDataFolder(),"global.yml"));
	    	for (Setting s : Setting.values())
	    	{
	    		if (global.get(s.getString()) == null) global.set(s.getString(), s.getDefault());
	    	}
	    	
	    	
	    	global.save(new File (Alcatraz.instance.getDataFolder(),"global.yml"));
	    	

		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
    
    public static synchronized Connection getConnection() {
    	if (connection == null) connection = createConnection();
            try
            {
                if(connection.isClosed()) connection = createConnection();
            } 
            catch (SQLException ex) 
            {
                ex.printStackTrace();
            }
        
    	return connection;
    }
    
    private static Connection createConnection() {
        
    	try
    	{
                Class.forName("org.sqlite.JDBC");
                Connection ret = DriverManager.getConnection("jdbc:sqlite:" +  new File(Alcatraz.instance.getDataFolder().getPath(), "db.sqlite").getPath());
                ret.setAutoCommit(false);
                return ret;
        } 
        catch (ClassNotFoundException e) 
        {
        	Alcatraz.log.log(Level.SEVERE, Alcatraz.consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(), "consoleDatabaseConnError", "Fatal database connection error - 1"));
        	e.printStackTrace();
        	return null;
        } 
        catch (SQLException e) 
        {
        	Alcatraz.log.log(Level.SEVERE, Alcatraz.consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(), "consoleDatabaseException", "Fatal database connection error - SQL Exception"));
        	e.printStackTrace();
        	return null;
        }
    }
    
    public static synchronized void freeConnection() {
		Connection conn = getConnection();
        if(conn != null) {
            try {
            	conn.close();
            	conn = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void prepareDB()
    {
    	Connection conn = getConnection();
        Statement st = null;
        try 
        {
        		st = conn.createStatement();
            	st.executeUpdate("CREATE TABLE IF NOT EXISTS \"alcatraz_prisons\" (\"Name\" VARCHAR PRIMARY KEY NOT NULL, \"Max\" DOUBLE, \"X1\" DOUBLE, \"Y1\" DOUBLE, \"Z1\" DOUBLE, \"X2\" DOUBLE, \"Y2\" DOUBLE, \"Z2\" DOUBLE, \"ReturnX\" DOUBLE, \"ReturnY\" DOUBLE, \"ReturnZ\" DOUBLE, \"StartX\" DOUBLE, \"StartY\" DOUBLE, \"StartZ\" DOUBLE, \"playWorld\" VARCHAR, \"returnWorld\" VARCHAR)");
                st.executeUpdate("CREATE TABLE IF NOT EXISTS \"alcatraz_signs\" (\"X\" DOUBLE, \"Y\" DOUBLE, \"Z\" DOUBLE, \"World\" VARCHAR, \"Prison\" VARCHAR, \"Type\" VARCHAR, \"Cell\" VARCHAR)");
                st.executeUpdate("CREATE TABLE IF NOT EXISTS \"alcatraz_inmates\" (\"UUID\" VARCHAR PRIMARY KEY  NOT NULL, \"MinutesIn\" DOUBLE, \"MinutesLeft\" DOUBLE, \"Strikes\" DOUBLE, \"Kills\" DOUBLE, \"Money\" DOUBLE, \"Prison\" VARCHAR)");
                st.executeUpdate("CREATE TABLE IF NOT EXISTS \"alcatraz_chests\" (\"X\" DOUBLE, \"Y\" DOUBLE, \"Z\" DOUBLE, \"World\" DOUBLE, \"Prison\" VARCHAR, \"Type\" VARCHAR, \"Cell\" VARCHAR)");
                st.executeUpdate("CREATE TABLE IF NOT EXISTS \"alcatraz_cells\" (\"Prison\" VARCHAR, \"CellNumber\" VARCHAR, \"InmateUUID\" VARCHAR)");
                
                conn.commit();
                st.close();

        } 
        catch (SQLException e) 
        {
            Alcatraz.log.log(Level.SEVERE, Alcatraz.consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(), "consoleDatabasePrepareSQL", "SQL error while attempting to prepare database!"));
            e.printStackTrace();
        }
        catch (Exception e) 
		{
        	Alcatraz.log.log(Level.SEVERE, Alcatraz.consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(), "consoleDatabasePrepareUnknown", "Unknown error while attempting to prepare database!"));
		}
    }
    
    public void updateDB()
    {
    	//Update("SELECT Arena  FROM sandfall_signs", "ALTER TABLE sandfall_signs ADD Arena VARCHAR;", "ALTER TABLE sandfall_signs ADD Arena varchar(250);" );
    }
    
    public void Update(String check, String sql)
    {
    	Update(check, sql, sql);
    }
    
    public void Update(String check, String sqlite, String mysql)
    {
    	try
    	{
    		Statement statement = getConnection().createStatement();
			statement.executeQuery(check);
			statement.close();
    	}
    	catch(SQLException ex)
    	{
    		try {
    			String[] query;
    			
    			query = sqlite.split(";");
            	Connection conn = getConnection();
    			Statement st = conn.createStatement();
    			for (String q : query)	
    				st.executeUpdate(q);
    			conn.commit();
    			st.close();
    			Alcatraz.log.log(Level.INFO, Alcatraz.consolePrefix + "Database updated to new version!");
    		} 
    		catch (SQLException e)
    		{
    			Alcatraz.log.log(Level.SEVERE, Alcatraz.consolePrefix + "Error while upgrading database to new version!");
                e.printStackTrace();
    		}
    	}
        
	}
    
    public void loadInmates()
    {
    	try
		{
	    	Connection conn;
			PreparedStatement ps = null;
			ResultSet result = null;
			conn = getConnection();
			ps = conn.prepareStatement("SELECT `UUID`, `MinutesIn`, `MinutesLeft`, `Strikes`, `Kills`, `Money`, `Prison` FROM `alcatraz_inmates`");
			result = ps.executeQuery();
			
			int count = 0;
			int removed = 0;
			
			while (result.next())
			{
				int minutesIn = (int)result.getDouble("MinutesIn");
				int minutesLeft = (int)result.getDouble("MinutesLeft");
				int strikes = (int)result.getDouble("Strikes");
				int kills = (int)result.getDouble("Kills");
				double money = result.getDouble("Money");
				
				if (Alcatraz.prisonController.prisonExists(result.getString("Prison")))
				{
					Prison p = Alcatraz.prisonController.getPrison(result.getString("Prison"));
					Inmate i = new Inmate(result.getString("UUID"), minutesIn, minutesLeft, strikes, kills, money, p);
					
					p.getInmateManager().addInmate(i);
					count++;
				}
				else
				{
					//Prison no longer exists
					this.deleteInmate(Bukkit.getOfflinePlayer(UUID.fromString(result.getString("UUID"))));
					//Alcatraz.log.log(Level.INFO, Alcatraz.consolePrefix +"Attempted to load inmate for prison " + result.getString("Prison") + " which no longer exists! Deleting inmate");
					removed++;
				}
			}
			
			//Loaded
			if (count > 0)
			{
				Alcatraz.log.log(Level.INFO, Alcatraz.consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(), "consoleInmatesLoaded", "{0} inmates loaded", count));
			}
			
			//Removed
			if (removed > 0)
			{
				Alcatraz.log.log(Level.INFO, Alcatraz.consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(), "consoleInmatesRemoved", "{0} inmates removed due to deleted prisons", removed));
			}
			
			 conn.commit();
             ps.close();
		}
		catch (SQLException e)
		{
			Alcatraz.log.log(Level.WARNING, Alcatraz.consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(), "consoleInmatesLoadError", "Encountered an issue while attempting to load inmates..."));
		}
    }
    
    public void newInmate(Inmate inmate)
    {
    	try 
    	{
	    	String sql;
			Connection conn = InputOutput.getConnection();
			
			sql = "INSERT INTO alcatraz_inmates (`UUID`, `MinutesIn`, `MinutesLeft`, `Strikes`, `Kills`, `Money`, `Prison`) VALUES (?,?,?,?,?,?,?)";
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			
			
	        preparedStatement.setString(1, inmate.getUUID());
	        preparedStatement.setDouble(2, inmate.getMinutesIn());
	        preparedStatement.setDouble(3, inmate.getMinutesLeft());
	        preparedStatement.setDouble(4, inmate.getStrikes());
	        preparedStatement.setDouble(5, inmate.getKills());
	        preparedStatement.setDouble(6, inmate.getMoney());
	        preparedStatement.setString(7, inmate.getPrison().getName()+"");
	        preparedStatement.executeUpdate();
	        conn.commit();
    	}
    	catch (SQLException e) 
		{
    		Alcatraz.log.log(Level.WARNING, Alcatraz.consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(), "consoleInmateStoreError", "Encountered an error when attempting to store inmate to database..."));
		}
    }
    
    public void updateInmate(Inmate inmate)
    {
    	try 
		{
    		String sql;
    		Connection conn = InputOutput.getConnection();
    		
    		sql = "UPDATE `alcatraz_inmates` SET `MinutesIn` = ?, `MinutesLeft` = ?, `Strikes` = ?, `Kills` = ?, `Money` = ?, `Prison` = ? WHERE `UUID` = ?";
			//update
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
	        preparedStatement.setDouble(1, inmate.getMinutesIn());
	        preparedStatement.setDouble(2, inmate.getMinutesLeft());
	        preparedStatement.setDouble(3, inmate.getStrikes());
	        preparedStatement.setDouble(4, inmate.getKills());
	        preparedStatement.setDouble(5, inmate.getMoney());
	        preparedStatement.setString(6, inmate.getPrison().getName()+"");
	        preparedStatement.setString(7, inmate.getUUID()+"");
	        preparedStatement.executeUpdate();
	        connection.commit();
    		
    		conn.commit();
   		
    		
		} 
		catch (SQLException e) 
		{
			Alcatraz.log.log(Level.WARNING, Alcatraz.consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(), "consoleInmateUpdateError", "Encountered an issue while attempting to update inmate {0}", inmate.getPlayer().getName()));
		}
    }
    
    /**
     * Removes player from Alcatraz DB
     * @param player
     */
    public void deleteInmate(OfflinePlayer player)
    {
    	try 
    	{
			Connection conn = InputOutput.getConnection();
			PreparedStatement ps = conn.prepareStatement("DELETE FROM alcatraz_inmates WHERE UUID = ?");
			ps.setString(1, player.getUniqueId().toString());
			ps.executeUpdate();
			conn.commit();
			
			ps.close();
		} 
    	catch (SQLException e) 
    	{
    		Alcatraz.log.log(Level.WARNING, Alcatraz.consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(), "consoleInmateDeleteError", "Encountered an issue while attempting to delete inmate {0}", player.getName()));
		}
    }
    
    /**
     * Removes inmate from Alcatraz DB
     * @param inmate
     */
    public void deleteInmate(Inmate inmate)
    {
    	deleteInmate(Bukkit.getOfflinePlayer(UUID.fromString(inmate.getUUID())));
    }
    
    public void loadPrisons()
    {
    	try
		{
	    	Connection conn;
			PreparedStatement ps = null;
			ResultSet result = null;
			conn = getConnection();
			ps = conn.prepareStatement("SELECT `Name`, `Max`, `X1`, `Y1`, `Z1`, `X2`, `Y2`, `Z2`, `ReturnX`, `ReturnY`, `ReturnZ`, `StartX`, `StartY`, `StartZ`, `playWorld`, `returnWorld` FROM `alcatraz_prisons`");
			result = ps.executeQuery();
			
			int count = 0;
			while (result.next())
			{
				
				Location startPoint = new Location(Bukkit.getWorld(result.getString("playWorld")), result.getDouble("StartX"),result.getDouble("StartY"),result.getDouble("StartZ"));
				Location returnPoint = new Location(Bukkit.getWorld(result.getString("returnWorld")), result.getDouble("ReturnX"),result.getDouble("ReturnY"),result.getDouble("ReturnZ"));
				
				
				Alcatraz.prisonController.addPrison(new Prison(result.getString("Name"), (int)result.getDouble("Max"), result.getDouble("X1"), result.getDouble("Y1"), result.getDouble("Z1"), result.getDouble("X2"), result.getDouble("Y2"), result.getDouble("Z2"), startPoint, returnPoint));
				Alcatraz.log.log(Level.INFO, Alcatraz.consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(), "consolePrisonLoaded", "Prison [{0}] loaded", result.getString("Name")));
				count++;
			}
			
			if (count > 0)
			{
				Alcatraz.log.log(Level.INFO, Alcatraz.consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(),"consolePrisonsLoaded", "{0} prisons loaded", count));
			}
			
			 conn.commit();
             ps.close();
		}
		catch (SQLException e)
		{
			Alcatraz.log.log(Level.WARNING, Alcatraz.consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(),"consolePrisonsLoadError", "Encountered an issue when attempting to load prisons..."));
		}
    }
    
    public void newPrison(Prison prison)
    {
    	try 
    	{
	    	String sql;
			Connection conn = InputOutput.getConnection();
			
			sql = "INSERT INTO alcatraz_prisons (`Name`, `Max`, `X1`, `Y1`, `Z1`, `X2`, `Y2`, `Z2`, `StartX`, `StartY`, `StartZ`, `ReturnX`, `ReturnY`, `ReturnZ`, `playWorld`, `returnWorld`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			
			
	        preparedStatement.setString(1, prison.getName());
	        preparedStatement.setDouble(2, prison.getMaxInmates());
	        preparedStatement.setDouble(3, prison.getX1());
	        preparedStatement.setDouble(4, prison.getY1());
	        preparedStatement.setDouble(5, prison.getZ1());
	        preparedStatement.setDouble(6, prison.getX2());
	        preparedStatement.setDouble(7, prison.getY2());
	        preparedStatement.setDouble(8, prison.getZ2());
	        preparedStatement.setDouble(9, prison.getStartPoint().getX());
	        preparedStatement.setDouble(10, prison.getStartPoint().getY());
	        preparedStatement.setDouble(11, prison.getStartPoint().getZ());
	        preparedStatement.setDouble(12, prison.getReturnPoint().getX());
	        preparedStatement.setDouble(13, prison.getReturnPoint().getY());
	        preparedStatement.setDouble(14, prison.getReturnPoint().getZ());
	        preparedStatement.setString(15, prison.getStartPoint().getWorld().getName()+"");
	        preparedStatement.setString(16, prison.getReturnPoint().getWorld().getName()+"");
	        preparedStatement.executeUpdate();
	        conn.commit();
	        //Alcatraz.log.log(Level.INFO, Alcatraz.consolePrefix + "Stored prison [" + prison.getName() + "] to database");
    	}
    	catch (SQLException e) 
		{
    		Alcatraz.log.log(Level.WARNING, Alcatraz.consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(),"consolePrisonStoreError", "Encountered an error while attempting to store prison to database"));
			e.printStackTrace();
	    }
    }
    
    public void deletePrison(Prison prison)
    {
    	try 
    	{
			Connection conn = InputOutput.getConnection();
			PreparedStatement ps = conn.prepareStatement("DELETE FROM alcatraz_prisons WHERE Name = ?");
			ps.setString(1, prison.getName()+"");
			ps.executeUpdate();
			conn.commit();
			
			ps.close();
			Alcatraz.log.log(Level.INFO, Alcatraz.consolePrefix + "Deleted prison " + prison.getName());
		} 
    	catch (SQLException e) 
    	{
			Alcatraz.log.log(Level.WARNING, Alcatraz.consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(),"consolePrisonDeleteError", "Encountered an error while attempting to remove prison ") + prison.getName());
		}
    }
    
    @SuppressWarnings("unused")
	public void loadSigns()
    {
    	try
		{
	    	Connection conn;
			PreparedStatement ps = null;
			ResultSet result = null;
			conn = getConnection();
			ps = conn.prepareStatement("SELECT `X`, `Y`, `Z`, `World`, `Prison`, `Type`, `Cell` FROM `alcatraz_signs` ORDER BY `Cell` ASC");
			result = ps.executeQuery();
			
			int count = 0;
			int joinSigns = 0;
			int removed = 0;
			
			while (result.next())
			{
				Location l = new Location(Bukkit.getWorld(result.getString("World")), result.getDouble("X"), result.getDouble("Y"), result.getDouble("Z"));
				
				if (Alcatraz.prisonController.prisonExists(result.getString("Prison")))
				{
					try
					{
						if (l.getBlock().getState().getType() == Material.SIGN || l.getBlock().getState().getType() == Material.WALL_SIGN || l.getBlock().getState().getType() == Material.SIGN_POST)
						{
							if(result.getString("Type").equalsIgnoreCase("cell"))
							{
								Sign s = (Sign)l.getBlock().getState();
								Prison p = Alcatraz.prisonController.getPrison(result.getString("Prison"));
								PrisonCell cell = p.getCellManager().getCell(result.getString("Cell"));
								CellSign sign = new CellSign(p,cell, l);
								cell.addSign(sign);
								sign.update();
								count++;
							} else if(result.getString("Type").equalsIgnoreCase("Join"))
							{
								Sign s = (Sign)l.getBlock().getState();
								Prison p = Alcatraz.prisonController.getPrison(result.getString("Prison"));
								p.getJoinSignManager().addJoinSign(new JoinSign(p, l));
								joinSigns++;
							}
						}
						else
						{
							//Sign is broken / not a sign
							this.removeSign(l.getBlock().getLocation());
							removed++;
						}
						
						
					}
					catch (Exception e)
					{
						removeSign(l);
						removed++;
					}
				}
				else
				{
					//Prison no longer exists, remove it
					removeSign(l);
					removed++;
				}
			}
			
			if (count > 0)
			{
				Alcatraz.log.log(Level.INFO, Alcatraz.consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(),"consolesignsLoaded", "{0} prison signs loaded", count));
			}

			if (joinSigns > 0)
			{
				Alcatraz.log.log(Level.INFO, Alcatraz.consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(),"consoleJoinSignsLoaded", "{0} join signs loaded", joinSigns));
			}

			if (removed > 0)
			{	
				Alcatraz.log.log(Level.INFO, Alcatraz.consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(),"consoleSignsRemoved", "{0} prison signs found broken and removed from database", removed));
			}
			
			 conn.commit();
             ps.close();
		}
		catch (SQLException e)
		{
			Alcatraz.log.log(Level.WARNING, Alcatraz.consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(),"consoleSignLoadError", "Encountered an error while attempting to load prison signs"));
		}
    }
    
    public void newSign(Prison p, Location l, String type, String cellNumber)
    {
    	try 
    	{
	    	String sql;
			Connection conn = InputOutput.getConnection();
			
			sql = "INSERT INTO alcatraz_signs (`X`, `Y`, `Z`, `World`, `Prison`, `Type`, `Cell`) VALUES (?,?,?,?,?,?,?)";
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			
			
	        preparedStatement.setDouble(1, l.getX());
	        preparedStatement.setDouble(2, l.getY());
	        preparedStatement.setDouble(3, l.getZ());
	        preparedStatement.setString(4, l.getWorld().getName());
	        preparedStatement.setString(5, p.getName());
	        preparedStatement.setString(6, type);
	        preparedStatement.setString(7, cellNumber);
	        
	        preparedStatement.executeUpdate();
	        conn.commit();
	        //Alcatraz.log.log(Level.INFO, Alcatraz.consolePrefix + "Stored sign in " + p.getName() + " as a " + type + " sign");
    	}
    	catch (SQLException e) 
		{
    		Alcatraz.log.log(Level.WARNING, Alcatraz.consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(),"consoleSignStore", "Encountered an error while attempting to store sign to database"));
			e.printStackTrace();
	    }
    }
    
    public void newCellSign(CellSign sign)
    {
    	newSign(sign.getPrison(), sign.getSign().getLocation(), "Cell", sign.getCell().getCellNumber());
    }
    
    public void removeSign(Location l)
    {
    	try 
    	{
			Connection conn = InputOutput.getConnection();
			PreparedStatement ps = conn.prepareStatement("DELETE FROM alcatraz_signs WHERE World = ? AND X = ? AND Y = ? AND Z = ?");
			ps.setString(1, l.getWorld().getName());
			ps.setDouble(2, l.getX());
			ps.setDouble(3, l.getY());
			ps.setDouble(4, l.getZ());
			ps.executeUpdate();
			conn.commit();
			
			ps.close();
		
		} 
    	catch (SQLException e) 
    	{
			Alcatraz.log.log(Level.WARNING, Alcatraz.consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(),"consoleSignRemoveError", "Encountered an error while trying to remove sign from database"));
		}
    }  
    
    public void loadChests()
    {
    	try
		{
	    	Connection conn;
			PreparedStatement ps = null;
			ResultSet result = null;
			conn = getConnection();
			ps = conn.prepareStatement("SELECT `X`, `Y`, `Z`, `World`, `Prison`, `Type`, `Cell` FROM `alcatraz_chests`");
			result = ps.executeQuery();
			
			int food = 0;
			int cell = 0;
			int random = 0;
			int removed = 0; //broken and deleted count
			
			while (result.next())
			{
				Location l = new Location(Bukkit.getWorld(result.getString("World")), result.getDouble("X"),result.getDouble("Y"),result.getDouble("Z"));
				
				if (Alcatraz.prisonController.prisonExists(result.getString("Prison")))
				{
					Prison p = Alcatraz.prisonController.getPrison(result.getString("Prison"));
					
					if (l.getBlock().getState().getType() == Material.CHEST || l.getBlock().getState().getType() == Material.TRAPPED_CHEST)
					{
						if (result.getString("Type").equalsIgnoreCase("Food"))
						{
							p.getChestManager().addFoodChest(l);
							food++;
						}
						else if (result.getString("Type").equalsIgnoreCase("Random"))
						{
							p.getChestManager().addRandomChest(l);
							random++;
						}
						else if (result.getString("Type").equalsIgnoreCase("Reward"))
						{
							p.getChestManager().addRewardChest(l);
							random++;
						}
						else if (result.getString("Type").equalsIgnoreCase("Cell"))
						{
							p.getCellManager().getCell(result.getString("Cell")).setChest(l);
							cell++;
						}
					}
					else
					{
						//The chest is broken
						removeChest(l);
						removed++;
					}
				}
				else
				{
					//Prison no longer exists, remove it
					removeChest(l);
					removed++;
				}
			}
			
			if (food > 0)
			{
				Alcatraz.log.log(Level.INFO, Alcatraz.consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(),"consoleChestLoadFood", "{0} food chests loaded", food));
			}
			
			if (cell > 0)
			{
				Alcatraz.log.log(Level.INFO, Alcatraz.consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(),"consoleChestLoadCell", "{0} cell chests loaded", cell));
			}
			
			if (random > 0)
			{
				Alcatraz.log.log(Level.INFO, Alcatraz.consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(),"consoleChestLoadRandom", "{0} random chests loaded", random));
			}
			
			if (removed > 0)
			{
				Alcatraz.log.log(Level.INFO, Alcatraz.consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(),"consoleChestLoadRemoved", "{0} chests found broken and removed", removed));
			}
			
		
			conn.commit();
            ps.close();
		}
		catch (SQLException e)
		{
			Alcatraz.log.log(Level.WARNING, Alcatraz.consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(), "consoleChestLoadError", "Error when attempting to load chests..."));
		}
    }
    
    public void newChest(Location l, String type, Prison p, int cellNum)
    {
    	try 
    	{
	    	String sql;
			Connection conn = InputOutput.getConnection();
			
			sql = "INSERT INTO alcatraz_chests (`X`, `Y`, `Z`, `World`, `Prison`, `Type`, `Cell`) VALUES (?,?,?,?,?,?,?)";
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			
			
	        preparedStatement.setDouble(1, l.getX());
	        preparedStatement.setDouble(2, l.getY());
	        preparedStatement.setDouble(3, l.getZ());
	        preparedStatement.setString(4, l.getWorld().getName());
	        preparedStatement.setString(5, p.getName());
	        preparedStatement.setString(6, type);
	        preparedStatement.setString(7, Integer.toString(cellNum));
	        
	        preparedStatement.executeUpdate();
	        conn.commit();
	        //Alcatraz.log.log(Level.INFO, Alcatraz.consolePrefix + "Stored chest in " + p.getName() + " as a " + type + " chest");
    	}
    	catch (SQLException e) 
		{
    		Alcatraz.log.log(Level.WARNING, Alcatraz.consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(), "consoleChestStoreError", "Error while attempting to store chest...") + e.getMessage());
			e.printStackTrace();
	    }
    }
    
    public void newChest(Location l, String type, Prison p)
    {
    	newChest(l,type,p, 0);
    }
    
    public void removeChest(Location l)
    {
    	try 
    	{
			Connection conn = InputOutput.getConnection();
			PreparedStatement ps = conn.prepareStatement("DELETE FROM alcatraz_chests WHERE World = ? AND X = ? AND Y = ? AND Z = ?");
			ps.setString(1, l.getWorld().getName());
			ps.setDouble(2, l.getX());
			ps.setDouble(3, l.getY());
			ps.setDouble(4, l.getZ());
			ps.executeUpdate();
			conn.commit();
			
			ps.close();
		
		} 
    	catch (SQLException e) 
    	{
			Alcatraz.log.log(Level.WARNING, Alcatraz.consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(), "consoleRemoveChestError", "Error while removing chest...") + e.getMessage());
		}
    }

    public void newCell(Prison p, String cellNumber)
    {
    	try 
    	{
	    	String sql;
			Connection conn = InputOutput.getConnection();
			
			sql = "INSERT INTO alcatraz_cells (`Prison`, `CellNumber`) VALUES (?,?)";
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			
			
	        preparedStatement.setString(1, p.getName());
	        preparedStatement.setString(2, cellNumber);
	  
	        
	        preparedStatement.executeUpdate();
	        conn.commit();
	        //Alcatraz.log.log(Level.INFO, Alcatraz.consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(), "consoleCellStored", "Cell stored in {0}", p.getName()));
    	}
    	catch (SQLException e) 
		{
    		Alcatraz.log.log(Level.WARNING, Alcatraz.consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(), "consoleCellStoredError", "Error while attempting to store cell...") + e.getMessage());
			e.printStackTrace();
	    }
    }
    
    public void loadCells()
    {
    	try
		{
	    	Connection conn;
			PreparedStatement ps = null;
			ResultSet result = null;
			conn = getConnection();
			ps = conn.prepareStatement("SELECT `Prison`, `CellNumber`, `InmateUUID` FROM `alcatraz_cells`");
			result = ps.executeQuery();
			int cells = 0;
			int removed = 0;
			
			while (result.next())
			{
				cells++; //increment
				
				
				if (Alcatraz.prisonController.prisonExists(result.getString("prison")))
				{
					Prison p = Alcatraz.prisonController.getPrison(result.getString("Prison"));
					PrisonCell cell = new PrisonCell(p, result.getString("CellNumber"));
					p.getCellManager().addCell(cell);	
					
					if (p.getInmateManager().inmateExists(result.getString("InmateUUID")))
					{
						Inmate i = p.getInmateManager().getInmate(result.getString("InmateUUID"));
						i.setCell(cell);
						cell.setInmate(i);
					}
				}
				else
				{
					//Prison no longer exists
					this.deleteCell(result.getString("Prison"), result.getString("CellNumber"));
					removed++;
				}
			}
			
			if (cells > 0)
			{
				Alcatraz.log.log(Level.INFO, Alcatraz.consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(), "consoleCellsLoaded", "{0} cells loaded", cells));
			}
			
			if (removed > 0)
			{
				Alcatraz.log.log(Level.INFO, Alcatraz.consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(), "consoleCellsRemoved", "{0} cells of deleted prisons removed", cells));
			}
			
			
			conn.commit();
            ps.close();
		}
		catch (SQLException e)
		{
			Alcatraz.log.log(Level.WARNING, Alcatraz.consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(), "consoleLoadPrisonCellError", "Error while loading prison cell") + e.getMessage());
		}
    }
    
    public void updateCell(PrisonCell c)
    {
    	try 
		{
    		String sql;
    		Connection conn = InputOutput.getConnection();
    		
    		sql = "UPDATE `alcatraz_cells` SET `InmateUUID` = ? WHERE `Prison` = ? AND `CellNumber` = ?";
			//update
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			
			if (c.isOccupied())
			{
				preparedStatement.setString(1, c.getInmate().getUUID()+"");
			}
			else
			{
				preparedStatement.setString(1, "");
			}
	        preparedStatement.setString(2, c.getPrison().getName()+"");
	        preparedStatement.setString(3, c.getCellNumber()+"");
	      
	        preparedStatement.executeUpdate();
	        connection.commit();
    		
    		conn.commit();
    		
		} 
		catch (SQLException e) 
		{
			Alcatraz.log.log(Level.WARNING, Alcatraz.consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(), "consoleUpdatePrisonCellError", "Error while attempting to update prison cell") + e.getMessage());
		}
    }

    public void deleteCell(PrisonCell c)
    {
    	try 
    	{
			Connection conn = InputOutput.getConnection();
			PreparedStatement ps = conn.prepareStatement("DELETE FROM alcatraz_cells WHERE Prison = ? AND CellNumber = ?");
			ps.setString(1, c.getPrison().getName()+"");
			ps.setString(2, c.getCellNumber()+"");
			ps.executeUpdate();
			conn.commit();
			
			ps.close();
		} 
    	catch (SQLException e) 
    	{
			Alcatraz.log.log(Level.WARNING, Alcatraz.consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(), "consoleRemovePrisonCellError", "Error while removing prison cell") + e.getMessage() );
		}
    }
    
    public void deleteCell(String prison, String cellNumber)
    {
    	try 
    	{
			Connection conn = InputOutput.getConnection();
			PreparedStatement ps = conn.prepareStatement("DELETE FROM alcatraz_cells WHERE Prison = ? AND CellNumber = ?");
			ps.setString(1, prison+"");
			ps.setString(2,	cellNumber+"");
			ps.executeUpdate();
			conn.commit();
			
			ps.close();
		} 
    	catch (SQLException e) 
    	{
			Alcatraz.log.log(Level.WARNING, Alcatraz.consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(), "consoleRemovePrisonCellError", "Error while removing prison cell") + e.getMessage() );
		}
    }

}
