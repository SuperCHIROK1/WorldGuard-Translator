package dev.superchirok1.wgtranslator.serializer;

import dev.superchirok1.wgtranslator.serializer.impl.LegacyAmpersandSerializer;
import dev.superchirok1.wgtranslator.serializer.impl.MiniMessageSerializer;
import lombok.Getter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Text {
    @Getter
    private final LegacyAmpersandSerializer legacyAmpersandSerializer = new LegacyAmpersandSerializer();

    public TextSerializer get;

    public void init(Type type) {
        get = (type == Type.LEGACY_AMPERSAND)
                ? legacyAmpersandSerializer
                : new MiniMessageSerializer();
    }

    public enum Type {
        LEGACY_AMPERSAND,
        MINI_MESSAGE;
    }
}
