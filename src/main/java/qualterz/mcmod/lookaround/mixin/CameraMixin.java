package qualterz.mcmod.lookaround.mixin;

import qualterz.mcmod.lookaround.CameraManager;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin
{
	@Inject(method = "update", at = @At("HEAD"))
	private void onCameraUpdateBegin(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci)
	{
        focusedEntity.setYaw(CameraManager.lookYaw);
        focusedEntity.setPitch(CameraManager.lookPitch);
	}

	@Inject(method = "update", at = @At("RETURN"))
	private void onCameraUpdateEnd(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci)
	{
		focusedEntity.setYaw(CameraManager.actualYaw);
		focusedEntity.setPitch(CameraManager.actualPitch);
	}
}
