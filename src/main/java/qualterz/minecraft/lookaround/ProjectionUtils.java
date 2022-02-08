package qualterz.minecraft.lookaround;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.*;

public class ProjectionUtils {
    public static Vec3f worldToScreen(Vec3d destination)
    {
        var client = MinecraftClient.getInstance();
        var renderer = client.gameRenderer;
        var camera = renderer.getCamera();
        var position = camera.getPos();
        var rotation = camera.getRotation().copy();

        rotation.conjugate();

        var calculation = new Vec3f(
            (float) (position.x - destination.getX()),
            (float) (position.y - destination.getY()),
            (float) (position.z - destination.getZ())
        );

        calculation.transform(new Matrix3f(rotation));

        // TODO: use dynamic fov value
        var fov = client.options.fov;

        var half = client.getWindow().getScaledHeight() / 2;
        var scale = half / (calculation.getZ() * Math.tan(Math.toRadians(fov / 2)));

        return new Vec3f((float) (calculation.getX() * scale), (float) (calculation.getY() * scale), calculation.getZ());
    }
}
