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
