package qualterz.minecraft.lookaround.mixin;

import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import net.minecraft.client.render.Camera;

import qualterz.minecraft.lookaround.CameraManager;

@Mixin(Camera.class)
public abstract class CameraMixin
{
	@ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V"))
	private void modifyRotationArgs(Args args)
	{
		if (CameraManager.viewLock || CameraManager.animate) {
			args.set(0, CameraManager.lookYaw);
			args.set(1, CameraManager.lookPitch);
		}
	}
}
