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

    public static float lookYaw;
    public static float lookPitch;

    public static boolean shouldAnimate;

    public static boolean isDirectionLocked;
    public static boolean shouldLockDirection;
}
