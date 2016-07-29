package com.pilzbros.Alcatraz.Plugins;

import io.puharesource.mc.titlemanager.api.ActionbarTitleObject;
import io.puharesource.mc.titlemanager.api.TitleObject;

import org.bukkit.entity.Player;

public class TitleManagerAPI 
{
	public void sendMessage(Player player, String title, String subtitle)
	{
		new TitleObject(title, subtitle).setFadeIn(5).setStay(60).setFadeOut(5).send(player);
	}
	
	public void actionBar(Player player)
	{
		new ActionbarTitleObject("Test!").send(player);
	
	}

}
