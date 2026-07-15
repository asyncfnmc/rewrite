package dev.sinister.rewrite;

import com.mojang.brigadier.CommandDispatcher;
import dev.sinister.rewrite.config.RewriteConfig;
import dev.sinister.rewrite.gui.RewriteScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

public final class RewriteClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        RewriteConfig.load();
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, context) -> register(dispatcher));
    }

    private static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("rewrite").executes(context -> {
            Minecraft client = Minecraft.getInstance();
            client.schedule(() -> client.setScreen(new RewriteScreen(null)));
            return 1;
        }));
    }
}
