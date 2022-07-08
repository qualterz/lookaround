package me.qualterz.minecraft.lookaround.mixin;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.client.render.Camera;

import me.qualterz.minecraft.lookaround.CameraState;
import me.qualterz.minecraft.lookaround.LookaroundMod;

@Mixin(Camera.class)
public abstract class CameraMixin
{
	@Inject(method = "update", at = @At("HEAD"))
	private void onCameraUpdate(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci)
	{
		CameraState cameraState = LookaroundMod.getInstance().getCameraState();

		var limitNegativeYaw = cameraState.getActualYaw() - 180;
		var limitPositiveYaw = cameraState.getActualYaw() + 180;

		// TODO: make smoother transition if limit reached
		if (cameraState.getLookYaw() > limitPositiveYaw)
			cameraState.setLookYaw(limitPositiveYaw);

		if (cameraState.getLookYaw() < limitNegativeYaw)
			cameraState.setLookYaw(limitNegativeYaw);
	}

	@ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V"))
	private void modifyRotationArgs(Args args)
	{
		var cameraState = LookaroundMod.getInstance().getCameraState();

		if (CameraState.isDirectionLocked) {
			var yaw = cameraState.getLookYaw();
			var pitch = cameraState.getLookPitch();

			if (MinecraftClient.getInstance().options.getPerspective().isFrontView()) {
				yaw -= 180;
				pitch = -pitch;
			}

			args.set(0, yaw);
			args.set(1, pitch);
		} else if (CameraState.shouldAnimate) {
			// TODO: account skipped frames
			var steps = 2;
			var yawDiff = cameraState.getLookYaw() - cameraState.getActualYaw();
			var pitchDiff = cameraState.getLookPitch() - cameraState.getActualPitch();
			var yawStep = yawDiff / steps;
			var pitchStep = pitchDiff / steps;
			var yaw = MathHelper.stepTowards(cameraState.getLookYaw(), cameraState.getActualYaw(), yawStep);
			var pitch = MathHelper.stepTowards(cameraState.getLookPitch(), cameraState.getActualPitch(), pitchStep);

			cameraState.setLookYaw(yaw);
			cameraState.setLookPitch(pitch);

			args.set(0, yaw);
			args.set(1, pitch);

			CameraState.shouldAnimate =
					(int)cameraState.getActualYaw() != (int)yaw &&
					(int)cameraState.getActualPitch() != (int)pitch;
		}
	}
}
