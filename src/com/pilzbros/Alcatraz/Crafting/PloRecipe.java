package com.pilzbros.Alcatraz.Crafting;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class PloRecipe {
	/**
	 * @author Plo457
	 */
	private ItemStack output;
	private ItemStack slot1;
	private ItemStack slot2;
	private ItemStack slot3;
	private ItemStack slot4;
	private ItemStack slot5;
	private ItemStack slot6;
	private ItemStack slot7;
	private ItemStack slot8;
	private ItemStack slot9;
	public PloRecipe(ItemStack output){
		this.output = output;
		this.slot1 = new ItemStack(Material.AIR);
		this.slot2 = new ItemStack(Material.AIR);
		this.slot3 = new ItemStack(Material.AIR);
		this.slot4 = new ItemStack(Material.AIR);
		this.slot5 = new ItemStack(Material.AIR);
		this.slot6 = new ItemStack(Material.AIR);
		this.slot7 = new ItemStack(Material.AIR);
		this.slot8 = new ItemStack(Material.AIR);
		this.slot9 = new ItemStack(Material.AIR);
	}
	public ItemStack getOutput(){
		return this.output;
	}
	public void register(){
		ShapedRecipe rec = new ShapedRecipe(output);
		rec.shape("ABC","DEF","GHI");
		if (slot1.getType() != Material.AIR)
		rec.setIngredient('A',slot1.getType());
		if (slot2.getType() != Material.AIR)
		rec.setIngredient('B',slot2.getType());
		if (slot3.getType() != Material.AIR)
		rec.setIngredient('C',slot3.getType());
		if (slot4.getType() != Material.AIR)
		rec.setIngredient('D',slot4.getType());
		if (slot5.getType() != Material.AIR)
		rec.setIngredient('E',slot5.getType());
		if (slot6.getType() != Material.AIR)
		rec.setIngredient('F',slot6.getType());
		if (slot7.getType() != Material.AIR)
		rec.setIngredient('G',slot7.getType());
		if (slot8.getType() != Material.AIR)
		rec.setIngredient('H',slot8.getType());
		if (slot9.getType() != Material.AIR)
		rec.setIngredient('I',slot9.getType());
		Bukkit.addRecipe(rec);
		PloRecipeList.addShapedRecipe(this);
	}
	public PloRecipe setSlot1(ItemStack i){
		i.setAmount(1);
		this.slot1 = i;
		return this;
	}
	public PloRecipe setSlot2(ItemStack i){
		i.setAmount(1);
		this.slot2 = i;
		return this;
	}
	public PloRecipe setSlot3(ItemStack i){
		i.setAmount(1);
		this.slot3 = i;
		return this;
	}
	public PloRecipe setSlot4(ItemStack i){
		i.setAmount(1);
		this.slot4 = i;
		return this;
	}
	public PloRecipe setSlot5(ItemStack i){
		i.setAmount(1);
		this.slot5 = i;
		return this;
	}
	public PloRecipe setSlot6(ItemStack i){
		i.setAmount(1);
		this.slot6 = i;
		return this;
	}
	public PloRecipe setSlot7(ItemStack i){
		i.setAmount(1);
		this.slot7 = i;
		return this;
	}
	public PloRecipe setSlot8(ItemStack i){
		i.setAmount(1);
		this.slot8 = i;
		return this;
	}
	public PloRecipe setSlot9(ItemStack i){
		i.setAmount(1);
		this.slot9 = i;
		return this;
	}
	public ItemStack[] getSlots(){
		return new ItemStack[]{slot1,slot2,slot3,slot4,slot5,slot6,slot7,slot8,slot9};
	}
	public void setSlot(int i,ItemStack it){
		switch(i){
		case 1:
			slot1 = it;
			break;
		case 2:
			slot2 = it;
			break;
		case 3:
			slot3 = it;
			break;
		case 4:
			slot4 = it;
			break;
		case 5:
			slot5 = it;
			break;
		case 6:
			slot6 = it;
			break;
		case 7:
			slot7 = it;
			break;
		case 8:
			slot8 = it;
			break;
		case 9:
			slot9 = it;
			break;
		}
		//Bukkit.broadcastMessage("Set slot "+i);
	}
}
