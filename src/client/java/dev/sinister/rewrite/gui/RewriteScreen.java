package dev.sinister.rewrite.gui;

import dev.sinister.rewrite.config.RewriteConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Map;

public final class RewriteScreen extends Screen {
    private static final int PANEL_WIDTH = 360;
    private final Screen parent;
    private int page;

    public RewriteScreen(Screen parent) {
        super(Component.translatable("rewrite.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int left = width / 2 - PANEL_WIDTH / 2;
        int top = height / 2 - 110;
        addRenderableWidget(new StringWidget(left, top + 10, PANEL_WIDTH, 20, title, font));

        var entries = new ArrayList<>(RewriteConfig.entries().entrySet());
        int rows = 5;
        int maxPage = Math.max(0, (entries.size() - 1) / rows);
        page = Math.min(page, maxPage);
        int start = page * rows;
        int end = Math.min(entries.size(), start + rows);

        if (entries.isEmpty()) {
            addRenderableWidget(new StringWidget(left, top + 82, PANEL_WIDTH, 20,
                Component.translatable("rewrite.empty"), font));
            addRenderableWidget(new StringWidget(left, top + 102, PANEL_WIDTH, 20,
                Component.translatable("rewrite.empty_hint"), font));
        }

        for (int i = start; i < end; i++) {
            Map.Entry<String, String> entry = entries.get(i);
            String alias = entry.getKey();
            int y = top + 42 + (i - start) * 28;
            Component label = Component.literal("/" + alias + "  →  /" + entry.getValue());
            addRenderableWidget(new StringWidget(left + 8, y, 220, 20, label, font));
            addRenderableWidget(Button.builder(Component.translatable("rewrite.edit"), button ->
                minecraft.setScreen(new EditRewriteScreen(this, alias, entry.getValue())))
                .bounds(left + 230, y, 56, 20).build());
            addRenderableWidget(Button.builder(Component.literal("×"), button -> remove(alias))
                .bounds(left + 292, y, 60, 20).build());
        }

        int footer = top + 188;
        Button previous = addRenderableWidget(Button.builder(Component.literal("‹"), b -> { page--; rebuildWidgets(); })
            .bounds(left, footer, 32, 20).build());
        previous.active = page > 0;
        Button next = addRenderableWidget(Button.builder(Component.literal("›"), b -> { page++; rebuildWidgets(); })
            .bounds(left + 38, footer, 32, 20).build());
        next.active = page < maxPage;
        addRenderableWidget(Button.builder(Component.translatable("rewrite.add"), b ->
            minecraft.setScreen(new EditRewriteScreen(this, null, "")))
            .bounds(left + 80, footer, 136, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("rewrite.done"), b -> onClose())
            .bounds(left + 224, footer, 136, 20).build());
    }

    private void remove(String alias) {
        RewriteConfig.entries().remove(alias);
        RewriteConfig.save();
        rebuildWidgets();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        graphics.fill(0, 0, width, height, 0x66000000);
        int left = width / 2 - 190;
        int top = height / 2 - 120;
        graphics.fill(left, top, left + 380, top + 240, 0xFF101010);
        graphics.outline(left, top, 380, 240, 0xFFFFFFFF);
    }

    @Override
    public boolean isPauseScreen() { return false; }

    @Override
    public void onClose() { minecraft.setScreen(parent); }
}
