package me.hex.searcher;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class SearchGui extends GuiScreen {
    public static GuiTextField textField;
    private GuiScreen externalGui;

    public SearchGui(GuiScreen gui) {
        externalGui = gui;
    }

    @Override
    public void initGui() {
        OpenInventoryEvent.notFirst = true;
        textField = new GuiTextField(1, fontRendererObj, 40
                , 40, width - 80, 20);
        textField.setEnableBackgroundDrawing(true);
        textField.setMaxStringLength(400);
        textField.setVisible(true);
        textField.setEnabled(true);
        textField.drawTextBox();
        textField.setFocused(true);

        Keyboard.enableRepeatEvents(true);

        super.initGui();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (textField.getText().isEmpty()) {
            Main.searchBy = "";
        }
        textField.textboxKeyTyped(typedChar, keyCode);
        if (keyCode == 14) {
            // backspace
            if (!Main.searchBy.isEmpty())
                Main.searchBy = Main.searchBy.substring(0, Main.searchBy.length() - 1);
        } else {
            //add letter
            Main.searchBy = Main.searchBy.concat(String.valueOf(typedChar));
        }
        if (keyCode == 28) {
            //enter
            mc.displayGuiScreen(externalGui);
            return;
        }
        super.keyTyped(typedChar, keyCode);

    }

    @Override
    public void updateScreen() {

        textField.updateCursorCounter();
        super.updateScreen();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        textField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int x, int y, int btn) throws IOException {
        super.mouseClicked(x, y, btn);
        textField.mouseClicked(x, y, btn);
    }

}
