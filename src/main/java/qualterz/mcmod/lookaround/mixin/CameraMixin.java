package qualterz.mcmod.lookaround.mixin;

import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import qualterz.mcmod.lookaround.CameraManager;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(Camera.class)
public abstract class CameraMixin
{
	@ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V"))
	private void modifyRotationArgs(Args args)
	{
		if (CameraManager.viewLock) {
			args.set(0, CameraManager.lookYaw);
			args.set(1, CameraManager.lookPitch);
		}
	}
}
