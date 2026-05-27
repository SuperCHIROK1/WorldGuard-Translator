package dev.superchirok1.wgtranslator.serializer;

import dev.superchirok1.wgtranslator.serializer.impl.LegacyAmpersandSerializer;
import dev.superchirok1.wgtranslator.serializer.impl.MiniMessageSerializer;
import lombok.Getter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Text {
    @Getter
    private final LegacyAmpersandSerializer legacyAmpersandSerializer = new LegacyAmpersandSerializer();
    @Getter
    private final MiniMessageSerializer miniMessageSerializer = new MiniMessageSerializer();

    public TextSerializer get;

    public void init(Type type) {
        get = type.getSerializer();
    }

    @Getter
    public enum Type {
        LEGACY_AMPERSAND(Text.getLegacyAmpersandSerializer()),
        MINI_MESSAGE(Text.getMiniMessageSerializer());

        final TextSerializer serializer;
        Type(TextSerializer serializer) {
            this.serializer = serializer;
        }
    }
}
