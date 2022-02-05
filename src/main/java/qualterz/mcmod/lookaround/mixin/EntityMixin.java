package qualterz.mcmod.lookaround.mixin;

import qualterz.mcmod.lookaround.CameraManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    private void onChangeLookDirection(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci)
    {
        Entity entity = (Entity) (Object) this;

        CameraManager.actualYaw = entity.getYaw();
        CameraManager.actualPitch = entity.getPitch();

        if (!CameraManager.cameraLocked) {
            CameraManager.lookYaw = entity.getYaw();
            CameraManager.lookPitch = entity.getPitch();
        }

        if (CameraManager.cameraLocked) {
            CameraManager.changeLookDirection(cursorDeltaX, cursorDeltaY);

            // TODO: implement camera lock for vehicle
            if (entity.hasVehicle())
                return;

            // TODO: implement camera lock for spectatable entity
            var client = MinecraftClient.getInstance();
            if (client.player.isSpectator() && entity != client.cameraEntity)
                return;

            ci.cancel();
        }
    }
}
