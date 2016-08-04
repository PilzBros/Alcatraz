package com.pilzbros.Alcatraz;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import com.pilzbros.Alcatraz.Runnable.ChestGenerator;
import com.pilzbros.Alcatraz.Runnable.InmateUpdate;
import com.pilzbros.Alcatraz.Runnable.LocationCheck;
import com.pilzbros.Alcatraz.Runnable.MealService;
import com.pilzbros.Alcatraz.Runnable.MinuteCounter;
import com.pilzbros.Alcatraz.Runnable.MoneyDeposit;
import com.pilzbros.Alcatraz.Runnable.PrisonCheck;
import com.pilzbros.Alcatraz.Runnable.ScoreboardTask;
import com.pilzbros.Alcatraz.Runnable.SignUpdate;

public class Alcatraz extends JavaPlugin implements Listener 
{
	public static final String pluginName = "Alcatraz";
	public static final String pluginVersion = "1.5";
	public static final String pluginPrefix = ChatColor.GOLD + "[Alcatraz] " + ChatColor.WHITE;
	public static final String pluginAdminPrefix = ChatColor.GOLD + "[Alcatraz Admin] " + ChatColor.WHITE;
	public static final String signPrefix = "[Alcatraz]";
	public final static String consolePrefix = "[Alcatraz] ";
	
	public final static String pluginURL = "http://dev.bukkit.org/bukkit-plugins/alcatraz/";
	public static final Logger log = Logger.getLogger("Minecraft");
	public static boolean updateNeeded;
	public static Economy econ = null;
	public static LanguageWrapper language;
	
	public static RecipeManager recipeManager;
	public static PrisonController prisonController;
	public static Alcatraz instance;
	public static InputOutput IO;
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
	
	@SuppressWarnings({ "deprecation", "unused" })
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
		Alcatraz.IO.loadPrisons();
		Alcatraz.IO.loadInmates();
		Alcatraz.IO.loadCells();
		Alcatraz.IO.loadChests();
		Alcatraz.IO.loadSigns();
		
		//Check Prisoners (in case of reload)
		Alcatraz.prisonController.checkPlayerReload();
		
		//Listeners
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		getServer().getPluginManager().registerEvents(new BlockListener(), this);
		
		//Commands
		getCommand("alcatraz").setExecutor(new UserCommand());
		getCommand("alc").setExecutor(new UserCommand());
		getCommand("alcatrazadmin").setExecutor(new AdminCommand());
		getCommand("alca").setExecutor(new AdminCommand());
		
		//Schedule Tasks
		BukkitTask managerTask = Bukkit.getScheduler().runTaskTimer(this, new MinuteCounter(), 60, 60); //Inmate minute counter
		BukkitTask scoreboardTask = Bukkit.getScheduler().runTaskTimer(this, new ScoreboardTask(), 40, 40); //Update inmate scoreboards
		BukkitTask mealService = Bukkit.getScheduler().runTaskTimer(this, new MealService(), 60, 60); //Meal Time
		BukkitTask inmateUpdate = Bukkit.getScheduler().runTaskTimer(this, new InmateUpdate(), 600, 600); //Updating inmate information
		BukkitTask signUpdate = Bukkit.getScheduler().runTaskTimer(this, new SignUpdate(), 80, 80); //Updating Cell Signs
		BukkitTask chestGenerator = Bukkit.getScheduler().runTaskTimer(this, new ChestGenerator(), 60, 60); //Refreshing reward chests
		BukkitTask prisonCheck = Bukkit.getScheduler().runTaskTimer(this, new PrisonCheck(), 200, 200); //10 sec
		BukkitTask moneyDeposit = Bukkit.getScheduler().runTaskTimer(this, new MoneyDeposit(), 100, 100); //Sync Vault balance
		BukkitTask locationCheck = Bukkit.getScheduler().runTaskTimer(this, new LocationCheck(), 100, 100); //Check inmate locations
		
		//In Signs Plus
		Plugin insignsplus = getServer().getPluginManager().getPlugin("InSignsPlus");
		if ((insignsplus!=null) && insignsplus.isEnabled()) 
		{
		        this.ISP = (InSignsPlus) insignsplus;
		        ISS iss = new ISS();
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
	       log.log(Level.WARNING, consolePrefix + Alcatraz.language.get(Bukkit.getConsoleSender(), "consoleMetricsSubmitted", "Encountered an error while attempting to submit metrics!"));
	    }
		
		//Log bootup time
		log.log(Level.INFO, Alcatraz.consolePrefix + language.get(Bukkit.getConsoleSender(), "consolePluginEnabled", "Bootup took {0} ms", (System.currentTimeMillis() % 1000 - startMili)));
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
