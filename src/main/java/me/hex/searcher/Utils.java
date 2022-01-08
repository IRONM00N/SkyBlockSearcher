package me.hex.searcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import static me.hex.searcher.ChestGuiRendering.textField;

import java.awt.*;
import java.util.ArrayList;

public class Utils {
    public final Gson gson;
    ArrayList<Slot> slots;
    ArrayList<Boolean> booleans;
    final int overlayColourDark;

    public Utils() {
        gson = new GsonBuilder().setPrettyPrinting().create();
        slots = new ArrayList<Slot>();
        booleans = new ArrayList<Boolean>();
        overlayColourDark = new Color(0, 0, 0, 120).getRGB();
    }

    public void renderItem(GuiChest gui, ItemStack stack, int x, int y) {

        if (stack != null && !(stack.stackSize >= 1)) return;

        boolean matches = false;
        booleans.clear();

        if (textField.getText().trim().isEmpty()) {
            matches = true;
        } else {

            if (!textField.getText().contains(";")) {
                matches = doesStackMatchSearch(stack, textField.getText().trim());

            } else {
                for (String split : textField.getText().split(";")) {
                    booleans.add(doesStackMatchSearch(stack, split.trim()));
                }

                if (!booleans.contains(false)) {
                    matches = true;
                }

                booleans.clear();
            }
        }
        if (!matches) {

            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0, 110 + Minecraft.getMinecraft().getRenderItem().zLevel);
            int size = gui.inventorySlots.inventorySlots.size();
            drawOnSlot(size, x, y, overlayColourDark);

        }
    }

    /*
    ------------------------------------
    method taken from DSM's SkyblockMOD
    ------------------------------------
     */
    public static void drawOnSlot(int size, int xSlotPos, int ySlotPos, int colour) {

        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

        int guiLeft = (sr.getScaledWidth() - 176) / 2;
        int guiTop = (sr.getScaledHeight() - 222) / 2;
        int x = guiLeft + xSlotPos;
        int y = guiTop + ySlotPos;
        // Move down when chest isn't 6 rows
        if (size != 90) y += (6 - (size - 36) / 9) * 9;

        GlStateManager.translate(0, 0, 1);
        Gui.drawRect(x, y, x + 16, y + 16, colour);
        GlStateManager.popMatrix();

    }

    /*
    ---------------------------------------------------------------
    methods from here and below are taken from Moulberry's NEU Mod.
    ---------------------------------------------------------------
    */
    public boolean doesStackMatchSearch(ItemStack stack, String query) {

        if (query.startsWith("title:")) {
            query = query.substring(6);
            return searchString(stack.getDisplayName(), query);
        } else if (query.startsWith("desc:")) {
            query = query.substring(5);
            String lore = "";
            NBTTagCompound tag = stack.getTagCompound();
            if (tag != null) {
                NBTTagCompound display = tag.getCompoundTag("display");
                if (display.hasKey("Lore", 9)) {
                    NBTTagList list = display.getTagList("Lore", 8);
                    for (int i = 0; i < list.tagCount(); i++) {
                        lore += list.getStringTagAt(i) + " ";
                    }
                }
            }
            return searchString(lore, query);
        } else if (query.startsWith("id:")) {
            query = query.substring(3);
            String internalName = getInternalNameForItem(stack);
            return query.equalsIgnoreCase(internalName);
        } else {
            boolean result = false;
            if (!query.trim().contains(" ")) {
                StringBuilder sb = new StringBuilder();
                for (char c : query.toCharArray()) {
                    sb.append(c).append(" ");
                }
                result = searchString(stack.getDisplayName(), sb.toString());
            }
            result = result || searchString(stack.getDisplayName(), query);

            String lore = "";
            NBTTagCompound tag = stack.getTagCompound();
            if (tag != null) {
                NBTTagCompound display = tag.getCompoundTag("display");
                if (display.hasKey("Lore", 9)) {
                    NBTTagList list = display.getTagList("Lore", 8);
                    for (int i = 0; i < list.tagCount(); i++) {
                        lore += list.getStringTagAt(i) + " ";
                    }
                }
            }

            result = result || searchString(lore, query);

            return result;
        }
    }


    public boolean searchString(String toSearch, String query) {

        int lastMatch = -1;

        toSearch = clean(toSearch).toLowerCase();
        query = clean(query).toLowerCase();
        String[] splitToSearch = toSearch.split(" ");
        out:
        for (String s : query.split(" ")) {
            for (int i = 0; i < splitToSearch.length; i++) {
                if (lastMatch == -1 || lastMatch == i - 1) {
                    if (splitToSearch[i].startsWith(s)) {
                        lastMatch = i;
                        continue out;
                    }
                }
            }
            return false;
        }

        return true;
    }

    private String clean(String str) {
        return str.replaceAll("(\u00a7.)|[^0-9a-zA-Z ]", "").toLowerCase().trim();
    }

    public String getInternalNameForItem(ItemStack stack) {

        if (stack == null) return null;
        NBTTagCompound tag = stack.getTagCompound();
        return getInternalnameFromNBT(tag);
    }

    public String getInternalnameFromNBT(NBTTagCompound tag) {

        String internalname = null;
        if (tag != null && tag.hasKey("ExtraAttributes", 10)) {
            NBTTagCompound ea = tag.getCompoundTag("ExtraAttributes");

            if (ea.hasKey("id", 8)) {
                internalname = ea.getString("id").replaceAll(":", "-");
            } else {
                return null;
            }

            if ("PET".equals(internalname)) {
                String petInfo = ea.getString("petInfo");
                if (petInfo.length() > 0) {
                    JsonObject petInfoObject = gson.fromJson(petInfo, JsonObject.class);
                    internalname = petInfoObject.get("type").getAsString();
                    String tier = petInfoObject.get("tier").getAsString();
                    if ("COMMON".equals(tier)) {
                        internalname += ";0";
                    } else if ("UNCOMMON".equals(tier)) {
                        internalname += ";1";
                    } else if ("RARE".equals(tier)) {
                        internalname += ";2";
                    } else if ("EPIC".equals(tier)) {
                        internalname += ";3";
                    } else if ("LEGENDARY".equals(tier)) {
                        internalname += ";4";
                    } else if ("MYTHIC".equals(tier)) {
                        internalname += ";5";
                    }
                }
            }
            if ("ENCHANTED_BOOK".equals(internalname)) {
                NBTTagCompound enchants = ea.getCompoundTag("enchantments");

                for (String enchname : enchants.getKeySet()) {
                    internalname = enchname.toUpperCase() + ";" + enchants.getInteger(enchname);
                    break;
                }
            }
        }

        return internalname;
    }

}
