package dev.superchirok1.wgtranslator.config;

import dev.superchirok1.wgtranslator.WorldGuardTranslator;
import dev.superchirok1.wgtranslator.serializer.Text;
import lombok.experimental.UtilityClass;
import org.bukkit.configuration.file.FileConfiguration;

@UtilityClass
public class Configuration {

    public Text.Type serializer = Text.Type.LEGACY_AMPERSAND;
    public String language = "en";
    public String prefix = "&3&l[WG-Translator]&f";
    public boolean checkUpdates = true;

    public String permissionAdmin = "wgtranslator.admin";
    public String permissionUpdateLog = "wgtranslator.update";

    public void init(WorldGuardTranslator pluginModel) {
        FileConfiguration config = pluginModel.getConfig();
        serializer = Text.Type.valueOf(config.getString("serializer", serializer.toString()).toUpperCase());
        Text.init(serializer);
        prefix = config.getString("prefix", prefix);
        language = config.getString("lang", language.toLowerCase());
        checkUpdates = config.getBoolean("check_updates", checkUpdates);
        permissionAdmin = config.getString("permission.admin_command", permissionAdmin);
        permissionUpdateLog = config.getString("permission.update_log", permissionUpdateLog);
    }

}
