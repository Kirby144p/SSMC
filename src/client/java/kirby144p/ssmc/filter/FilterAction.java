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

import java.util.function.Consumer;

import kirby144p.ssmc.SSMCClient;
import net.minecraft.text.Text;

public enum FilterAction {
    NONE((text) -> {}),
    SHOW_IN_ACTION_BAR((text) -> {
        SSMCClient.displayMessage(text, true);
    }),
    SHOW_AS_TITLE((text) -> {
        SSMCClient.displayTitle(text, false);
    }),
    SHOW_AS_SUBTITLE((text) -> {
        SSMCClient.displayTitle(text, true);
    }),
    SHOW_AS_TOAST((text) -> {
        SSMCClient.displayToast(text, Text.translatable("ssmc.chat_toast.title"));
    });

    private final Consumer<Text> action;

    private FilterAction(Consumer<Text> action) {
        this.action = action;
    }

    public void doAction(Text chatMessage) {
        action.accept(chatMessage);
    }
}
