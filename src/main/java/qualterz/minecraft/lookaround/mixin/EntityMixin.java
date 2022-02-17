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

                cameraState.lookYaw += transformedCursorDeltaX;
                cameraState.lookPitch += transformedCursorDeltaY;
                cameraState.lookPitch = MathHelper.clamp(cameraState.lookPitch, -90, 90);

                handleLookAngleLimit();

                cameraState.shouldAnimate = true;
            }

            if (cameraState.shouldLockDirection) {
                callback.cancel();
                cameraState.isDirectionLocked = callback.isCancelled();
            }
        }
    }

    private void handleBeforeDirectionLocked()
    {
        cameraState.lookYaw = cameraState.getActualYaw();
        cameraState.lookPitch = cameraState.getActualPitch();
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
        if (cameraState.lookYaw > limitPositiveYaw)
            cameraState.lookYaw = limitPositiveYaw;

        if (cameraState.lookYaw < limitNegativeYaw)
            cameraState.lookYaw = limitNegativeYaw;
    }
}
