package me.qualterz.minecraft.lookaround;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class LookaroundMod implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Lookaround!");

	private static LookaroundMod instance;

	public static LookaroundMod getInstance() {
		return instance;
	}

	private CameraState camera;

	public CameraState getCameraState() {
		return camera;
	}

	@Override
	public void onInitializeClient() {
		instance = this;

		camera = new CameraState();

		var lookAroundBinding = KeyBindingHelper.registerKeyBinding(
			new KeyBinding(
				"key.lookAround",
				InputUtil.Type.KEYSYM,
				InputUtil.GLFW_KEY_LEFT_ALT,
				KeyBinding.GAMEPLAY_CATEGORY
			)
		);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			var doLock = lookAroundBinding.isPressed() && !camera.doLock;
			var doUnlock = !lookAroundBinding.isPressed() && camera.doLock;

			if (doLock) {
				if (!camera.doTransition) {
					camera.lookYaw = camera.originalYaw();
					camera.lookPitch = camera.originalPitch();
				}

				camera.doLock = true;
			}

			if (doUnlock) {
				camera.doLock = false;
				camera.doTransition = true;

				camera.transitionInitialYaw = camera.lookYaw;
				camera.transitionInitialPitch = camera.lookPitch;
			}
		});
	}
}
