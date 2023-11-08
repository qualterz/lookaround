package me.qualterz.minecraft.lookaround.mixin;

import me.qualterz.minecraft.lookaround.CameraState;
import me.qualterz.minecraft.lookaround.LookaroundMod;
import me.qualterz.minecraft.lookaround.ProjectionUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    private CameraState camera;

    private double offsetCrosshairX;
    private double offsetCrosshairY;

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void onRenderCrosshairBegin(DrawContext context, CallbackInfo ci) {
        camera = LookaroundMod.getInstance().getCameraState();

        var shouldDrawCrosshair = false;

        if (camera.doTransition || camera.doLock) {
            var cameraEntity = MinecraftClient.getInstance().getCameraEntity();

            var distance = Integer.MAX_VALUE;
            var position = cameraEntity.getPos();

            // TODO: smooth rotation using previous rotation value
            var rotation = Vec3d.fromPolar(camera.originalPitch(), camera.originalYaw());

            var point = position.add(
                    rotation.getX() * distance,
                    rotation.getY() * distance,
                    rotation.getZ() * distance
            );

            var projected = ProjectionUtils.worldToScreen(point);

            if (projected.getZ() < 0) {
                offsetCrosshairX = -projected.getX();
                offsetCrosshairY = -projected.getY();
                shouldDrawCrosshair = true;
            }

            shouldDrawCrosshair |= MinecraftClient.getInstance().inGameHud.getDebugHud().shouldShowDebugHud();

            if (!shouldDrawCrosshair)
                ci.cancel();
        }
    }

    @ModifyArgs(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"))
    private void modifyDrawTextureArgs(Args args) {
        if (camera.doTransition || camera.doLock) {
            args.set(1, args.<Integer>get(1) + (int) offsetCrosshairX);
            args.set(2, args.<Integer>get(2) + (int) offsetCrosshairY);
        }
    }
}
