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


package kirby144p.ssmc.filter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import kirby144p.ssmc.SSMC;
import kirby144p.ssmc.SSMCClient;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;

public class ChatFilterConfig {
    private static final Text ERROR_LOADING_CONFIG_TITLE = Text.translatable("ssmc.config_toast.error.loading_config_file.title");
    private static final Text ERROR_SAVING_CONFIG_TITLE = Text.translatable("ssmc.config_toast.error.saving_config_file.title");
    private static final Text ERROR_LOADING_CONFIG_TEXT = Text.translatable("ssmc.config_toast.error.loading_config_file");
    private static final Text ERROR_SAVING_CONFIG_TEXT = Text.translatable("ssmc.config_toast.error.saving_config_file");

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_FILE_PATH = FabricLoader.getInstance().getConfigDir().resolve("ssmc").resolve("filters.json");

    private final List<ChatFilter> chatFilters = new ArrayList<ChatFilter>();

    /**
     * Loads the config from the config directory.
     * Creates a new config if no file exists or there was an error reading the file.
     * @return A filter config.
     */
    public static ChatFilterConfig load() {
        if (!Files.isReadable(CONFIG_FILE_PATH)) {
            return new ChatFilterConfig();
        }

        try (var reader = Files.newBufferedReader(CONFIG_FILE_PATH)) {
            return GSON.fromJson(reader, ChatFilterConfig.class);
        } catch (IOException | SecurityException | JsonIOException | JsonSyntaxException ex) {
            SSMC.LOGGER.error("Error reading config file!", ex);
            SSMCClient.displayToast(ERROR_LOADING_CONFIG_TEXT, ERROR_LOADING_CONFIG_TITLE);
            return new ChatFilterConfig();
        }
    }

    /**
     * Creates a new filter config containing default entries.
     */
    public ChatFilterConfig() {
        this.chatFilters.add(new ChatFilter(true, "Respawn point set", FilterStrategy.CONTAINS, FilterAction.SHOW_AS_SUBTITLE, true, false));
        this.chatFilters.add(new ChatFilter(true, "Hello, World!", FilterStrategy.CONTAINS, FilterAction.SHOW_IN_ACTION_BAR, true, false));
        this.chatFilters.add(new ChatFilter(true, "Welcome to SSMC!", FilterStrategy.EXACT_MATCH, FilterAction.SHOW_AS_TITLE, true, true));
    }

    /**
     * Returns the currently loaded chat filters.
     * @return A list of chat filters.
     */
    public List<ChatFilter> ChatFilters() {
        return this.chatFilters;
    }

    /**
     * Saves the config to the config directory.
     * Creates the directory if it doesn't exist.
     */
    public void save() {
        try {
            Files.createDirectories(CONFIG_FILE_PATH.getParent());
        } catch (IOException IOEx) {
            SSMC.LOGGER.error("Error creating config directory!", IOEx);
            return;
        }

        try (var writer = Files.newBufferedWriter(CONFIG_FILE_PATH, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
            GSON.toJson(this, writer);
        } catch (IOException | SecurityException | JsonIOException ex) {
            SSMC.LOGGER.error("Error opening, serializing or writing config file!", ex);
            SSMCClient.displayToast(ERROR_SAVING_CONFIG_TEXT, ERROR_SAVING_CONFIG_TITLE);
        }
        
    }
}
