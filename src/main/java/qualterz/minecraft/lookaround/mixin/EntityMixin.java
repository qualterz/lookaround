package qualterz.minecraft.lookaround.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.util.math.MathHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;

import qualterz.minecraft.lookaround.LookAroundMod;
import qualterz.minecraft.lookaround.ProjectionUtils;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    private void onChangeLookDirection(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci)
    {
        if ((Entity)(Object)this instanceof ClientPlayerEntity)
        {
            if (LookAroundMod.shouldLockDirection && !LookAroundMod.isDirectionLocked)
                handleBeforeDirectionLocked();

            if (!LookAroundMod.shouldLockDirection && LookAroundMod.isDirectionLocked)
                handleDirectionUnlock();

            if (LookAroundMod.isDirectionLocked) {
                var cursorDeltaMultiplier = 0.15f;
                var transformedCursorDeltaX = (float)cursorDeltaX * cursorDeltaMultiplier;
                var transformedCursorDeltaY = (float)cursorDeltaY * cursorDeltaMultiplier;

                LookAroundMod.lookYaw += transformedCursorDeltaX;
                LookAroundMod.lookPitch += transformedCursorDeltaY;
                LookAroundMod.lookPitch = MathHelper.clamp(LookAroundMod.lookPitch, -90, 90);

                handleDirectionChange();
                handleLookAngleLimit();

                LookAroundMod.shouldAnimate = true;
            } else if (LookAroundMod.shouldAnimate) {
                handleDirectionChange();
            }

            if (LookAroundMod.shouldLockDirection) {
                ci.cancel();
                LookAroundMod.isDirectionLocked = true;
            }
        }
    }

    private void handleBeforeDirectionLocked()
    {
        handleDirectionChange();

        LookAroundMod.lookYaw = LookAroundMod.actualYaw;
        LookAroundMod.lookPitch = LookAroundMod.actualPitch;
    }

    private void handleDirectionUnlock()
    {
        LookAroundMod.isDirectionLocked = false;
    }

    private void handleDirectionChange()
    {
        var cameraEntity = MinecraftClient.getInstance().getCameraEntity();

        LookAroundMod.actualYaw = cameraEntity.getHeadYaw();
        LookAroundMod.actualPitch = cameraEntity.getPitch();
    }

    private void handleLookAngleLimit()
    {
        var limitNegativeYaw = LookAroundMod.actualYaw - 180;
        var limitPositiveYaw = LookAroundMod.actualYaw + 180;

        // TODO: make smoother transition if limit reached
        if (LookAroundMod.lookYaw > limitPositiveYaw)
            LookAroundMod.lookYaw = limitPositiveYaw;

        if (LookAroundMod.lookYaw < limitNegativeYaw)
            LookAroundMod.lookYaw = limitNegativeYaw;
    }
}
