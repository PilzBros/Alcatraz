package com.pilzbros.Alcatraz.Crafting;

import java.util.ArrayList;
import java.util.List;

public class PloRecipeList {
	/**
	 * @author Plo457
	 */
	static List<PloRecipe> shaped;
	public static void addShapedRecipe(PloRecipe recipe){
		if (shaped == null){
			shaped = new ArrayList<PloRecipe>();
		}
		shaped.add(recipe);
	}
	public static List<PloRecipe> getShapedRecipes(){
		return shaped;
	}
}
