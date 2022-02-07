package qualterz.mcmod.lookaround.mixin;

import net.minecraft.util.math.MathHelper;
import qualterz.mcmod.lookaround.CameraManager;
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

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow @Final private MinecraftClient client;

    private Entity getCameraEntity()
    {
        return client.getCameraEntity() == null ? client.player : client.getCameraEntity();
    }

    @Inject(method = "renderWorld", at = @At("HEAD"))
    private void onRenderWorldBegin(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo ci)
    {
        CameraManager.tickDelta = tickDelta;

        var limitNegativeYaw = CameraManager.actualYaw - 180f;
        var limitPositiveYaw = CameraManager.actualYaw + 180f;

        if (CameraManager.lookYaw > limitPositiveYaw)
            CameraManager.lookYaw = limitPositiveYaw;

        if (CameraManager.lookYaw < limitNegativeYaw)
            CameraManager.lookYaw = limitNegativeYaw;
    }

    @Inject(method = "renderHand", at = @At("HEAD"))
    private void onRenderHandBegin(MatrixStack matrices, Camera camera, float tickDelta, CallbackInfo ci)
    {
        var cameraEntity = getCameraEntity();

        var pitch = CameraManager.lookPitch;

        if (CameraManager.viewLock)
            pitch -= MathHelper.abs(CameraManager.lookYaw - CameraManager.actualYaw);

        cameraEntity.setYaw(CameraManager.lookYaw);
        cameraEntity.setPitch(pitch);
    }

    @Inject(method = "renderHand", at = @At("RETURN"))
    private void onRenderHandEnd(MatrixStack matrices, Camera camera, float tickDelta, CallbackInfo ci)
    {
        var cameraEntity = getCameraEntity();

        cameraEntity.setYaw(CameraManager.actualYaw);
        cameraEntity.setPitch(CameraManager.actualPitch);
    }
}
