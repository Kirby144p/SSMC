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

import java.util.Objects;

import net.minecraft.text.Text;

public class ChatFilter implements Cloneable {

    private boolean enabled;
    private String pattern;
    private FilterStrategy strategy;
    private FilterAction action;
    private boolean hideFromChat;
    private boolean hideFromLog;
    // TODO: Add list of servers which this filter applies to.

    public ChatFilter() {
        this.enabled = true;
        this.pattern = "test";
        this.strategy = FilterStrategy.EXACT_MATCH;
        this.action = FilterAction.SHOW_IN_ACTION_BAR;
        this.hideFromChat = true;
        this.hideFromLog = false;
    }
    
    public ChatFilter(boolean enabled, String pattern, FilterStrategy strategy, FilterAction action, boolean hideFromChat, boolean hideFromLog) {
        this.enabled = enabled;
        this.pattern = pattern;
        this.strategy = strategy;
        this.action = action;
        this.hideFromChat = hideFromChat;
        this.hideFromLog = hideFromLog;       
    }

    public boolean isMatch(Text text) {
        String message = text.getString();
        return strategy.isMatch(message, pattern);
    }

    public void doAction(Text text) {
        this.action.doAction(text);
    }

    public boolean Enabled() {
        return this.enabled;
    }

    public void Enabled(boolean newEnabled) {
        this.enabled = newEnabled;
    }

    public String Pattern() {
        return this.pattern;
    }

    public void Pattern(String newPattern) {
        this.pattern = newPattern;
    }

    public FilterStrategy Strategy() {
        return this.strategy;
    }

    public void Strategy(FilterStrategy newStrategy) {
        this.strategy = newStrategy;
    }

    public FilterAction Action() {
        return this.action;
    }

    public void Action(FilterAction newAction) {
        this.action = newAction;
    }

    public boolean HideFromChat() {
        return this.hideFromChat;
    }

    public void HideFromChat(boolean newValue) {
        this.hideFromChat = newValue;
    }

    public boolean HideFromLog() {
        return this.hideFromLog;
    }

    public void HideFromLog(boolean newValue) {
        this.hideFromLog = newValue;
    }

    @Override
    public ChatFilter clone() throws CloneNotSupportedException {
        final var newFilter = new ChatFilter(this.enabled, new String(this.pattern), this.strategy, this.action, this.hideFromChat, this.hideFromLog);
        return newFilter;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other == null) return false;

        if (this.getClass() != other.getClass()) return false;
        ChatFilter otherChatFilter = (ChatFilter) other;
        if (this.enabled != otherChatFilter.enabled) return false;
        if (!Objects.equals(this.pattern, otherChatFilter.pattern)) return false;
        if (!Objects.equals(this.strategy, otherChatFilter.strategy)) return false;
        if (!Objects.equals(this.action, otherChatFilter.action)) return false;
        if (this.hideFromChat != otherChatFilter.hideFromChat) return false;
        if (this.hideFromLog != otherChatFilter.hideFromLog) return false;

        return true;
    }
}
