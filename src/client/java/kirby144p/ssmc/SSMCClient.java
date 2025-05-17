//	Stop spamming my Chat (SSMC) is a modification for Minecraft which
//  allows for filtering and performing certain actions on chat messages.
//	Copyright (C) 2025  Kirby144p
//	
//	This program is free software: you can redistribute it and/or modify
//	it under the terms of the GNU General Public License as published by
//	the Free Software Foundation, either version 3 of the License, or
//	(at your option) any later version.
//	
//	This program is distributed in the hope that it will be useful,
//	but WITHOUT ANY WARRANTY; without even the implied warranty of
//	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//	GNU General Public License for more details.
//	
//	You should have received a copy of the GNU General Public License
//	along with this program.  If not, see <https://www.gnu.org/licenses/>.

package kirby144p.ssmc;

import java.time.Instant;

import org.jetbrains.annotations.Nullable;

import com.mojang.authlib.GameProfile;

import kirby144p.ssmc.filter.ChatFilterConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents.AllowChat;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents.AllowGame;
import net.minecraft.network.message.MessageType.Parameters;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;

public class SSMCClient implements ClientModInitializer, AllowGame, AllowChat {

	private static SSMCClient instance;

	private ChatFilterConfig config;

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.

		instance = this;

		loadConfig();

		saveConfig();

		ClientReceiveMessageEvents.ALLOW_GAME.register(this);
		ClientReceiveMessageEvents.ALLOW_CHAT.register(this);

		SSMC.LOGGER.info("Ready to can up some spam?");
	}

	@Override
	public boolean allowReceiveChatMessage(Text message, @Nullable SignedMessage signedMessage,
			@Nullable GameProfile sender, Parameters params, Instant receptionTimestamp) {
		return chatMessage(message);
	}

	@Override
	public boolean allowReceiveGameMessage(Text message, boolean overlay) {
		return overlay || chatMessage(message);
	}

	/**
	 * Executes the actions of matching chat filters and
	 * determines whether the chat message should be displayed and logged.
	 * @param message	The message which should be checked.
	 * @return			Whether the chat message should be shown in chat.
	 */
	private boolean chatMessage(Text message) {
		boolean shouldHideFromChat = false;
		boolean shouldHideFromLog = false;
		for (var chatFilter : config.ChatFilters()) {
			if (chatFilter.Enabled() && chatFilter.isMatch(message)) {
				chatFilter.doAction(message);
				if (chatFilter.HideFromChat()) shouldHideFromChat = true;
				if (chatFilter.HideFromLog()) shouldHideFromLog = true;
			}
		}

		/* Message gets logged anyways when not hidden from chat */
		if (shouldHideFromChat && !shouldHideFromLog) {
			SSMC.LOGGER.info("*[CHAT] {}", message.getString());
		}

		return !shouldHideFromChat;
	}

	/**
	 * Displays a message in either the chat or on the action bar of the client.
	 * @param message	The message to display.
	 * @param actionBar	Whether the message should be displayed in chat or on the action bar.
	 */
	public static void displayMessage(Text message, boolean actionBar) {
		var client = MinecraftClient.getInstance();
		if (client.player != null) {
			client.player.sendMessage(message, actionBar);
		}
	}

	/**
	 * Displays a message as either a title or a subtitle.
	 * @param message	The message to display.
	 * @param subtitle	Whether the message should be displayed as a title or a subtitle.
	 */
	public static void displayTitle(Text message, boolean subtitle) {
		var client = MinecraftClient.getInstance();
		if (client.player != null) {
			if (subtitle) {
				client.inGameHud.setSubtitle(message);
				client.inGameHud.setTitle(Text.empty());
			} else {
				client.inGameHud.setSubtitle(Text.empty());
				client.inGameHud.setTitle(message);
			}
		}
	}

	/**
	 * Displays a message as a toast.
	 * @param message	The message to display.
	 * @param title		The title of the message.
	 */
	public static void displayToast(Text message, Text title) {
		var client = MinecraftClient.getInstance();
		if (client.player != null) {
			client.getToastManager().add(SystemToast.create(client, SystemToast.Type.PERIODIC_NOTIFICATION, title, message));
		}
	}

	/**
	 * Loads the chat filter config from disk.
	 */
	public static void loadConfig() {
		SSMC.LOGGER.info("Loading config file");
		instance.config = ChatFilterConfig.load();
	}

	/**
	 * Saves the current chat filter config to disk.
	 */
	public static void saveConfig() {
		SSMC.LOGGER.info("Saving config file");
		instance.config.save();
	}

	/**
	 * Gets the chat filter config.
	 * @return	The chat filter config.
	 */
	public static ChatFilterConfig ChatFilterConfig() {
		return instance.config;
	}
}
