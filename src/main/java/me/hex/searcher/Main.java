package me.hex.searcher;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Main.MODID, version = Main.VERSION)
public class Main {
    public static final String MODID = "searcher";
    public static final String VERSION = "1.0";
    public static Minecraft mc;
    public static String content = "";

    @EventHandler
    public void init(FMLInitializationEvent event) {
        mc = Minecraft.getMinecraft();
    }

    @EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ChestGuiRendering(new Utils()));
    }

    public static String getGUIName(GuiScreen gui) {
        if (gui instanceof GuiChest) {
            GuiChest chest = (GuiChest) gui;
            return ((ContainerChest) chest.inventorySlots)
                    .getLowerChestInventory().getDisplayName().getUnformattedText();
        }
        return null;
    }

}
