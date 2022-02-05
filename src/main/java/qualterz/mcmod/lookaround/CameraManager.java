package qualterz.mcmod.lookaround;

import net.minecraft.util.math.MathHelper;

public class CameraManager {
    public static float actualYaw;
    public static float actualPitch;
    public static float lookYaw;
    public static float lookPitch;
    public static boolean cameraLocked;

    public static void changeLookDirection(double cursorDeltaX, double cursorDeltaY) {
        float f = (float)cursorDeltaY * 0.15f;
        float g = (float)cursorDeltaX * 0.15f;

        lookPitch += f;
        lookYaw += g;

        lookPitch = MathHelper.clamp(lookPitch, -90f, 90f);
    }
}
