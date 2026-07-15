package dev.sinister.rewrite.mixin;

import dev.sinister.rewrite.config.RewriteConfig;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
    @ModifyVariable(method = "sendCommand", at = @At("HEAD"), argsOnly = true)
    private String rewrite$expandAlias(String command) {
        return RewriteConfig.expand(command);
    }
}
