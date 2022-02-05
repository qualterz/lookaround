package qualterz.mcmod.lookaround;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LookAroundMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("LookAround");

	private static final KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(
		new KeyBinding(
			"key.lookAround",
			InputUtil.Type.KEYSYM,
			InputUtil.GLFW_KEY_LEFT_ALT,
			KeyBinding.MISC_CATEGORY
		)
	);

	@Override
	public void onInitialize() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			CameraManager.cameraLocked = keyBinding.isPressed();
		});
	}
}
