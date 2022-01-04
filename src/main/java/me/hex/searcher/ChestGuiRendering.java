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

public class ChestGuiRendering {
    public static GuiTextField textField;
    public ArrayList<Slot> slots;
    private final Utils utils;
    boolean focused = false;


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

//        int mouseX = Mouse.getX();
//        int mouseY = Mouse.getY();
//        int elementX = textField.xPosition;
//        int elementY = textField.yPosition;
//        int elementWidth = textField.width;
//        int elementHeight = textField.height;
//
//        if (mouseX >= elementX && mouseX <= elementX + elementWidth
//                && mouseY >= elementY && mouseY <= elementY + elementHeight) {
//
//            if(!focused) {
//                textField.setFocused(true);
//                focused = true;
//                System.out.println("WA");
//            } else {
//                textField.setFocused(false);
//                focused = false;
//                System.out.println("AW");
//            }
//        }

    }
//    @SubscribeEvent
//    public void tickEvent(TickEvent.ClientTickEvent event){
//        if(!(Main.mc.currentScreen instanceof GuiChest)) return;
//
//        if(!(event.phase == TickEvent.Phase.END)) return;
//        if(Keyboard.getEventKey() == 15) return;
//        setFocused(focused);
//    }


    @SubscribeEvent
    public void onKeyboardInputPre(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        if (!(event.gui instanceof GuiChest)) return;
        if (textField == null) {
            System.out.println("DEBUG ID: 31");
            return;
        }

        if(Keyboard.getEventKey() == 15){
            if(!focused){
                focused = true;
                setFocused(true);
                return;
            } else {
                focused = false;
                setFocused(false);
                return;
            }
        }
        if (!(String.valueOf(Keyboard.getEventCharacter()).equals(";")
                || !(String.valueOf(Keyboard.getEventCharacter()).equals("'")))) {

            Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(String.valueOf(Keyboard.getEventCharacter()));
            boolean b = m.find();

            if (b) {
                event.setCanceled(true);
                return;
            }
        }

        if (String.valueOf(Keyboard.getEventCharacter()).equalsIgnoreCase("e") && textField.isFocused()) {
            event.setCanceled(true);
            textField.textboxKeyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
        }
    }

    @SubscribeEvent
    public void onKeyboardInput(GuiScreenEvent.KeyboardInputEvent.Post event) {
        if (!(event.gui instanceof GuiChest)) return;

        if (textField == null) {
            System.out.println("DEBUG ID: 41");
            return;
        }

        if (Keyboard.getEventKey() == 14) {
            // backspace

            if (!textField.getText().isEmpty()) {
                textField.setText(textField.getText().substring(0, textField
                        .getText().length() - 1));

            }
        } else {
            //add letter
            textField.writeText(String.valueOf(Keyboard.getEventCharacter()));
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

        if(focused){
            setFocused(true);
        } else {
            setFocused(false);
        }

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
}