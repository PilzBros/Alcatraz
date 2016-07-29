package com.pilzbros.Alcatraz.IO;

public enum Setting {

	//Update
	CheckForUpdates("CheckForUpdates", true),
	NotifyOnNewUpdates("NotifyOnNewUpdates", true),
	ReportMetrics("MetricReporting",true),
	NotifyOnAustinPilz("NotifyOnPluginCreatorJoin", true),
	
	//Setup
	SelectionTool("Setup.SelectionTool", 284),
	
	//Prison Settings
	DefaultMaxInmates("Prisons.DefaultMaxInmates", 30),
	InteractKills("Prisons.KillsToAllowInteract", 3),
	RegenerateBlocks("Prisons.RegenerateAfterBrokenBlocks", 100),
	
	//Cost
	MinuteCost("Prices.Minute", 15),
	StrikeCost("Prices.Strike", 500),
	
	//Player Settings
	DefaultMoney("Inmates.DefaultMoney", 500),
	MaxStrikes("Inmates.MaxStrikes", 3),
	DefaultTime("Inmates.DefaultMinutesUntilExecution", 90),
	ClearInventory("Inmates.ClearInventoryOnStart", true);
	
	
	
	
	private String name;
	private Object def;
	
	private Setting(String Name, Object Def)
	{
		name = Name;
		def = Def;
	}
	
	public String getString()
	{
		return name;
	}
	
	public Object getDefault()
	{
		return def;
	}
}

