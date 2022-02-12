package qualterz.minecraft.lookaround.mixin;

import net.minecraft.util.math.MathHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import qualterz.minecraft.lookaround.LookAroundMod;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow @Final private MinecraftClient client;

    private Entity getCameraEntity()
    {
        return client.getCameraEntity() == null ? client.player : client.getCameraEntity();
    }

    @Inject(method = "renderHand", at = @At("HEAD"))
    private void onRenderHandBegin(MatrixStack matrices, Camera camera, float tickDelta, CallbackInfo ci)
    {
        if (LookAroundMod.isDirectionLocked || LookAroundMod.shouldAnimate) {
            var cameraEntity = getCameraEntity();

            var pitch = LookAroundMod.lookPitch;

            pitch -= MathHelper.abs(LookAroundMod.lookYaw - LookAroundMod.actualYaw);

            cameraEntity.setYaw(LookAroundMod.lookYaw);
            cameraEntity.setPitch(pitch);
        }
    }

    @Inject(method = "renderHand", at = @At("RETURN"))
    private void onRenderHandEnd(MatrixStack matrices, Camera camera, float tickDelta, CallbackInfo ci)
    {
        if (LookAroundMod.isDirectionLocked || LookAroundMod.shouldAnimate) {
            var cameraEntity = getCameraEntity();

            cameraEntity.setYaw(LookAroundMod.actualYaw);
            cameraEntity.setPitch(LookAroundMod.actualPitch);
        }
    }
}
