package qualterz.minecraft.lookaround.mixin;

import net.minecraft.util.math.MathHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qualterz.minecraft.lookaround.LookAroundMod;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    private Entity cameraEntity;
    private float previousYaw;
    private float previousPitch;

    @Inject(method = "renderHand", at = @At("HEAD"))
    private void onRenderHandBegin(MatrixStack matrices, Camera camera, float tickDelta, CallbackInfo ci)
    {
        if (LookAroundMod.shouldAnimate) {
            cameraEntity = MinecraftClient.getInstance().getCameraEntity();
            previousYaw = cameraEntity.getYaw();
            previousPitch = cameraEntity.getPitch();

            var pitch = LookAroundMod.lookPitch;

            pitch -= MathHelper.abs(LookAroundMod.lookYaw - LookAroundMod.actualYaw);

            cameraEntity.setYaw(LookAroundMod.lookYaw);
            cameraEntity.setPitch(pitch);
        }
    }

    @Inject(method = "renderHand", at = @At("RETURN"))
    private void onRenderHandEnd(MatrixStack matrices, Camera camera, float tickDelta, CallbackInfo ci)
    {
        if (LookAroundMod.shouldAnimate) {
            cameraEntity.setYaw(previousYaw);
            cameraEntity.setPitch(previousPitch);
        }
    }
}
