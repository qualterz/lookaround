package qualterz.mcmod.lookaround.mixin;

import net.minecraft.util.math.MathHelper;
import qualterz.mcmod.lookaround.CameraManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qualterz.mcmod.lookaround.ProjectionUtils;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    private void onChangeLookDirection(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci)
    {
        var client = MinecraftClient.getInstance();
        var camera = client.getCameraEntity();

        CameraManager.actualYaw = camera.getYaw();
        CameraManager.actualPitch = camera.getPitch();

        if (!CameraManager.viewLock) {
            CameraManager.offsetCrosshairX = 0;
            CameraManager.offsetCrosshairY = 0;

            // TODO: adjust values according to spectatable entity
            CameraManager.lookYaw = camera.getYaw();
            CameraManager.lookPitch = camera.getPitch();

            CameraManager.drawCrosshair = true;
        }

        if (CameraManager.viewLock) {
            // TODO: combine values with tick delta to prevent shaking or flickering

            CameraManager.lookYaw += (float)cursorDeltaX * 0.15f;
            CameraManager.lookPitch += (float)cursorDeltaY * 0.15f;
            CameraManager.lookPitch = MathHelper.clamp(CameraManager.lookPitch, -90f, 90f);

            var distance = Integer.MAX_VALUE;
            var position = camera.getPos();
            var rotation = camera.getRotationVecClient();

            var point = position.add(
                rotation.getX() * distance,
                rotation.getY() * distance,
                rotation.getZ() * distance
            );

            var projected = ProjectionUtils.worldToScreen(point);

            if (projected.getZ() < 0) {
                CameraManager.offsetCrosshairX = -projected.getX();
                CameraManager.offsetCrosshairY = -projected.getY();
                CameraManager.drawCrosshair = true;
            } else {
                CameraManager.drawCrosshair = false;
            }

            ci.cancel();
        }
    }
}
