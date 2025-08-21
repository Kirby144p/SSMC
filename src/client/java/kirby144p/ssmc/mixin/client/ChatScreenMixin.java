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
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {
    private static final Text CONFIG_BUTTON_TEXT = Text.translatable("ssmc.screen.chat.config_button");
    private static final Text CONFIG_BUTTON_TOOLTIP_TEXT = Text.translatable("ssmc.screen.chat.config_button.tooltip");

    protected ChatScreenMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("TAIL"), method = "init")
	private void placeConfigButton(CallbackInfo info) {
        /* Creating an anonymous instance of a class does not care about a protected constructor. */
        final var openConfigButton = new ButtonWidget(4, 4, 60, ButtonWidget.DEFAULT_HEIGHT, CONFIG_BUTTON_TEXT,
            (button) -> {
                this.client.setScreen(new FilterConfigScreen(this));
            },
            /* There is a static field for this but it is protected. */
            textSupplier -> (MutableText)textSupplier.get()
        ) {
            /**
             * This prevents the button from being selected using
             * the arrow keys which would break typing in chat.
             */
            @Override
	        public GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
	        	return null;
	        }
        };

        openConfigButton.setTooltip(Tooltip.of(CONFIG_BUTTON_TOOLTIP_TEXT));

        this.addDrawableChild(openConfigButton);
	}
}
