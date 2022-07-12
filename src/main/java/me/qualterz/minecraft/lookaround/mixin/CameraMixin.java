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

import me.qualterz.minecraft.lookaround.LookaroundMod;

@Mixin(Camera.class)
public abstract class CameraMixin
{
	private float lastUpdate;

	@Inject(method = "update", at = @At("HEAD"))
	private void onCameraUpdate(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci)
	{
		var camera = LookaroundMod.getInstance().getCameraState();

		var limitNegativeYaw = camera.getActualYaw() - 180;
		var limitPositiveYaw = camera.getActualYaw() + 180;

		// TODO: make smoother transition if limit reached
		if (camera.lookYaw > limitPositiveYaw)
			camera.lookYaw = limitPositiveYaw;

		if (camera.lookYaw < limitNegativeYaw)
			camera.lookYaw = limitNegativeYaw;
	}

	@ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V"))
	private void modifyRotationArgs(Args args)
	{
		var camera = LookaroundMod.getInstance().getCameraState();

		if (camera.isDirectionLocked) {
			var yaw = camera.lookYaw;
			var pitch = camera.lookPitch;

			if (MinecraftClient.getInstance().options.getPerspective().isFrontView()) {
				yaw -= 180;
				pitch = -pitch;
			}

			args.set(0, yaw);
			args.set(1, pitch);
		} else if (camera.shouldAnimate) {
			var delta = (getCurrentTime() - lastUpdate);
			var steps = 1.1f;
			var yawDiff = camera.lookYaw - camera.getActualYaw();
			var pitchDiff = camera.lookPitch - camera.getActualPitch();
			var yawStep = (yawDiff / steps) * delta;
			var pitchStep = (pitchDiff / steps) * delta;
			var yaw = MathHelper.stepTowards(camera.lookYaw, camera.getActualYaw(), yawStep);
			var pitch = MathHelper.stepTowards(camera.lookPitch, camera.getActualPitch(), pitchStep);

			camera.lookYaw = yaw;
			camera.lookPitch = pitch;

			args.set(0, yaw);
			args.set(1, pitch);

			camera.shouldAnimate =
					(int)camera.getActualYaw() != (int)yaw &&
					(int)camera.getActualPitch() != (int)pitch;
		}

		lastUpdate = getCurrentTime();
	}

	private float getCurrentTime() {
		return (float) (System.nanoTime() * 0.00000001);
	}
}
