package com.pilzbros.Alcatraz.Manager;

import java.util.HashMap;

import com.pilzbros.Alcatraz.Objects.Inmate;
import com.pilzbros.Alcatraz.Objects.Prison;

public class PrisonManager 
{
	private Prison prison;
	public HashMap<String, Inmate> inmates;
	
	public PrisonManager(Prison p)
	{
		this.prison = p;
		this.inmates = new HashMap<String, Inmate>();
	}
	
	public void autoCheck()
	{
		//
	}

	
}
