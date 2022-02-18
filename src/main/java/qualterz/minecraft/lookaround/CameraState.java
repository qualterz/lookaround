package qualterz.minecraft.lookaround;

import net.minecraft.client.MinecraftClient;

public class CameraState {
    public float getActualYaw()
    {
        return MinecraftClient.getInstance().getCameraEntity().getYaw();
    }

    public float getActualPitch()
    {
        return MinecraftClient.getInstance().getCameraEntity().getPitch();
    }

    private float previousLookYaw;
    private float previousLookPitch;

    public float getPreviousLookYaw() {
        return previousLookYaw;
    }

    public float getPreviousLookPitch() {
        return previousLookPitch;
    }

    private float lookYaw;
    private float lookPitch;

    public float getLookYaw() {
        return lookYaw;
    }

    public float getLookPitch() {
        return lookPitch;
    }

    public void setLookYaw(float lookYaw) {
        previousLookYaw = this.lookYaw;
        this.lookYaw = lookYaw;
    }

    public void setLookPitch(float lookPitch) {
        previousLookPitch = this.lookPitch;
        this.lookPitch = lookPitch;
    }

    public static boolean shouldAnimate;

    public static boolean isDirectionLocked;
    public static boolean shouldLockDirection;
}
