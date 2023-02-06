package me.qualterz.minecraft.lookaround.mixin;

import net.minecraft.util.math.MathHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.qualterz.minecraft.lookaround.CameraState;
import me.qualterz.minecraft.lookaround.LookaroundMod;

@Mixin(Entity.class)
public abstract class EntityMixin {
    private CameraState camera;

    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    private void onChangeLookDirection(double cursorDeltaX, double cursorDeltaY, CallbackInfo callback)
    {
        if ((Entity)(Object)this instanceof ClientPlayerEntity) {
            camera = LookaroundMod.getInstance().getCameraState();

            if (camera.doLock) {
                applyTransformedAngle(cursorDeltaX, cursorDeltaY);
                callback.cancel();
            } else if (camera.doTransition) {
                applyTransformedAngle(cursorDeltaX, cursorDeltaY);
            }
        }
    }
    
    private void applyTransformedAngle(double cursorDeltaX, double cursorDeltaY)
    {
        var cursorDeltaMultiplier = 0.15f;
        var transformedCursorDeltaX = (float)cursorDeltaX * cursorDeltaMultiplier;
        var transformedCursorDeltaY = (float)cursorDeltaY * cursorDeltaMultiplier;

        var yaw = camera.lookYaw;
        var pitch = camera.lookPitch;

        yaw += transformedCursorDeltaX;
        pitch += transformedCursorDeltaY;
        pitch = MathHelper.clamp(pitch, -90, 90);

        camera.lookYaw = yaw;
        camera.lookPitch = pitch;
    }
}
