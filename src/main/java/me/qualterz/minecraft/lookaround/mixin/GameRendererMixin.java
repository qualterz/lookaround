package me.qualterz.minecraft.lookaround.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.util.math.MathHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

import me.qualterz.minecraft.lookaround.CameraState;
import me.qualterz.minecraft.lookaround.LookaroundMod;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    private CameraState camera;

    private Entity cameraEntity;
    private float previousYaw;
    private float previousPitch;

    @Inject(method = "renderHand", at = @At("HEAD"))
    private void onRenderHandBegin(MatrixStack matrices, Camera camera, float tickDelta, CallbackInfo ci)
    {
        this.camera = LookaroundMod.getInstance().getCameraState();

        if (this.camera.shouldAnimate) {
            cameraEntity = MinecraftClient.getInstance().getCameraEntity();
            previousYaw = cameraEntity.getYaw();
            previousPitch = cameraEntity.getPitch();

            var pitch = this.camera.lookPitch;

            pitch -= MathHelper.abs(this.camera.lookYaw - this.camera.getActualYaw());

            cameraEntity.setYaw(this.camera.lookYaw);
            cameraEntity.setPitch(pitch);
        }
    }

    @Inject(method = "renderHand", at = @At("RETURN"))
    private void onRenderHandEnd(MatrixStack matrices, Camera camera, float tickDelta, CallbackInfo ci)
    {
        if (this.camera.shouldAnimate) {
            cameraEntity.setYaw(previousYaw);
            cameraEntity.setPitch(previousPitch);
        }
    }
}
