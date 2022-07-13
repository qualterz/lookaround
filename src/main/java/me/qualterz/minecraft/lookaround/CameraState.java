package me.qualterz.minecraft.lookaround;

import net.minecraft.client.MinecraftClient;

public class CameraState {
    public float getOriginalYaw()
    {
        return MinecraftClient.getInstance().getCameraEntity().getHeadYaw();
    }

    public float getOriginalPitch()
    {
        return MinecraftClient.getInstance().getCameraEntity().getPitch();
    }

    public float lookYaw;
    public float lookPitch;

    public boolean shouldAnimate;
    public boolean isDirectionLocked;
    public boolean shouldLockDirection;
}
