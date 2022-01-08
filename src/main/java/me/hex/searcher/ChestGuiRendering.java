package me.hex.searcher;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.Slot;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.hex.searcher.Main.content;

public class ChestGuiRendering {

    public static GuiTextField textField;
    public ArrayList<Slot> slots;
    private final Utils utils;
    boolean first = true;

    public ChestGuiRendering(Utils utils) {
        slots = new ArrayList<Slot>();
        this.utils = utils;
    }

    @SubscribeEvent
    public void onInit(GuiScreenEvent.InitGuiEvent.Post event) {
        if (!(event.gui instanceof GuiChest)) return;

        textField = new GuiTextField(1, event.gui.mc.fontRendererObj, 40
                , 40, event.gui.width - 80, 20);
        textField.setEnableBackgroundDrawing(true);
        textField.setMaxStringLength(400);
        textField.setVisible(true);
        textField.setEnabled(true);
        textField.drawTextBox();
        textField.setFocused(false);
        textField.setCanLoseFocus(true);
        Keyboard.enableRepeatEvents(true);

    }

    @SubscribeEvent
    public void onDraw(GuiScreenEvent.DrawScreenEvent event) {
        if (!(event.gui instanceof GuiChest)) return;
        if (textField == null) {
            System.out.println("DEBUG ID: 11");
            return;
        }
        textField.drawTextBox();
    }

    @SubscribeEvent
    public void onMouseClick(GuiScreenEvent.MouseInputEvent.Post event) {

        if (!(event.gui instanceof GuiChest)) return;

        if (textField == null) {
            System.out.println("DEBUG ID: 21");
            return;
        }

        textField.mouseClicked(Mouse.getX(), Mouse.getY(), Mouse.getEventButton());

    }


    @SubscribeEvent
    public void onKeyboardInputPre(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        if (!(event.gui instanceof GuiChest)) return;
        if (textField == null) {
            System.out.println("DEBUG ID: 31");
            return;
        }
        if (!textField.isFocused()) return;

        try {
            if (Integer.parseInt(String.valueOf(Keyboard.getEventCharacter())) < 10) {
                return;
            }
        } catch (NumberFormatException ignored) {
        }

        if (String.valueOf(Keyboard.getEventCharacter()).equalsIgnoreCase("e")
                || String.valueOf(Keyboard.getEventCharacter()).equalsIgnoreCase("q")) {
            event.setCanceled(true);
        }


        if (Keyboard.getEventKey() == 14) {
            // backspace
            if (!first) {
                first = true;
                return;
            }

            first = false;
            if (textField.getText().isEmpty() || content.isEmpty()) return;
            content = content.substring(0, content.length() - 1);
            textField.setText(content);
        } else {
            //add letter
            if (containsNonAlpha(Keyboard.getEventCharacter())) return;
            textField.writeText(String.valueOf(Keyboard.getEventCharacter()));
            content = content.concat(String.valueOf(Keyboard.getEventCharacter()));
        }
    }

    @SubscribeEvent
    public void onRenderGuiScreen(GuiScreenEvent.DrawScreenEvent.Post event) {

        GuiScreen gui = event.gui;
        String guiName = Main.getGUIName(gui);

        if (guiName == null) {

            return;
        }
        if (!(gui instanceof GuiChest)) {

            return;
        }
        if (textField == null) {
            System.out.println("DEBUG ID: 51");
            return;
        }

        setFocused(Keyboard.isKeyDown(15));

        slots.clear();

        for (Slot slot : ((GuiChest) gui).inventorySlots.inventorySlots) {

            if (slot.getHasStack()) {
                slots.add(slot);
                utils.renderItem(((GuiChest) gui), slot.getStack(), slot.xDisplayPosition, slot.yDisplayPosition);
            }
            if (slots.contains(slot) && ((GuiChest) gui).inventorySlots
                    .inventoryItemStacks.lastIndexOf(slot.getStack()) >= 1) {
                utils.renderItem(((GuiChest) gui), slot.getStack(), slot.xDisplayPosition, slot.yDisplayPosition);
            }
        }

        slots.clear();

        textField.setText(content);
    }

    private void setFocused(boolean power) {
        if (power) {
            textField.setCanLoseFocus(false);
            textField.setFocused(true);
        } else {
            textField.setCanLoseFocus(true);
            textField.setFocused(false);
        }
    }

    private boolean containsNonAlpha(char s) {
        if (String.valueOf(s).equals(";")) return false;
        if (String.valueOf(s).equals("'")) return false;

        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(String.valueOf(s));

        return m.find();
    }
}