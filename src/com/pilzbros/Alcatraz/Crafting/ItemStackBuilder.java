package com.pilzbros.Alcatraz.Crafting;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;

public class ItemStackBuilder {
	/**
	 * @author Plo457
	 */
	ItemStack result;
	ItemMeta resultMeta;
	public ItemStackBuilder(Material result){
		this.result = new ItemStack(result);
		this.resultMeta = this.result.getItemMeta();
	}
	public ItemStackBuilder(ItemStack result){
		this.result = result;
		this.resultMeta = this.result.getItemMeta();
	}
	public ItemStack build(){
		this.result.setItemMeta(this.resultMeta);
		return this.result;
	}
	public ItemStackBuilder setDisplayName(String s){
		
		this.resultMeta.setDisplayName(s);
		return this;
	}
	public ItemStackBuilder addLore(String s){
		List<String> lore = new ArrayList<String>();
		if (this.resultMeta.hasLore()){
			lore = this.resultMeta.getLore();
		}
		lore.add(s);
		this.resultMeta.setLore(lore);
		return this;
	}
	public ItemStackBuilder setLore(List<String> lore){
		this.resultMeta.setLore(lore);
		return this;
	}
	public ItemStackBuilder addEnchantment(Enchantment enchantment,int level){
		this.resultMeta.addEnchant(enchantment,level,true);
		return this;
	}
	public ItemStackBuilder setArmorColor(Color color){
		LeatherArmorMeta meta = (LeatherArmorMeta) this.resultMeta;
		meta.setColor(color);
		return this;
	}
	public ItemStackBuilder setAuthor(String name){
		BookMeta meta = (BookMeta) this.resultMeta;
		meta.setAuthor(name);
		return this;
	}
	public ItemStackBuilder setTitle(String name){
		BookMeta meta = (BookMeta) this.resultMeta;
		meta.setTitle(name);
		return this;
	}
	public ItemStackBuilder setPage(int page,String text){
		BookMeta meta = (BookMeta) this.resultMeta;
		meta.setPage(page,text);
		return this;
	}
	public ItemStackBuilder addPotionEffect(PotionEffect effect){
		PotionMeta meta = (PotionMeta)this.resultMeta;
		meta.addCustomEffect(effect,true);
		return this;
	}
	public ItemStackBuilder setSkullOwner(String name){
		SkullMeta meta = (SkullMeta) this.resultMeta;
		meta.setOwner(name);
		return this;
	}
	public ItemStackBuilder setDamage(int dmg){
		short dm = (short) dmg;
		this.result.setDurability(dm);
		return this;
	}
	public ItemStackBuilder setAmount(int amount){
		this.result.setAmount(amount);
		return this;
	}
}
