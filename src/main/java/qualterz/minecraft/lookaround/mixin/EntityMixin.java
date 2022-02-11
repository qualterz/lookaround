package qualterz.minecraft.lookaround.mixin;

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
        var cameraEntity = MinecraftClient.getInstance().getCameraEntity();

        if (LookAroundMod.shouldLockDirection && !LookAroundMod.isDirectionLocked) {
            LookAroundMod.actualYaw = cameraEntity.getYaw();
            LookAroundMod.actualPitch = cameraEntity.getPitch();
        }

        if (!LookAroundMod.shouldLockDirection && LookAroundMod.isDirectionLocked) {
            LookAroundMod.offsetCrosshairX = 0;
            LookAroundMod.offsetCrosshairY = 0;

            LookAroundMod.shouldDrawCrosshair = true;
            LookAroundMod.isDirectionLocked = false;
        }

        if (!LookAroundMod.isDirectionLocked) {
            var yaw = cameraEntity.getYaw();
            var pitch = cameraEntity.getPitch();

            LookAroundMod.actualYaw = yaw;
            LookAroundMod.actualPitch = pitch;

            LookAroundMod.lookYaw = yaw;
            LookAroundMod.lookPitch = pitch;

            LookAroundMod.shouldDrawCrosshair = true;
        }

        if (LookAroundMod.isDirectionLocked) {
            var cursorDeltaMultiplier = 0.15f;
            var transformedCursorDeltaX = (float)cursorDeltaX * cursorDeltaMultiplier;
            var transformedCursorDeltaY = (float)cursorDeltaY * cursorDeltaMultiplier;

            LookAroundMod.lookYaw += transformedCursorDeltaX;
            LookAroundMod.lookPitch += transformedCursorDeltaY;
            LookAroundMod.lookPitch = MathHelper.clamp(LookAroundMod.lookPitch, -90f, 90f);

            var distance = Integer.MAX_VALUE;
            var position = cameraEntity.getPos();
            var rotation = cameraEntity.getRotationVecClient();

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

        if (LookAroundMod.shouldLockDirection) {
            ci.cancel();
            LookAroundMod.isDirectionLocked = true;
        }
    }
}
