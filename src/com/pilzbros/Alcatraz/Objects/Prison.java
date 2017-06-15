package com.pilzbros.Alcatraz.Objects;

import com.pilzbros.Alcatraz.Manager.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.pilzbros.Alcatraz.Alcatraz;

import java.util.Arrays;

public class Prison 
{
	private String prisonName;
	
	private int maxInmates;
	
	private double x1;
	private double y1;
	private double z1;
	
	private double x2;
	private double y2;
	private double z2;
	
	private Location startPoint;
	private Location returnPoint;

	private final InmateManager inmateManager;
	private final ChestManager chestManager;
	private final CellManager cellManager;
	private final MiningManager miningManager;
	private final JoinSignManager joinSignManager;
	
	public static long lastRaid;
	
	public Prison(String name, int max, double px1, double py1, double pz1, double px2, double py2, double pz2, double pstartx, double pstarty, double pstartz, double preturnx, double preturny, double preturnz, String pWorld, String rWorld)
	{
		this.prisonName = name;
		this.maxInmates = max;
		this.x1 = pz1;
		this.y1 = py1;
		this.z1 = pz1;
		this.x2 = px2;
		this.y2 = py2;
		this.z2 = pz2;
		this.startPoint = new Location (Bukkit.getWorld(pWorld), pstartx, pstarty, pstartz);
		this.returnPoint = new Location (Bukkit.getWorld(rWorld), preturnx, preturny, preturnz);
		this.inmateManager = new InmateManager(this);
		this.chestManager = new ChestManager(this);
		Prison.lastRaid = 0;
		this.cellManager = new CellManager(this);
		this.miningManager = new MiningManager(this);
		this.joinSignManager = new JoinSignManager(this);
	}
	
	public Prison(String name, int max, double px1, double py1, double pz1, double px2, double py2, double pz2, Location pstart, Location preturn)
	{
		this.prisonName = name;
		this.maxInmates = max;
		this.x1 = pz1;
		this.y1 = py1;
		this.z1 = pz1;
		this.x2 = px2;
		this.y2 = py2;
		this.z2 = pz2;
		this.startPoint = pstart;
		this.returnPoint = preturn;
		this.inmateManager = new InmateManager(this);
		this.chestManager = new ChestManager(this);
		this.cellManager = new CellManager(this);
		this.miningManager = new MiningManager(this);
		this.joinSignManager = new JoinSignManager(this);
		Prison.lastRaid = 0;
	}
	
	/**
	 * Returns the prison's name
	 */
	public String getName()
	{
		return this.prisonName;
	}

	/**
	 * Return the maximum number of inmates allowed in the prison
	 * @return
	 */
	public int getMaxInmates()
	{
		return this.maxInmates;
	}

	/**
	 * Sets the maximum number of inmates allowed in a prison
	 * @param max
	 */
	public void setMaxInmates(int max)
	{
		this.maxInmates = max;
		//TODO - add DB updateInDatabase function
	}

	public double getX1() 
	{
		return x1;
	}

	public double getY1() {
		return y1;
	}

	public double getZ1() {
		return z1;
	}
	
	public double getX2() {
		return x2;
	}

	public double getY2() {
		return y2;
	}

	public double getZ2() {
		return z2;
	}

	public Location getStartPoint()
	{
		return this.startPoint;
	}
	
	public Location getReturnPoint()
	{
		return this.returnPoint;
	}
	
	public InmateManager getInmateManager()
	{
		return this.inmateManager;
	}
	
	public ChestManager getChestManager()
	{
		return this.chestManager;
	}
	
	public CellManager getCellManager()
	{
		return this.cellManager;
	}
	
	public MiningManager getMiningManager()
	{
		return this.miningManager;
	}

	public JoinSignManager getJoinSignManager() {
		return joinSignManager;
	}

	/**
	 * Actions for a prison performed on a regular basis
	 */
	public void autoCheck()
	{
		miningManager.autoCheck();
		joinSignManager.autoCheck();
	}

	/**
	 * Returns if the supplied location is within the prison boundaries
	 * @param inQuestion
	 * @return
	 */
	public boolean isLocationWithinPrisonBoundaries(Location inQuestion)
	{
		double[] dim = new double[2];

		dim[0] = getX1();
		dim[1] = getX2();
		Arrays.sort(dim);
		if(inQuestion.getX() > dim[1] || inQuestion.getX() < dim[0])
			return false;

		dim[0] = getY1();
		dim[1] = getY2();
		Arrays.sort(dim);
		if(inQuestion.getY() > dim[1] || inQuestion.getY() < dim[0])
			return false;

		dim[0] = getZ1();
		dim[1] = getZ2();
		Arrays.sort(dim);
		if(inQuestion.getZ() > dim[1] || inQuestion.getZ() < dim[0])
			return false;

		return true;
	}
	
	/**
	 * Replenishes meal chests and notifies active inmates
	 */
	public void mealTime()
	{
		chestManager.regenerateFoodChests();
	}

	/**
	 * Performs permanent prison shutdown actions
	 */
	public void shutdownActions()
	{
		miningManager.forceRegenerate();
		inmateManager.updateInmates();
		cellManager.updateCells();
	}
	
	/**
	 * Delete's prison from Alcatraz and DB
	 */
	public void deletePrison()
	{
		shutdownActions();
		inmateManager.releaseAllInmates();
		joinSignManager.markSignsDeleted();
		Alcatraz.IO.deletePrison(this);
		Alcatraz.prisonController.removePrison(this);

	}
}
