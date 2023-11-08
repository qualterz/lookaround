package me.qualterz.minecraft.lookaround.mixin;

import me.qualterz.minecraft.lookaround.CameraState;
import me.qualterz.minecraft.lookaround.LookaroundMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    private CameraState camera;

    private Entity cameraEntity;
    private float originalYaw;
    private float originalPitch;

    @Inject(method = "renderHand", at = @At("HEAD"))
    private void onRenderHandBegin(MatrixStack matrices, Camera camera, float tickDelta, CallbackInfo ci) {
        this.camera = LookaroundMod.getInstance().getCameraState();

        if (this.camera.doTransition || this.camera.doLock) {
            cameraEntity = MinecraftClient.getInstance().getCameraEntity();
            originalYaw = cameraEntity.getYaw();
            originalPitch = cameraEntity.getPitch();

            var pitch = this.camera.lookPitch;

            pitch -= MathHelper.abs(this.camera.lookYaw - this.camera.originalYaw());

            cameraEntity.setYaw(this.camera.lookYaw);
            cameraEntity.setPitch(pitch);
        }
    }

    @Inject(method = "renderHand", at = @At("RETURN"))
    private void onRenderHandEnd(MatrixStack matrices, Camera camera, float tickDelta, CallbackInfo ci) {
        if (this.camera.doTransition || this.camera.doLock) {
            cameraEntity.setYaw(originalYaw);
            cameraEntity.setPitch(originalPitch);
        }
    }
}
