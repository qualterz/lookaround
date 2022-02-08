package qualterz.minecraft.lookaround.mixin;

import net.minecraft.client.render.Camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Camera.class)
public interface CameraAccessorMixin {
    @Accessor
    float getYaw();

    @Accessor
    void setYaw(float yaw);

    @Accessor
    float getPitch();

    @Accessor
    void setPitch(float pitch);
}
