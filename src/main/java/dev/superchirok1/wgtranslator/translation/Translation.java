package dev.superchirok1.wgtranslator.translation;

import dev.superchirok1.wgtranslator.config.Configuration;
import dev.superchirok1.wgtranslator.serializer.Text;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@Getter
public class Translation {
    private final YamlConfiguration config;
    private final File file;

    public Translation(YamlConfiguration config, File file) {
        denyMessage = Text.get.colorize(config.getString("deny_message", "&#ff5400&l❌ Hey! &fSorry, but you can`t &#ff5400%what% &fhere!"));
        entryDenyMessage = Text.get.colorize(config.getString("entry_deny_message", "&#ff5400&l❌ Hey! &fYou are not permitted to enter this area."));
        exitDenyMessage = Text.get.colorize(config.getString("exit_deny_message", "&#ff5400&l❌ Hey! &fYou are not permitted to leave this area."));
        this.config = config;
        this.file = file;
        components = new Components(this);
        components.init(config.getConfigurationSection("components"));
        messages = new Messages(config.getConfigurationSection("messages"));
    }

    public Messages messages;
    public Components components;
    public String denyMessage, entryDenyMessage, exitDenyMessage;

    @Getter
    public static class Components {
        protected final Object2ObjectOpenHashMap<String, String> map = new Object2ObjectOpenHashMap<>();
        private final Translation translation;
        private ConfigurationSection section;

        protected Components(Translation translation) {
            this.translation = translation;
        }

        public void init(ConfigurationSection section) {
            map.clear();
            if (section == null) return;
            section.getKeys(false).forEach(k -> map.put(k, section.getString(k)));
            this.section = section;
        }

        public String get(String what) {
            return map.getOrDefault(what, what);
        }

        public void set(String key, String value) {
            this.section.set(key, value);
            map.put(key, this.section.getString(key));
            try {
                translation.getConfig().save(translation.getFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void remove(String key) {
            map.remove(key);
            this.section.set(key, null);
            try {
                translation.getConfig().save(translation.getFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public record Messages(
            String reloaded,
            String denyMessageInstalled,
            String denyMessageInstalledTemp,
            String componentAdded,
            String componentUsage,
            String componentListHeader,
            String componentListFormat,
            String help
    ) {
        public Messages(ConfigurationSection section) {
            this(
                    format(section.getString("reloaded", "")),
                    format(section.getString("deny_message.installed", "")),
                    format(section.getString("deny_message.installed_temp", "")),
                    format(section.getString("component.added", "")),
                    format(section.getString("component.usage", "")),
                    format(section.getString("component.list.header", "")),
                    format(section.getString("component.list.format", "")),
                    format(section.getString("help", ""))
            );
        }

        private static String format(String message) {
            return Text.get.colorize(message).replace("%prefix%", Configuration.prefix);
        }
    }
}
