package dev.superchirok1.wgtranslator.util;

import dev.superchirok1.wgtranslator.serializer.Text;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

@UtilityClass
public class Logger {
    private final CommandSender CONSOLE = Bukkit.getConsoleSender();
    private final String PREFIX = "&b&l[WG-Translator] &f";

    public void info(String message) {
        log(message);
    }
    public void warn(String message) {
        log("&e" + message);
    }
    public void error(String message) {
        log("&c" + message);
    }

    private void log(String msg) {
        CONSOLE.sendMessage(Text.getLegacyAmpersandSerializer().colorize(PREFIX + msg));
    }
}
