package qualterz.minecraft.lookaround.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import qualterz.minecraft.lookaround.LookAroundMod;
import qualterz.minecraft.lookaround.ProjectionUtils;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    private float offsetCrosshairX;
    private float offsetCrosshairY;

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void onRenderCrosshairBegin(MatrixStack matrices, CallbackInfo ci)
    {
        var shouldDrawCrosshair = false;

        if (LookAroundMod.shouldAnimate) {
            var cameraEntity = MinecraftClient.getInstance().getCameraEntity();

            var distance = Integer.MAX_VALUE;
            var position = cameraEntity.getPos();

            // TODO: smooth rotation using previous rotation value
            var rotation = Vec3d.fromPolar(LookAroundMod.actualPitch, LookAroundMod.actualYaw);

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

            shouldDrawCrosshair |= MinecraftClient.getInstance().options.debugEnabled;

            if (!shouldDrawCrosshair)
                ci.cancel();
        }
    }

    @ModifyArgs(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"))
    private void modifyDrawTextureArgs(Args args)
    {
        if (LookAroundMod.shouldAnimate) {
            args.set(1, args.<Integer>get(1) + (int)offsetCrosshairX);
            args.set(2, args.<Integer>get(2) + (int)offsetCrosshairY);
        }
    }
}
