package qualterz.minecraft.lookaround.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import qualterz.minecraft.lookaround.CameraState;
import qualterz.minecraft.lookaround.LookAroundMod;

@Mixin(Entity.class)
public abstract class EntityMixin {
    private CameraState cameraState;

    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    private void onChangeLookDirection(double cursorDeltaX, double cursorDeltaY, CallbackInfo callback)
    {
        if ((Entity)(Object)this instanceof ClientPlayerEntity)
        {
            cameraState = LookAroundMod.getInstance().getCameraState();

            if (cameraState.shouldLockDirection && !cameraState.isDirectionLocked)
                handleBeforeDirectionLocked();

            if (!cameraState.shouldLockDirection && cameraState.isDirectionLocked)
                handleDirectionUnlock();

            if (cameraState.isDirectionLocked) {
                var cursorDeltaMultiplier = 0.15f;
                var transformedCursorDeltaX = (float)cursorDeltaX * cursorDeltaMultiplier;
                var transformedCursorDeltaY = (float)cursorDeltaY * cursorDeltaMultiplier;

                var yaw = cameraState.getLookYaw();
                var pitch = cameraState.getLookPitch();

                yaw += transformedCursorDeltaX;
                pitch += transformedCursorDeltaY;
                pitch = MathHelper.clamp(pitch, -90, 90);

                cameraState.setLookYaw(yaw);
                cameraState.setLookPitch(pitch);

                handleLookAngleLimit();

            }

            if (cameraState.shouldLockDirection) {
                callback.cancel();
                cameraState.isDirectionLocked = callback.isCancelled();
            }
        }
    }

    private void handleBeforeDirectionLocked()
    {
        cameraState.setLookYaw(cameraState.getActualYaw());
        cameraState.setLookPitch(cameraState.getActualPitch());

        cameraState.shouldAnimate = true;
    }

    private void handleDirectionUnlock()
    {
        cameraState.isDirectionLocked = false;
    }

    private void handleLookAngleLimit()
    {
        var limitNegativeYaw = cameraState.getActualYaw() - 180;
        var limitPositiveYaw = cameraState.getActualYaw() + 180;

        // TODO: make smoother transition if limit reached
        if (cameraState.getLookYaw() > limitPositiveYaw)
            cameraState.setLookYaw(limitPositiveYaw);

        if (cameraState.getLookYaw() < limitNegativeYaw)
            cameraState.setLookYaw(limitNegativeYaw);
    }
}
