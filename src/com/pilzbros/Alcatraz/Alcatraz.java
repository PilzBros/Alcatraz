package com.pilzbros.Alcatraz;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pilzbros.Alcatraz.IO.SpigotUpdateChecker;
import com.pilzbros.Alcatraz.Runnable.*;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import com.empcraft.InSignsPlus;
import com.pilzbros.Alcatraz.Command.AdminCommand;
import com.pilzbros.Alcatraz.Command.UserCommand;
import com.pilzbros.Alcatraz.Controller.PrisonController;
import com.pilzbros.Alcatraz.IO.InputOutput;
import com.pilzbros.Alcatraz.Listener.BlockListener;
import com.pilzbros.Alcatraz.Listener.PlayerListener;
import com.pilzbros.Alcatraz.Manager.RecipeManager;
import com.pilzbros.Alcatraz.Plugins.ISS;
import com.pilzbros.Alcatraz.Plugins.InventoryActions;
import com.pilzbros.Alcatraz.Plugins.LanguageWrapper;
import com.pilzbros.Alcatraz.Plugins.MetricsLite;
import com.pilzbros.Alcatraz.Plugins.TitleManagerAPI;

public class Alcatraz extends JavaPlugin implements Listener 
{
	public static final String pluginName = "Alcatraz";
	public static final String pluginVersion = "1.6.4";
	public static final String pluginPrefix = ChatColor.GOLD + "[Alcatraz] " + ChatColor.WHITE;
	public static final String pluginAdminPrefix = ChatColor.GOLD + "[Alcatraz Admin] " + ChatColor.WHITE;
	public static final String signPrefix = "[Alcatraz]";
	public final static String consolePrefix = "[Alcatraz] ";
	
	public final static String pluginURL = "http://dev.bukkit.org/bukkit-plugins/alcatraz/";
	public static final Logger log = Logger.getLogger("Minecraft");
	public static Economy econ = null;
	public static LanguageWrapper language;
	
	public static RecipeManager recipeManager;
	public static PrisonController prisonController;
	public static Alcatraz instance;
	public static InputOutput IO;
	public static SpigotUpdateChecker updateChecker;
	public static BukkitScheduler scheduler;
	public static InventoryActions inventoryActions;
	public static InSignsPlus ISP;
	public static TitleManagerAPI titleManagerAPI;
	
	public static long lastMinuteCheck;
	public static long lastMealServiceCheck;
	public static long lastInmateUpdate;
	public static long lastSignUpdate;
	public static long lastRandomChest;
	
	@Override
	public void onLoad() 
	{
		//
	}

	@Override
	public void onEnable()
	{
		long startMili = System.currentTimeMillis() % 1000;
		
		//Language 
		language = new LanguageWrapper(this, "eng");
		
		//Vars
		this.lastMinuteCheck = 0;
		this.lastMealServiceCheck = 0;
		this.lastInmateUpdate = 0;
		this.lastSignUpdate = 0;
		this.lastRandomChest = 0;
		
		//Instance
		Alcatraz.instance = this;
		Alcatraz.scheduler = Bukkit.getServer().getScheduler();
		
		//Managers
		this.recipeManager = new RecipeManager();
		this.recipeManager.loadRecipes();
		
		//Controllers
		Alcatraz.prisonController = new PrisonController();
		
		//Dependencies
		this.inventoryActions = new InventoryActions();
		this.setupVault();
		this.ISP = null;
		this.titleManagerAPI = new TitleManagerAPI();
		
		//IO
		Alcatraz.IO = new InputOutput();
		
		//Load Database
		Alcatraz.IO.LoadSettings();
		Alcatraz.IO.prepareDB();
		Alcatraz.IO.updateDB();
		Alcatraz.IO.loadPrisons();
		Alcatraz.IO.loadInmates();
		Alcatraz.IO.loadCells();
		Alcatraz.IO.loadChests();
		Alcatraz.IO.loadSigns();
		
		//Listeners
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		getServer().getPluginManager().registerEvents(new BlockListener(), this);
		
		//Commands
		getCommand("alcatraz").setExecutor(new UserCommand());
		getCommand("alc").setExecutor(new UserCommand());
		getCommand("alcatrazadmin").setExecutor(new AdminCommand());
		getCommand("alca").setExecutor(new AdminCommand());
		
		//Schedule Tasks (20 ticks = 1 second)
		int prisonMinuteCounterTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new MinuteCounter(), 20, 300);
		int prisonMealServiceTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new MealService(), 20, 180000);
		int prisonChestRegerationTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new ChestGenerator(), 20, 75600);
		int prisonAutoCheckTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new PrisonCheck(), 20, 6000);
		int prisonCellSignUpdateTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new SignUpdate(), 20, 3000);
		int inmateScoreboardTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new ScoreboardTask(), 20, 300);
		int inmateDatabaseUpdateTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new InmateCellUpdate(), 12000, 72000);
		
		//In Signs Plus
		Plugin insignsplus = getServer().getPluginManager().getPlugin("InSignsPlus");
		if ((insignsplus!=null) && insignsplus.isEnabled()) 
		{
		        this.ISP = (InSignsPlus) insignsplus;
		        ISS iss = new ISS();
		        iss.addPlaceholders(); //Add all placeholders
				log.log(Level.INFO, consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(), "consoleISPHooked", "InSignsPlus detected and hooked"));
		}
		else
		{
			log.log(Level.INFO, consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(), "consoleISP404", "InSignsPlus not found"));
		}
		
		//Metrics
		try 
		{
	        MetricsLite metrics = new MetricsLite(this);
	        metrics.start();
	        log.log(Level.INFO, consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(), "consoleMetricsSubmitted", "Metrics submitted!"));
	    } 
		catch (IOException e) 
		{
	       log.log(Level.WARNING, consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(), "consoleMetricsSubmitted", "Encountered an error while attempting to submit metrics"));
	    }

	    //Check for Update
		Alcatraz.updateChecker = new SpigotUpdateChecker();
		try {
			Alcatraz.updateChecker.checkUpdate(Alcatraz.pluginVersion);
			log.log(Level.INFO, Alcatraz.consolePrefix + language.get(Bukkit.getConsoleSender(), "consolePluginUpdateChecked", "Checked for update. Current version: v{0} - Newest Version: v{1}", Alcatraz.pluginVersion, Alcatraz.updateChecker.getLatestVersion()));
		} catch (Exception e) {
			log.log(Level.INFO, Alcatraz.consolePrefix + language.get(Bukkit.getConsoleSender(), "consolePluginUpdateFailed", "Update check failed!"));
			e.printStackTrace();
		}

		//Check Prisoners (in case of reload)
		Alcatraz.prisonController.checkPlayerReload();


		//Log boot time
		log.log(Level.INFO, Alcatraz.consolePrefix + language.get(Bukkit.getConsoleSender(), "consolePluginEnabled", "Bootup took {0} ms", (System.currentTimeMillis() - startMili) % 1000));
	}
	
	
	@Override
	public void onDisable()
	{
		Alcatraz.prisonController.shutdownActions();
		
		//Log disable
		log.log(Level.INFO, language.get(Bukkit.getConsoleSender(), "consolePluginDisabled", "Disabling {0} v{1}", Alcatraz.pluginName, Alcatraz.pluginVersion));
	}
	
	private void setupVault()
	{
		if (!setupEconomy())
		{
			//Vault not found
			log.log(Level.SEVERE, Alcatraz.consolePrefix + language.get(Bukkit.getConsoleSender(), "consoleBootupHaulted", "Vault was not found, bootup haulted!"));
	        getServer().getPluginManager().disablePlugin(this);
		}
		else
		{
			//Vault hooked
			log.log(Level.INFO, Alcatraz.consolePrefix + language.get(Bukkit.getConsoleSender(), "consoleVaultHooked", "Vault hooked"));
		}
	}
	
	private boolean setupEconomy() 
	{
        if (getServer().getPluginManager().getPlugin("Vault") == null) 
        {
            return false;
        }
        
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) 
        {
            return false;
        }
        
        econ = rsp.getProvider();
        
        return econ != null;
    }

	
}
