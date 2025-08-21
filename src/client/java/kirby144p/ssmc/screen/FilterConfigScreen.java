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

package kirby144p.ssmc.screen;

import kirby144p.ssmc.SSMC;
import kirby144p.ssmc.SSMCClient;
import kirby144p.ssmc.filter.ChatFilterConfig;
import kirby144p.ssmc.screen.widget.ChatFilterListWidget;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

public class FilterConfigScreen extends Screen {

    private static final Text SCREEN_TITLE = Text.translatable("ssmc.screen.config.title");
    private static final Text BACK_BUTTON_TEXT = ScreenTexts.BACK;
    private static final Text LOAD_BUTTON_TEXT = Text.translatable("ssmc.screen.config.load_button");
    private static final Text SAVE_BUTTON_TEXT = Text.translatable("ssmc.screen.config.save_button");
    private static final Text ADD_BUTTON_TEXT = Text.translatable("ssmc.screen.config.add_button");
    private static final Text DUPLICATE_BUTTON_TEXT = Text.translatable("ssmc.screen.config.duplicate_button");
    private static final Text REMOVE_BUTTON_TEXT = Text.translatable("ssmc.screen.config.remove_button");
    private static final Text CONFIRM_REMOVE_BUTTON_TEXT = Text.translatable("ssmc.screen.config.confirm_remove_button").formatted(Formatting.RED);
    private static final Text SEARCH_TEXT = Text.translatable("ssmc.screen.config.search");
    private static final Text SEARCH_BY_STRATEGY = Text.translatable("ssmc.screen.config.search_by_strategy");
    private static final Text HIDE_NON_MATCHING_TEXT = Text.translatable("ssmc.screen.config.hide_non_matching");
    private static final Text VERSION_TEXT = Text.translatable("ssmc.version", SSMC.MOD_VERSION.getFriendlyString());
    private static final Text COPYRIGHT_NOTICE_TEXT = Text.translatable("ssmc.copyright_notice");
    private static final Text LICENSE_NOTICE_TEXT = Text.translatable("ssmc.license_notice");

    private static final String REPOSITORY_URI = "https://github.com/Kirby144p/SSMC";
    private static final String LICENSE_URI = "https://www.gnu.org/licenses/gpl-3.0.html#license-text";

    /** Common padding or margin. */
    protected static final int SPACER = 4;

    /** Width of the side buttons. */
    protected static final int SIDE_BUTTON_WIDTH = 60;

    /** Height of a button. */
    protected static final int BUTTON_HEIGHT = ButtonWidget.DEFAULT_HEIGHT;

    /** Padded width of the side buttons. */
    protected static final int SIDE_BUTTONS_PADDED_WIDTH = SIDE_BUTTON_WIDTH + 3*SPACER;

    private final Screen parent;

    private ChatFilterConfig config;

    private ChatFilterListWidget list;
    private ButtonWidget duplicateButton;
    private ButtonWidget removeButton;
    private ButtonWidget confirmRemoveButton;

    /**
     * Creates a new filter config screen which allows editing the currently loaded chat filter config.
     * @param parent The parent screen which will be switched to when this screen closes.
     */
    public FilterConfigScreen(Screen parent) {
        super(SCREEN_TITLE);
        
        this.parent = parent;
    }
    
    @Override
    protected void init() {
        super.init();

        this.config = SSMCClient.ChatFilterConfig();

        var yOffset = 0;

        this.list = new ChatFilterListWidget(this.client,
            /* X */ SIDE_BUTTONS_PADDED_WIDTH,
            /* Y */ SPACER + 2*(SPACER + BUTTON_HEIGHT),
            /* W */ this.width - SIDE_BUTTON_WIDTH - 5*SPACER,
            /* H */ this.height - 2*this.textRenderer.fontHeight - 3*SPACER - 2*(SPACER + BUTTON_HEIGHT),
            68 + 2*SPACER,
            true, this.config
        );

        /* Text field for searching */
        final var search = new TextFieldWidget(
            this.textRenderer,
            0, 0, (int)(this.list.getWidth() * 0.7) - 2*(SPACER - BUTTON_HEIGHT), BUTTON_HEIGHT,
            Text.empty()
        );

        search.setPosition(SIDE_BUTTONS_PADDED_WIDTH + (this.list.getWidth() - search.getWidth())/2, SPACER);
        search.setChangedListener((value) -> this.list.search(value));
        search.setMaxLength(512);
        search.setTooltip(Tooltip.of(SEARCH_TEXT));

        this.addDrawableChild(search);

        /* Checkbox for searching using entry's strategy */
        final var searchByStrategy = CheckboxWidget.builder(SEARCH_BY_STRATEGY, this.textRenderer)
            .checked(false)
            .callback((checkbox, checked) -> {
                this.list.searchByStrategy(checked);
            })
            .tooltip(Tooltip.of(SEARCH_BY_STRATEGY))
            .pos(SIDE_BUTTONS_PADDED_WIDTH, search.getHeight() + 2*SPACER)
            .build();

        this.addDrawableChild(searchByStrategy);

        /* Checkbox for hiding non-matching entries */
        final var hideNoneMatchingEntries = CheckboxWidget.builder(HIDE_NON_MATCHING_TEXT, this.textRenderer)
            .checked(false)
            .callback((checkbox, checked) -> {
                this.list.hideNonMatching(checked);
            })
            .tooltip(Tooltip.of(HIDE_NON_MATCHING_TEXT))
            .pos(SIDE_BUTTONS_PADDED_WIDTH + searchByStrategy.getWidth() + SPACER, search.getHeight() + 2*SPACER)
            .build();

        this.addDrawableChild(hideNoneMatchingEntries);

        this.addDrawableChild(new ButtonWidget.Builder(BACK_BUTTON_TEXT, (button) -> {this.close();})
            .dimensions(SPACER, yOffset += SPACER, SIDE_BUTTON_WIDTH, ButtonWidget.DEFAULT_HEIGHT)
            .build());

        this.addDrawableChild(new ButtonWidget.Builder(SAVE_BUTTON_TEXT, (button) -> {SSMCClient.saveConfig();})
            .dimensions(SPACER, yOffset += SPACER + ButtonWidget.DEFAULT_HEIGHT, SIDE_BUTTON_WIDTH, ButtonWidget.DEFAULT_HEIGHT)
            .build());

        this.addDrawableChild(new ButtonWidget.Builder(LOAD_BUTTON_TEXT, (button) -> {SSMCClient.loadConfig(); this.client.setScreen(this);})
            .dimensions(SPACER, yOffset += SPACER + ButtonWidget.DEFAULT_HEIGHT, SIDE_BUTTON_WIDTH, ButtonWidget.DEFAULT_HEIGHT)
            .build());

        this.addDrawableChild(new ButtonWidget.Builder(ADD_BUTTON_TEXT, (button) -> {this.list.addNewEntry();})
            .dimensions(SPACER, yOffset += 3*SPACER + ButtonWidget.DEFAULT_HEIGHT, SIDE_BUTTON_WIDTH, ButtonWidget.DEFAULT_HEIGHT)
            .build());

        this.addDrawableChild(this.duplicateButton = new ButtonWidget.Builder(DUPLICATE_BUTTON_TEXT, (button) -> {this.list.duplicateSelectedEntry();})
            .dimensions(SPACER, yOffset += SPACER + ButtonWidget.DEFAULT_HEIGHT, SIDE_BUTTON_WIDTH, ButtonWidget.DEFAULT_HEIGHT)
            .build());

        this.addDrawableChild(this.removeButton = new ButtonWidget.Builder(REMOVE_BUTTON_TEXT, (button) -> {setConfirmRemove(true);})
            .dimensions(SPACER, yOffset += SPACER + ButtonWidget.DEFAULT_HEIGHT, SIDE_BUTTON_WIDTH, ButtonWidget.DEFAULT_HEIGHT)
            .build());

        this.addDrawableChild(this.confirmRemoveButton = new ButtonWidget.Builder(CONFIRM_REMOVE_BUTTON_TEXT, (button) -> {setConfirmRemove(false); this.list.removeSelectedEntry();})
            .dimensions(SPACER, yOffset, SIDE_BUTTON_WIDTH, ButtonWidget.DEFAULT_HEIGHT)
            .build());

        this.list.SelectedConsumer((entry) -> {
            setConfirmRemove(false);
            if (entry == null) {
                this.removeButton.active = this.duplicateButton.active = false;
            } else {
                this.removeButton.active = this.duplicateButton.active = true;
            }
        });

        this.addDrawableChild(list);

        /* This resets the remove confirmation and disables the buttons which interact with the selected entry */
        this.list.setSelected(null);

        /* Version, Copyright and license notices */
        final var versionText =  new TextWidget(VERSION_TEXT, this.textRenderer);
        versionText.setPosition(SPACER, this.height - (this.textRenderer.fontHeight + 2));
        this.addDrawableChild(versionText);

        final var copyrightNoticeText =  new PressableTextWidget(
            this.width - (this.textRenderer.getWidth(COPYRIGHT_NOTICE_TEXT) + SPACER),
            this.height - (this.textRenderer.fontHeight + 2),
            this.textRenderer.getWidth(LICENSE_NOTICE_TEXT),
            this.textRenderer.fontHeight,
            COPYRIGHT_NOTICE_TEXT,
            (button) -> {
                this.client.setScreen(new ConfirmLinkScreen((confirmed) -> {
                    if (confirmed) {
                        Util.getOperatingSystem().open(REPOSITORY_URI);
                    }

                    this.client.setScreen(this);
                    }, REPOSITORY_URI, false));
            },
            this.textRenderer
        );
        this.addDrawableChild(copyrightNoticeText);

        final var licenseNoticeText =  new PressableTextWidget(
            (this.width - this.textRenderer.getWidth(LICENSE_NOTICE_TEXT)) / 2,
            this.height - (this.textRenderer.fontHeight + 2) * 2,
            this.textRenderer.getWidth(LICENSE_NOTICE_TEXT),
            this.textRenderer.fontHeight,
            LICENSE_NOTICE_TEXT,
            (button) -> {
                this.client.setScreen(new ConfirmLinkScreen((confirmed) -> {
                    if (confirmed) {
                        Util.getOperatingSystem().open(LICENSE_URI);
                    }

                    this.client.setScreen(this);
                    }, LICENSE_URI, false));
            },
            this.textRenderer
        );
        this.addDrawableChild(licenseNoticeText);
    }

    /**
     * Sets the remove confirmation to active.
     * @param value Whether the remove confirmation should be active or not.
     */
    private void setConfirmRemove(boolean value) {
        if (this.removeButton != null && this.confirmRemoveButton != null) {
            this.removeButton.active = this.removeButton.visible = !value;
            this.confirmRemoveButton.active = this.confirmRemoveButton.visible = value;
        }
    }

    @Override
    public void close() {
        this.client.setScreen(parent);
    }
}
