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

        var transformedCursorDeltaX = (float)cursorDeltaX * 0.15f;
        var transformedCursorDeltaY = (float)cursorDeltaY * 0.15f;

        CameraManager.lookYaw += transformedCursorDeltaX;
        CameraManager.lookPitch += transformedCursorDeltaY;
        CameraManager.lookPitch = MathHelper.clamp(CameraManager.lookPitch, -90f, 90f);

        // TODO: combine values with tick delta to prevent shaking or flickering
        if (!CameraManager.viewLock) {
            final var actualYaw = camera.getYaw();
            final var actualPitch = camera.getPitch();

            CameraManager.actualYaw = actualYaw;
            CameraManager.actualPitch = actualPitch;

            CameraManager.offsetCrosshairX = 0;
            CameraManager.offsetCrosshairY = 0;

            // TODO: rework animation, make it more smoother, use animation steps
            if (CameraManager.animate) {
                CameraManager.animateYaw = CameraManager.lookYaw != actualYaw;
                CameraManager.animatePitch = CameraManager.lookPitch != actualPitch;

                if (CameraManager.animateYaw) {
                    var yawOffset = CameraManager.animationSpeed * CameraManager.tickDelta + transformedCursorDeltaX;

                    if (CameraManager.lookYaw > actualYaw) {
                        if (CameraManager.lookYaw - yawOffset < actualYaw)
                            CameraManager.lookYaw = actualYaw;
                        else
                            CameraManager.lookYaw -= yawOffset;
                    } else if (CameraManager.lookYaw < actualYaw) {
                        if (CameraManager.lookYaw + yawOffset > actualYaw)
                            CameraManager.lookYaw = actualYaw;
                        else
                            CameraManager.lookYaw += yawOffset;
                    }
                }

                if (CameraManager.animatePitch) {
                    var pitchOffset = (CameraManager.animationSpeed / 2) * CameraManager.tickDelta + transformedCursorDeltaY;

                    if (CameraManager.lookPitch > actualPitch) {
                        if (CameraManager.lookPitch - pitchOffset < actualPitch)
                            CameraManager.lookPitch = actualPitch;
                        else
                            CameraManager.lookPitch -= pitchOffset;
                    } else if (CameraManager.lookPitch < actualPitch) {
                        if (CameraManager.lookPitch + pitchOffset > actualPitch)
                            CameraManager.lookPitch = actualPitch;
                        else
                            CameraManager.lookPitch += pitchOffset;
                    }
                }

                CameraManager.animate = CameraManager.animateYaw || CameraManager.animatePitch;

            } else {
                // TODO: adjust values according to spectatable entity
                CameraManager.lookYaw = camera.getYaw();
                CameraManager.lookPitch = camera.getPitch();
            }

            CameraManager.drawCrosshair = true;
        } else {
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

            CameraManager.animate = true;

            ci.cancel();
        }
    }
}
