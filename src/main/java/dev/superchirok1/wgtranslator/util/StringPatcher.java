package dev.superchirok1.wgtranslator.util;

import com.sk89q.worldguard.bukkit.listener.RegionProtectionListener;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StringFlag;
import dev.superchirok1.wgtranslator.translation.TranslationManager;
import lombok.experimental.UtilityClass;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Field;

@UtilityClass
public class StringPatcher {

    static {
        ByteBuddyAgent.install();
    }

    // https://github.com/EngineHub/WorldGuard/blob/20eb298cacbf4519222884b0178ed779d614f8fa/worldguard-core/src/main/java/com/sk89q/worldguard/protection/flags/Flags.java#L222-L226
    public void setDenyMessage(String denyMessage) {
        StringFlag deny = Flags.DENY_MESSAGE;
        try {
            Field field = StringFlag.class.getDeclaredField("defaultValue");
            field.setAccessible(true);

            field.set(deny, denyMessage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // https://github.com/EngineHub/WorldGuard/blob/20eb298cacbf4519222884b0178ed779d614f8fa/worldguard-bukkit/src/main/java/com/sk89q/worldguard/bukkit/listener/RegionProtectionListener.java#L123-L128
    public void patchFlags() {
        new ByteBuddy()
                .redefine(RegionProtectionListener.class)
                .visit(Advice.to(TellAdvice.class).on(ElementMatchers.named("formatAndSendDenyMessage")))
                .make()
                .load(RegionProtectionListener.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
    }

    public class TellAdvice {
        @Advice.OnMethodEnter
        public static void onEnter(@Advice.Argument(value = 0, readOnly = false) String what) {
            if (what != null) {
                what = TranslationManager.getTranslation().components.get(what);
            }
        }
    }


}
