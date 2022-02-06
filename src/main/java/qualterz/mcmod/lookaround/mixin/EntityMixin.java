package qualterz.mcmod.lookaround.mixin;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Shadow;
import qualterz.mcmod.lookaround.CameraManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qualterz.mcmod.lookaround.LookAroundMod;
import qualterz.mcmod.lookaround.ProjectionUtils;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public abstract void changeLookDirection(double cursorDeltaX, double cursorDeltaY);

    @Shadow public abstract void discard();

    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    private void onChangeLookDirection(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci)
    {
        Entity entity = (Entity) (Object) this;

        CameraManager.actualYaw = entity.getYaw();
        CameraManager.actualPitch = entity.getPitch();

        if (!CameraManager.cameraLocked) {
            CameraManager.offsetCrosshairX = 0;
            CameraManager.offsetCrosshairY = 0;

            CameraManager.lookYaw = entity.getYaw();
            CameraManager.lookPitch = entity.getPitch();

            CameraManager.drawCrosshair = true;
        }

        if (CameraManager.cameraLocked) {
            CameraManager.lookPitch += (float)cursorDeltaY * 0.15f;
            CameraManager.lookYaw += (float)cursorDeltaX * 0.15f;
            CameraManager.lookPitch = MathHelper.clamp(CameraManager.lookPitch, -90f, 90f);

            var client = MinecraftClient.getInstance();

            var distance = client.interactionManager.getReachDistance();
            var camera = client.gameRenderer.getCamera();
            var position = camera.getPos();
            var rotation = entity.getRotationVecClient();

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

            // TODO: implement camera lock for vehicle: horse
            if (entity.hasVehicle())
                return;

            // TODO: implement camera lock for spectatable entity
            if (client.player.isSpectator() && entity != client.cameraEntity)
                return;

            ci.cancel();
        }
    }
}
