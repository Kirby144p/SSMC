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

package kirby144p.ssmc.screen.widget;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import kirby144p.ssmc.filter.FilterAction;
import kirby144p.ssmc.SSMC;
import kirby144p.ssmc.filter.ChatFilter;
import kirby144p.ssmc.filter.ChatFilterConfig;
import kirby144p.ssmc.filter.FilterStrategy;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;

public class ChatFilterListWidget extends ElementListWidget<kirby144p.ssmc.screen.widget.ChatFilterListWidget.Entry> {

    private static final Text ENABLED_TEXT = Text.translatable("ssmc.screen.setting.enabled");
    private static final Text FILTER_PATTERN_TEXT = Text.translatable("ssmc.screen.setting.filter_pattern");
    private static final Text FILTER_STRATEGY_TEXT = Text.translatable("ssmc.screen.setting.filter_strategy");
    private static final Text FILTER_ACTION_TEXT = Text.translatable("ssmc.screen.setting.filter_action");
    private static final Text HIDE_FROM_CHAT_TEXT = Text.translatable("ssmc.screen.setting.hide_message_from_chat");
    private static final Text HIDE_FROM_LOG_TEXT = Text.translatable("ssmc.screen.setting.hide_message_from_log");
    private static final Text HIDE_FROM_LOG_DISABLED_TOOLTIP = Text.translatable("ssmc.screen.setting.hide_message_from_log.disabled_tooltip");

    /** Common padding or margin. */
    protected static final int SPACER = 4;

    /** Scrollbar width. Magic number taken from {@link #renderWidget(DrawContext, int, int, float)}. */
    protected static final int SCROLL_BAR_WIDTH = 6;

    /** Whether the entries in the list are selectable. */
    protected final boolean selectable;

    /** The config which can be edited using this list. */
    private final ChatFilterConfig config;

    /** A consumer which gets called when an entry is selected. */
    private Consumer<@Nullable Entry> selectedConsumer;

    public ChatFilterListWidget(MinecraftClient minecraftClient, int x, int y, int width, int height, int itemHeight, boolean selectable, ChatFilterConfig config) {
        super(minecraftClient, width, height, y, itemHeight);
        setX(x);

        this.selectable = selectable;

        this.config = config;

        final var filters = config.ChatFilters();
        for (var filter : filters) {
            this.addEntry(new Entry(filter));
        }
    }

    public boolean isScrollbarVisible() {
        return this.overflows();
    }
    
    @Override
    protected boolean isSelectedEntry(int index) {
        return this.selectable ? Objects.equals(this.getSelectedOrNull(), this.children().get(index)) : false;
    }

    @Override
    public int getRowWidth() {
        return isScrollbarVisible() ? this.width - (SPACER * 2 + SCROLL_BAR_WIDTH) : this.width - (SPACER * 2);
    }

    @Override
    public int getRowLeft() {
        return this.getX() + SPACER;
    }

    @Override
    public int getRowRight() {
        return this.getRowLeft() + this.getRowWidth();
    }

    /**
     * If the scrollbar is outside of the boundaries of the list then it cannot be clicked or dragged.
     */
    @Override
    protected int getScrollbarX() {
        return this.getRight() - SCROLL_BAR_WIDTH;
    }

    @Override
    public void setSelected(@Nullable Entry entry) {
        super.setSelected(entry);
        this.selectedConsumer.accept(entry);
    }

    /**
     * Draws shadows at the top and bottom of the list.
     */
    @Override
    public void drawHeaderAndFooterSeparators(DrawContext context) {
        super.drawHeaderAndFooterSeparators(context);

        /* Render the horizontal shadows */
        context.fillGradient(RenderLayer.getGuiOverlay(), this.getX(), this.getY(), this.getRight(), this.getY() + 4, 0xFF000000, 0, 0);
        context.fillGradient(RenderLayer.getGuiOverlay(), this.getX(), this.getBottom() - 4, this.getRight(), this.getBottom(), 0, 0xFF000000, 0);
    }
    
    @Override
    protected void drawSelectionHighlight(DrawContext context, int y, int entryWidth, int entryHeight, int borderColor, int fillColor) {
        context.fill(getRowLeft() - 2, y - 2, getRowRight() + 2, y + entryHeight + 2, borderColor);
        context.fill(getRowLeft() - 1, y - 1, getRowRight() + 1, y + entryHeight + 1, fillColor);
    }

    /**
     * Adds a new default entry to the list.
     */
    public void addNewEntry() {
        this.addNewEntry(new ChatFilter());
    }

    /**
     * Duplicates the selected entry. If no entry is selected then no new entry will be created.
     */
    public void duplicateSelectedEntry() {
        final var entry = this.getSelectedOrNull();
        if (entry != null) {
            try {
                this.addNewEntry(entry.filter.clone());
            } catch (CloneNotSupportedException cnsEx) {
                SSMC.LOGGER.error("Could not duplicate filter entry!", cnsEx);
            }
        }
    }

    /**
     * Adds a new entry with the specified chat filter to the list and to the config.
     * @param filter The filter which should be added.
     */
    private void addNewEntry(ChatFilter filter) {
        final var entry = new Entry(filter);
        this.addEntry(entry);
        this.config.ChatFilters().add(filter);
    }

    /**
     * Removes the selected entry frmo the list.
     */
    public void removeSelectedEntry() {
        final var entry = this.getSelectedOrNull();
        if (entry != null) {
            this.removeEntry(entry);
            if (entry.ChatFilter() != null) {
                this.config.ChatFilters().remove(entry.ChatFilter());
            }
        }
    }

    public void SelectedConsumer(Consumer<@Nullable Entry> newConsumer) {
        this.selectedConsumer = newConsumer;
    }

    @Environment(value=EnvType.CLIENT)
    public class Entry extends ElementListWidget.Entry<Entry> {
        private final ChatFilter filter;

        private final CheckboxWidget enabled;
        private final TextFieldWidget pattern;
        private final CyclingButtonWidget<FilterStrategy> strategy;
        private final CyclingButtonWidget<FilterAction> action;
        private final CheckboxWidget shouldHideFromChat;
        private final CheckboxWidget shouldHideFromLog;

        public Entry(ChatFilter filter) {
            this.filter = filter;

            /* Checkbox for enabled status */
            this.enabled = CheckboxWidget.builder(ENABLED_TEXT, ChatFilterListWidget.this.client.textRenderer)
                .checked(this.filter.Enabled())
                .callback((checkbox, checked) -> {
                    this.filter.Enabled(checked);
                })
                .tooltip(Tooltip.of(ENABLED_TEXT))
                .build();

            /* Text field for pattern */
            this.pattern = new TextFieldWidget(
                ChatFilterListWidget.this.client.textRenderer,
                0, 0, 100, ButtonWidget.DEFAULT_HEIGHT,
                Text.literal(this.filter.Pattern())
            );

            this.pattern.setChangedListener((value) -> this.filter.Pattern(value));
            this.pattern.setMaxLength(64);
            this.pattern.setText(this.filter.Pattern());
            this.pattern.setTooltip(Tooltip.of(FILTER_PATTERN_TEXT));

            /* Fix first few letters of text being cut off */
            this.pattern.setCursorToStart(false);

            /* Two cycling buttons for switching between the different filter strategies and actions */
            this.strategy = CyclingButtonWidget.<FilterStrategy>builder((value) -> {return Text.literal(value.toString());})
                .values(FilterStrategy.values())
                .initially(this.filter.Strategy())
                .build(FILTER_STRATEGY_TEXT, (button, value) -> {this.filter.Strategy(value);});

            this.action = CyclingButtonWidget.<FilterAction>builder((value) -> {return Text.literal(value.toString());})
                .values(FilterAction.values())
                .initially(this.filter.Action())
                .build(FILTER_ACTION_TEXT, (button, value) -> {this.filter.Action(value);});

            /* Two checkboxes for hiding chat messages from the chat or the log */
            this.shouldHideFromLog = CheckboxWidget.builder(HIDE_FROM_LOG_TEXT, ChatFilterListWidget.this.client.textRenderer)
                .checked(this.filter.HideFromLog())
                .callback((checkbox, checked) -> {
                    this.filter.HideFromLog(checked);
                })
                .tooltip(Tooltip.of(this.filter.HideFromChat() ? HIDE_FROM_LOG_TEXT : HIDE_FROM_LOG_DISABLED_TOOLTIP))
                .build();
            
            this.shouldHideFromLog.active = this.filter.HideFromChat();

            this.shouldHideFromChat = CheckboxWidget.builder(HIDE_FROM_CHAT_TEXT, ChatFilterListWidget.this.client.textRenderer)
                .checked(this.filter.HideFromChat())
                .callback((checkbox, checked) -> {
                    this.filter.HideFromChat(checked);

                    /* Cannot hide message from log when not hiding from chat */
                    this.shouldHideFromLog.active = checked;
                    this.shouldHideFromLog.setTooltip(Tooltip.of(checked ? HIDE_FROM_LOG_TEXT : HIDE_FROM_LOG_DISABLED_TOOLTIP));
                })
                .tooltip(Tooltip.of(HIDE_FROM_CHAT_TEXT))
                .build();
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            int xOffset = 0, yOffset = 0;
            
            this.pattern.setX(xOffset += x);
            this.pattern.setY(yOffset += y);
            this.pattern.setWidth(entryWidth - this.enabled.getWidth() - SPACER);

            this.enabled.setX(xOffset + SPACER + this.pattern.getWidth());
            this.enabled.setY(yOffset);

            this.strategy.setX(xOffset);
            this.strategy.setY(yOffset += this.pattern.getHeight() + SPACER);

            this.action.setX(xOffset + this.strategy.getWidth() + SPACER);
            this.action.setY(yOffset);

            this.shouldHideFromChat.setX(xOffset);
            this.shouldHideFromChat.setY(yOffset += this.strategy.getHeight() + SPACER);

            this.shouldHideFromLog.setX(xOffset + this.shouldHideFromChat.getWidth() + SPACER);
            this.shouldHideFromLog.setY(yOffset);

            this.pattern.render(context, mouseX, mouseY, tickDelta);
            this.enabled.render(context, mouseX, mouseY, tickDelta);
            this.strategy.render(context, mouseX, mouseY, tickDelta);
            this.action.render(context, mouseX, mouseY, tickDelta);
            this.shouldHideFromChat.render(context, mouseX, mouseY, tickDelta);
            this.shouldHideFromLog.render(context, mouseX, mouseY, tickDelta);
        }

        /**
         * Return a list of all widgets contained within this entry.
         */
        @Override
        public List<? extends Element> children() {
            return List.of(
                this.pattern,
                this.enabled,
                this.strategy,
                this.action,
                this.shouldHideFromChat,
                this.shouldHideFromLog
            );
        }

        /**
         * Return a list of all widgets contained within this entry.
         */
        @Override
        public List<? extends Selectable> selectableChildren() {
            return List.of(
                this.pattern,
                this.enabled,
                this.strategy,
                this.action,
                this.shouldHideFromChat,
                this.shouldHideFromLog
            );
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            super.mouseClicked(mouseX, mouseY, button);
            if(selectable) {
                ChatFilterListWidget.this.setSelected(this);
            }
            return true;
        }

        public ChatFilter ChatFilter() {
            return this.filter;
        }
    }
}
