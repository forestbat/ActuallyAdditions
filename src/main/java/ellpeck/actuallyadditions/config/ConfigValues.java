package ellpeck.actuallyadditions.config;

import ellpeck.actuallyadditions.config.values.ConfigBoolValues;
import ellpeck.actuallyadditions.config.values.ConfigCrafting;
import ellpeck.actuallyadditions.config.values.ConfigFloatValues;
import ellpeck.actuallyadditions.config.values.ConfigIntValues;
import net.minecraftforge.common.config.Configuration;

public class ConfigValues{

    public static ConfigCrafting[] craftingConfig = ConfigCrafting.values();
    public static ConfigIntValues[] intConfig = ConfigIntValues.values();
    public static ConfigFloatValues[] floatConfig = ConfigFloatValues.values();
    public static ConfigBoolValues[] boolConfig = ConfigBoolValues.values();

    public static String[] crusherRecipeExceptions;
    public static String[] mashedFoodCraftingExceptions;
    public static String[] oreMagnetExceptions;

    public static void defineConfigValues(Configuration config){

        for(ConfigCrafting currConf : craftingConfig){
            currConf.currentValue = config.getBoolean(currConf.name, currConf.category, currConf.defaultValue, "If the " + currConf.extraText + "Crafting Recipe for the "+currConf.name+" is Enabled");
        }
        for(ConfigIntValues currConf : intConfig){
            currConf.currentValue = config.getInt(currConf.name, currConf.category, currConf.defaultValue, currConf.min, currConf.max, currConf.desc);
        }
        for(ConfigFloatValues currConf : floatConfig){
            currConf.currentValue = config.getFloat(currConf.name, currConf.category, currConf.defaultValue, currConf.min, currConf.max, currConf.desc);
        }
        for(ConfigBoolValues currConf : boolConfig){
            currConf.currentValue = config.getBoolean(currConf.name, currConf.category, currConf.defaultValue, currConf.desc);
        }

        crusherRecipeExceptions = config.getStringList("Crusher Recipe Exceptions", ConfigCategories.CRUSHER_RECIPES.name, new String[]{"ingotBrick", "ingotBrickNether"}, "The Ingots, Dusts and Ores blacklisted from being auto-registered to be crushed by the Crusher. This list uses OreDictionary Names of the Inputs only.");
        mashedFoodCraftingExceptions = config.getStringList("Mashed Food Crafting Exceptions", ConfigCategories.ITEMS_CRAFTING.name, new String[]{"ActuallyAdditions:itemCoffee"}, "The ItemFood, IGrowable and IPlantable Items that can not be used to craft Mashed Food. These are the actual registered Item Names, the ones you use, for example, when using the /give Command.");
        oreMagnetExceptions = config.getStringList("Ore Magnet Exceptions", ConfigCategories.MACHINE_VALUES.name, new String[0], "By default, the Ore Magnet pulls up everything that is registered in the OreDictionary as a String that starts with 'ore'. If you want any Ore not to be pulled up by the Magnet, put its ORE DICTIONARY name here.");
    }
}
