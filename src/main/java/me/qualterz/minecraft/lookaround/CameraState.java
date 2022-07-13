package me.qualterz.minecraft.lookaround;

import net.minecraft.client.MinecraftClient;

public class CameraState {
    public float originalYaw()
    {
        return MinecraftClient.getInstance().getCameraEntity().getHeadYaw();
    }

    public float originalPitch()
    {
        return MinecraftClient.getInstance().getCameraEntity().getPitch();
    }

    public float lookYaw;
    public float lookPitch;

    public float transitionInitialYaw;
    public float transitionInitialPitch;

    public boolean doLock;
    public boolean doTransition;
}
