package dev.sinister.rewrite;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.sinister.rewrite.gui.RewriteScreen;

public final class RewriteModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return RewriteScreen::new;
    }
}
