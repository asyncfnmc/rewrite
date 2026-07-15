package dev.sinister.rewrite.gui;

import dev.sinister.rewrite.config.RewriteConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class EditRewriteScreen extends Screen {
    private final Screen parent;
    private final String originalAlias;
    private final String originalCommand;
    private EditBox aliasField;
    private EditBox commandField;
    private Button saveButton;

    public EditRewriteScreen(Screen parent, String alias, String command) {
        super(Component.translatable(alias == null ? "rewrite.add" : "rewrite.edit"));
        this.parent = parent;
        this.originalAlias = alias;
        this.originalCommand = command == null ? "" : command;
    }

    @Override
    protected void init() {
        int left = width / 2 - 150;
        int top = height / 2 - 91;

        addRenderableWidget(new StringWidget(left, top + 10, 300, 20, title, font));
        addRenderableWidget(new StringWidget(left + 10, top + 38, 130, 14,
            Component.translatable("rewrite.alias"), font));
        aliasField = addRenderableWidget(new EditBox(font, left + 10, top + 54, 280, 20,
            Component.translatable("rewrite.alias")));
        aliasField.setMaxLength(64);
        aliasField.setValue(originalAlias == null ? "" : originalAlias);

        addRenderableWidget(new StringWidget(left + 10, top + 82, 130, 14,
            Component.translatable("rewrite.command"), font));
        commandField = addRenderableWidget(new EditBox(font, left + 10, top + 98, 280, 20,
            Component.translatable("rewrite.command")));
        commandField.setMaxLength(512);
        commandField.setValue(originalCommand);

        saveButton = addRenderableWidget(Button.builder(Component.translatable("rewrite.save"), b -> save())
            .bounds(left + 10, top + 140, 136, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("rewrite.cancel"), b -> onClose())
            .bounds(left + 154, top + 140, 136, 20).build());

        aliasField.setResponder(value -> validate());
        commandField.setResponder(value -> validate());
        validate();
        setInitialFocus(aliasField);
    }

    private void validate() {
        if (saveButton == null || aliasField == null || commandField == null) return;
        String alias = RewriteConfig.clean(aliasField.getValue());
        String command = RewriteConfig.clean(commandField.getValue());
        boolean unused = alias.equals(originalAlias) || !RewriteConfig.entries().containsKey(alias);
        saveButton.active = !alias.isBlank() && !command.isBlank() && !alias.contains(" ")
            && !alias.equalsIgnoreCase("rewrite") && unused;
    }

    private void save() {
        if (!saveButton.active) return;
        String alias = RewriteConfig.clean(aliasField.getValue());
        String command = RewriteConfig.clean(commandField.getValue());
        if (originalAlias != null) RewriteConfig.entries().remove(originalAlias);
        RewriteConfig.entries().put(alias, command);
        RewriteConfig.save();
        minecraft.setScreen(parent);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        graphics.fill(0, 0, width, height, 0x66000000);
        int left = width / 2 - 150;
        int top = height / 2 - 91;
        graphics.fill(left, top, left + 300, top + 182, 0xFF101010);
        graphics.outline(left, top, 300, 182, 0xFFFFFFFF);
    }

    @Override
    public boolean isPauseScreen() { return false; }

    @Override
    public void onClose() { minecraft.setScreen(parent); }
}
