package me.qualterz.minecraft.lookaround;

import net.minecraft.client.MinecraftClient;

public class CameraState {
    public float getActualYaw()
    {
        return MinecraftClient.getInstance().getCameraEntity().getHeadYaw();
    }

    public float getActualPitch()
    {
        return MinecraftClient.getInstance().getCameraEntity().getPitch();
    }

    public float lookYaw;
    public float lookPitch;

    public boolean shouldAnimate;
    public boolean isDirectionLocked;
    public boolean shouldLockDirection;
}
