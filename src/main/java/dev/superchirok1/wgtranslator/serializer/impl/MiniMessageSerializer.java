package dev.superchirok1.wgtranslator.serializer.impl;

import dev.superchirok1.wgtranslator.serializer.TextSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class MiniMessageSerializer implements TextSerializer {
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

    @Override
    public String colorize(String message) {
        Component component = MINI_MESSAGE.deserialize(message);
        return LEGACY.serialize(component);
    }
}
