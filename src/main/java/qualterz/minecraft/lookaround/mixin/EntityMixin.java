package qualterz.minecraft.lookaround.mixin;

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

            handleCrosshair();
            handleDirectionChange();
            handleLookAngleLimit();

            LookAroundMod.shouldAnimate = true;
        } else if (LookAroundMod.shouldAnimate) {
            handleDirectionChange();
            handleCrosshair();
        } else {
            LookAroundMod.offsetCrosshairX = 0;
            LookAroundMod.offsetCrosshairY = 0;
            LookAroundMod.shouldDrawCrosshair = true;
        }

        if (LookAroundMod.shouldLockDirection) {
            ci.cancel();
            LookAroundMod.isDirectionLocked = true;
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

    private void handleCrosshair()
    {
        var cameraEntity = MinecraftClient.getInstance().getCameraEntity();

        if (MinecraftClient.getInstance().options.debugEnabled) {
            LookAroundMod.shouldDrawCrosshair = true;
        } else {
            var distance = Integer.MAX_VALUE;
            var position = cameraEntity.getPos();

            // TODO: smooth rotation using previous rotation value
            var rotation = Vec3d.fromPolar(LookAroundMod.actualPitch, LookAroundMod.actualYaw);

            var point = position.add(
                    rotation.getX() * distance,
                    rotation.getY() * distance,
                    rotation.getZ() * distance
            );

            var projected = ProjectionUtils.worldToScreen(point);

            if (projected.getZ() < 0) {
                LookAroundMod.offsetCrosshairX = -projected.getX();
                LookAroundMod.offsetCrosshairY = -projected.getY();
                LookAroundMod.shouldDrawCrosshair = true;
            } else {
                LookAroundMod.shouldDrawCrosshair = false;
            }
        }
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
