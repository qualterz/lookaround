package me.qualterz.minecraft.lookaround.mixin;

import net.minecraft.util.math.MathHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.qualterz.minecraft.lookaround.CameraState;
import me.qualterz.minecraft.lookaround.LookAroundMod;

@Mixin(Entity.class)
public abstract class EntityMixin {
    private CameraState cameraState;

    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    private void onChangeLookDirection(double cursorDeltaX, double cursorDeltaY, CallbackInfo callback)
    {
        if ((Entity)(Object)this instanceof ClientPlayerEntity)
        {
            cameraState = LookAroundMod.getInstance().getCameraState();

            if (CameraState.shouldLockDirection && !CameraState.isDirectionLocked)
                handleBeforeDirectionLocked();

            if (!CameraState.shouldLockDirection && CameraState.isDirectionLocked)
                handleDirectionUnlock();

            if (CameraState.isDirectionLocked) {
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
            }

            if (CameraState.shouldLockDirection) {
                callback.cancel();
                CameraState.isDirectionLocked = callback.isCancelled();
            }
        }
    }

    private void handleBeforeDirectionLocked()
    {
        cameraState.setLookYaw(cameraState.getActualYaw());
        cameraState.setLookPitch(cameraState.getActualPitch());

        CameraState.shouldAnimate = true;
    }

    private void handleDirectionUnlock()
    {
        CameraState.isDirectionLocked = false;
    }
}
