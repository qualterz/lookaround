package qualterz.minecraft.lookaround.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import net.minecraft.client.render.Camera;

import qualterz.minecraft.lookaround.LookAroundMod;

@Mixin(Camera.class)
public abstract class CameraMixin
{
	@ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V"))
	private void modifyRotationArgs(Args args)
	{
		var cameraState = LookAroundMod.getInstance().getCameraState();

		if (cameraState.isDirectionLocked) {
			var yaw = cameraState.lookYaw;
			var pitch = cameraState.lookPitch;

			if (MinecraftClient.getInstance().options.getPerspective().isFrontView()) {
				yaw -= 180;
				pitch = -pitch;
			}

			args.set(0, yaw);
			args.set(1, pitch);
		} else if (cameraState.shouldAnimate) {
			// TODO: account skipped frames
			var steps = 2;
			var yawDiff = cameraState.lookYaw - cameraState.getActualYaw();
			var pitchDiff = cameraState.lookPitch - cameraState.getActualPitch();
			var yawStep = yawDiff / steps;
			var pitchStep = pitchDiff / steps;
			var yaw = cameraState.lookYaw = MathHelper.stepTowards(cameraState.lookYaw, cameraState.getActualYaw(), yawStep);
			var pitch = cameraState.lookPitch = MathHelper.stepTowards(cameraState.lookPitch, cameraState.getActualPitch(), pitchStep);

			args.set(0, yaw);
			args.set(1, pitch);

			cameraState.shouldAnimate =
					(int)cameraState.getActualYaw() != (int)yaw &&
					(int)cameraState.getActualPitch() != (int)pitch;
		}
	}
}
