package me.qualterz.minecraft.lookaround;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class LookAroundMod implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("LookAround");

	private static LookAroundMod instance;

	public static LookAroundMod getInstance() {
		return instance;
	}

	private me.qualterz.minecraft.lookaround.CameraState cameraState;

	public me.qualterz.minecraft.lookaround.CameraState getCameraState() {
		return cameraState;
	}

	@Override
	public void onInitializeClient() {
		instance = this;

		cameraState = new me.qualterz.minecraft.lookaround.CameraState();

		var lookAroundBinding = KeyBindingHelper.registerKeyBinding(
			new KeyBinding(
				"key.lookAround",
				InputUtil.Type.KEYSYM,
				InputUtil.GLFW_KEY_LEFT_ALT,
				KeyBinding.MISC_CATEGORY
			)
		);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			CameraState.shouldLockDirection = lookAroundBinding.isPressed();
		});
	}
}
