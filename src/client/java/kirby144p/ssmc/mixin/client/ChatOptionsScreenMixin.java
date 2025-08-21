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

package kirby144p.ssmc.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import kirby144p.ssmc.screen.FilterConfigScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.ChatOptionsScreen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

@Mixin(ChatOptionsScreen.class)
public abstract class ChatOptionsScreenMixin extends GameOptionsScreen {
    
    private static final Text CONFIG_BUTTON_TEXT = Text.translatable("ssmc.screen.chat.config_button");
    private static final Text CONFIG_BUTTON_TOOLTIP_TEXT = Text.translatable("ssmc.screen.chat.config_button.tooltip");

    public ChatOptionsScreenMixin(Screen parent, GameOptions gameOptions, Text title) {
        super(parent, gameOptions, title);
    }

    @Inject(method = "addOptions()V", at = @At("TAIL"))
    private void addConfigButton(CallbackInfo ci) {
        final var openConfigButton = new ButtonWidget.Builder(CONFIG_BUTTON_TEXT,
            (button) -> {
                this.client.setScreen(new FilterConfigScreen(this));
            }
        )
            /* There is a static field for this but it is protected. */
            .narrationSupplier(textSupplier -> (MutableText)textSupplier.get())

            .dimensions(4, 4, ButtonWidget.DEFAULT_WIDTH, ButtonWidget.DEFAULT_HEIGHT)
            .build();

        openConfigButton.setTooltip(Tooltip.of(CONFIG_BUTTON_TOOLTIP_TEXT));

        this.body.addWidgetEntry(openConfigButton, null);
    }
}
