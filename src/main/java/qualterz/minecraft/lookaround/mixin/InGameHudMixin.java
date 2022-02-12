package qualterz.minecraft.lookaround.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import qualterz.minecraft.lookaround.LookAroundMod;
import qualterz.minecraft.lookaround.ProjectionUtils;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void onRenderCrosshairBegin(MatrixStack matrices, CallbackInfo ci)
    {
        if (!LookAroundMod.shouldDrawCrosshair)
            ci.cancel();
    }

    @ModifyArgs(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"))
    private void modifyDrawTextureArgs(Args args)
    {
        if (LookAroundMod.isDirectionLocked) {
            args.set(1, args.<Integer>get(1) + (int)LookAroundMod.offsetCrosshairX);
            args.set(2, args.<Integer>get(2) + (int)LookAroundMod.offsetCrosshairY);
        }
    }
}
