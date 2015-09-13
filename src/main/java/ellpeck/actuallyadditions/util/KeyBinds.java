/*
 * This file ("KeyBinds.java") is part of the Actually Additions Mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Actually Additions License to be found at
 * http://github.com/Ellpeck/ActuallyAdditions/blob/master/README.md
 * View the source code at https://github.com/Ellpeck/ActuallyAdditions
 *
 * � 2015 Ellpeck
 */

package ellpeck.actuallyadditions.util;

import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class KeyBinds{

    public static KeyBinding keybindOpenBooklet = new KeyBinding("key."+ModUtil.MOD_ID_LOWER+".openBooklet.name", Keyboard.KEY_I, "key."+ModUtil.MOD_ID_LOWER+".category");

    public static void init(){
        ClientRegistry.registerKeyBinding(keybindOpenBooklet);
    }

}
