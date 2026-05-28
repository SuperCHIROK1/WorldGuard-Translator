package dev.superchirok1.wgtranslator;

import dev.superchirok1.wgtranslator.command.TranslatorCommand;
import dev.superchirok1.wgtranslator.config.Configuration;
import dev.superchirok1.wgtranslator.util.StringPatcher;
import dev.superchirok1.wgtranslator.serializer.Text;
import dev.superchirok1.wgtranslator.translation.TranslationManager;
import dev.superchirok1.wgtranslator.util.Logger;
import dev.superchirok1.wgtranslator.util.Metrics;
import dev.superchirok1.wgtranslator.util.UpdateChecker;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.function.Consumer;

@Getter
public final class WorldGuardTranslator extends JavaPlugin {

    public static boolean isFirstLaunch = false;
    private final List<String> translators = List.of("git-jamesfrench");

    @Override
    public void onEnable() {
        isFirstLaunch = !getDataFolder().exists();

        int pluginId = 31595;
        Metrics metrics = new Metrics(this, pluginId);

        saveDefaultConfig();
        if (isFirstLaunch) {
            saveResource("translations/en.yml", false);
        }
        Text.init(Text.Type.valueOf(getConfig().getString("serializer", "LEGACY_AMPERSAND").toUpperCase()));
        Configuration.init(this);
        TranslationManager.setPlugin(this);
        TranslationManager.loadTranslations(Configuration.language, (time)->{
            StringPatcher.patchFlags();
            StringPatcher.setDenyMessage(TranslationManager.getTranslation().denyMessage);
            Bukkit.getScheduler().runTask(this, ()->{
                TranslatorCommand translatorCommand = new TranslatorCommand(this);
                PluginCommand command = getCommand("wgt");
                command.setExecutor(translatorCommand);
                command.setTabCompleter(translatorCommand);
                getServer().getPluginManager().registerEvents(new UpdateChecker(this), this);
                checkForUpdates();
            });
        });

        metrics.addCustomChart(new Metrics.SimplePie("used_language", () -> {
            return Configuration.language;
        }));

        metrics.addCustomChart(new Metrics.SimplePie("used_serializer", () -> {
            return Configuration.serializer.name();
        }));
    }

    public void reload(String name, Consumer<Long> onComplete) {
        long start = System.currentTimeMillis();
        Logger.info(name + " start task reloading");

        reloadConfig();
        Configuration.init(this);

        TranslationManager.loadTranslations(Configuration.language, (v) -> {
            StringPatcher.setDenyMessage(TranslationManager.getTranslation().denyMessage);
            long time = System.currentTimeMillis() - start;
            Logger.info("Reload completed in " + time + "ms");
            onComplete.accept(time);
        });
    }

    public void checkForUpdates() {
        UpdateChecker.check(this, version -> {
            if (!Configuration.checkUpdates) return;

            String currentVersion = getDescription().getVersion();
            if (currentVersion.equals(version)) {
                Logger.info("");
                Logger.info("&eUpdate available! &c" + currentVersion + " &7-> &a" + version);
                Logger.info("Download: https://github.com/SuperCHIROK1/WorldGuard-Translator");
                Logger.info("");
                UpdateChecker.updateAvailable = true;
            }
        });
    }

}
