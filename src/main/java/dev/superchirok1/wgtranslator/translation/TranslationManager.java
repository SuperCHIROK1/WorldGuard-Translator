package dev.superchirok1.wgtranslator.translation;

import dev.superchirok1.wgtranslator.WorldGuardTranslator;
import dev.superchirok1.wgtranslator.util.StringPatcher;
import dev.superchirok1.wgtranslator.serializer.Text;
import dev.superchirok1.wgtranslator.util.Logger;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;

@UtilityClass
public class TranslationManager {
    @Setter
    private WorldGuardTranslator plugin;
    public static final String REPO_URL = "https://raw.githubusercontent.com/SuperCHIROK1/WorldGuard-Translator/main/translations/";

    @Getter
    private File file;
    @Getter
    private YamlConfiguration config;
    @Getter
    private Translation translation;

    public void loadTranslations(String lang, Runnable callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            File folder = new File(plugin.getDataFolder(), "translations");
            if (!folder.exists()) folder.mkdirs();

            file = new File(folder, lang + ".yml");
            if (!file.exists()) {
                try {
                    URL url = new URL(REPO_URL + lang + ".yml");
                    try (InputStream in = url.openStream()) {
                        Files.copy(in, file.toPath());
                    }
                } catch (Exception e) {
                    Logger.error("Failed to download localization file [" + lang + "] from repository. English localization is used by default.");
                    file = new File(folder, "en.yml");
                    if (!file.exists()) {
                        plugin.saveResource("translations/en.yml", false);
                    }
                }
            }

            config = YamlConfiguration.loadConfiguration(file);
            translation = new Translation(config, file);
            callback.run();

        });
    }

    public void setMemoryDenyMessage(String message) {
        message = Text.get.colorize(message);
        StringPatcher.setDenyMessage(message);
    }

    public void setDenyMessage(String message) {
        setMemoryDenyMessage(message);
        config.set("deny_message", message);
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
