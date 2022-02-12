package qualterz.minecraft.lookaround.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.world.BlockView;
import org.checkerframework.checker.units.qual.A;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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
		if (LookAroundMod.isDirectionLocked) {
			var yaw = LookAroundMod.lookYaw;
			var pitch = LookAroundMod.lookPitch;

			if (MinecraftClient.getInstance().options.getPerspective().isFrontView()) {
				yaw -= 180;
				pitch = -pitch;
			}

			args.set(0, yaw);
			args.set(1, pitch);
		} else if (LookAroundMod.shouldAnimate) {
			// TODO: account skipped frames
			var steps = 2;
			var yawDiff = LookAroundMod.lookYaw - LookAroundMod.actualYaw;
			var pitchDiff = LookAroundMod.lookPitch - LookAroundMod.actualPitch;
			var yawStep = yawDiff / steps;
			var pitchStep = pitchDiff / steps;
			var yaw = LookAroundMod.lookYaw = MathHelper.stepTowards(LookAroundMod.lookYaw, LookAroundMod.actualYaw, yawStep);
			var pitch = LookAroundMod.lookPitch = MathHelper.stepTowards(LookAroundMod.lookPitch, LookAroundMod.actualPitch, pitchStep);

			args.set(0, yaw);
			args.set(1, pitch);

			LookAroundMod.shouldAnimate =
					(int)LookAroundMod.actualYaw != (int)yaw &&
					(int)LookAroundMod.actualPitch != (int)pitch;
		}
	}
}
